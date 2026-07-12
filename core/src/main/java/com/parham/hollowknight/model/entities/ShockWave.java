package com.parham.hollowknight.model.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class ShockWave {

    public Vector2 position;

    public float velocityX;
    public boolean facingRight;

    public Rectangle hitbox;

    public boolean isDestroyed = false;

    public float stateTimer = 0f;


    public ShockWave(float x, float y, boolean facingRight) {
        this.position = new Vector2(x, y);
        this.facingRight = facingRight;
        this.velocityX = facingRight ? 550f : -550f;
        this.hitbox = new Rectangle(x, y, 180, 150);
    }

    public void update(float delta) {
        if (facingRight) velocityX += 800f * delta;
        else velocityX -= 800f * delta;

        position.x += velocityX * delta;
        hitbox.setPosition(position.x, position.y);

        stateTimer += delta;
        if (stateTimer > 1.5f) isDestroyed = true;
    }
}
