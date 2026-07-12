package com.parham.hollowknight.model.entities.bossfight.strategy;

import com.badlogic.gdx.math.Vector2;
import com.parham.hollowknight.controller.AudioManager;
import com.parham.hollowknight.model.entities.bossfight.FalseKnight;
import com.parham.hollowknight.model.enums.EnemyState;

import static com.parham.hollowknight.model.enums.PhysicsConstants.LEAP_V_X;
import static com.parham.hollowknight.model.enums.PhysicsConstants.LEAP_V_Y;

public class DefensiveLeapStrategy implements BossStrategy {
    private float stateTimer = 0f;

    @Override
    public void enterStrategy(FalseKnight boss) {
        if (!boss.facingRight && boss.position.x > 1250) exitStrategy(boss);
        stateTimer = 0f;

        boss.onGround = false;
        boss.state = EnemyState.DEFENSIVE_LEAP;

        AudioManager.getInstance().playSound(boss.getGameScreen().getGameAssets().fkJumpSound);
        boss.getGameScreen().triggerScreenShake(0.3f, 6f);

        int direction = boss.facingRight ? -1 : 1;
        boss.vY = LEAP_V_Y;
        boss.vX = LEAP_V_X * direction;
    }

    @Override
    public void updateStrategy(FalseKnight boss, float delta, Vector2 playerPos) {
        stateTimer += delta;
        if (stateTimer > 0.2f && boss.onGround)
            boss.changeStrategy(new IdleStrategy());
    }

    @Override
    public void exitStrategy(FalseKnight boss) {
        boss.vX = 0;
    }
}
