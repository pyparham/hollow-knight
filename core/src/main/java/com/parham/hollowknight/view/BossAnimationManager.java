package com.parham.hollowknight.view;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.parham.hollowknight.model.entities.bossfight.FalseKnight;
import com.parham.hollowknight.model.enums.EnemyState;

public class BossAnimationManager {

    private final Animation<TextureRegion> idleAnim;
    private final Animation<TextureRegion> runAnticAnim;
    private final Animation<TextureRegion> runAnim;
    private final Animation<TextureRegion> maceAnticAnim;
    private final Animation<TextureRegion> maceAttackAnim;
    private final Animation<TextureRegion> maceRecoverAnim;
    private final Animation<TextureRegion> jumpAnticAnim;
    private final Animation<TextureRegion> jumpAnim;
    private final Animation<TextureRegion> jumpAttackAnim;
    private final Animation<TextureRegion> landAnim;
    private final Animation<TextureRegion> stunAnim;
    private final Animation<TextureRegion> stunRecoverAnim;
    private final Animation<TextureRegion> deathFallAnim;
    private final Animation<TextureRegion> deathHitAnim;
    private final Animation<TextureRegion> deathLandAnim;

    private float stateTime = 0f;
    private EnemyState lastState = null;

    private static final float DRAW_WIDTH = 650;
    private static final float DRAW_HEIGHT = 400;

    private ShaderProgram whiteShader;
    private static final String VERTEX_SHADER =
        "attribute vec4 a_position;\n" +
            "attribute vec4 a_color;\n" +
            "attribute vec2 a_texCoord0;\n" +
            "uniform mat4 u_projTrans;\n" +
            "varying vec4 v_color;\n" +
            "varying vec2 v_texCoords;\n" +
            "void main() {\n" +
            "   v_color = a_color;\n" +
            "   v_texCoords = a_texCoord0;\n" +
            "   gl_Position = u_projTrans * a_position;\n" +
            "}\n";

    private static final String FRAGMENT_SHADER =
        "#ifdef GL_ES\n" +
            "precision mediump float;\n" +
            "#endif\n" +
            "varying vec4 v_color;\n" +
            "varying vec2 v_texCoords;\n" +
            "uniform sampler2D u_texture;\n" +
            "void main() {\n" +
            "   vec4 texColor = texture2D(u_texture, v_texCoords);\n" +
            "   vec3 tinted = mix(texColor.rgb, vec3(1.0), 0.5);\n" +
            "   gl_FragColor = vec4(tinted, texColor.a * v_color.a);\n" +
            "}\n";


    public BossAnimationManager(
        Texture idleSheet, int idleFrames,
        Texture runAnticSheet, int runAnticFrames,
        Texture runSheet, int runFrames,
        Texture maceAnticSheet, int maceAnticFrames,
        Texture maceAttackSheet, int maceAttackFrames,
        Texture maceRecoverSheet, int maceRecoverFrames,
        Texture jumpAnticSheet, int jumpAnticFrames,
        Texture jumpSheet, int jumpFrames,
        Texture jumpAttackSheet, int jumpAttackFrames,
        Texture landSheet, int landFrames,
        Texture stunSheet, int stunFrames,
        Texture stunRecoverSheet, int stunRecoverFrames,
        Texture deathFallSheet, int deathFallFrames,
        Texture deathHitSheet, int deathHitFrames,
        Texture deathLandSheet, int deathLandFrames
    ) {
        idleAnim = buildAnim(idleSheet, idleFrames, 0.1f, Animation.PlayMode.LOOP);
        runAnticAnim = buildAnim(runAnticSheet, runAnticFrames, 0.08f, Animation.PlayMode.NORMAL);
        runAnim = buildAnim(runSheet, runFrames, 0.06f, Animation.PlayMode.LOOP);
        maceAnticAnim = buildAnim(maceAnticSheet, maceAnticFrames, 0.1f, Animation.PlayMode.NORMAL);
        maceAttackAnim = buildAnim(maceAttackSheet, maceAttackFrames, 0.06f, Animation.PlayMode.NORMAL);
        maceRecoverAnim = buildAnim(maceRecoverSheet, maceRecoverFrames, 0.08f, Animation.PlayMode.NORMAL);
        jumpAnticAnim = buildAnim(jumpAnticSheet, jumpAnticFrames, 0.09f, Animation.PlayMode.NORMAL);
        jumpAnim = buildAnim(jumpSheet, jumpFrames, 0.08f, Animation.PlayMode.LOOP);
        jumpAttackAnim = buildAnim(jumpAttackSheet, jumpAttackFrames, 0.06f, Animation.PlayMode.NORMAL);
        landAnim = buildAnim(landSheet, landFrames, 0.07f, Animation.PlayMode.NORMAL);
        stunAnim = buildAnim(stunSheet, stunFrames, 0.1f, Animation.PlayMode.LOOP);
        stunRecoverAnim = buildAnim(stunRecoverSheet, stunRecoverFrames, 0.1f, Animation.PlayMode.NORMAL);
        deathFallAnim = buildAnim(deathFallSheet, deathFallFrames, 0.09f, Animation.PlayMode.NORMAL);
        deathHitAnim = buildAnim(deathHitSheet, deathHitFrames, 0.08f, Animation.PlayMode.NORMAL);
        deathLandAnim = buildAnim(deathLandSheet, deathLandFrames, 0.1f, Animation.PlayMode.NORMAL);

        whiteShader = new ShaderProgram(VERTEX_SHADER, FRAGMENT_SHADER);
    }

