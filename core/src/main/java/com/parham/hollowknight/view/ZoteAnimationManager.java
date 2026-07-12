package com.parham.hollowknight.view;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.parham.hollowknight.model.entities.Zote;
import com.parham.hollowknight.model.enums.EnemyState;

public class ZoteAnimationManager {

    private final Animation<TextureRegion> idleAnim;
    private final Animation<TextureRegion> talkAnim;
    private final Animation<TextureRegion> attackAnim;
    private final Animation<TextureRegion> turnAnim;

    private float stateTime = 0f;

    private static final float DRAW_WIDTH = 349f;
    private static final float DRAW_HEIGHT = 186f;

    public ZoteAnimationManager(
        Texture idleSheet, int idleFrames,
        Texture talkSheet, int talkFrames,
        Texture attackSheet, int attackFrames,
        Texture turnSheet, int turnFrames
    ) {
        idleAnim = buildAnim(idleSheet, idleFrames, 0.15f, Animation.PlayMode.LOOP);
        talkAnim = buildAnim(talkSheet, talkFrames, 0.12f, Animation.PlayMode.LOOP);
        attackAnim = buildAnim(attackSheet, attackFrames, 0.08f, Animation.PlayMode.LOOP);
        turnAnim = buildAnim(turnSheet, turnFrames, 0.08f, Animation.PlayMode.LOOP);
    }

    public void update(float delta) {
        stateTime += delta;
    }

    public void draw(SpriteBatch batch, Zote zote) {
        Animation<TextureRegion> current = zote.isAngry() ? attackAnim
            : zote.isTalking ? talkAnim
            : zote.state == EnemyState.TURN ? turnAnim : idleAnim;
        TextureRegion frame = current.getKeyFrame(stateTime);

        float x = zote.getPosition().x - 150;
        float y = zote.getPosition().y;

        if (zote.facingRight) batch.draw(frame, x + 360, y, -DRAW_WIDTH, DRAW_HEIGHT);
        else batch.draw(frame, x, y, DRAW_WIDTH, DRAW_HEIGHT);

    }

    private Animation<TextureRegion> buildAnim(Texture sheet, int frameCount,
                                               float frameDuration, Animation.PlayMode mode) {
        int fw = sheet.getWidth() / frameCount;
        int fh = sheet.getHeight();
        TextureRegion[] frames = new TextureRegion[frameCount];
        for (int i = 0; i < frameCount; i++)
            frames[i] = new TextureRegion(sheet, i * fw, 0, fw, fh);
        Animation<TextureRegion> anim = new Animation<>(frameDuration, frames);
        anim.setPlayMode(mode);
        return anim;
    }
}
