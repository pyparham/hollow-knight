package com.parham.hollowknight.view;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.parham.hollowknight.model.entities.Knight;
import com.parham.hollowknight.model.enums.KnightState;

public class KnightAnimationManager {

    private final Animation<TextureRegion> idleAnim;
    private final Animation<TextureRegion> runAnim;
    private final Animation<TextureRegion> jumpAnim;
    private final Animation<TextureRegion> doubleJumpAnim;
    private final Animation<TextureRegion> fallAnim;
    private final Animation<TextureRegion> landAnim;
    private final Animation<TextureRegion> dashAnim;
    private final Animation<TextureRegion> attackAnim;
    private final Animation<TextureRegion> healAnim;
    private final Animation<TextureRegion> hitAnim;
    private final Animation<TextureRegion> deadAnim;
    private final Animation<TextureRegion> sideSlashAnim;
    private final Animation<TextureRegion> upSlashAnim;
    private final Animation<TextureRegion> downSlashAnim;
    private final Animation<TextureRegion> lookUpStartAnim;
    private final Animation<TextureRegion> lookUpLoopAnim;
    private final Animation<TextureRegion> lookUpEndAnim;
    private final Animation<TextureRegion> lookDownStartAnim;
    private final Animation<TextureRegion> lookDownLoopAnim;
    private final Animation<TextureRegion> lookDownEndAnim;
    private final Animation<TextureRegion> dashEffectAnim;
    private final Animation<TextureRegion> wallSlideAnim;
    private final Animation<TextureRegion> wallJumpAnim;


//    private final Animation<TextureRegion> standUpAnim;

    private enum VerticalLookPhase {
        NORMAL,
        LOOK_UP_START, LOOK_UP_LOOP, LOOK_UP_END,
        LOOK_DOWN_START, LOOK_DOWN_LOOP, LOOK_DOWN_END
    }

    private VerticalLookPhase lookPhase = VerticalLookPhase.NORMAL;
    private float lookTimer = 0f;


    private float stateTime = 0f;
    private KnightState lastState = null;

    private boolean slashVisible = false;
    private float slashTime = 0f;

    private boolean dashEffectVisible = false;
    private float dashEffectTime = 0f;

    private boolean blinkVisible = true;
    private float blinkTimer = 0f;
    private boolean isBlinking = false;

    private static final float BLINK_ON_TIME = 0.1f;
    private static final float BLINK_OFF_TIME = 0.08f;
    private static final float DRAW_WIDTH = 349f;
    private static final float DRAW_HEIGHT = 186f;


    public KnightAnimationManager(
        Texture idleSheet, int idleFrames,
        Texture runSheet, int runFrames,
        Texture jumpSheet, int jumpFrames,
        Texture doubleJumpSheet, int doubleJumpFrames,
        Texture fallSheet, int fallFrames,
        Texture landSheet, int landFrames,
        Texture dashSheet, int dashFrames,
        Texture attackSheet, int attackFrames,
        Texture focusStartSheet, int focusStartFrames,
        Texture focusLoopSheet, int focusLoopFrames,
        Texture focusGetSheet, int focusGetFrames,
        Texture focusEndSheet, int focusEndFrames,
        Texture hitSheet, int hitFrames,
        Texture deadSheet, int deadFrames,
        Texture slashLeftSheet, int slashFrames,
        Texture slashUpSheet, int slashUpFrames,
        Texture slashDownSheet, int slashDownFrames,
        Texture lookUpSheet, int lookUpFrames,
        Texture lookDownSheet, int lookDownFrames,
        Texture dashEffectSheet, int dashEffectFrames,
        Texture wallSlideSheet, int wallSlideFrames,
        Texture wallJumpSheet, int wallJumpFrames
    ) {

        idleAnim = buildAnim(idleSheet, idleFrames, 0.12f, Animation.PlayMode.LOOP);
        runAnim = buildAnim(runSheet, runFrames, 0.08f, Animation.PlayMode.LOOP);
        jumpAnim = buildAnim(jumpSheet, jumpFrames, 0.05f, Animation.PlayMode.NORMAL);
        doubleJumpAnim = buildAnim(doubleJumpSheet, doubleJumpFrames, 0.05f, Animation.PlayMode.NORMAL);
        fallAnim = buildAnim(fallSheet, fallFrames, 0.08f, Animation.PlayMode.LOOP);
        landAnim = buildAnim(landSheet, landFrames, 0.04f, Animation.PlayMode.NORMAL);
        dashAnim = buildAnim(dashSheet, dashFrames, 0.05f, Animation.PlayMode.NORMAL);
        attackAnim = buildAnim(attackSheet, attackFrames, 0.06f, Animation.PlayMode.NORMAL);
        healAnim = buildCombinedHealAnim(
            0.08f,
            focusStartSheet, focusStartFrames,
            focusLoopSheet, focusLoopFrames,
            focusGetSheet, focusGetFrames,
            focusEndSheet, focusEndFrames
        );
        hitAnim = buildAnim(hitSheet, hitFrames, 0.08f, Animation.PlayMode.NORMAL);
        deadAnim = buildAnim(deadSheet, deadFrames, 0.10f, Animation.PlayMode.NORMAL);
        sideSlashAnim = buildAnim(slashLeftSheet, slashFrames, 0.04f, Animation.PlayMode.NORMAL);
        upSlashAnim = buildAnim(slashUpSheet, slashUpFrames, 0.04f, Animation.PlayMode.NORMAL);
        downSlashAnim = buildAnim(slashDownSheet, slashDownFrames, 0.04f, Animation.PlayMode.NORMAL);

        PhaseAnims lookUpAnim = buildLookAnimation(lookUpSheet, lookUpFrames);
        this.lookUpStartAnim = lookUpAnim.start;
        this.lookUpLoopAnim = lookUpAnim.loop;
        this.lookUpEndAnim = lookUpAnim.end;
        PhaseAnims lookDownAnim = buildLookAnimation(lookDownSheet, lookDownFrames);
        this.lookDownStartAnim = lookDownAnim.start;
        this.lookDownLoopAnim = lookDownAnim.loop;
        this.lookDownEndAnim = lookDownAnim.end;

        dashEffectAnim = buildAnim(dashEffectSheet, dashEffectFrames, 0.04f, Animation.PlayMode.NORMAL);
        wallSlideAnim = buildAnim(wallSlideSheet, wallSlideFrames, 0.1f, Animation.PlayMode.LOOP);
        wallJumpAnim = buildAnim(wallJumpSheet, wallJumpFrames, 0.08f, Animation.PlayMode.NORMAL);

    }


