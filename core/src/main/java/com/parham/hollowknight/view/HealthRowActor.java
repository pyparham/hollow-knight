package com.parham.hollowknight.view;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;


public class HealthRowActor extends Actor {

    private static final int MAX_MASKS = 5;

    private static final float MASK_W = 72f;
    private static final float MASK_H = 90f;
    private static final float MASK_STEP = 50f;

    private static final int BREAK_FRAME_COUNT = 6;
    private static final int REFILL_FRAME_COUNT = 5;
    private static final int SHINE_FRAME_COUNT = 5;

    private static final float BREAK_FRAME_DUR = 0.07f;
    private static final float REFILL_FRAME_DUR = 0.08f;
    private static final float SHINE_FRAME_DUR = 0.12f;

    private final TextureRegion filledRegion;
    private final TextureRegion emptyRegion;
    private final Animation<TextureRegion> shineAnim;
    private final Animation<TextureRegion> refillAnim;
    private final Animation<TextureRegion> breakAnim;

    private final float[] breakTime = new float[MAX_MASKS];
    private final float[] refillTime = new float[MAX_MASKS];
    private final boolean[] isBreaking = new boolean[MAX_MASKS];
    private final boolean[] isRefilling = new boolean[MAX_MASKS];
    private float shineTime = 0f;

    private int currentHealth = MAX_MASKS;
    private int maxHealth = MAX_MASKS;

    public HealthRowActor(
        Texture filledHealthTex,
        Texture emptyHealthTex,
        Texture shineTex,
        Texture refillTex,
        Texture breakTex
    ) {
        filledRegion = new TextureRegion(filledHealthTex);
        emptyRegion = new TextureRegion(emptyHealthTex);

        shineAnim = buildStripAnim(shineTex, SHINE_FRAME_COUNT, SHINE_FRAME_DUR, Animation.PlayMode.LOOP);
        refillAnim = buildStripAnim(refillTex, REFILL_FRAME_COUNT, REFILL_FRAME_DUR, Animation.PlayMode.NORMAL);
        breakAnim = buildStripAnim(breakTex, BREAK_FRAME_COUNT, BREAK_FRAME_DUR, Animation.PlayMode.NORMAL);

        setSize(MASK_STEP * MAX_MASKS, MASK_H);
    }

    public void setHealth(int newHealth, int newMaxHealth) {
        if (newHealth < currentHealth) {
            for (int i = newHealth; i < currentHealth && i < MAX_MASKS; i++) {
                isBreaking[i] = true;
                breakTime[i] = 0f;
                isRefilling[i] = false;
            }
        } else if (newHealth > currentHealth) {
            for (int i = currentHealth; i < newHealth && i < MAX_MASKS; i++) {
                isRefilling[i] = true;
                refillTime[i] = 0f;
                isBreaking[i] = false;
            }
        }
        currentHealth = newHealth;
        maxHealth = newMaxHealth;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        shineTime += delta;

        for (int i = 0; i < MAX_MASKS; i++) {
            if (isBreaking[i]) {
                breakTime[i] += delta;
                if (breakAnim.isAnimationFinished(breakTime[i])) isBreaking[i] = false;
            }
            if (isRefilling[i]) {
                refillTime[i] += delta;
                if (refillAnim.isAnimationFinished(refillTime[i])) isRefilling[i] = false;
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float baseX = getX();
        float baseY = getY();

        batch.setColor(1f, 1f, 1f, parentAlpha);

        for (int i = 0; i < maxHealth && i < MAX_MASKS; i++) {
            float x = baseX + i * MASK_STEP;
            float y = baseY;

            if (isBreaking[i]) {
                batch.draw(breakAnim.getKeyFrame(breakTime[i]), x, y, MASK_W, MASK_H);

            } else if (isRefilling[i]) {
                batch.draw(refillAnim.getKeyFrame(refillTime[i]), x, y, MASK_W, MASK_H);

            } else if (i < currentHealth) {
                batch.draw(filledRegion, x, y, MASK_W, MASK_H);

                batch.setColor(1f, 1f, 1f, 0.55f * parentAlpha);
                batch.draw(shineAnim.getKeyFrame(shineTime), x, y, MASK_W, MASK_H);
                batch.setColor(1f, 1f, 1f, parentAlpha);

            } else {
                batch.draw(emptyRegion, x, y, MASK_W, MASK_H);
            }
        }
    }

    private Animation<TextureRegion> buildStripAnim(
        Texture tex, int frameCount, float frameDur, Animation.PlayMode mode
    ) {
        TextureRegion[] frames = new TextureRegion[frameCount];
        int fw = tex.getWidth() / frameCount;
        int fh = tex.getHeight();
        for (int i = 0; i < frameCount; i++)
            frames[i] = new TextureRegion(tex, i * fw, 0, fw, fh);
        Animation<TextureRegion> anim = new Animation<>(frameDur, frames);
        anim.setPlayMode(mode);
        return anim;
    }
}
