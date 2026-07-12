package com.parham.hollowknight.model.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.parham.hollowknight.controller.AudioManager;
import com.parham.hollowknight.controller.screens.GameScreen;
import com.parham.hollowknight.model.enums.AttackDirection;
import com.parham.hollowknight.model.enums.KnightState;
import com.parham.hollowknight.model.enums.PhysicsConstants;

import java.util.List;

import static com.parham.hollowknight.model.enums.PhysicsConstants.*;

public class Knight {

    public final Vector2 position = new Vector2();
    public float vX = 0f;
    public float vY = 0f;

    public final Rectangle hitbox = new Rectangle();
    public static final float WIDTH = 60;
    public static final float HEIGHT = 120;
    public static final float FOCUS_START_TIMER = 0.25f;
    public static final float FOCUS_END_TIMER = 0.15f;
    public static final float FOCUS_DURATION_TIMER = 0.6f;
    private static final float RUN_SOUND_DURATION = 2f;

    public final int maxHealth = 5;
    public int currentHealth = 5;
    public float soul = 0f;

    public AttackDirection attackDirection = AttackDirection.SIDE;
    public boolean wantsAttackUp = false;
    public boolean wantsAttackDown = false;

    public KnightState currentState = KnightState.IDLE;
    private KnightState stateBeforeAttack = KnightState.IDLE;


    private Rectangle nailHitbox = null;

    public final Vector2 lastSafePosition = new Vector2();
    private float safePositionTimer = 0f;
    private static final float SAFE_POSITION_INTERVAL = 0.5f;

    public boolean facingRight = true;
    public boolean onGround = false;

    private float dashTimer = 0f;
    private float attackTimer = 0f;
    private float hitTimer = 0f;
    private float healTimer = 0f;
    private float healCooldownTimer = 0f;
    private float landingTimer = 0f;
    private float invincibleTimer = 0f;
    private float pogoSoundCooldownTimer = 0f;
    private float spikeCollisionTimer = 0f;
    private float foucusStartTimer = 0f;
    private float foucusDurationTimer = 0f;
    private float foucusEndTimer = 0f;
    private float walkTimer = 0f;


    public boolean wantsLeft = false;
    public boolean wantsRight = false;
    public boolean wantsJump = false;
    public boolean holdingJump = false;
    public boolean wantsDash = false;
    public boolean wantsAttack = false;
    public boolean wantsHeal = false;
    public boolean wantsLookUp = false;
    public boolean wantsLookDown = false;
    public boolean wantsCast = false;

    public float attackStateTime = 0f;
    public float dashStateTime = 0f;
    private float dashCooldownTimer = 0f;

    private boolean hasDoubleJumped = false;
    private boolean hasDashedInAir = false;
    private float attackCooldownTimer = 0f;
    public boolean needsRespawn = false;

    private float blinkTimer = 0f;
    private static final float BLINK_INTERVAL = 0.15f;
    private boolean darkPhase = false;

    public boolean touchingWall = false;
    public boolean wallOnRight = false;
    private float wallJumpLockTimer = 0f;

    private GameScreen gameScreen;


    public Knight(GameScreen gameScreen, float startX, float startY) {
        this.gameScreen = gameScreen;
        position.set(startX, startY);
        syncHitbox();
    }

    public void update(float delta, List<Rectangle> platforms, List<Spike> spikes) {

        handleTimers(delta);

        if (checkSpikeCollisions(spikes)) return;

        if (isStateHandled(delta, platforms)) return;

        handleMovement();
        handleJump();

        if (isWantsDash()) return;

        if (isWantsAttack()) return;

        if (wantsHeal && onGround && soul >= PhysicsConstants.HEAL_SOUL_COST - 1) {
            if ((currentState == KnightState.IDLE || currentState == KnightState.RUNNING) &&
                healTimer == 0f && healCooldownTimer <= 0f) {
                currentState = KnightState.HEALING;
                if (foucusStartTimer <= 0 && foucusEndTimer <= 0 && foucusDurationTimer <= 0) {
                    AudioManager.getInstance().playSound(gameScreen.getGame().assets.focusEndSound);
                    foucusStartTimer = FOCUS_START_TIMER;
                }
            }
        }

        onGround = false;

        applyGravity(delta);
        moveX(delta);
        moveY(delta);
        resolveCollisions(platforms);

        updateFallState();
        changeCurrentState();

        updateSafePosition(delta);
        syncHitbox();
        clearFrameInputs();
    }


    private void applyGravity(float delta) {
        vY += PhysicsConstants.GRAVITY * delta;
        if (vY < PhysicsConstants.MAX_FALL_SPEED)
            vY = PhysicsConstants.MAX_FALL_SPEED;
    }

