package com.parham.hollowknight.controller;

import com.parham.hollowknight.model.entities.Enemy;
import com.parham.hollowknight.model.entities.Knight;
import com.parham.hollowknight.model.enums.KnightState;

import java.util.List;

public class CombatManager {


    public void update(Knight knight, List<Enemy> enemies) {

        if (knight.currentState == KnightState.DEAD || knight.isInvincible()) return;

        for (Enemy enemy : enemies) {
            if (enemy.isDead()) continue;

            if (knight.getHitbox().overlaps(enemy.getBodyHitbox())) {
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
            }

            if (knight.isCurrentlyAttacking() && knight.getNailHitbox() != null) {
                if (enemy.getBodyHitbox().overlaps(knight.getNailHitbox())) {
                    boolean hitFromLeft = knight.getPosition().x < enemy.getPosition().x;
                    enemy.takeDamage(knight.getNailDamage(), hitFromLeft);
                    knight.applyNailRecoil();
                    return;
                }
            }
        }
    }

}
