package com.parham.hollowknight.view;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.parham.hollowknight.model.entities.Enemy;

public class EnemyAnimationManager {

    private final Animation<TextureRegion> idleAnim;
    private final Animation<TextureRegion> walkAnim;
    private final Animation<TextureRegion> turnAnim;
    private final Animation<TextureRegion> attackAnim;
    private final Animation<TextureRegion> attackJumpAnim;
    private final Animation<TextureRegion> hitAnim;
    private final Animation<TextureRegion> deadAirAnim;
    private final Animation<TextureRegion> deadGroundAnim;


    public EnemyAnimationManager(
        Texture walkSheet, int walkFrames, float walkSpeed,
        Texture idleSheet, int idleFrames, float idleSpeed,
        Texture turnSheet, int turnFrames, float turnSpeed,
        Texture attackSheet, int attackFrames, float attackSpeed,
        Texture attackJumpSheet, int attackJumpFrames, float attackJumpSpeed,
        Texture hitSheet, int hitFrames, float hitSpeed,
        Texture deadAirSheet, int deadAirFrames, float deadAirSpeed,
        Texture deadGroundSheet, int deadGroundFrames, float deadGroundSpeed
    ) {

        this.walkAnim = buildAnim(walkSheet, walkFrames, walkSpeed, Animation.PlayMode.LOOP);

        this.idleAnim = (idleSheet != null) ?
            buildAnim(idleSheet, idleFrames, idleSpeed, Animation.PlayMode.LOOP) : null;
        this.turnAnim = (turnSheet != null) ?
            buildAnim(turnSheet, turnFrames, turnSpeed, Animation.PlayMode.NORMAL) : null;
        this.attackAnim = (attackSheet != null) ?
            buildAnim(attackSheet, attackFrames, attackSpeed, Animation.PlayMode.LOOP) : null;
        this.attackJumpAnim = (attackJumpSheet != null) ?
            buildAnim(attackJumpSheet, attackJumpFrames, attackJumpSpeed, Animation.PlayMode.NORMAL) : null;
        this.hitAnim = (hitSheet != null) ?
            buildAnim(hitSheet, hitFrames, hitSpeed, Animation.PlayMode.NORMAL) : null;
        this.deadAirAnim = (deadAirSheet != null) ?
            buildAnim(deadAirSheet, deadAirFrames, deadAirSpeed, Animation.PlayMode.NORMAL) : null;
        this.deadGroundAnim = (deadGroundSheet != null) ?
            buildAnim(deadGroundSheet, deadGroundFrames, deadGroundSpeed, Animation.PlayMode.NORMAL) : null;
    }

    public void draw(SpriteBatch batch, Enemy enemy) {
        TextureRegion frame = getCurrentFrame(enemy);
        if (frame == null) return;

        float fw = frame.getRegionWidth();
        float fh = frame.getRegionHeight();

        float x = enemy.getPosition().x - (fw - enemy.getBodyHitbox().width) / 2f;
        float y = enemy.getPosition().y - 15f;

        if (!enemy.facingRight) batch.draw(frame, x, y, fw, fh);
        else batch.draw(frame, x + fw, y, -fw, fh);

    }

    private TextureRegion getCurrentFrame(Enemy enemy) {
        float time = enemy.animTime;

        return switch (enemy.state) {
            case WALKING -> walkAnim.getKeyFrame(time);
            case IDLE -> (idleAnim != null) ? idleAnim.getKeyFrame(time) : walkAnim.getKeyFrame(0f);
            case TURN -> (turnAnim != null) ? turnAnim.getKeyFrame(time) : walkAnim.getKeyFrame(0f);
            case ATTACK -> (
                attackAnim != null) ? attackAnim.getKeyFrame(time) : walkAnim.getKeyFrame(time);
            case ATTACK_JUMP ->
                (attackJumpAnim != null) ? attackJumpAnim.getKeyFrame(time) : walkAnim.getKeyFrame(time);
            case HIT -> (hitAnim != null) ? hitAnim.getKeyFrame(time) : walkAnim.getKeyFrame(0f);
            case DEAD_AIR -> (deadAirAnim != null) ? deadAirAnim.getKeyFrame(time) : null;
            case DEAD_GROUND -> (deadGroundAnim != null) ? deadGroundAnim.getKeyFrame(time) : null;
            default -> walkAnim.getKeyFrame(time);
        };
    }

    private Animation<TextureRegion> buildAnim(Texture sheet, int frameCount, float frameDuration, Animation.PlayMode mode) {
        if (sheet == null || frameCount <= 0) return null;

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
