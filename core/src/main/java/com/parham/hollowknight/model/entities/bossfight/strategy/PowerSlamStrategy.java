package com.parham.hollowknight.model.entities.bossfight.strategy;

import com.badlogic.gdx.math.Vector2;
import com.parham.hollowknight.controller.AudioManager;
import com.parham.hollowknight.model.entities.bossfight.FalseKnight;
import com.parham.hollowknight.model.enums.EnemyState;

public class PowerSlamStrategy implements BossStrategy {
    private float timer = 0f;
    private boolean striked = false;

    private static final float LEAP_VEL_Y = 650f;
    private static final float LEAP_VEL_X = 180f;

    private boolean jumping = true;

    @Override
    public void enterStrategy(FalseKnight boss) {
        boss.state = EnemyState.POWER_SLAM;
        striked = false;
        jumping = true;
        boss.onGround = false;
        timer = 0f;
        boss.getGameScreen().triggerScreenShake(0.3f, 6f);

        AudioManager.getInstance().playSound(boss.getGameScreen().getGameAssets().attackAnticipateSound);

        int direction = boss.facingRight ? 1 : -1;
        boss.vX = LEAP_VEL_X * direction;
        boss.vY = LEAP_VEL_Y;
    }

    @Override
    public void updateStrategy(FalseKnight boss, float delta, Vector2 playerPos) {
        timer += delta;

        if (jumping) {
            if (timer > 0.2f && boss.onGround) {
                timer = 0f;
                jumping = false;
                boss.vX = 0;
            }
        } else {
            if (!striked) {
                striked = true;
                performPowerStrike(boss);
            }
            if (timer > 0.35f)
                boss.clearAttackHitbox();

            if (timer >= 1f)
                boss.changeStrategy(new IdleStrategy());

        }
    }

    @Override
    public void exitStrategy(FalseKnight boss) {
        boss.vX = 0;
        jumping = false;
        striked = false;
    }

    private void performPowerStrike(FalseKnight boss) {
        boss.buildMaceHitbox();
        AudioManager.getInstance().playSound(boss.getGameScreen().getGameAssets().fkPowerMaceSound);
        boss.getGameScreen().triggerScreenShake(0.5f, 12f);
        boss.spawnShockWave(boss.facingRight);
    }
}
