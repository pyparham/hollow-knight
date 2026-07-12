package com.parham.hollowknight.model.entities.bossfight.strategy;

import com.badlogic.gdx.math.Vector2;
import com.parham.hollowknight.model.entities.bossfight.FalseKnight;
import com.parham.hollowknight.model.enums.EnemyState;

public class StunRecoverStrategy implements BossStrategy {
    private float timer = 0f;
    private static final float RECOVER_DURATION = 0.6f;

    @Override
    public void enterStrategy(FalseKnight boss) {
        boss.state = EnemyState.STUN_RECOVER;
        timer = 0f;
        boss.vX = 0;
        boss.vY = 0;
    }

    @Override
    public void updateStrategy(FalseKnight boss, float delta, Vector2 playerPos) {
        timer += delta;
        if (timer >= RECOVER_DURATION) boss.changeStrategy(new IdleStrategy());
    }

    @Override
    public void exitStrategy(FalseKnight boss) {
        boss.moveSpeed *= 1.7f;
        boss.chargeSpeedX *= 1.7f;
        boss.actionCooldown *= 0.6f;
    }
}
