package com.parham.hollowknight.controller;

import com.badlogic.gdx.math.Rectangle;
import com.parham.hollowknight.model.entities.Enemy;
import com.parham.hollowknight.model.entities.Knight;
import com.parham.hollowknight.model.entities.VengefulSpirit;
import com.parham.hollowknight.model.enums.PhysicsConstants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SpellManager {

    private final List<VengefulSpirit> activeSpells = new ArrayList<>();

    public boolean tryCastVengefulSpirit(Knight knight) {
        if (knight.soul < PhysicsConstants.SPELL_SOUL_COST) return false;

        knight.soul -= PhysicsConstants.SPELL_SOUL_COST;
        VengefulSpirit spell = new VengefulSpirit(
            knight.position.x, knight.position.y, knight.facingRight);
        activeSpells.add(spell);
        return true;
    }

    public void update(float delta, List<Rectangle> platforms, List<Enemy> enemies) {
        Iterator<VengefulSpirit> it = activeSpells.iterator();

        while (it.hasNext()) {
            VengefulSpirit spell = it.next();
            spell.update(delta, platforms);

            for (Enemy enemy : enemies) {
                if (enemy.isDead() || spell.hitEnemies.contains(enemy)) continue;

                if (spell.hitbox.overlaps(enemy.getBodyHitbox())) {
                    enemy.takeDamage(spell.damage, spell.facingRight, true);
                    spell.hitEnemies.add(enemy);
                }
            }

            if (spell.isDestroyed) {
                it.remove();
            }
        }
    }

    public List<VengefulSpirit> getActiveSpells() {
        return activeSpells;
    }
}
