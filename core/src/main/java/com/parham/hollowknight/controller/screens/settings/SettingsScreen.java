package com.parham.hollowknight.controller.screens.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.parham.hollowknight.Main;
import com.parham.hollowknight.controller.screens.BaseScreen;
import com.parham.hollowknight.controller.screens.MenuPointerManager;
import com.parham.hollowknight.view.BackGroundDust;

import static com.parham.hollowknight.Constants.SCREEN_HEIGHT;
import static com.parham.hollowknight.Constants.SCREEN_WIDTH;

public class SettingsScreen extends BaseScreen {

    private final Main game;
    private Stage stage;
    private FitViewport viewport;

    private TextButton[] menuButtons;
    private Runnable[] menuActions;
    private int selectedIndex = 0;
    private MenuPointerManager pointerManager;
    private int menuBGIndex = 0;

    BackGroundDust menuDust = new BackGroundDust();


    public SettingsScreen(Main game) {
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
        ensureAssetsLoaded();
        buildUI();
        pointerManager = new MenuPointerManager(stage, game);
        stage.act();
        highlightSelected();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(stage.getCamera().combined);
        game.batch.begin();
        game.batch.draw(game.assets.menuBackground, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        game.batch.end();

        game.batch.begin();
        menuDust.updateAndDraw(game.batch, delta, 1f);
        game.batch.end();

        stage.act(delta);
        stage.draw();

        handleKeyboard();
    }

    private void ensureAssetsLoaded() {
        if (game.assets.settingsDivider == null) {
            game.assets.queueSettings();
            while (!game.assets.isFinished()) game.assets.update();
            game.assets.finishSettings();
        }
    }

    private void buildUI() {
        Label.LabelStyle titleStyle = new Label.LabelStyle(game.assets.menuFont, Color.WHITE);

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = game.assets.listFont;
        btnStyle.fontColor = Color.WHITE;

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        root.add(new Label("Settings", titleStyle)).padBottom(8).row();

        root.add(new Image(game.assets.settingsDivider)).width(600).height(45).padBottom(60).row();

        String[] labels = {"Audio", "Video", "Keyboard", "Language", "Change Background", "Back"};
        menuActions = new Runnable[]{
            () -> game.pushScreen(new AudioScreen(game)),
            () -> game.pushScreen(new TextureScreen(game)),
            () -> game.pushScreen(new KeyboardScreen(game)),
            () -> { /* game.pushScreen(new LanguageScreen(game)); */ },
            this::changeBackground,
            game::popScreen
        };

        menuButtons = new TextButton[labels.length];
        for (int i = 0; i < labels.length; i++) {
            menuButtons[i] = new TextButton(labels[i], btnStyle);
            final int index = i;
            menuButtons[i].addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent e, float x, float y) {
                    menuActions[index].run();
                }

                @Override
                public void enter(InputEvent e, float x, float y, int pointer, Actor from) {
                    if (selectedIndex == index) return;
                    selectedIndex = index;
                    highlightSelected();
                }
            });

            if (i == labels.length - 1)
                root.add(menuButtons[i]).padTop(80).row();
            else
                root.add(menuButtons[i]).padBottom(22).row();
        }
    }

    private void highlightSelected() {
        if (menuButtons == null) return;
        if (pointerManager != null)
            pointerManager.updatePosition(menuButtons[selectedIndex]);
    }

    private void handleKeyboard() {
        if (menuButtons == null) return;
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedIndex = (selectedIndex + 1) % menuButtons.length;
            highlightSelected();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectedIndex = (selectedIndex - 1 + menuButtons.length) % menuButtons.length;
            highlightSelected();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER))
            menuActions[selectedIndex].run();
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) ||
            Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE))
            game.popScreen();
    }

    @Override
    public void resize(int w, int h) {
        viewport.update(w, h, true);
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

    private void changeBackground() {
        game.assets.menuBackground = game.assets.menuBackgrounds.get(menuBGIndex);
        menuBGIndex = (menuBGIndex + 1) % game.assets.menuBackgrounds.size();
    }
}
