package com.parham.hollowknight.controller.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.parham.hollowknight.Main;
import com.parham.hollowknight.controller.screens.settings.SettingsScreen;
import com.parham.hollowknight.view.BackGroundDust;

import static com.parham.hollowknight.Constants.SCREEN_HEIGHT;
import static com.parham.hollowknight.Constants.SCREEN_WIDTH;


public class MainMenuScreen extends BaseScreen {
    private Stage stage;
    private final Main game;
    private Viewport viewport;
    private int selectedIndex = 0;
    private float alpha = 0f;

    private TextButton[] menuButtons;
    private Runnable[] menuActions;
    private MenuPointerManager pointerManager;
    BackGroundDust menuDust = new BackGroundDust();

    public MainMenuScreen(Main game) {
        this.game = game;
    }

    public Stage getStage() {
        return this.stage;
    }

    @Override
    public void show() {
        viewport = new FitViewport(SCREEN_WIDTH, SCREEN_HEIGHT);
        stage = new Stage(viewport, game.batch);
        Gdx.input.setInputProcessor(stage);
        buildUI();
        pointerManager = new MenuPointerManager(stage, game);
        stage.act();
        highlightSelected();
    }

    @Override
    public void render(float delta) {
        if (alpha < 1)
            alpha = Math.min(alpha + delta / 2, 1f);

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        game.batch.setProjectionMatrix(stage.getCamera().combined);
        game.batch.begin();
        game.batch.setColor(1f, 1f, 1f, alpha);
        game.batch.draw(game.assets.menuBackground, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        game.batch.end();

        game.batch.begin();
        menuDust.updateAndDraw(game.batch, delta, 1f);
        game.batch.end();

        stage.getRoot().getColor().a = alpha;
        stage.act(delta);
        stage.draw();

        handleKeyboard();
    }


    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
    }

    private void buildUI() {
        TextButton.TextButtonStyle style = makeButtonStyle();

        String[] labels = {
            "Start Game", "Settings", "Achievements", "Guide", "Quit Game"
        };

        menuActions = new Runnable[]{
            () -> game.pushScreen(new StartGameScreen(game)),
            () -> game.pushScreen(new SettingsScreen(game)),
            () -> {
            },
            () -> game.pushScreen(new GuideScreen(game)),
            () -> Gdx.app.exit()
        };

        menuButtons = new TextButton[labels.length];

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        Image logo = new Image(game.assets.menuLogo);
        root.add(logo).width(900).height(350).padBottom(70).row();

        for (int i = 0; i < labels.length; i++) {
            menuButtons[i] = new TextButton(labels[i], style);
            attachClickListener(menuButtons[i], menuActions[i]);
            root.add(menuButtons[i]).padBottom(18).row();
        }
    }

    private TextButton.TextButtonStyle makeButtonStyle() {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = game.assets.listFont;
        style.fontColor = Color.WHITE;
        return style;
    }

    private void attachClickListener(TextButton btn, Runnable action) {
        btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action.run();
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer,
                              com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                for (int i = 0; i < menuButtons.length; i++) {
                    if (menuButtons[i] == btn) {
                        if (selectedIndex == i) break;
                        selectedIndex = i;
                        highlightSelected();
                        break;
                    }
                }
            }
        });
    }

    private void highlightSelected() {
        for (int i = 0; i < menuButtons.length; i++) {
            menuButtons[i].getLabel().setFontScale(
                i == selectedIndex ? 1.1f : 1f
            );
            if (i == selectedIndex && pointerManager != null) {
                pointerManager.updatePosition(menuButtons[i]);
            }
        }
    }

    private void handleKeyboard() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedIndex = (selectedIndex + 1) % menuButtons.length;
            highlightSelected();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectedIndex = (selectedIndex - 1 + menuButtons.length) % menuButtons.length;
            highlightSelected();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            menuActions[selectedIndex].run();
        }
    }

}
