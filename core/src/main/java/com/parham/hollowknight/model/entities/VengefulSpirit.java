package com.parham.hollowknight.model.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VengefulSpirit {

    public final Vector2 position = new Vector2();
    public final Rectangle hitbox = new Rectangle();
    public boolean facingRight;
    public float ballAnimTime = 0f;

    private final float vX;
    private static final float SPEED = 1850f;
    private static final float WIDTH = 80f;

    public final Vector2 blastPosition = new Vector2();
    public float blastAnimTime = 0f;
    public boolean blastFinished = false;
    public static final float BLAST_DURATION = 0.7f;

    public boolean isDestroyed = false;
    public int damage = 33;

    public final Set<Enemy> hitEnemies = new HashSet<>();

    public VengefulSpirit(float knightX, float knightY, boolean facingRight) {
        this.facingRight = facingRight;
        this.vX = facingRight ? SPEED : -SPEED;

        float blastX = facingRight ? knightX + Knight.WIDTH
            : knightX - 300;
        blastPosition.set(blastX, knightY + Knight.HEIGHT / 2 - 150);

        float startX = facingRight ? knightX + Knight.WIDTH : knightX - WIDTH;
        position.set(startX, knightY);
        hitbox.set(position.x, position.y, WIDTH, 50);
    }

    public void update(float delta, List<Rectangle> platforms) {
        if (!blastFinished) {
            blastAnimTime += delta;
            if (blastAnimTime >= BLAST_DURATION) {
                blastFinished = true;
            }
        }

        ballAnimTime += delta;
        position.x += vX * delta;
        hitbox.setPosition(position.x, position.y);

        if (ballAnimTime >= 1.5f || hitsWall(platforms)) {
            isDestroyed = true;
        }
    }

    private boolean hitsWall(List<Rectangle> platforms) {
        for (Rectangle p : platforms) if (hitbox.overlaps(p)) return true;
        return false;
    }
}
