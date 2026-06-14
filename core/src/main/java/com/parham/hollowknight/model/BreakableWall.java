package com.parham.hollowknight.model;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.parham.hollowknight.model.enums.WallState;

public class BreakableWall {
    public Rectangle hitbox;
    public int health;
    public String targetLayerName;

    public WallState state = WallState.IDLE;
    public float stateTimer = 0f;

    public boolean physicsRemoved = false;

    private float hitCooldown = 0f;
    private final float SHAKE_DURATION = 0.2f;

    private TextureRegion idleFrame;
    private Animation<TextureRegion> shakeAnim;

    public BreakableWall(Rectangle hitbox, int health, String targetLayerName,
                         TextureRegion idleFrame,
                         Animation<TextureRegion> shakeAnim) {
        this.hitbox = hitbox;
        this.health = health;
        this.targetLayerName = targetLayerName;
        this.idleFrame = idleFrame;
        this.shakeAnim = shakeAnim;
    }

    public void update(float delta) {
        if (hitCooldown > 0f) hitCooldown -= delta;
        stateTimer += delta;
        if (state == WallState.SHAKING && stateTimer >= SHAKE_DURATION) state = WallState.IDLE;
    }

    public boolean takeDamage() {
        if (state == WallState.BREAKING || state == WallState.DESTROYED || hitCooldown > 0f) return false;

        health--;
        hitCooldown = 0.25f;
        stateTimer = 0f;

        if (health <= 0) state = WallState.BREAKING;
        else state = WallState.SHAKING;

        return true;
    }

    public void draw(SpriteBatch batch) {
        if (state == WallState.DESTROYED) return;

        TextureRegion currentFrame = idleFrame;

        if (state == WallState.IDLE) currentFrame = idleFrame;

        else if (state == WallState.SHAKING)
            currentFrame = shakeAnim.getKeyFrame(stateTimer, true);

        if (state == WallState.BREAKING)
            state = WallState.DESTROYED;

        if (currentFrame != null)
            batch.draw(currentFrame, hitbox.x, hitbox.y, hitbox.width, hitbox.height);
    }

    public static Animation<TextureRegion> buildAnim(Texture sheet, int frameCount,
                                               float frameDuration,
                                               Animation.PlayMode mode) {
        TextureRegion[] frames = new TextureRegion[frameCount];
        int frameW = sheet.getWidth() / frameCount;
        int frameH = sheet.getHeight();
        for (int i = 0; i < frameCount; i++) {
            frames[i] = new TextureRegion(sheet, i * frameW, 0, frameW, frameH);
        }
        Animation<TextureRegion> anim = new Animation<>(frameDuration, frames);
        anim.setPlayMode(mode);
        return anim;
    }
}
