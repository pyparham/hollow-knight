package com.parham.hollowknight.model.entities.bossfight.strategy;

import com.badlogic.gdx.math.Vector2;
import com.parham.hollowknight.model.entities.bossfight.FalseKnight;
import com.parham.hollowknight.model.enums.EnemyState;

public class DeathStrategy implements BossStrategy {

    @Override
    public void enterStrategy(FalseKnight boss) {
        boss.state = EnemyState.DEAD_AIR;
        boss.vX = 0;
    }

    @Override
    public void updateStrategy(FalseKnight boss, float delta, Vector2 playerPos) {
    }

    @Override
    public void exitStrategy(FalseKnight boss) {
    }
}
