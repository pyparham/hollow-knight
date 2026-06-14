package com.parham.hollowknight.model.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.parham.hollowknight.controller.screens.GameScreen;
import com.parham.hollowknight.model.enums.EnemyType;

import java.util.List;

public class Crawlid extends Enemy {

    private static final float W = 108f;
    private static final float H = 81f;
    private static final float WALK_SPEED = 200f;

    public Crawlid(GameScreen gameScreen, float x, float y, float walkLeft, float walkRight) {
        super(gameScreen, EnemyType.CRAWLID, x, y, W, H, 33, walkLeft, walkRight);
        walkSpeed = WALK_SPEED;
        contactDamage = 1;
        soulDrop = 11f;
        this.restDuration = 0;
    }

    @Override
    protected void updateBehavior(float delta, List<Rectangle> platforms, Vector2 knightPos) {
        groundWalk(delta, platforms);
    }


}
