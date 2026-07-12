package com.parham.hollowknight.model.entities.bossfight.strategy;

import com.badlogic.gdx.math.Vector2;
import com.parham.hollowknight.controller.AudioManager;
import com.parham.hollowknight.model.entities.bossfight.FalseKnight;
import com.parham.hollowknight.model.enums.EnemyState;

public class StunnedStrategy implements BossStrategy {
    public float timer = 0f;

    public StunnedStrategy() {
        this.timer = 0f;
    }

    public StunnedStrategy(float timer) {
        this.timer = timer;
    }

    @Override
    public void enterStrategy(FalseKnight boss) {
        boss.state = EnemyState.STUN;
        boss.vX = 0;
        boss.vY = 0;
        AudioManager.getInstance().playSound(boss.getGameScreen().getGameAssets().fkRoll);
    }

    @Override
    public void updateStrategy(FalseKnight boss, float delta, Vector2 playerPos) {
        timer += delta;
        if (timer >= boss.stunDuration) {
            boss.changeStrategy(new StunRecoverStrategy());
        }
    }

    @Override
    public void exitStrategy(FalseKnight boss) {

    }
}
