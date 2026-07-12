package com.parham.hollowknight.controller.screens.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.parham.hollowknight.Main;
import com.parham.hollowknight.controller.screens.BaseScreen;
import com.parham.hollowknight.controller.screens.MenuPointerManager;
import com.parham.hollowknight.model.KeySetting;
import com.parham.hollowknight.view.BackGroundDust;

import static com.parham.hollowknight.Constants.SCREEN_HEIGHT;
import static com.parham.hollowknight.Constants.SCREEN_WIDTH;

public class KeyboardScreen extends BaseScreen {

    private final Main game;
    private Stage stage;
    private FitViewport viewport;

    private final KeySetting keys = KeySetting.getInstance();

    private String listeningFor = null;
    private Label listeningLabel = null;

    private TextButton resetBtn, backBtn;
    private TextButton[] navButtons;
    private Runnable[] navActions;
    private int selectedIndex = 0;
    private MenuPointerManager pointerManager;
    private boolean isListeningForKey = false;

    private TextButton.TextButtonStyle keyBtnStyle;
    BackGroundDust menuDust = new BackGroundDust();

    private final InputAdapter listeningAdapter = new InputAdapter() {
        @Override
        public boolean keyDown(int keycode) {
            if (listeningFor == null) return false;

            if (keycode == Input.Keys.ESCAPE) {
                if (listeningLabel != null)
                    listeningLabel.setText(getKeyName(listeningFor));
            } else {
                applyKeyBinding(listeningFor, keycode);
                if (listeningLabel != null)
                    listeningLabel.setText(Input.Keys.toString(keycode));
            }


            listeningFor = null;
            listeningLabel = null;

            Gdx.input.setInputProcessor(stage);
            return true;
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            if (listeningFor == null) return false;

            applyKeyBinding(listeningFor, button);
            if (listeningLabel != null)
                listeningLabel.setText(getDisplayString(button));

            listeningFor = null;
            listeningLabel = null;
            Gdx.input.setInputProcessor(stage);
            return true;
        }
    };

    public KeyboardScreen(Main game) {
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
        Label.LabelStyle actionStyle = new Label.LabelStyle(game.assets.listFont, Color.WHITE);

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = game.assets.menuFont;
        btnStyle.fontColor = Color.WHITE;
        btnStyle.overFontColor = new Color(0.85f, 0.85f, 0.85f, 1f);

        keyBtnStyle = makeKeyButtonStyle();

        Table root = new Table();
        root.setFillParent(true);
        root.pad(50);
        stage.addActor(root);

        root.add(new Label("Keyboard", titleStyle))
            .colspan(5).center().padBottom(8).row();
        root.add(new Image(game.assets.settingsDivider))
            .colspan(5).width(700).height(45).center().padBottom(45).row();


        Table leftCol = new Table();
        Table rightCol = new Table();

        addKeyRow(leftCol, "Up", keys.getUpKey(), keys::setUpKey, actionStyle);
        addKeyRow(leftCol, "Down", keys.getDownKey(), keys::setDownKey, actionStyle);
        addKeyRow(leftCol, "Jump", keys.getJumpKey(), keys::setJumpKey, actionStyle);
        addKeyRow(leftCol, "Attack", keys.getAttackKey(), keys::setAttackKey, actionStyle);
        addKeyRow(leftCol, "Dash", keys.getDashKey(), keys::setDashKey, actionStyle);
        addKeyRow(leftCol, "Focus", keys.getFocusKey(), keys::setFocusKey, actionStyle);

        addKeyRow(rightCol, "Left", keys.getLeftKey(), keys::setLeftKey, actionStyle);
        addKeyRow(rightCol, "Right", keys.getRightKey(), keys::setRightKey, actionStyle);
        addKeyRow(rightCol, "Quick Map", keys.getQuickMapKey(), keys::setQuickMapKey, actionStyle);
        addKeyRow(rightCol, "Super Dash", keys.getSuperDashKey(), keys::setSuperDashKey, actionStyle);
        addKeyRow(rightCol, "Dream Nail", keys.getDreamNailKey(), keys::setDreamNailKey, actionStyle);
        addKeyRow(rightCol, "Cast", keys.getCastKey(), keys::setCastKey, actionStyle);

        root.add(leftCol).top().padRight(150);
        root.add(rightCol).top().row();

        Table invRow = new Table();
        Label invLabel = new Label("Inventory", actionStyle);
        TextButton invBtn = makeKeyButton("Inventory", keys.getInventoryKey(), keys::setInventoryKey);

        invRow.add(invLabel).padRight(20);
        invRow.add(invBtn).size(90, 42);
        root.add(invRow).colspan(2).center().padTop(10).padBottom(50).row();

        resetBtn = new TextButton("Reset Defaults", btnStyle);
        backBtn = new TextButton("Back", btnStyle);

        resetBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                resetDefaults();
            }

