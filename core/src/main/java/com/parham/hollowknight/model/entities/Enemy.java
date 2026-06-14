package com.parham.hollowknight.model.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.parham.hollowknight.controller.screens.GameScreen;
import com.parham.hollowknight.model.Spike;
import com.parham.hollowknight.model.enums.AttackDirection;
import com.parham.hollowknight.model.enums.EnemyState;
import com.parham.hollowknight.model.enums.EnemyType;
import com.parham.hollowknight.model.enums.PhysicsConstants;

import java.util.List;

public abstract class Enemy {

    GameScreen gameScreen;
    public final Vector2 position = new Vector2();
    protected float vX = 0f;
    protected float vY = 0f;

    protected final Rectangle bodyHitbox = new Rectangle();
    protected Rectangle attackHitbox = null;
    protected float width;
    protected float height;

    protected int maxHealth;
    protected int currentHealth;
    protected int contactDamage = 1;
    protected int attackDamage = 1;
    protected int mass = 50;

    public EnemyState state = EnemyState.WALKING;
    public EnemyType type;

    public boolean facingRight = true;
    public boolean onGround = false;
    protected boolean dead = false;

    protected float walkLeft;
    protected float walkRight;
    protected float walkSpeed = 80f;
    protected float walkPeriod = 3f;
    protected float restDuration = 1.5f;
    private float walkTimer = 0f;
    private float restTimer = 0f;


    protected float turnTimer = 0f;
    protected float hitTimer = 0f;
    protected float stateTimer = 0f;
    public float animTime = 0f;
    protected float knockBackTimer = 0f;

    protected float soulDrop = 11f;

    protected static final float HIT_DURATION = 0.35f;
    protected static final float DETECT_RANGE = 600f;
    protected static final float ATTACK_RANGE = 90f;
    protected static final float KNOCK_BACK_DURATION = 0.04f;


    protected Enemy(GameScreen gameScreen,
                    EnemyType type,
                    float x, float y, float width, float height,
                    int maxHealth,
                    float walkLeft, float walkRight) {
        this.gameScreen = gameScreen;
        this.type = type;
        this.width = width;
        this.height = height;
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.walkLeft = walkLeft;
        this.walkRight = walkRight;
        position.set(x, y);
        syncHitbox();
    }


    public final void update(float delta, List<Rectangle> platforms, Vector2 knightPos, List<Spike> spikes) {
        if (!(this instanceof Mossfly && ((Mossfly) this).asleep))
            animTime += delta;


        if (state == EnemyState.DEAD_GROUND) return;


        if (state == EnemyState.DEAD_AIR) {
            applyGravity(delta);
            applyVelocity(delta);
            resolveCollisions(platforms);

            if (onGround) {
                state = EnemyState.DEAD_GROUND;
                animTime = 0f;
                vX = 0f;
            }
            return;
        }

        if (state == EnemyState.TURN) {
            turnTimer -= delta;
            if (turnTimer <= 0f) {
                state = EnemyState.WALKING;
                turnTimer = 0f;
                facingRight = !facingRight;
            }
        }

        if (state == EnemyState.HIT) {
            hitTimer -= delta;
            knockBackTimer -= delta;
            applyGravity(delta);
            applyVelocity(delta);
            resolveCollisions(platforms);
            syncHitbox();
            if (hitTimer <= 0f) onHitEnd();
            if (knockBackTimer <= 0f) walk();
            return;
        }

        stateTimer += delta;

        if (state != EnemyState.TURN)
            updateBehavior(delta, platforms, knightPos);
        applyGravity(delta);
        applyVelocity(delta);
        resolveCollisions(platforms);
        checkSpikeCollisions(spikes);
        syncHitbox();
        buildAttackHitbox();
    }


    protected abstract void updateBehavior(float delta, List<Rectangle> platforms, Vector2 knightPos);

    protected void buildAttackHitbox() {
        attackHitbox = null;
    }

    protected void onHitEnd() {
        state = EnemyState.WALKING;
        stateTimer = 0f;
    }

    protected void applyGravity(float delta) {
        if (!usesGravity() && state != EnemyState.DEAD_AIR) return;
        vY += PhysicsConstants.GRAVITY * delta;
        if (vY < PhysicsConstants.MAX_FALL_SPEED)
            vY = PhysicsConstants.MAX_FALL_SPEED;
    }

    protected boolean usesGravity() {
        return true;
    }

    private void applyVelocity(float delta) {
        position.x += vX * delta;
        position.y += vY * delta;
    }

