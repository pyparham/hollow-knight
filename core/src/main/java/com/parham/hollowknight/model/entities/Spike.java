package com.parham.hollowknight.model.entities;


import com.badlogic.gdx.math.Rectangle;

public class Spike {
    public final Rectangle hitbox;
    public final boolean isOnGround;

    public Spike(Rectangle hitbox, boolean isOnGround) {
        this.hitbox = hitbox;
        this.isOnGround = isOnGround;
    }
}
