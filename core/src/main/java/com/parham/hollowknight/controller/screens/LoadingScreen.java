package com.parham.hollowknight.controller.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.parham.hollowknight.Main;
import com.parham.hollowknight.controller.AudioManager;
import com.parham.hollowknight.view.BackGroundDust;

import static com.parham.hollowknight.Constants.*;

public class LoadingScreen implements Screen {
    private final Main game;
    private FitViewport viewport;
    private ShapeRenderer shapeRenderer;
    private Texture backgroundTexture;
    private float alpha = 0f;
    private float displayTimer = 0f;
    private boolean isFadingOut = false;

    BackGroundDust menuDust = new BackGroundDust();


    public LoadingScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        viewport = new FitViewport(SCREEN_WIDTH, SCREEN_HEIGHT);
        shapeRenderer = new ShapeRenderer();
        backgroundTexture = new Texture(Gdx.files.internal(LOADING_BG));
        backgroundTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    @Override
    public void render(float delta) {

        displayTimer += delta;
        float progress = game.assets.update();

        if (!isFadingOut) {
            if (alpha < 1f)
                alpha = Math.min(1f, alpha + delta / 2);
            if (game.assets.isFinished() && displayTimer >= MIN_DISPLAY_TIME)
                isFadingOut = true;

        } else {
            alpha -= delta / 2;
            if (alpha <= 0f) {
                alpha = 0f;
                game.assets.finishMainMenu();
                game.assets.finishMusic();
                game.audioManager = AudioManager.getInstance();
                game.audioManager.playMenuMusic(game);
                game.setScreen(new MainMenuScreen(game));
                return;
            }
        }

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();

        game.batch.setProjectionMatrix(viewport.getCamera().combined);
        game.batch.begin();
        game.batch.setColor(1f, 1f, 1f, alpha);
        game.batch.draw(backgroundTexture, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        game.batch.end();

        game.batch.begin();
        menuDust.updateAndDraw(game.batch, delta, 1f);
        game.batch.end();

        drawLoadingBar(progress);
    }

    private void drawLoadingBar(float progress) {
        float barX = (SCREEN_WIDTH - LOADING_BAR_WIDTH) / 2f;
        float barY = LOADING_BAR_Y_OFFSET;

        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 0.5f * alpha);
        shapeRenderer.rect(barX, barY, LOADING_BAR_WIDTH, LOADING_BAR_HEIGHT);

        shapeRenderer.setColor(1f, 1f, 1f, 0.9f * alpha);
        shapeRenderer.rect(barX, barY, LOADING_BAR_WIDTH * progress, LOADING_BAR_HEIGHT);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.6f, 0.6f, 0.6f, 0.4f * alpha);
        shapeRenderer.rect(barX, barY, LOADING_BAR_WIDTH, LOADING_BAR_HEIGHT);
        shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
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
        shapeRenderer.dispose();
        backgroundTexture.dispose();
    }

}