    protected void resolveCollisions(List<Rectangle> platforms) {
        onGround = false;
        syncHitbox();

        for (Rectangle p : platforms) {
            if (!bodyHitbox.overlaps(p)) continue;

            float overlapX = Math.min(bodyHitbox.x + bodyHitbox.width, p.x + p.width) - Math.max(bodyHitbox.x, p.x);
            float overlapY = Math.min(bodyHitbox.y + bodyHitbox.height, p.y + p.height) - Math.max(bodyHitbox.y, p.y);

            if (overlapX < overlapY) {
                if (bodyHitbox.x + bodyHitbox.width / 2f < p.x + p.width / 2f)
                    position.x -= overlapX;
                else
                    position.x += overlapX;

                vX = 0f;
                facingRight = !facingRight;

            } else {
                if (bodyHitbox.y + bodyHitbox.height / 2f < p.y + p.height / 2f) {
                    position.y -= overlapY;
                } else {
                    position.y += overlapY;
                    onGround = true;
                }
                vY = 0f;
            }
            syncHitbox();
        }
    }

    protected void checkSpikeCollisions(List<Spike> spikes) {
        if (dead) return;

        syncHitbox();
        for (Spike spike : spikes) {
            if (bodyHitbox.overlaps(spike.hitbox)) {
                currentHealth = 0;
                dead = true;
                state = EnemyState.DEAD_AIR;
                animTime = 0f;
                vX = 0f;
                vY = PhysicsConstants.KNOCKBACK_Y * 2;
                break;
            }
        }
    }

    protected void syncHitbox() {
        bodyHitbox.set(position.x, position.y, width, height);
    }

    protected float distanceTo(Vector2 target) {
        return Vector2.dst(position.x, position.y, target.x, target.y);
    }

    protected boolean playerInRange(Vector2 knightPos, float range) {
        return distanceTo(knightPos) <= range;
    }

    protected void facePlayer(Vector2 knightPos) {
        facingRight = knightPos.x > position.x + width / 2f;
    }

    protected void chasePlayer(Vector2 knightPos, float speed) {
        facePlayer(knightPos);
        vX = facingRight ? speed : -speed;
    }

    protected void walk() {
        if (facingRight) {
            if (position.x + width >= walkRight) onWalkTurn();
            else vX = walkSpeed;
        } else {
            if (position.x <= walkLeft) onWalkTurn();
            else vX = -walkSpeed;
        }
    }

    protected void onWalkTurn() {
        state = EnemyState.TURN;
        vX = 0;
        turnTimer = 0.15f;
    }

    protected void groundWalk(float delta, List<Rectangle> platforms) {
        if (restTimer > 0f) {
            restTimer -= delta;
            state = EnemyState.IDLE;
            vX = 0f;
            return;
        }

        state = EnemyState.WALKING;
        walk();

        walkTimer += delta;
        if (walkTimer >= walkPeriod) {
            walkTimer = 0f;
            restTimer = restDuration;
        }
    }

    public void takeDamage(int damage, boolean hitFromLeft) {
        if (dead || state == EnemyState.HIT) return;
        currentHealth -= damage;
        Knight knight = gameScreen.getKnight();
        knight.gainSoul(damage);

        if (currentHealth <= 0) {
            currentHealth = 0;
            dead = true;
            state = EnemyState.DEAD_AIR;
            animTime = 0f;
            vX = 0;
            vY = PhysicsConstants.KNOCKBACK_Y * 2;
            return;
        }
        state = EnemyState.HIT;
        hitTimer = HIT_DURATION;
        knockBackTimer = KNOCK_BACK_DURATION;
        animTime = 0f;
        float massRatio = 50f / this.mass;
        if (knight.attackDirection != AttackDirection.DOWN)
            vX = hitFromLeft ? PhysicsConstants.KNOCKBACK_X * 3f * massRatio
                : -PhysicsConstants.KNOCKBACK_X * 3f * massRatio;
    }

    public boolean isDead() {
        return dead;
    }

    public boolean isAttacking() {
        return state == EnemyState.ATTACK ||
            state == EnemyState.ATTACK_JUMP;
    }

    public Rectangle getBodyHitbox() {
        return bodyHitbox;
    }

    public Rectangle getAttackHitbox() {
        return attackHitbox;
    }

    public Vector2 getPosition() {
        return position;
    }

    public int getContactDamage() {
        return contactDamage;
    }

    public int getAttackDamage() {
        return attackDamage;
    }

    public EnemyType getType() {
        return type;
    }

    public Vector2 getAttackPosition() {
        return attackHitbox != null
            ? new Vector2(attackHitbox.x, attackHitbox.y)
            : position;
    }
}