    public void update(float delta, Knight knight) {
        KnightState state = knight.currentState;

        if (state == KnightState.IDLE)
            updateLookPhase(delta, knight.wantsLookUp, knight.wantsLookDown);
        else
            changePhase(VerticalLookPhase.NORMAL);

        if (state == KnightState.DASHING && !dashEffectVisible) {
            dashEffectVisible = true;
            dashEffectTime = 0f;
        }

        if (dashEffectVisible) {
            dashEffectTime += delta;
            if (dashEffectAnim.isAnimationFinished(dashEffectTime))
                dashEffectVisible = false;
        }

        if (state != lastState) {
            stateTime = 0f;
            lastState = state;
            if (state == KnightState.ATTACKING) {
                slashVisible = true;
                slashTime = 0f;
            }
        }

        if (slashVisible) {
            slashTime += delta;

            Animation<TextureRegion> currentSlash;
            switch (knight.attackDirection) {
                case UP -> currentSlash = upSlashAnim;
                case DOWN -> currentSlash = downSlashAnim;
                default -> currentSlash = sideSlashAnim;
            }

            if (currentSlash.isAnimationFinished(slashTime))
                slashVisible = false;
        }

        switch (state) {
            case DASHING:
                stateTime = knight.dashStateTime;
                break;
            case ATTACKING:
                stateTime = knight.attackStateTime;
                break;
            default:
                stateTime += delta;
                break;
        }
    }

    public void draw(SpriteBatch batch, Knight knight) {
        TextureRegion frame = getCurrentFrame(knight.currentState);
        if (frame == null) return;

        float x = knight.position.x - (DRAW_WIDTH - Knight.WIDTH) / 2f;
        float y = knight.position.y - (DRAW_HEIGHT - Knight.HEIGHT) / 2f + 30;

        if (!knight.facingRight) batch.draw(frame, x, y, DRAW_WIDTH, DRAW_HEIGHT);
        else batch.draw(frame, x + DRAW_WIDTH, y, -DRAW_WIDTH, DRAW_HEIGHT);


        if (slashVisible && knight.currentState == KnightState.ATTACKING) drawSlash(batch, knight);

        if (dashEffectVisible) drawDash(batch, knight);
    }


