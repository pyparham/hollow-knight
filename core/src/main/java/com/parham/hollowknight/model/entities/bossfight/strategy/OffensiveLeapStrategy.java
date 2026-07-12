package com.parham.hollowknight.model.entities.bossfight.strategy;

import com.badlogic.gdx.math.Vector2;
import com.parham.hollowknight.controller.AudioManager;
import com.parham.hollowknight.model.entities.bossfight.FalseKnight;
import com.parham.hollowknight.model.enums.EnemyState;

import static com.parham.hollowknight.model.enums.PhysicsConstants.LEAP_V_X;
import static com.parham.hollowknight.model.enums.PhysicsConstants.LEAP_V_Y;

public class OffensiveLeapStrategy implements BossStrategy {
    private float stateTimer = 0f;
    private boolean jumped = false;
    private static final float ANTIC_DURATION = 0.4f;

    @Override
    public void enterStrategy(FalseKnight boss) {
        stateTimer = 0f;
        boss.vX = 0;
        boss.vY = 0;

        jumped = false;

        boss.state = EnemyState.OFFENSIVE_LEAP;

        boss.getGameScreen().triggerScreenShake(0.3f, 6f);

        AudioManager.getInstance().playSound(boss.getGameScreen().getGameAssets().fkJumpSound);
        AudioManager.getInstance().playSound(boss.getGameScreen().getGameAssets().attackAnticipateSound);
    }

    @Override
    public void updateStrategy(FalseKnight boss, float delta, Vector2 playerPos) {
        stateTimer += delta;

        float anticTime = ANTIC_DURATION / boss.speedCoef;

        if (stateTimer >= anticTime && !jumped) {
            jumped = true;
            boss.onGround = false;
            int direction = boss.facingRight ? 1 : -1;
            boss.vY = LEAP_V_Y;
            boss.vX = LEAP_V_X * direction;
        }

        if (jumped && boss.onGround)
            boss.changeStrategy(new IdleStrategy());

    }

    @Override
    public void exitStrategy(FalseKnight boss) {
        boss.vX = 0;
        boss.vY = 0;
    }
}
