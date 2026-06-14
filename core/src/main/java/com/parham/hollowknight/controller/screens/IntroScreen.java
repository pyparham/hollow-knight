package com.parham.hollowknight.controller.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.parham.hollowknight.Constants;
import com.parham.hollowknight.Main;

import static com.parham.hollowknight.Constants.*;

public class IntroScreen implements Screen {
    private final Main game;
    private Texture logoTexture;
    private Viewport viewport;
    private float timer = 0f;


    public IntroScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        logoTexture = new Texture(Gdx.files.internal(Constants.INTRO_LOGO));
        logoTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        viewport = new FitViewport(SCREEN_WIDTH, SCREEN_HEIGHT);

    }

    @Override
    public void render(float delta) {

        timer += delta;

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        game.batch.setProjectionMatrix(viewport.getCamera().combined);


        float alpha = 1f;
        if (timer < INTRO_FADE_TIME)
            alpha = timer;
        else if (timer > INTRO_DURATION - INTRO_FADE_TIME)
            alpha = (INTRO_DURATION - timer);

        game.batch.begin();
        game.batch.setColor(1, 1, 1, Math.max(0, alpha));
        float logoW = logoTexture.getWidth() * 1.3f;
        float logoH = logoTexture.getHeight() * 1.3f;
        float x = (SCREEN_WIDTH - logoW) / 2f;
        float y = (SCREEN_HEIGHT - logoH) / 2f;
        game.batch.draw(logoTexture, x, y, logoW, logoH);
        game.batch.end();
        if (timer >= INTRO_DURATION) {
            game.assets.queueMainMenu();
            game.assets.queueMusic();
            game.setScreen(new LoadingScreen(game));
        }
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
        logoTexture.dispose();
    }

}
