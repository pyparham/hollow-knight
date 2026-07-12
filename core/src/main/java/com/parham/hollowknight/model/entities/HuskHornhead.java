package com.parham.hollowknight.model.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.parham.hollowknight.controller.screens.GameScreen;
import com.parham.hollowknight.model.enums.EnemyState;
import com.parham.hollowknight.model.enums.EnemyType;

import java.util.List;


public class HuskHornhead extends Enemy {

    private static final float W = 160f;
    private static final float H = 180f;
    private static final float WALK_SPEED = 70f;
    private static final float CHARGE_SPEED = 320f;
    private static final float ANTICIPATE_DURATION = 0.3f;
    private static final float CHARGE_DURATION = 1.0f;
    private static final float CHARGE_COOLDOWN = 0.7f;
    private float chargeTimer = 0f;

    private float chargeCooldown = 0.7f;
    private boolean charging = false;

    public HuskHornhead(GameScreen gameScreen, float x, float y, float walkLeft, float walkRight) {
        super(gameScreen, EnemyType.HUSK_HORNHEAD, x, y, W, H, 44, walkLeft, walkRight);
        walkSpeed = WALK_SPEED;
        contactDamage = 1;
        attackDamage = 1;
        soulDrop = 11f;
        restDuration = 0.7f;
        this.mass = 100;
    }

    @Override
    protected void updateBehavior(float delta, List<Rectangle> platforms, Vector2 knightPos) {
        chargeCooldown -= delta;

        if (charging) {
            chargeTimer -= delta;

            if (state == EnemyState.ATTACK_JUMP) {
                vX = 0f;
                if (chargeTimer <= 0f) {
                    state = EnemyState.ATTACK;
                    chargeTimer = CHARGE_DURATION;
                }
            } else if (state == EnemyState.ATTACK) {
                chasePlayer(knightPos, CHARGE_SPEED);
                if (chargeTimer <= 0f) {
                    charging = false;
                    chargeCooldown = CHARGE_COOLDOWN;
                    state = EnemyState.WALKING;
                    vX = 0f;
                }
            }
            return;
        }

        if (playerInSight(knightPos, platforms) && chargeCooldown <= 0f) {
            charging = true;
            facePlayer(knightPos);
            state = EnemyState.ATTACK_JUMP;
            chargeTimer = ANTICIPATE_DURATION;
            animTime = 0f;
            return;
        }

        groundWalk(delta, platforms);
    }

    private boolean playerInSight(Vector2 knightPos, List<Rectangle> platforms) {
        if (Math.abs(knightPos.y - position.y) > height) return false;

        if (!gameScreen.isWallBreaked()) return false;

        float centerX = position.x + (width / 2f);
        float dist = Math.abs(knightPos.x - centerX);

        if (dist > DETECT_RANGE) return false;

        if (facingRight && knightPos.x >= centerX) return true;
        if (!facingRight && knightPos.x <= centerX) return true;

        if (dist < (width / 2f) + 30f) return true;

        return false;
    }

    @Override
    protected void buildAttackHitbox() {
        if (!charging || state == EnemyState.ATTACK_JUMP) {
            attackHitbox = null;
            return;
        }
        float hitWidth = 50f;
        float hx = facingRight ? position.x + width : position.x - hitWidth;
        attackHitbox = new Rectangle(hx, position.y + 20f, hitWidth, height - 40f);
    }

    @Override
    public void takeDamage(int damage, boolean hitFromLeft, boolean isSpell) {
        super.takeDamage(damage, hitFromLeft, isSpell);

        this.facingRight = !hitFromLeft;

        if (charging) {
            charging = false;
            chargeCooldown = CHARGE_COOLDOWN;
        }
    }
}
