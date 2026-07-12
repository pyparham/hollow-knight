package com.parham.hollowknight.model.enums;

public enum PhysicsConstants {
    ;
    public static final float GRAVITY = -3000;
    public static final float MOVE_SPEED = 400f;
    public static final float JUMP_VELOCITY = 1500;
    public static final float DASH_SPEED = 1200;
    public static final float DASH_DURATION = 0.3f;
    public static final float DASH_COOLDOWN = 0.25f;
    public static final float LANDING_DURATION = 0.16f;
    public static final float ATTACK_DURATION = 0.25f;
    public static final float ATTACK_COOLDOWN = 0.2f;

    public static final float WALL_SLIDE_SPEED = -300f;
    public static final float WALL_JUMP_LOCK_DURATION = 0.2f;

    //knight constants
    public static final float HEAL_DURATION = 1f;
    public static final float HEAL_SOUL_COST = 33f;
    public static final float MAX_SOUL = 99f;
    public static final float MAX_FALL_SPEED = -1200f;
    public static final float KNOCKBACK_X = 600f;
    public static final float KNOCKBACK_Y = 200f;

    public static final float SPELL_SOUL_COST = 33f;


    //boss constants
    public static final float LEAP_V_X = 600;
    public static final float LEAP_V_Y = 1500;

    public static final float FLASH_DURATION = 0.1f;
}
