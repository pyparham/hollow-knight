package com.parham.hollowknight.model.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.parham.hollowknight.controller.screens.GameScreen;
import com.parham.hollowknight.model.enums.EnemyState;
import com.parham.hollowknight.model.enums.EnemyType;

import java.util.List;

public class Mossfly extends Enemy {

    private static final float W = 90;
    private static final float H = 90;

    private static final float APPEAR_DURATION = 0.6f;
    private static final float WAKE_UP_RANGE = 400;
    private static final float CHASE_SPEED = 130f;

    private static final float TAKE_OFF_DURATION = 0.25f;

    public boolean takingOff = false;
    public boolean asleep = true;
    public boolean appearing = false;
    private boolean usesGravity = false;
    private float phaseTimer = 0f;

    public Mossfly(GameScreen gameScreen, float x, float y, float patrolLeft, float patrolRight) {
        super(gameScreen, EnemyType.MOSSFLY, x, y, W, H, 33, patrolLeft, patrolRight);
        contactDamage = 1;
        soulDrop = 11f;
        mass = 30;
    }

    @Override
    protected boolean usesGravity() {
        return usesGravity;
    }

    @Override
    protected void resolveCollisions(List<Rectangle> platforms) {
        if (!(asleep || appearing || takingOff) || state == EnemyState.DEAD_AIR) {
            super.resolveCollisions(platforms);
        }
    }

    @Override
    public void takeDamage(int damage, boolean hitFromLeft) {
        if (asleep || appearing) {
            asleep = false;
            appearing = false;
        }
        super.takeDamage(damage, hitFromLeft);
        if (currentHealth <= 0) usesGravity = true;

    }

    @Override
    protected void updateBehavior(float delta, List<Rectangle> platforms, Vector2 knightPos) {

        if (state == EnemyState.DEAD_AIR) {
            usesGravity = true;
            applyGravity(delta);
        }

        if (asleep) {
            state = EnemyState.IDLE;
            vX = 0f;
            vY = 0f;
            if (playerInRange(knightPos, WAKE_UP_RANGE)) {
                asleep = false;
                appearing = true;
                phaseTimer = APPEAR_DURATION;
                animTime = 0f;
                state = EnemyState.ATTACK_JUMP;
            }
            return;
        }

        if (appearing) {
            state = EnemyState.ATTACK_JUMP;
            vX = 0f;
            vY = 0f;
            phaseTimer -= delta;
            if (phaseTimer <= 0f) {
                appearing = false;
                takingOff = true;
                phaseTimer = TAKE_OFF_DURATION;
                state = EnemyState.TURN;
                animTime = 0f;
            }
            return;
        }

        if (takingOff) {
            state = EnemyState.TURN;
            vX = 0f;
            vY = 0f;
            phaseTimer -= delta;
            if (phaseTimer <= 0f) {
                takingOff = false;
                state = EnemyState.ATTACK;
                animTime = 0f;
            }
            return;
        }

        state = EnemyState.ATTACK;

        facingRight = knightPos.x > position.x;
        if (Math.abs(knightPos.x - position.x) > 10f) vX = facingRight ? CHASE_SPEED : -CHASE_SPEED;
        else vX = 0;


        float knightCenterY = knightPos.y + (Knight.HEIGHT / 2f);
        if (centerY() < knightCenterY - 10f)
            vY = CHASE_SPEED;
        else if (centerY() > knightCenterY + 10f)
            vY = -CHASE_SPEED;
        else
            vY = 0;

    }

    private float centerY() {
        return position.y + height / 2f;
    }
}
