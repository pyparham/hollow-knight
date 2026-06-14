package com.parham.hollowknight.controller.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.parham.hollowknight.Main;
import com.parham.hollowknight.model.KeySetting;
import com.parham.hollowknight.view.BackGroundDust;

import static com.parham.hollowknight.Constants.SCREEN_HEIGHT;
import static com.parham.hollowknight.Constants.SCREEN_WIDTH;

public class GuideScreen extends BaseScreen {
    private final Main game;
    private Stage stage;
    private MenuPointerManager pointerManager;
    private TextButton backButton;
    BackGroundDust menuDust = new BackGroundDust();


    public GuideScreen(Main game) {
        this.game = game;
    }

    public Stage getStage() {
        return this.stage;
    }
    @Override
    public void show() {
        stage = new Stage(new FitViewport(SCREEN_WIDTH, SCREEN_HEIGHT), game.batch);
        Gdx.input.setInputProcessor(stage);

        Label.LabelStyle titleStyle = new Label.LabelStyle(game.assets.menuFont, Color.GOLD);
        Label.LabelStyle textStyle = new Label.LabelStyle(game.assets.listFont, Color.WHITE);
        Label.LabelStyle cheatStyle = new Label.LabelStyle(game.assets.listFont, Color.GRAY);

        Table root = new Table();
        root.setFillParent(true);
        root.pad(100);
        stage.addActor(root);

        root.add(new Label("GAME GUIDE", titleStyle)).padBottom(30).row();

        KeySetting keys = KeySetting.getInstance();

        Table controlsTable = new Table();
        controlsTable.add(new Label("CONTROLS", textStyle)).colspan(2).padBottom(15).row();

        controlsTable.add(new Label("Move: " + formatKey(keys.getLeftKey()) + " / " + formatKey(keys.getRightKey()), textStyle)).padRight(40).left();
        controlsTable.add(new Label("Look: " + formatKey(keys.getUpKey()) + " / " + formatKey(keys.getDownKey()), textStyle)).left().row();

        controlsTable.add(new Label("Jump: " + formatKey(keys.getJumpKey()), textStyle)).padRight(40).left();
        controlsTable.add(new Label("Dash: " + formatKey(keys.getDashKey()), textStyle)).left().row();

        controlsTable.add(new Label("Attack: " + formatKey(keys.getAttackKey()), textStyle)).padRight(40).left();
        controlsTable.add(new Label("Focus (Heal): " + formatKey(keys.getFocusKey()), textStyle)).left().row();

        controlsTable.add(new Label("Quick Cast: " + formatKey(keys.getQuickCastKey()), textStyle)).padRight(40).left();
        controlsTable.add(new Label("Super Dash: " + formatKey(keys.getSuperDashKey()), textStyle)).left().row();

        controlsTable.add(new Label("Dream Nail: " + formatKey(keys.getDreamNailKey()), textStyle)).padRight(40).left();
        controlsTable.add(new Label("Map: " + formatKey(keys.getQuickMapKey()) + " | Inv: " + formatKey(keys.getInventoryKey()), textStyle)).left().row();

        root.add(controlsTable).padBottom(25).row();

        Table abilitiesTable = new Table();
        abilitiesTable.add(new Label("ABILITIES & VITALITY", textStyle)).padBottom(10).row();
        abilitiesTable.add(new Label("- SOUL: Attack enemies to gather Soul.", textStyle)).row();
        abilitiesTable.add(new Label("- FOCUS: Hold '" + formatKey(keys.getFocusKey()) + "' to consume Soul and heal.", textStyle)).row();
        root.add(abilitiesTable).padBottom(25).row();

        Table cheatsTable = new Table();
        cheatsTable.add(new Label("CHEAT CODES", textStyle)).padBottom(10).row();
        cheatsTable.add(new Label("IDDQD - God Mode | IDKFA - Full Power", cheatStyle)).row();
        root.add(cheatsTable).padBottom(30).row();

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = game.assets.menuFont;
        btnStyle.fontColor = Color.GRAY;
        btnStyle.overFontColor = Color.WHITE;

        backButton = new TextButton("BACK", btnStyle);
        backButton.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                game.popScreen();
            }

            @Override
            public void enter(InputEvent e, float x, float y, int pointer, Actor from) {
                highlightSelected();
            }
        });
        root.add(backButton);
        pointerManager = new MenuPointerManager(stage, game);
        stage.act();
        highlightSelected();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.05f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        game.batch.setColor(0.3f, 0.3f, 0.3f, 1f);
        game.batch.draw(game.assets.menuBackground, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        game.batch.end();

        stage.act(delta);
        stage.draw();

        game.batch.begin();
        menuDust.updateAndDraw(game.batch, delta, 1f);
        game.batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE))
            game.popScreen();

    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
        stage.dispose();
    }

    private void highlightSelected() {
        if (pointerManager != null)
            pointerManager.updatePosition(backButton);
    }

    private String formatKey(int keycode) {
        if (keycode == Input.Buttons.LEFT) return "L-CLICK";
        if (keycode == Input.Buttons.RIGHT) return "R-CLICK";
        String keyName = Input.Keys.toString(keycode);
        return keyName != null ? keyName.toUpperCase() : "UNKNOWN";
    }
}
