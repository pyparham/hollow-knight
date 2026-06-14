package com.parham.hollowknight.view;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;


public class HudRenderer {

    private static final float HUD_W = 1920f;
    private static final float HUD_H = 1080f;

    private static final float ORB_CELL_W = 257;
    private static final float ORB_CELL_H = ORB_CELL_W * (164f / 257f);

    private static final float HEALTH_ROW_NEGATIVE_PAD = -120f;
    private static final float HEALTH_ROW_TOP_PAD = 20f;

    private final Stage stage;
    private final SoulOrbActor soulOrb;
    private final HealthRowActor healthRow;

    public HudRenderer(
        Texture shellSheet,
        TextureAtlas soulOrbAtlas,
        Texture eyeTex,
        Texture circleMaskTex,
        Texture filledHealthTex,
        Texture emptyHealthTex,
        Texture shineTex,
        Texture refillTex,
        Texture breakTex
    ) {
        FitViewport hudViewport = new FitViewport(HUD_W, HUD_H);
        stage = new Stage(hudViewport);

        soulOrb = new SoulOrbActor(shellSheet, soulOrbAtlas, eyeTex, circleMaskTex);
        healthRow = new HealthRowActor(filledHealthTex, emptyHealthTex, shineTex, refillTex, breakTex);

        Table table = new Table();
        table.setFillParent(true);
        table.top().left();
        table.pad(30f, 30f, 0f, 0f);

        table.add(soulOrb).size(ORB_CELL_W, ORB_CELL_H);
        table.add(healthRow)
            .padLeft(HEALTH_ROW_NEGATIVE_PAD)
            .padTop(HEALTH_ROW_TOP_PAD)
            .size(healthRow.getWidth(), healthRow.getHeight());

        stage.addActor(table);
    }

    public void playIntro() {
        soulOrb.playShellGrowIntro();
    }

    public void notifySoulChanged(float currentSoul) {
        soulOrb.setSoul(currentSoul);
    }

    public void notifyHealthChanged(int newHealth, int maxHealth) {
        healthRow.setHealth(newHealth, maxHealth);
    }

    public void render(float delta) {
        stage.act(delta);
        stage.getViewport().apply();
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void dispose() {
        stage.dispose();
        soulOrb.dispose();
    }
}