    private void moveX(float delta) {
        position.x += vX * delta;
    }

    private void moveY(float delta) {
        position.y += vY * delta;
    }


    private void resolveCollisions(List<Rectangle> platforms) {
        syncHitbox();
        touchingWall = false;
        for (Rectangle platform : platforms) {

            if (!hitbox.overlaps(platform)) continue;

            float overlapX = Math.min(hitbox.x + hitbox.width, platform.x + platform.width) - Math.max(hitbox.x, platform.x);
            float overlapY = Math.min(hitbox.y + hitbox.height, platform.y + platform.height) - Math.max(hitbox.y, platform.y);
            if (overlapX < overlapY) {
                if (hitbox.x + hitbox.width / 2f < platform.x + platform.width / 2f) {
                    position.x -= overlapX;
                    wallOnRight = true;
                } else {
                    position.x += overlapX;
                    wallOnRight = false;
                }
                touchingWall = true;
                hasDashedInAir = false;

                vX = 0f;
            } else {
                if (hitbox.y + hitbox.height / 2f < platform.y + platform.height / 2f) {
                    position.y -= overlapY;
                } else {
                    position.y += overlapY;
                    if (!onGround && (currentState == KnightState.FALLING ||
                        currentState == KnightState.JUMPING ||
                        currentState == KnightState.DOUBLE_JUMPING)) {
                        currentState = KnightState.LANDING;
                        landingTimer = LANDING_DURATION;
                        AudioManager.getInstance().playSound(gameScreen.getGame().assets.landSound);
                    }
                    onGround = true;
                    hasDashedInAir = false;
                    hasDoubleJumped = false;
                }
                vY = 0f;
            }

            syncHitbox();
        }
    }

    public boolean checkSpikeCollisions(List<Spike> spikes) {
        if (currentState == KnightState.HIT || currentState == KnightState.DEAD) return false;

        syncHitbox();
        for (Spike spike : spikes) {

            if (nailHitbox != null && !isCurrentlyAttacking()) nailHitbox.setPosition(-10000, -10000);
            if (isCurrentlyAttacking() && attackDirection == AttackDirection.DOWN && nailHitbox != null) {
                if (nailHitbox.overlaps(spike.hitbox) && pogoSoundCooldownTimer <= 0f) {
                    applyNailRecoil();
                    pogoSoundCooldownTimer = 0.3f;
                    return false;
                }
            }
            if (isCurrentlyAttacking() && nailHitbox != null && nailHitbox.overlaps(spike.hitbox) && spikeCollisionTimer <= 0) {
                AudioManager.getInstance().playSound(gameScreen.getGame().assets.pogoSound);
                spikeCollisionTimer = 0.3f;
            }


            if (hitbox.overlaps(spike.hitbox)) {

                boolean hitFromRight = (spike.hitbox.x + spike.hitbox.width / 2f) > (hitbox.x + hitbox.width / 2f);
                this.takeHit(1, hitFromRight);
                vY = spike.isOnGround ? PhysicsConstants.KNOCKBACK_Y : -PhysicsConstants.KNOCKBACK_Y;
                this.needsRespawn = true;
                return true;
            }
        }
        return false;
    }

    private void updateFallState() {
        if (!onGround && vY <= 0 && currentState != KnightState.HIT) currentState = KnightState.FALLING;
    }

    private void syncHitbox() {
        hitbox.set(position.x, position.y, WIDTH, HEIGHT);
    }

    public void takeHit(int damage, boolean hitFromRight) {
        if (currentState == KnightState.HIT || currentState == KnightState.DEAD) return;
        if (isInvincible()) return;
        currentHealth -= damage;
        AudioManager.getInstance().playSound(gameScreen.getGame().assets.knightHitSound);
        gameScreen.triggerScreenShake(0.2f, 8f);
        if (currentHealth <= 0) {
            currentHealth = 0;
            currentState = KnightState.DEAD;
            AudioManager.getInstance().playSound(gameScreen.getGame().assets.deathSound);
            gameScreen.getGame().currentGameData.deathCount++;
            vX = 0;
            vY = 0;
            return;
        }
        vX = hitFromRight ? -PhysicsConstants.KNOCKBACK_X : PhysicsConstants.KNOCKBACK_X;
        currentState = KnightState.HIT;
        invincibleTimer = 1f;
        blinkTimer = 0f;
        darkPhase = true;
    }

    private void clearFrameInputs() {
        wantsJump = false;
        wantsDash = false;
        wantsAttack = false;
        wantsAttackUp = false;
        wantsAttackDown = false;
    }

