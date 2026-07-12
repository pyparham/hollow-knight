package com.parham.hollowknight.view;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.parham.hollowknight.model.entities.ShockWave;

public class ShockWaveAnimationManager {

    private final Animation<TextureRegion> waveAnim;

    private static final float DRAW_WIDTH = 240f;
    private static final float DRAW_HEIGHT = 200f;

    public ShockWaveAnimationManager(Texture sheet, int frameCount) {
        int fw = sheet.getWidth() / frameCount;
        int fh = sheet.getHeight();
        TextureRegion[] frames = new TextureRegion[frameCount];
        for (int i = 0; i < frameCount; i++) {
            frames[i] = new TextureRegion(sheet, i * fw, 0, fw, fh);
        }
        waveAnim = new Animation<>(0.05f, frames);
        waveAnim.setPlayMode(Animation.PlayMode.LOOP);
    }

    public void draw(SpriteBatch batch, ShockWave sw) {
        TextureRegion frame = waveAnim.getKeyFrame(sw.stateTimer);

        if (sw.facingRight)
            batch.draw(frame, sw.position.x, sw.position.y - 15, DRAW_WIDTH, DRAW_HEIGHT);
        else
            batch.draw(frame, sw.position.x + DRAW_WIDTH, sw.position.y - 15, -DRAW_WIDTH, DRAW_HEIGHT);

    }
}
