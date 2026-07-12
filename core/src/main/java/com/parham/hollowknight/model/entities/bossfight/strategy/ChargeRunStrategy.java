package com.parham.hollowknight.model.entities.bossfight.strategy;

import com.badlogic.gdx.math.Vector2;
import com.parham.hollowknight.controller.AudioManager;
import com.parham.hollowknight.model.entities.bossfight.FalseKnight;
import com.parham.hollowknight.model.enums.EnemyState;

public class ChargeRunStrategy implements BossStrategy {
    private float stateTimer = 0f;
    private static final float CHARGE_DURATION = 1.2f;
    private int direction;

    @Override
    public void enterStrategy(FalseKnight boss) {
        if (boss.facingRight && boss.position.x > 1250) exitStrategy(boss);
        stateTimer = 0f;

        boss.state = EnemyState.CHARGE_RUN;
        AudioManager.getInstance().playSound(boss.getGameScreen().getGameAssets().fkRoll);
        FalseKnight.runTimer = FalseKnight.RUN_DURATION_SOUND;
        direction = boss.facingRight ? 1 : -1;
        boss.vX = boss.chargeSpeedX * direction;
        boss.getGameScreen().triggerScreenShake(0.3f, 6f);

    }

    @Override
    public void updateStrategy(FalseKnight boss, float delta, Vector2 playerPos) {
        stateTimer += delta;
        boss.vX = boss.chargeSpeedX * direction;

        if (stateTimer >= CHARGE_DURATION) {
            boss.changeStrategy(new IdleStrategy());
            return;
        }

        if (stateTimer > 0.1f && boss.vX == 0f) boss.changeStrategy(new IdleStrategy()); //for collisions

    }

    @Override
    public void exitStrategy(FalseKnight boss) {
        boss.vX = 0f;
    }
}