    public void update(float delta, FalseKnight boss) {
        if (boss.state != lastState) {
            if (boss.state == EnemyState.DEAD_GROUND && !boss.bossFightStarted) stateTime = 999f;
            else stateTime = 0f;
            lastState = boss.state;
        } else stateTime += delta;

    }

    public void draw(SpriteBatch batch, FalseKnight boss) {
        TextureRegion frame = getCurrentFrame(boss);
        if (frame == null) return;

        float x = boss.position.x - (DRAW_WIDTH - boss.getBodyHitbox().width) / 2f;
        float y = boss.position.y - 20;


        if (boss.isFlashing()) batch.setShader(whiteShader);

        if (!boss.facingRight) batch.draw(frame, x, y, DRAW_WIDTH, DRAW_HEIGHT);
        else batch.draw(frame, x + DRAW_WIDTH, y, -DRAW_WIDTH, DRAW_HEIGHT);

        if (boss.isFlashing()) batch.setShader(null);
    }

    private TextureRegion getCurrentFrame(FalseKnight boss) {
        return switch (boss.state) {
            case IDLE -> idleAnim.getKeyFrame(stateTime, true);
            case CHARGE_RUN -> {
                if (!runAnticAnim.isAnimationFinished(stateTime))
                    yield runAnticAnim.getKeyFrame(stateTime);
                float t = stateTime - runAnticAnim.getAnimationDuration();
                yield runAnim.getKeyFrame(t, true);
            }
            case MACE_SLAM -> {
                if (!maceAnticAnim.isAnimationFinished(stateTime))
                    yield maceAnticAnim.getKeyFrame(stateTime);
                float afterAntic = stateTime - maceAnticAnim.getAnimationDuration();
                if (!maceAttackAnim.isAnimationFinished(afterAntic))
                    yield maceAttackAnim.getKeyFrame(afterAntic);
                float afterAttack = afterAntic - maceAttackAnim.getAnimationDuration();
                yield maceRecoverAnim.getKeyFrame(afterAttack);
            }
            case OFFENSIVE_LEAP, DEFENSIVE_LEAP -> {
                if (!jumpAnticAnim.isAnimationFinished(stateTime))
                    yield jumpAnticAnim.getKeyFrame(stateTime);
                float airTime = stateTime - jumpAnticAnim.getAnimationDuration();
                yield jumpAnim.getKeyFrame(airTime, true);
            }
            case LAND -> landAnim.getKeyFrame(stateTime, false);
            case POWER_SLAM -> {
                if (!jumpAttackAnim.isAnimationFinished(stateTime))
                    yield jumpAttackAnim.getKeyFrame(stateTime);
                float recover = stateTime - jumpAttackAnim.getAnimationDuration();
                yield maceRecoverAnim.getKeyFrame(recover);
            }
            case STUN -> stunAnim.getKeyFrame(stateTime, true);
            case STUN_HIT -> deathHitAnim.getKeyFrame(stateTime, false);
            case STUN_RECOVER -> stunRecoverAnim.getKeyFrame(stateTime, false);
            case DEAD_AIR -> deathFallAnim.getKeyFrame(stateTime, true);
            case DEAD_GROUND -> {
                if (!deathHitAnim.isAnimationFinished(stateTime))
                    yield deathHitAnim.getKeyFrame(stateTime);
                float afterHit = stateTime - deathHitAnim.getAnimationDuration();
                yield deathLandAnim.getKeyFrame(afterHit);
            }

            default -> null;
        };
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
