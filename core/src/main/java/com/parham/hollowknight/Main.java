package com.parham.hollowknight;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.parham.hollowknight.controller.AudioManager;
import com.parham.hollowknight.controller.SaveManager;
import com.parham.hollowknight.controller.TextureManager;
import com.parham.hollowknight.controller.screens.BaseScreen;
import com.parham.hollowknight.controller.screens.GameScreen;
import com.parham.hollowknight.controller.screens.IntroScreen;
import com.parham.hollowknight.model.GameData;

import java.util.Stack;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class Main extends Game {

    public SpriteBatch batch;
    public AssetLoader assets;
    public AssetLoader[] assetLoaders = new AssetLoader[4];
    public AudioManager audioManager;
    public TextureManager textureManager;
    public float globalDarkness = 0f;
    public ShapeRenderer globalShapeRenderer;
    public SaveManager saveManager;
    public GameData currentGameData;

    private final Stack<BaseScreen> screenHistory = new Stack<>();

    @Override
    public void create() {
        Pixmap directPixmap = new Pixmap(Gdx.files.internal(Constants.CURSOR));
        Pixmap scaledPixmap = new Pixmap(64, 64, directPixmap.getFormat());
        scaledPixmap.setFilter(Pixmap.Filter.BiLinear);
        scaledPixmap.drawPixmap(directPixmap,
            0, 0, directPixmap.getWidth(), directPixmap.getHeight(),
            0, 0, 64, 64);
        Cursor cursor = Gdx.graphics.newCursor(scaledPixmap, 0, 0);
        Gdx.graphics.setCursor(cursor);

        directPixmap.dispose();
        scaledPixmap.dispose();
        saveManager = SaveManager.getInstance();
        batch = new SpriteBatch();
        assets = new AssetLoader();
        setScreen(new IntroScreen(this));
        globalShapeRenderer = new ShapeRenderer();
        AudioManager.init(this);
        TextureManager.init(this);
        textureManager = TextureManager.getInstance();

    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
    public void render() {
        super.render();
        float delta = Gdx.graphics.getDeltaTime();
        if (this.assets.backgroundMusic != null) AudioManager.getInstance().update(delta);

        if (globalDarkness > 0f && getScreen() instanceof BaseScreen) {
            Stage currentStage = ((BaseScreen) getScreen()).getStage();
            Camera cam = currentStage.getCamera();

            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            globalShapeRenderer.setProjectionMatrix(cam.combined);

            globalShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            globalShapeRenderer.setColor(new Color(0f, 0f, 0f, globalDarkness));

            float camX = cam.position.x - (cam.viewportWidth / 2f);
            float camY = cam.position.y - (cam.viewportHeight / 2f);

            globalShapeRenderer.rect(camX, camY, cam.viewportWidth, cam.viewportHeight);

            globalShapeRenderer.end();

            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        assets.dispose();
        if (globalShapeRenderer != null) globalShapeRenderer.dispose();
    }

    public void pushScreen(BaseScreen newScreen) {
        if (getScreen() != null && getScreen() instanceof BaseScreen)
            screenHistory.push((BaseScreen) getScreen());

        setScreen(newScreen);
    }

    public void popScreen() {
        if (!screenHistory.isEmpty()) {
            BaseScreen previous = screenHistory.pop();
            setScreen(previous);

            if (previous instanceof GameScreen)
                ((GameScreen) previous).setPauseMenuVisible(true);

        }
    }

}
