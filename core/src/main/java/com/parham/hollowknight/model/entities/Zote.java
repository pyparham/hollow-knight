package com.parham.hollowknight.model.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.parham.hollowknight.controller.screens.GameScreen;
import com.parham.hollowknight.model.enums.EnemyState;
import com.parham.hollowknight.model.enums.EnemyType;
import com.parham.hollowknight.model.enums.ZoteDialogueState;

import java.util.List;

public class Zote extends Enemy {

    public ZoteDialogueState dialogueState = ZoteDialogueState.FIRST_MEETING;
    public boolean isTalking = false;
    private float dialogueCooldown = 0f;

    public boolean isAngry = false;
    private float angerDuration = 4f;
    private float angerTimer = 0f;
    private float turnTimer = 0f;
    private static final float TURN_DURATION = 0.16f;
    private boolean pendingFacingRight;

    public Zote(GameScreen gameScreen, float x, float y) {
        super(gameScreen, EnemyType.ZOTE, x, y, 70, 100, 999, x, x);
        contactDamage = 0;
        attackDamage = 1;
        soulDrop = 0f;
    }

    @Override
    protected void updateBehavior(float delta, List<Rectangle> platforms, Vector2 knightPos) {
        vX = 0f;

        if (state == EnemyState.TURN) {
            turnTimer -= delta;
            if (turnTimer <= 0f) {
                facingRight = pendingFacingRight;
                state = EnemyState.IDLE;
            }
            return;
        }

        if (dialogueCooldown > 0f) dialogueCooldown -= delta;

        if (isAngry) {
            angerTimer -= delta;
            state = EnemyState.ATTACK;
            facePlayer(knightPos);
            vX = facingRight ? 100 : -100;
            if (angerTimer <= 0f) {
                isAngry = false;
                state = EnemyState.IDLE;
            }
        } else {
            state = EnemyState.IDLE;
            vX = 0;
            boolean shouldFaceRight = facingRight;
            if (knightPos.x < position.x) shouldFaceRight = false;
            else if (knightPos.x > position.x) shouldFaceRight = true;

            if (shouldFaceRight != facingRight) {
                state = EnemyState.TURN;
                turnTimer = TURN_DURATION;
                pendingFacingRight = shouldFaceRight;
            }
        }
    }

    public boolean canInteract(Vector2 knightPos, float interactRange) {
        float distX = Math.abs(knightPos.x - position.x);
        return !isTalking && dialogueCooldown <= 0f && distX <= interactRange;
    }

    public void startDialogue() {
        isTalking = true;
    }

    public void endDialogue() {
        isTalking = false;
        dialogueCooldown = 0.5f;
        if (dialogueState == ZoteDialogueState.FIRST_MEETING) dialogueState = ZoteDialogueState.PRECEPT_TALK;

    }

    @Override
    public void takeDamage(int damage, boolean hitFromLeft, boolean isSpell) {
        if (isAngry) return;
        isAngry = true;
        angerTimer = angerDuration;
    }

    public boolean isAngry() {
        return isAngry;
    }
}
