package com.parham.hollowknight.controller.screens.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.parham.hollowknight.Main;
import com.parham.hollowknight.controller.TextureManager;
import com.parham.hollowknight.controller.screens.BaseScreen;
import com.parham.hollowknight.controller.screens.MenuPointerManager;
import com.parham.hollowknight.controller.screens.SliderBar;
import com.parham.hollowknight.view.BackGroundDust;

import static com.parham.hollowknight.Constants.SCREEN_HEIGHT;
import static com.parham.hollowknight.Constants.SCREEN_WIDTH;

public class TextureScreen extends BaseScreen {

    private final Main game;
    private Stage stage;
    private FitViewport viewport;
    private int brightness;
    private Slider brightnessSlider;

    private TextButton resetBtn, backBtn;
    private TextButton[] navButtons;
    private Runnable[] navActions;
    private int selectedIndex = 0;
    private MenuPointerManager pointerManager;
    BackGroundDust menuDust = new BackGroundDust();


    public TextureScreen(Main game) {
        this.game = game;
        brightness = Math.round(TextureManager.getInstance().getDarkness());
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
        stage.draw();
        brightnessSlider.setValue(brightness);
        highlightSelected();
    }

    private void buildUI() {
        Label.LabelStyle titleStyle = new Label.LabelStyle(game.assets.menuFont, Color.WHITE);
        Label.LabelStyle rowStyle = new Label.LabelStyle(game.assets.listFont, Color.WHITE);
        Label.LabelStyle valStyle = new Label.LabelStyle(game.assets.listFont, Color.WHITE);

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = game.assets.menuFont;
        btnStyle.fontColor = Color.WHITE;

        Table root = new Table();
        root.setFillParent(true);
        root.pad(20);
        stage.addActor(root);

        root.add(new Label("Video", titleStyle)).padBottom(8).row();
        root.add(new Image(game.assets.settingsDivider)).width(600).height(45).padBottom(55).row();

        brightnessSlider = new SliderBar(0, 10, 1, game);
        Label brightnessVal = new Label(String.valueOf(brightness), valStyle);

        brightnessSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent e, Actor a) {
                brightness = (int) brightnessSlider.getValue();
                brightnessVal.setText(String.valueOf((int) brightness));
                applyBrightness();
            }
        });

        Table sliderCol = new Table();

        sliderCol.add(brightnessSlider).width(280).row();

        Table brightnessRow = new Table();
        brightnessRow.add(new Label("Brightness:", rowStyle)).width(260).left().spaceRight(100);
        brightnessRow.add(sliderCol).padLeft(30).padRight(20);
        brightnessRow.add(brightnessVal).width(40).left();
        root.add(brightnessRow).padBottom(80).row();

        resetBtn = new TextButton("Reset Defaults", btnStyle);
        backBtn = new TextButton("Back", btnStyle);

        resetBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                resetDefaults();
            }

            @Override
            public void enter(InputEvent e, float x, float y, int pointer, Actor from) {
                if (selectedIndex == 0) return;
                selectedIndex = 0;
                highlightSelected();
            }
        });

        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                game.popScreen();
            }

            @Override
            public void enter(InputEvent e, float x, float y, int pointer, Actor from) {
                if (selectedIndex == 1) return;
                selectedIndex = 1;
                highlightSelected();
            }
        });
        root.add(resetBtn).padBottom(22).row();
        root.add(backBtn).row();

        navButtons = new TextButton[]{resetBtn, backBtn};
        navActions = new Runnable[]{
            this::resetDefaults,
            () -> game.pushScreen(new SettingsScreen(game))
        };
    }

    private void applyBrightness() {
        game.textureManager.updateDarkness((10 - brightness) / 10f);
    }

    private void resetDefaults() {
        brightness = 10;
        brightnessSlider.setValue(10f);
    }

    private void highlightSelected() {
        if (navButtons == null) return;
        if (pointerManager != null)
            pointerManager.updatePosition(navButtons[selectedIndex]);
    }

    private void handleKeyboard() {
        if (navButtons == null) return;
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedIndex = (selectedIndex + 1) % navButtons.length;
            highlightSelected();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectedIndex = (selectedIndex - 1 + navButtons.length) % navButtons.length;
            highlightSelected();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER))
            navActions[selectedIndex].run();
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) ||
            Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE))
            game.popScreen();
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
}