            @Override
            public void enter(InputEvent e, float x, float y, int p,
                              com.badlogic.gdx.scenes.scene2d.Actor f) {
                if (selectedIndex != 0) {
                    selectedIndex = 0;
                    highlightSelected();
                }
            }
        });
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                game.popScreen();
            }

            @Override
            public void enter(InputEvent e, float x, float y, int p,
                              com.badlogic.gdx.scenes.scene2d.Actor f) {
                if (selectedIndex != 1) {
                    selectedIndex = 1;
                    highlightSelected();
                }
            }
        });

        root.add(resetBtn).colspan(2).center().padBottom(20).row();
        root.add(backBtn).colspan(2).center().row();

        navButtons = new TextButton[]{resetBtn, backBtn};
        navActions = new Runnable[]{
            this::resetDefaults,
            () -> {
                game.setScreen(new SettingsScreen(game));
            }
        };
    }


    private void addKeyRow(Table col, String actionName, int currentKey,
                           IntSetter setter, Label.LabelStyle style) {
        col.add(new Label(actionName, style)).left().width(250).padBottom(18).padRight(100);
        col.add(makeKeyButton(actionName, currentKey, setter)).size(250, 42).padBottom(18).row();
    }


    private TextButton makeKeyButton(String actionName, int currentKey, IntSetter setter) {
        TextButton btn = new TextButton(getDisplayString(currentKey), keyBtnStyle);

        btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isListeningForKey) return;
                listeningFor = actionName;
                listeningLabel = btn.getLabel();
                btn.setText("...");

                InputMultiplexer mux = new InputMultiplexer(listeningAdapter, stage);
                Gdx.input.setInputProcessor(mux);
            }
        });
        return btn;
    }

    private TextButton.TextButtonStyle makeKeyButtonStyle() {
        Pixmap bg = new Pixmap(90, 42, Pixmap.Format.RGBA8888);
        bg.setColor(0f, 0f, 0f, 0f);
        bg.fill();
        bg.setColor(Color.WHITE);
        bg.drawRectangle(0, 0, 90, 42);
        bg.drawRectangle(1, 1, 88, 40);

        Pixmap bgPressed = new Pixmap(90, 42, Pixmap.Format.RGBA8888);
        bgPressed.setColor(0.3f, 0.3f, 0.3f, 0.6f);
        bgPressed.fill();
        bgPressed.setColor(Color.LIGHT_GRAY);
        bgPressed.drawRectangle(0, 0, 90, 42);

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = game.assets.listFont;
        style.fontColor = Color.WHITE;
        style.up = new TextureRegionDrawable(new TextureRegion(new Texture(bg)));
        style.down = new TextureRegionDrawable(new TextureRegion(new Texture(bgPressed)));
        style.over = style.down;

        bg.dispose();
        bgPressed.dispose();
        return style;
    }


    private void resetDefaults() {
        keys.resetDefaults();
        stage.clear();
        buildUI();
        stage.act();
        pointerManager = new MenuPointerManager(stage, game);
        highlightSelected();
    }


    private void highlightSelected() {
        if (navButtons == null || pointerManager == null) return;
        pointerManager.updatePosition(navButtons[selectedIndex]);
    }

    private void handleKeyboardNav() {
        if (listeningFor != null) return;

        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedIndex = (selectedIndex + 1) % navButtons.length;
            updatePointer();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectedIndex = (selectedIndex - 1 + navButtons.length) % navButtons.length;
            updatePointer();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER))
            navActions[selectedIndex].run();
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE) ||
            Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.popScreen();
        }
    }

    private void updatePointer() {
        if (pointerManager != null && navButtons != null)
            pointerManager.updatePosition(navButtons[selectedIndex]);
    }

    private void applyKeyBinding(String actionName, int keyCode) {
        switch (actionName) {
            case "Up":
                keys.setUpKey(keyCode);
                break;
            case "Down":
                keys.setDownKey(keyCode);
                break;
            case "Jump":
                keys.setJumpKey(keyCode);
                break;
            case "Attack":
                keys.setAttackKey(keyCode);
                break;
            case "Dash":
                keys.setDashKey(keyCode);
                break;
            case "Focus / Cast":
                keys.setFocusKey(keyCode);
                break;
            case "Left":
                keys.setLeftKey(keyCode);
                break;
            case "Right":
                keys.setRightKey(keyCode);
                break;
            case "Quick Map":
                keys.setQuickMapKey(keyCode);
                break;
            case "Super Dash":
                keys.setSuperDashKey(keyCode);
                break;
            case "Dream Nail":
                keys.setDreamNailKey(keyCode);
                break;
            case "Quick Cast":
                keys.setCastKey(keyCode);
                break;
            case "Inventory":
                keys.setInventoryKey(keyCode);
                break;
        }
    }


    private String getKeyName(String actionName) {
        int code = -1;
        switch (actionName) {
            case "Up":
                code = keys.getUpKey();
                break;
            case "Down":
                code = keys.getDownKey();
                break;
            case "Jump":
                code = keys.getJumpKey();
                break;
            case "Attack":
                code = keys.getAttackKey();
                break;
            case "Dash":
                code = keys.getDashKey();
                break;
            case "Focus / Cast":
                code = keys.getFocusKey();
                break;
            case "Left":
                code = keys.getLeftKey();
                break;
            case "Right":
                code = keys.getRightKey();
                break;
            case "Quick Map":
                code = keys.getQuickMapKey();
                break;
            case "Super Dash":
                code = keys.getSuperDashKey();
                break;
            case "Dream Nail":
                code = keys.getDreamNailKey();
                break;
            case "Quick Cast":
                code = keys.getCastKey();
                break;
            case "Inventory":
                code = keys.getInventoryKey();
                break;
        }
        return getDisplayString(code);
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

        handleKeyboardNav();

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
        listeningFor = null;
        dispose();
    }

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
    }


    @FunctionalInterface
    interface IntSetter {
        void set(int value);
    }

    private String getDisplayString(int code) {
        if (code == Input.Buttons.LEFT) return "Left Click";
        if (code == Input.Buttons.RIGHT) return "Right Click";
        if (code == Input.Buttons.MIDDLE) return "Middle Click";
        if (code == Input.Buttons.FORWARD) return "Forward Click";
        if (code == Input.Buttons.BACK) return "Back Click";

        String keyName = Input.Keys.toString(code);
        return keyName != null ? keyName : "Unknown";
    }
}

