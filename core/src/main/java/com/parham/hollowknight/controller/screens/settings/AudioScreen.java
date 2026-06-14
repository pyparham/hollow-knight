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
import com.parham.hollowknight.controller.AudioManager;
import com.parham.hollowknight.controller.screens.BaseScreen;
import com.parham.hollowknight.controller.screens.MenuPointerManager;
import com.parham.hollowknight.controller.screens.SliderBar;
import com.parham.hollowknight.view.BackGroundDust;

import static com.parham.hollowknight.Constants.SCREEN_HEIGHT;
import static com.parham.hollowknight.Constants.SCREEN_WIDTH;

public class AudioScreen extends BaseScreen {

    private final Main game;
    private Stage stage;
    private FitViewport viewport;
    private float masterVolume;
    private float soundVolume;
    private float musicVolume;

    private TextButton resetBtn, backBtn;
    private TextButton[] navButtons;
    private Runnable[] navActions;
    private int selectedIndex = 0;
    private MenuPointerManager pointerManager;
    BackGroundDust menuDust = new BackGroundDust();


    private Slider masterSlider, soundSlider, musicSlider;

    public AudioScreen(Main game) {
        this.game = game;
        masterVolume = AudioManager.getInstance().getMasterVolume() * 10;
        soundVolume = AudioManager.getInstance().getSoundVolume() * 10;
        musicVolume = AudioManager.getInstance().getMusicVolume() * 10;
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
        stage.act();
        masterSlider.setValue(masterVolume);
        soundSlider.setValue(soundVolume);
        musicSlider.setValue(musicVolume);
        pointerManager = new MenuPointerManager(stage, game);
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

        root.add(new Label("Audio", titleStyle)).padBottom(8).row();
        root.add(new Image(game.assets.settingsDivider)).width(600).height(45).padBottom(55).row();

        masterSlider = new SliderBar(0, 10, 1, game);
        soundSlider = new SliderBar(0, 10, 1, game);
        musicSlider = new SliderBar(0, 10, 1, game);

        Label masterVal = new Label("10", valStyle);
        Label soundVal = new Label("10", valStyle);
        Label musicVal = new Label("10", valStyle);

        masterSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent e, Actor a) {
                masterVolume = masterSlider.getValue();
                masterVal.setText(String.valueOf((int) masterVolume));
                applyVolumes();
            }
        });

        soundSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent e, Actor a) {
                soundVolume = soundSlider.getValue();
                soundVal.setText(String.valueOf((int) soundVolume));
                applyVolumes();
            }
        });

        musicSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent e, Actor a) {
                musicVolume = musicSlider.getValue();
                musicVal.setText(String.valueOf((int) musicVolume));
                applyVolumes();
            }
        });

        root.add(buildRow("Master Volume:", masterSlider, masterVal, rowStyle)).padBottom(30).row();
        root.add(buildRow("Sound Volume:", soundSlider, soundVal, rowStyle)).padBottom(30).row();
        root.add(buildRow("Music Volume:", musicSlider, musicVal, rowStyle)).padBottom(90).row();

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
            () -> game.setScreen(new SettingsScreen(game))
        };
    }

    private Table buildRow(String labelText, Slider slider, Label valueLabel, Label.LabelStyle style) {
        Table row = new Table();
        row.add(new Label(labelText, style)).width(260).left().spaceRight(100);
        row.add(slider).width(280).padLeft(30).padRight(20);
        row.add(valueLabel).width(40).left();
        return row;
    }

    private void applyVolumes() {
        if (game.audioManager != null)
            game.audioManager.updateMusicVolume(masterVolume, musicVolume, soundVolume);
    }

    private void resetDefaults() {
        masterVolume = 10f;
        soundVolume = 10f;
        musicVolume = 10f;
        masterSlider.setValue(10f);
        soundSlider.setValue(10f);
        musicSlider.setValue(10f);
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