    private void drawSlash(SpriteBatch batch, Knight knight) {
        TextureRegion slashFrame = null;
        float sx, sy, sw, sh;

        sw = 520;
        sh = 300;

        switch (knight.attackDirection) {
            case SIDE:
                slashFrame = sideSlashAnim.getKeyFrame(slashTime);
                if (knight.facingRight) {
                    sx = knight.position.x - Knight.WIDTH - 150f;
                    sy = knight.position.y - 50f;
                    batch.draw(slashFrame, sx + sw, sy, -sw, sh);
                } else {
                    sx = knight.position.x - Knight.WIDTH - 200f;
                    sy = knight.position.y - 50f;
                    batch.draw(slashFrame, sx, sy, sw, sh);
                }
                break;
            case UP:
                if (knight.facingRight) {
                    slashFrame = upSlashAnim.getKeyFrame(slashTime);
                    sx = knight.position.x - sw / 6 + Knight.WIDTH / 2;
                    sy = knight.position.y;
                    batch.draw(slashFrame, sx, sy, sw / 3, sh);
                    break;
                } else {
                    slashFrame = upSlashAnim.getKeyFrame(slashTime);
                    sx = knight.position.x + sw / 6 + Knight.WIDTH / 2;
                    sy = knight.position.y;
                    batch.draw(slashFrame, sx, sy, -sw / 3, sh);
                    break;
                }
            case DOWN:
                if (knight.facingRight) {
                    slashFrame = downSlashAnim.getKeyFrame(slashTime);
                    sx = knight.position.x - sw / 6 + Knight.WIDTH / 2;
                    sy = knight.position.y - sh / 2 - 20f;
                    batch.draw(slashFrame, sx, sy, sw / 3, sh);
                    break;
                } else {
                    slashFrame = downSlashAnim.getKeyFrame(slashTime);
                    sx = knight.position.x + sw / 6 + Knight.WIDTH / 2;
                    sy = knight.position.y - sh / 2 - 20f;
                    batch.draw(slashFrame, sx, sy, -sw / 3, sh);
                    break;
                }
        }
    }

    private void drawDash(SpriteBatch batch, Knight knight) {
        TextureRegion frame = dashEffectAnim.getKeyFrame(dashEffectTime);
        float sw = 402;
        float sh = 217;

        float sx = knight.facingRight ? (knight.position.x - sw + 20) : (knight.position.x + Knight.WIDTH - 20);
        float sy = knight.position.y + Knight.HEIGHT / 2 - sh / 2;

        if (knight.facingRight) batch.draw(frame, sx, sy, sw, sh);
        else batch.draw(frame, sx + sw, sy, -sw, sh);

    }

    private TextureRegion getCurrentFrame(KnightState state) {
        return switch (state) {
            case IDLE -> getIdleOrLookFrame();
            case RUNNING -> runAnim.getKeyFrame(stateTime);

            case JUMPING -> jumpAnim.getKeyFrame(stateTime);
            case DOUBLE_JUMPING -> doubleJumpAnim.getKeyFrame(stateTime);
            case FALLING -> fallAnim.getKeyFrame(stateTime);
            case LANDING -> landAnim.getKeyFrame(stateTime);

            case DASHING -> dashAnim.getKeyFrame(stateTime);
            case ATTACKING -> attackAnim.getKeyFrame(stateTime);
            case HEALING -> healAnim.getKeyFrame(stateTime);
            case HIT -> hitAnim.getKeyFrame(stateTime);
            case DEAD -> deadAnim.getKeyFrame(stateTime);
            case WALL_SLIDING -> wallSlideAnim.getKeyFrame(stateTime);
            case WALL_JUMPING -> wallJumpAnim.getKeyFrame(stateTime);
            default -> idleAnim.getKeyFrame(0f);
        };
    }

