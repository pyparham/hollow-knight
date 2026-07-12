package com.parham.hollowknight.controller;

import com.parham.hollowknight.model.entities.Enemy;
import com.parham.hollowknight.model.entities.Knight;
import com.parham.hollowknight.model.entities.Zote;
import com.parham.hollowknight.model.entities.bossfight.FalseKnight;
import com.parham.hollowknight.model.enums.EnemyState;
import com.parham.hollowknight.model.enums.KnightState;

import java.util.List;

public class CombatManager {


    public void update(Knight knight, List<Enemy> enemies) {

        if (knight.currentState == KnightState.DEAD || knight.isInvincible()) return;

        for (Enemy enemy : enemies) {
            if (enemy.isDead()) continue;


            if (knight.getHitbox().overlaps(enemy.getBodyHitbox())) {
                if (enemy instanceof Zote) continue; // Zote doesn't damage knight
                boolean hitFromRight = enemy.getPosition().x > knight.position.x;
                knight.takeHit(enemy.getContactDamage(), hitFromRight);
                return;
            }

            if (enemy.isAttacking() && enemy.getAttackHitbox() != null) {
                if (knight.getHitbox().overlaps(enemy.getAttackHitbox())) {
                    boolean hitFromRight = enemy.getAttackPosition().x > knight.position.x;
                    knight.takeHit(enemy.getAttackDamage(), hitFromRight);
                    return;
                }
                if (enemy instanceof FalseKnight && enemy.state == EnemyState.MACE_SLAM) {
                    FalseKnight falseKnight = (FalseKnight) enemy;
                    if (falseKnight.getAttackHitbox() != null && knight.getHitbox().overlaps(falseKnight.getAttackHitbox())) {
                        boolean hitFromRight = falseKnight.getPosition().x > knight.position.x;
                        knight.takeHit(falseKnight.getAttackDamage(), hitFromRight);
                        return;
                    }
                }

            }


            if (knight.isCurrentlyAttacking() && knight.getNailHitbox() != null) {
                if (enemy instanceof FalseKnight && enemy.state == EnemyState.STUN) {
                    FalseKnight falseKnight = (FalseKnight) enemy;
                    if (falseKnight.vulnerabilityHitbox != null && falseKnight.vulnerabilityHitbox.overlaps(knight.getNailHitbox())) {
                        boolean hitFromLeft = knight.getPosition().x < falseKnight.getPosition().x;
                        falseKnight.takeDamage(knight.getNailDamage(), hitFromLeft, false);
                        knight.applyNailRecoil();
                        return;
                    }
                } else if (enemy.getBodyHitbox().overlaps(knight.getNailHitbox())) {
                    boolean hitFromLeft = knight.getPosition().x < enemy.getPosition().x;
                    enemy.takeDamage(knight.getNailDamage(), hitFromLeft, false);
                    knight.applyNailRecoil();
                    return;
                }
            }


        }
    }

}
