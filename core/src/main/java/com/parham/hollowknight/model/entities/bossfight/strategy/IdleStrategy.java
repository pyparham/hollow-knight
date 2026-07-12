package com.parham.hollowknight.model.entities.bossfight.strategy;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.parham.hollowknight.controller.AudioManager;
import com.parham.hollowknight.model.entities.bossfight.FalseKnight;
import com.parham.hollowknight.model.enums.EnemyState;

public class IdleStrategy implements BossStrategy {
    private float timer = 0f;

    @Override
    public void enterStrategy(FalseKnight boss) {
        boss.state = EnemyState.IDLE;
        timer = 0f;
        boss.vX = 0;
        boss.vY = 0;
    }

    @Override
    public void updateStrategy(FalseKnight boss, float delta, Vector2 playerPos) {

        boss.vX = 0;
        boss.vY = 0;

        timer += delta;
        boss.facingRight = playerPos.x > boss.position.x;

        if (timer >= boss.actionCooldown) action(boss, playerPos);

    }

    @Override
    public void exitStrategy(FalseKnight boss) {
    }

    private void action(FalseKnight boss, Vector2 playerPos) {
        float dist = Math.abs(playerPos.x - boss.position.x);
        EnemyState next;

        if (dist <= boss.getCloseRange()) {
            next = MathUtils.randomBoolean(0.35f)
                ? EnemyState.DEFENSIVE_LEAP
                : EnemyState.MACE_SLAM;
        } else if (dist >= boss.getMidRange()) {
            next = MathUtils.randomBoolean(0.5f)
                ? EnemyState.OFFENSIVE_LEAP
                : EnemyState.CHARGE_RUN;
            if (next == EnemyState.CHARGE_RUN) {
                FalseKnight.runTimer = FalseKnight.RUN_DURATION_SOUND;
                AudioManager.getInstance().playSound(boss.gameScreen.getGameAssets().fkRoll);
            }

            if (boss.isPhaseTwo && MathUtils.randomBoolean(0.5f)) next = EnemyState.POWER_SLAM;

        } else {
            int randomNumber = MathUtils.random(1, 3);
            next = (randomNumber == 1) ? EnemyState.MACE_SLAM
                : (randomNumber == 2) ? EnemyState.OFFENSIVE_LEAP
                : EnemyState.CHARGE_RUN;
            if (next == EnemyState.CHARGE_RUN) {
                FalseKnight.runTimer = FalseKnight.RUN_DURATION_SOUND;
                AudioManager.getInstance().playSound(boss.gameScreen.getGameAssets().fkRoll);
            }
            if (boss.isPhaseTwo && MathUtils.randomBoolean(0.5f)) next = EnemyState.POWER_SLAM;

        }


        if (next == boss.lastsState)
            next = (next == EnemyState.MACE_SLAM) ? EnemyState.OFFENSIVE_LEAP : EnemyState.MACE_SLAM;


        boss.lastsState = next;

        switch (next) {
            case DEFENSIVE_LEAP -> boss.changeStrategy(new DefensiveLeapStrategy());
            case CHARGE_RUN -> boss.changeStrategy(new ChargeRunStrategy());
            case OFFENSIVE_LEAP -> boss.changeStrategy(new OffensiveLeapStrategy());
            case MACE_SLAM -> boss.changeStrategy(new MaceSlamStrategy());
            case POWER_SLAM -> boss.changeStrategy(new PowerSlamStrategy());

            default -> {
            }
        }
    }
}