    private Animation<TextureRegion> buildAnim(Texture sheet, int frameCount,
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

    private Animation<TextureRegion> buildCombinedHealAnim(
        float frameDuration,
        Texture startSheet, int startFrames,
        Texture loopSheet, int loopFrames,
        Texture getSheet, int getFrames,
        Texture endSheet, int endFrames
    ) {
        int totalFrames = startFrames + loopFrames + getFrames + endFrames;
        TextureRegion[] frames = new TextureRegion[totalFrames];
        int index = 0;

        int wStart = startSheet.getWidth() / startFrames;
        for (int i = 0; i < startFrames; i++)
            frames[index++] = new TextureRegion(startSheet, i * wStart, 0, wStart, startSheet.getHeight());

        int wLoop = loopSheet.getWidth() / loopFrames;
        for (int i = 0; i < loopFrames; i++)
            frames[index++] = new TextureRegion(loopSheet, i * wLoop, 0, wLoop, loopSheet.getHeight());

        int wGet = getSheet.getWidth() / getFrames;
        for (int i = 0; i < getFrames; i++)
            frames[index++] = new TextureRegion(getSheet, i * wGet, 0, wGet, getSheet.getHeight());

        int wEnd = endSheet.getWidth() / endFrames;
        for (int i = 0; i < endFrames; i++)
            frames[index++] = new TextureRegion(endSheet, i * wEnd, 0, wEnd, endSheet.getHeight());

        Animation<TextureRegion> anim = new Animation<>(frameDuration, frames);
        anim.setPlayMode(Animation.PlayMode.NORMAL);
        return anim;
    }


    private static class PhaseAnims {
        Animation<TextureRegion> start;
        Animation<TextureRegion> loop;
        Animation<TextureRegion> end;

        PhaseAnims(Animation<TextureRegion> start, Animation<TextureRegion> loop, Animation<TextureRegion> end) {
            this.start = start;
            this.loop = loop;
            this.end = end;
        }
    }

    private PhaseAnims buildLookAnimation(Texture sheet, int frameCount) {
        TextureRegion[][] tmp = TextureRegion.split(sheet, sheet.getWidth() / frameCount, sheet.getHeight());
        TextureRegion[] frames = tmp[0];

        Animation<TextureRegion> startAnim = new Animation<>(0.07f, frames[0], frames[1], frames[2]);
        startAnim.setPlayMode(Animation.PlayMode.NORMAL);

        Animation<TextureRegion> loopAnim = new Animation<>(0.1f, frames[3], frames[4], frames[5]);
        loopAnim.setPlayMode(Animation.PlayMode.LOOP);

        Animation<TextureRegion> endAnim = new Animation<>(0.07f, frames[2], frames[1], frames[0]);
        endAnim.setPlayMode(Animation.PlayMode.NORMAL);

        return new PhaseAnims(startAnim, loopAnim, endAnim);
    }

    private void updateLookPhase(float delta, boolean up, boolean down) {
        if (up && !down) {
            advanceLookPhase(delta, VerticalLookPhase.LOOK_UP_START, VerticalLookPhase.LOOK_UP_LOOP, lookUpStartAnim);
        } else if (down && !up) {
            advanceLookPhase(delta, VerticalLookPhase.LOOK_DOWN_START, VerticalLookPhase.LOOK_DOWN_LOOP, lookDownStartAnim);
        } else {
            if (lookPhase == VerticalLookPhase.LOOK_UP_START || lookPhase == VerticalLookPhase.LOOK_UP_LOOP)
                changePhase(VerticalLookPhase.LOOK_UP_END);
            else if (lookPhase == VerticalLookPhase.LOOK_DOWN_START || lookPhase == VerticalLookPhase.LOOK_DOWN_LOOP)
                changePhase(VerticalLookPhase.LOOK_DOWN_END);
            else if (lookPhase == VerticalLookPhase.LOOK_UP_END) {
                lookTimer += delta;
                if (lookUpEndAnim.isAnimationFinished(lookTimer)) changePhase(VerticalLookPhase.NORMAL);
            } else if (lookPhase == VerticalLookPhase.LOOK_DOWN_END) {
                lookTimer += delta;
                if (lookDownEndAnim.isAnimationFinished(lookTimer)) changePhase(VerticalLookPhase.NORMAL);
            }
        }
    }

    private void advanceLookPhase(float delta, VerticalLookPhase startPhase, VerticalLookPhase loopPhase, Animation<TextureRegion> startAnim) {
        if (lookPhase != startPhase && lookPhase != loopPhase) {
            changePhase(startPhase);
        } else if (lookPhase == startPhase) {
            lookTimer += delta;
            if (startAnim.isAnimationFinished(lookTimer)) changePhase(loopPhase);
        } else if (lookPhase == loopPhase) {
            lookTimer += delta;
        }
    }

    private void changePhase(VerticalLookPhase newPhase) {
        this.lookPhase = newPhase;
        this.lookTimer = 0f;
    }

    private TextureRegion getIdleOrLookFrame() {
        return switch (lookPhase) {
            case LOOK_UP_START -> lookUpStartAnim.getKeyFrame(lookTimer);
            case LOOK_UP_LOOP -> lookUpLoopAnim.getKeyFrame(lookTimer);
            case LOOK_UP_END -> lookUpEndAnim.getKeyFrame(lookTimer);

            case LOOK_DOWN_START -> lookDownStartAnim.getKeyFrame(lookTimer);
            case LOOK_DOWN_LOOP -> lookDownLoopAnim.getKeyFrame(lookTimer);
            case LOOK_DOWN_END -> lookDownEndAnim.getKeyFrame(lookTimer);

            case NORMAL -> idleAnim.getKeyFrame(stateTime);
        };
    }
}
