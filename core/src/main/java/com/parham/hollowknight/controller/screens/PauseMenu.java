package com.parham.hollowknight.controller.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.parham.hollowknight.Main;
import com.parham.hollowknight.controller.SaveManager;
import com.parham.hollowknight.controller.screens.settings.SettingsScreen;

import static com.parham.hollowknight.Constants.SCREEN_HEIGHT;
import static com.parham.hollowknight.Constants.SCREEN_WIDTH;


public class PauseMenu {

    private final Main game;

    private final Stage stage;
    private final FitViewport viewport;

    private boolean visible = false;
    private float overlayAlpha = 0f;
    private static final float TARGET_ALPHA = 0.65f;
    private static final float FADE_SPEED = 4f;

    private TextButton[] menuButtons;
    private Runnable[] menuActions;
    private int selectedIndex = 0;
    private final MenuPointerManager pointerManager;

    private GameScreen gameScreen;

    public PauseMenu(Main game, GameScreen gameScreen) {
        this.game = game;
        this.gameScreen = gameScreen;

        viewport = new FitViewport(SCREEN_WIDTH, SCREEN_HEIGHT);
        stage = new Stage(viewport, game.batch);

        buildUI();
        stage.act();
        pointerManager = new MenuPointerManager(stage, game);
        highlightPointer();
    }


    private void buildUI() {
        Label.LabelStyle titleStyle = new Label.LabelStyle(game.assets.menuFont, Color.WHITE);

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = game.assets.listFont;
        btnStyle.fontColor = Color.WHITE;
        btnStyle.overFontColor = new Color(0.8f, 0.8f, 0.8f, 1f);

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        root.add(new Label("Paused", titleStyle)).padBottom(50).row();

        String[] labels = {"Continue", "Options", "Guide", "Exit to Menu"};
        menuActions = new Runnable[]{
            this::resume,
            () -> {
                game.pushScreen(new SettingsScreen(game));
            },
            () -> {
                game.pushScreen(new GuideScreen(game));
            },
            this::exitToMenu
        };

        menuButtons = new TextButton[labels.length];
        for (int i = 0; i < labels.length; i++) {
            menuButtons[i] = new TextButton(labels[i], btnStyle);
            final int idx = i;
            menuButtons[i].addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent e, float x, float y) {
                    menuActions[idx].run();
                }

                @Override
                public void enter(InputEvent e, float x, float y, int p,
                                  com.badlogic.gdx.scenes.scene2d.Actor f) {
                    if (p == -1 && selectedIndex != idx) {
                        selectedIndex = idx;
                        highlightPointer();
                    }
                }
            });
            root.add(menuButtons[idx]).padBottom(24).row();
        }

    }

    public void show() {
        visible = true;
        selectedIndex = 0;
    }

    public void hide() {
        visible = false;
    }

    public boolean isVisible() {
        return visible;
    }

    public void update(float delta) {
        if (!visible) return;

        if (overlayAlpha < TARGET_ALPHA)
            overlayAlpha = Math.min(TARGET_ALPHA, overlayAlpha + FADE_SPEED * delta);

        stage.act(delta);
        handleKeyboardNav();
    }

    public void render(ShapeRenderer shapeRenderer) {
        if (!visible) return;

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, overlayAlpha);
        shapeRenderer.rect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);

        viewport.apply();
        stage.draw();
    }

    public void resize(int w, int h) {
        viewport.update(w, h, true);
    }

    private void handleKeyboardNav() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedIndex = (selectedIndex + 1) % menuButtons.length;
            highlightPointer();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectedIndex = (selectedIndex - 1 + menuButtons.length) % menuButtons.length;
            highlightPointer();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER))
            menuActions[selectedIndex].run();
    }

    private void highlightPointer() {
        if (pointerManager != null && menuButtons != null)
            pointerManager.updatePosition(menuButtons[selectedIndex]);
    }


    private void resume() {
        overlayAlpha = 0f;
        gameScreen.setPauseMenuVisible(false);
    }

    private void exitToMenu() {
        if (game.currentGameData != null)
            SaveManager.getInstance().save(game.currentGameData);
        overlayAlpha = 0f;
        game.audioManager.switchMusicWithFade(game.assets.backgroundMusic);
        game.setScreen(new MainMenuScreen(game));
    }

    public void dispose() {
        if (stage != null) stage.dispose();
    }

    public Stage getStage() {
        return stage;
    }
}
