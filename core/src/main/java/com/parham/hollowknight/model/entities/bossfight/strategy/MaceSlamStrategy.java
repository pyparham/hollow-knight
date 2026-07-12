package com.parham.hollowknight.model.entities.bossfight.strategy;

import com.badlogic.gdx.math.Vector2;
import com.parham.hollowknight.controller.AudioManager;
import com.parham.hollowknight.model.entities.bossfight.FalseKnight;
import com.parham.hollowknight.model.enums.EnemyState;

public class MaceSlamStrategy implements BossStrategy {
    private float stateTimer = 0f;
    private boolean striked = false;

    @Override
    public void enterStrategy(FalseKnight boss) {

        stateTimer = 0f;

        boss.state = EnemyState.MACE_SLAM;
        striked = false;
        boss.vX = 0;
        AudioManager.getInstance().playSound(boss.getGameScreen().getGameAssets().attackAnticipateSound);

    }

    @Override
    public void updateStrategy(FalseKnight boss, float delta, Vector2 playerPos) {
        stateTimer += delta;

        float antic = 0.6f / boss.speedCoef;

        if (stateTimer >= antic && !striked) {
            striked = true;
            performStrike(boss);
        }

        if (stateTimer >= antic + 0.35) {
            boss.clearAttackHitbox();
            boss.changeStrategy(new IdleStrategy());
        }
    }

    @Override
    public void exitStrategy(FalseKnight boss) {
        striked = false;
        boss.clearAttackHitbox();
    }

    private void performStrike(FalseKnight boss) {
        boss.buildMaceHitbox();
        AudioManager.getInstance().playSound(boss.getGameScreen().getGameAssets().fkPowerMaceSound);
        boss.getGameScreen().triggerScreenShake(0.3f, 6f);
    }
}
