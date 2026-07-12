package com.parham.hollowknight.model.entities.bossfight.strategy;

import com.badlogic.gdx.math.Vector2;
import com.parham.hollowknight.controller.AudioManager;
import com.parham.hollowknight.model.entities.bossfight.FalseKnight;

import static com.parham.hollowknight.model.enums.EnemyState.STUN;

public class StunHitStrategy implements BossStrategy {
    private float timer = 0f;
    private static final float HIT_ANIM_DURATION = 0.24f;
    private float StunTimer;

    @Override
    public void enterStrategy(FalseKnight boss) {
        timer = 0f;
        boss.vX = 0;
        boss.vY = 0;
        AudioManager.getInstance().playSound(boss.getGameScreen().getGameAssets().stunHitSound);
    }

    @Override
    public void updateStrategy(FalseKnight boss, float delta, Vector2 playerPos) {
        timer += delta;
        StunTimer += delta;
        if (timer >= HIT_ANIM_DURATION) boss.changeStrategy(new StunnedStrategy(StunTimer));
    }

    @Override
    public void exitStrategy(FalseKnight boss) {
        boss.state = STUN;
    }

    public void setStunTimer(float stunTimer) {
        this.StunTimer = stunTimer;
    }
}
