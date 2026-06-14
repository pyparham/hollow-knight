package com.parham.hollowknight.controller.screens;

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
import com.parham.hollowknight.controller.SaveManager;
import com.parham.hollowknight.model.GameData;
import com.parham.hollowknight.view.BackGroundDust;

import static com.parham.hollowknight.Constants.SCREEN_HEIGHT;
import static com.parham.hollowknight.Constants.SCREEN_WIDTH;

public class StartGameScreen extends BaseScreen {

    private final Main game;
    private Stage stage;
    private FitViewport viewport;
    private final SaveManager saveManager = SaveManager.getInstance();

    private Table[] slotRows;
    private TextButton[] clearButtons;
    private int selectedIndex = 0;
    private MenuPointerManager pointerManager;
    private TextButton backButton;

    BackGroundDust menuDust = new BackGroundDust();


    public StartGameScreen(Main game) {
        this.game = game;
    }

    @Override
    public Stage getStage() {
        return stage;
    }

    @Override
    public void show() {
        viewport = new FitViewport(SCREEN_WIDTH, SCREEN_HEIGHT);
        stage = new Stage(viewport, game.batch);
        Gdx.input.setInputProcessor(stage);

        buildUI();
        stage.act();
        pointerManager = new MenuPointerManager(stage, game);
        highlightSelected();
    }


    private void buildUI() {
        Label.LabelStyle titleStyle = new Label.LabelStyle(game.assets.menuFont, Color.WHITE);
        Label.LabelStyle areaStyle = new Label.LabelStyle(game.assets.listFont, Color.WHITE);
        Label.LabelStyle timeStyle = new Label.LabelStyle(game.assets.listFont,
            new Color(0.75f, 0.75f, 0.75f, 1f));
        Label.LabelStyle geoStyle = new Label.LabelStyle(game.assets.listFont,
            new Color(0.6f, 0.75f, 1f, 1f));
        Label.LabelStyle numberStyle = new Label.LabelStyle(game.assets.menuFont, Color.WHITE);

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = game.assets.menuFont;
        btnStyle.fontColor = Color.WHITE;
        btnStyle.overFontColor = new Color(0.8f, 0.8f, 0.8f, 1f);

        TextButton.TextButtonStyle clearStyle = new TextButton.TextButtonStyle();
        clearStyle.font = game.assets.listFont;
        clearStyle.fontColor = new Color(0.75f, 0.75f, 0.75f, 1f);
        clearStyle.overFontColor = Color.WHITE;

        Table root = new Table();
        root.setFillParent(true);
        root.pad(40);
        stage.addActor(root);

        root.add(new Label("Select Profile", titleStyle)).padBottom(8).row();
        root.add(new Image(game.assets.settingsDivider))
            .width(700).height(45).padBottom(40).row();

        int slotCount = saveManager.getMaxSlots();
        slotRows = new Table[slotCount + 1]; // saves and back
        clearButtons = new TextButton[slotCount];

        for (int i = 0; i < slotCount; i++) {
            final int slotIndex = i;
            GameData data = saveManager.getSlot(slotIndex);

            Table row = new Table();
            row.pad(10);

            Label number = new Label((i + 1) + ".", numberStyle);
            row.add(number).width(60).padRight(20);

            if (data != null) {
                Table infoCol = new Table();
                infoCol.add(new Label(data.areaName, areaStyle)).left().row();

                Table rightCol = new Table();
                rightCol.add(new Label(data.getFormattedPlayTime(), timeStyle)).right();

                row.add(infoCol).left().expandX();
                row.add(rightCol).right().padRight(40).fillX();

                row.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent e, float x, float y) {
                        startGame(slotIndex, data);
                    }

                    @Override
                    public void enter(InputEvent e, float x, float y, int p, Actor f) {
                        if (selectedIndex == slotIndex) return;
                        selectedIndex = slotIndex;
                        highlightSelected();
                    }
                });

                TextButton clearBtn = new TextButton("Clear Save", clearStyle);
                clearBtn.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent e, float x, float y) {
                        saveManager.clearSlot(slotIndex);
                        rebuildUI();
                    }
                });
                row.add(clearBtn).right().padLeft(20);
                row.left();

                clearButtons[i] = clearBtn;

            } else {
                Label newGameLabel = new Label("New Game", areaStyle);
                row.add(newGameLabel).center().expandX().fillX();

                row.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent e, float x, float y) {
                        startNewGame(slotIndex);
                    }

                    @Override
                    public void enter(InputEvent e, float x, float y, int p, Actor f) {
                        if (selectedIndex == slotIndex) return;
                        selectedIndex = slotIndex;
                        highlightSelected();
                    }
                });
            }

            slotRows[i] = row;
            root.add(row).width(1400).height(80).fillX().padBottom(20).row();

            root.add(new Image(game.assets.lineBar)).width(1400).height(2).padBottom(15).row();
        }

        backButton = new TextButton("Back", btnStyle);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }

            @Override
            public void enter(InputEvent e, float x, float y, int p, Actor f) {
                if (selectedIndex == slotCount) return;
                selectedIndex = slotCount;
                highlightSelected();
            }
        });
        Table backRow = new Table();
        backRow.add(backButton);
        slotRows[slotCount] = backRow;
        root.add(backRow).padTop(20).row();
    }


    private void rebuildUI() {
        stage.clear();
        buildUI();
        stage.act();
        pointerManager = new MenuPointerManager(stage, game);
        selectedIndex = 0;
        highlightSelected();
    }


    private void startGame(int slotIndex, GameData data) {
        game.currentGameData = data;
        game.setScreen(new GameScreen(game));
    }

    private void startNewGame(int slotIndex) {
        GameData newData = saveManager.createNewGame(slotIndex);
        game.currentGameData = newData;
        game.setScreen(new GameScreen(game));
    }


    private void highlightSelected() {
        if (slotRows == null || pointerManager == null) return;
        Table row = slotRows[selectedIndex];
        pointerManager.updatePosition(row);
    }

    private void handleKeyboard() {
        int total = slotRows.length;
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedIndex = (selectedIndex + 1) % total;
            highlightSelected();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectedIndex = (selectedIndex - 1 + total) % total;
            highlightSelected();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (selectedIndex == total - 1) {
                game.setScreen(new MainMenuScreen(game));
            } else {
                GameData data = saveManager.getSlot(selectedIndex);
                if (data != null) startGame(selectedIndex, data);
                else startNewGame(selectedIndex);
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE) ||
            Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
            game.setScreen(new MainMenuScreen(game));
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
    public void resize(int width, int heigh) {
        viewport.update(width, heigh, true);
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
