package com.parham.hollowknight.model.entities.bossfight.strategy;

import com.badlogic.gdx.math.Vector2;
import com.parham.hollowknight.model.entities.bossfight.FalseKnight;

public interface BossStrategy {

    void enterStrategy(FalseKnight boss);

    void updateStrategy(FalseKnight boss, float delta, Vector2 playerPos);

    void exitStrategy(FalseKnight boss);

}