    public boolean isInvincible() {
        return invincibleTimer > 0f;
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    private void updateSafePosition(float delta) {
        if (onGround) {
            safePositionTimer += delta;
            if (safePositionTimer >= SAFE_POSITION_INTERVAL) {
                lastSafePosition.set(position);
                safePositionTimer = 0f;
            }
        }
    }

    public void buildNailHitbox() {
        if (currentState != KnightState.ATTACKING) {
            nailHitbox = null;
            return;
        }

        float sw = 650;
        float sh = 340;

        switch (attackDirection) {

            case SIDE:
                float sideHeight = sh * 2 / 5f;
                if (facingRight) {
                    float hx = position.x + WIDTH;
                    float hy = position.y;
                    nailHitbox = new Rectangle(hx, hy, sw / 3f, sideHeight);
                } else {
                    float hx = position.x - sw / 3f;
                    float hy = position.y;
                    nailHitbox = new Rectangle(hx, hy, sw / 3f, sideHeight);
                }
                break;

            case UP:
                float upWidth = sw / 5f;
                float upX = position.x + (WIDTH / 2f) - upWidth / 2;
                float upY = position.y + 50;
                nailHitbox = new Rectangle(upX, upY, upWidth, sh * 2 / 3);
                break;

            case DOWN:
                float downWidth = sw / 5f;
                float downX = position.x + (WIDTH / 2f) - downWidth / 2;
                float downY = position.y - sh / 2;
                nailHitbox = new Rectangle(downX, downY, downWidth, sh * 2 / 3);
                break;
        }
    }

    public boolean isCurrentlyAttacking() {
        return currentState == KnightState.ATTACKING && nailHitbox != null;
    }

    public Rectangle getNailHitbox() {
        return nailHitbox;
    }

    public int getNailDamage() {
        return 11;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void applyNailRecoil() {
        if (attackDirection == AttackDirection.DOWN) {
            vY = PhysicsConstants.JUMP_VELOCITY;
            hasDashedInAir = false;
            hasDoubleJumped = false;
        } else if (attackDirection == AttackDirection.UP)
            vY = PhysicsConstants.JUMP_VELOCITY * 0.3f;
        else if (attackDirection == AttackDirection.SIDE)
            vX = facingRight ? -KNOCKBACK_X * 0.5f : KNOCKBACK_X * 0.5f;

    }

    public void gainSoul(float amount) {
        if (soul < MAX_SOUL && soul + amount >= MAX_SOUL)
            AudioManager.getInstance().playSound(gameScreen.getGame().assets.soulOrbFullSound);
        soul = Math.min(MAX_SOUL, soul + amount);
    }

    private void handleLand(float delta, List<Rectangle> platforms) {
        if (Math.abs(this.vX) > 0.1f) {
            if (currentState != KnightState.RUNNING) {
                walkTimer = RUN_SOUND_DURATION;
                AudioManager.getInstance().playSound(gameScreen.getGame().assets.walkSound);
            }
            currentState = KnightState.RUNNING;
            return;
        }

        landingTimer -= delta;
        vX = 0f;
        applyGravity(delta);
        moveY(delta);
        resolveCollisions(platforms);
        syncHitbox();

        if (landingTimer <= 0f) {
            if (wantsLeft || wantsRight) {
                if (currentState != KnightState.RUNNING) {
                    walkTimer = RUN_SOUND_DURATION;
                    AudioManager.getInstance().playSound(gameScreen.getGame().assets.walkSound);
                }
            }
            currentState = (wantsLeft || wantsRight) ? KnightState.RUNNING : KnightState.IDLE;
        }
        clearFrameInputs();
    }

    private void handleHit(float delta, List<Rectangle> platforms) {
        hitTimer -= delta;
        moveX(delta);
        moveY(delta);
        resolveCollisions(platforms);
        syncHitbox();
        if (hitTimer <= 0f)
            currentState = onGround ? KnightState.IDLE : KnightState.FALLING;
        resetAllStates();
    }

    private void handleAttack(float delta, List<Rectangle> platforms) {
        buildNailHitbox();
        attackTimer -= delta;
        attackStateTime = PhysicsConstants.ATTACK_DURATION - attackTimer;
        applyGravity(delta);
        moveX(delta);
        moveY(delta);
        resolveCollisions(platforms);
        syncHitbox();
        if (attackTimer <= 0f) {
            attackStateTime = 0f;
            if (onGround) {
                if (vX != 0f) {
                    if (currentState != KnightState.RUNNING) {
                        walkTimer = RUN_SOUND_DURATION;
                        AudioManager.getInstance().playSound(gameScreen.getGame().assets.walkSound);
                    }
                }
                currentState = (vX != 0f) ? KnightState.RUNNING : KnightState.IDLE;
            } else
                currentState = stateBeforeAttack;
        }
    }

    private void handleDash(float delta, List<Rectangle> platforms) {
        dashTimer -= delta;
        dashStateTime = PhysicsConstants.DASH_DURATION - dashTimer;
        vX = (facingRight ? 1 : -1) * PhysicsConstants.DASH_SPEED;
        vY = 0f;
        moveX(delta);
        resolveCollisions(platforms);
        syncHitbox();
        if (dashTimer <= 0f) {
            vX = 0f;
            currentState = onGround ? KnightState.IDLE : KnightState.FALLING;
        }
    }

    private void handleHeal(float delta) {
        if (!wantsHeal || !onGround) {
            currentState = KnightState.IDLE;
            AudioManager.getInstance().stopSound(gameScreen.getGame().assets.focusEndSound);
            AudioManager.getInstance().stopSound(gameScreen.getGame().assets.knightFocusSound);
            AudioManager.getInstance().playSound(gameScreen.getGame().assets.foucusStartSound);
            healTimer = 0;
        } else {
            healTimer += delta;
            if (foucusStartTimer <= 0 && foucusDurationTimer <= 0 && foucusEndTimer <= 0) {
                foucusDurationTimer = FOCUS_DURATION_TIMER;
                AudioManager.getInstance().playSound(gameScreen.getGame().assets.knightFocusSound);
            }

            if (healTimer >= 1f) {
                currentState = KnightState.IDLE;
                currentHealth = Math.min(maxHealth, currentHealth + 1);
                healTimer = 0f;
                healCooldownTimer = 1f;
                gameScreen.triggerScreenShake(0.2f, 8f);
                AudioManager.getInstance().stopSound(gameScreen.getGame().assets.knightFocusSound);
                AudioManager.getInstance().playSound(gameScreen.getGame().assets.foucusStartSound);
                foucusEndTimer = FOCUS_END_TIMER;
            }
            soul -= (delta / PhysicsConstants.HEAL_DURATION * PhysicsConstants.HEAL_SOUL_COST);
            if (soul < 0) soul = 0;
        }
        syncHitbox();
    }

    private boolean isStateHandled(float delta, List<Rectangle> platforms) {

        switch (currentState) {
            case HIT -> {
                handleHit(delta, platforms);
                return true;
            }
            case DEAD -> {
                return true;
            }
            case LANDING -> {
                handleLand(delta, platforms);
                return true;
            }
            case DASHING -> {
                handleDash(delta, platforms);
                return true;
            }
            case HEALING -> {
                handleHeal(delta);
                return true;
            }
            case ATTACKING -> {
                handleAttack(delta, platforms);
                return true;
            }
        }
        return false;
    }


    private void handleMovement() {
        if (wallJumpLockTimer > 0f) return;
        if (wantsLeft) {
            vX = -PhysicsConstants.MOVE_SPEED;
            facingRight = false;
        } else if (wantsRight) {
            vX = PhysicsConstants.MOVE_SPEED;
            facingRight = true;
        } else vX = 0f;
    }


    private void handleJump() {
        if (wantsJump && onGround) {
            vY = PhysicsConstants.JUMP_VELOCITY;
            onGround = false;
            currentState = KnightState.JUMPING;
            AudioManager.getInstance().playSound(gameScreen.getGame().assets.jumpSound);

        } else if (wantsJump && touchingWall) {
            vY = PhysicsConstants.JUMP_VELOCITY * 0.85f;

            vX = wallOnRight ? -PhysicsConstants.MOVE_SPEED : PhysicsConstants.MOVE_SPEED;
            facingRight = !wallOnRight;

            wallJumpLockTimer = WALL_JUMP_LOCK_DURATION;
            currentState = KnightState.WALL_JUMPING;
            touchingWall = false;
            hasDoubleJumped = false;
            AudioManager.getInstance().playSound(gameScreen.getGame().assets.jumpSound);

        } else if (wantsJump && !hasDoubleJumped) {
            vY = PhysicsConstants.JUMP_VELOCITY * 0.95f;
            currentState = KnightState.DOUBLE_JUMPING;
            hasDoubleJumped = true;
            AudioManager.getInstance().playSound(gameScreen.getGame().assets.jumpSound);

        }

        if (!holdingJump && vY > 0f) vY = 0f;
    }

    private boolean isWantsDash() {
        if (currentState == KnightState.HIT || currentState == KnightState.DEAD) return false;
        if (wantsDash && dashTimer <= 0f && dashCooldownTimer <= 0f && (onGround || !hasDashedInAir)) {
            currentState = KnightState.DASHING;
            dashTimer = PhysicsConstants.DASH_DURATION;
            AudioManager.getInstance().playSound(gameScreen.getGame().assets.dashSound);
            dashCooldownTimer = PhysicsConstants.DASH_DURATION + PhysicsConstants.DASH_COOLDOWN;
            dashStateTime = 0f;
            wantsDash = false;
            hasDashedInAir = true;
            return true;
        }
        return false;
    }

    private boolean isWantsAttack() {
        if (currentState == KnightState.HIT || currentState == KnightState.DEAD) return false;

        if (wantsAttack && attackCooldownTimer <= 0f) {
            stateBeforeAttack = currentState;
            currentState = KnightState.ATTACKING;
            attackTimer = PhysicsConstants.ATTACK_DURATION;
            AudioManager.getInstance().playSound(gameScreen.getGame().assets.knightSlashSound);
            attackCooldownTimer = PhysicsConstants.ATTACK_DURATION + PhysicsConstants.ATTACK_COOLDOWN;
            attackStateTime = 0f;
            wantsAttack = false;

            if (wantsAttackUp) attackDirection = AttackDirection.UP;
            else if (wantsAttackDown && !onGround) attackDirection = AttackDirection.DOWN;
            else attackDirection = AttackDirection.SIDE;

            return true;
        }
        return false;
    }


    public void resetAllStates() {
        dashTimer = 0f;
        dashCooldownTimer = 0f;
        hasDashedInAir = false;
        hasDoubleJumped = false;
        attackTimer = 0f;
        wantsDash = false;
        holdingJump = false;
        wantsCast = false;

    }


    private void handleTimers(float delta) {

        if (dashCooldownTimer > 0f) dashCooldownTimer -= delta;
        if (attackCooldownTimer > 0f) attackCooldownTimer -= delta;
        if (healCooldownTimer > 0f) healCooldownTimer -= delta;
        if (wallJumpLockTimer > 0f) wallJumpLockTimer -= delta;
        if (pogoSoundCooldownTimer > 0f) pogoSoundCooldownTimer -= delta;
        if (foucusDurationTimer > 0f) foucusDurationTimer -= delta;
        if (foucusStartTimer > 0f) foucusStartTimer -= delta;
        if (foucusEndTimer > 0f) foucusEndTimer -= delta;
        if (spikeCollisionTimer > 0f) spikeCollisionTimer -= delta;
        if (invincibleTimer > 0f) {
            invincibleTimer -= delta;
            blinkTimer += delta;
            if (blinkTimer >= BLINK_INTERVAL) {
                blinkTimer = 0f;
                darkPhase = !darkPhase;
            }
        } else darkPhase = false;
        if (walkTimer > 0f) walkTimer -= delta;
        if (walkTimer <= 0 && currentState == KnightState.RUNNING) {
            walkTimer = RUN_SOUND_DURATION;
            AudioManager.getInstance().stopSound(gameScreen.getGame().assets.walkSound);
            AudioManager.getInstance().playSound(gameScreen.getGame().assets.walkSound);
        }
        if (currentState != KnightState.RUNNING)
            AudioManager.getInstance().stopSound(gameScreen.getGame().assets.walkSound);

    }

    private void changeCurrentState() {
        if (currentState != KnightState.ATTACKING &&
            currentState != KnightState.DASHING &&
            currentState != KnightState.HEALING &&
            currentState != KnightState.HIT &&
            currentState != KnightState.LANDING &&
            currentState != KnightState.WALL_JUMPING) {

            if (!onGround) {
                if (touchingWall && vY < 0 && ((wallOnRight && wantsRight) || (!wallOnRight && wantsLeft))) {
                    currentState = KnightState.WALL_SLIDING;
                    vY = Math.max(vY, PhysicsConstants.WALL_SLIDE_SPEED);
                } else if (currentState != KnightState.JUMPING && currentState != KnightState.DOUBLE_JUMPING)
                    currentState = KnightState.FALLING;
                else if (vY <= 0)
                    currentState = KnightState.FALLING;
            } else {
                if (vX != 0f) {
                    if (currentState != KnightState.RUNNING) {
                        walkTimer = RUN_SOUND_DURATION;
                        AudioManager.getInstance().playSound(gameScreen.getGame().assets.walkSound);
                    }
                    currentState = KnightState.RUNNING;
                } else {
                    currentState = KnightState.IDLE;
                }
            }
        }
    }

    public boolean isBlinking() {
        return invincibleTimer > 0f && darkPhase;
    }

}
