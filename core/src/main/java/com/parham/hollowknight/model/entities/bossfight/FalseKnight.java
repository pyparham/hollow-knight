package com.parham.hollowknight.model.entities.bossfight;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.parham.hollowknight.controller.AudioManager;
import com.parham.hollowknight.controller.screens.GameScreen;
import com.parham.hollowknight.model.entities.Enemy;
import com.parham.hollowknight.model.entities.ShockWave;
import com.parham.hollowknight.model.entities.bossfight.strategy.*;
import com.parham.hollowknight.model.enums.EnemyState;
import com.parham.hollowknight.model.enums.EnemyType;
import com.parham.hollowknight.model.enums.PhysicsConstants;

import java.util.List;

import static com.parham.hollowknight.model.enums.PhysicsConstants.FLASH_DURATION;

//thanks to Elyas Hajinejad for helping me with the boss fight logic and design

public class FalseKnight extends Enemy {

    public boolean bossFightStarted = false;

    private BossStrategy currentStrategy;

    public EnemyState lastsState = EnemyState.IDLE;

    public Rectangle vulnerabilityHitbox = new Rectangle(10000, 100000, 60f, 60f);

    public boolean isPhaseTwo = false;

    public float moveSpeed;
    public float chargeSpeedX = 420f;
    public float actionCooldown = 2f;
    public float stunDuration = 5f;

    public float speedCoef = 1.0f;
    private float flashTimer = 0f;

    public static float runTimer = 0f;
    public static float RUN_DURATION_SOUND = 1.0f;


    public final Array<ShockWave> activeShockWaves = new Array<>();
    BossStrategy pendingStrategyAfterHit;


    private static final float W = 250;
    private static final float H = 240;

    public FalseKnight(GameScreen gameScreen, float x, float y, float walkLeft, float walkRight) {
        super(gameScreen, EnemyType.FALSE_KNIGHT, x, y, W, H, 250, walkLeft, walkRight);
        contactDamage = 1;
        attackDamage = 1;
        soulDrop = 0f;
        mass = 400;
        moveSpeed = 0;
        changeStrategy(new IdleStrategy());
    }


    @Override
    public void takeDamage(int damage, boolean hitFromLeft, boolean isSpell) {
        if (dead) return;
        if (hitTimer > 0f) return;

        currentHealth -= damage;

        float massRatio = 50f / this.mass;
        vX = hitFromLeft ? PhysicsConstants.KNOCKBACK_X * 2f * massRatio
            : -PhysicsConstants.KNOCKBACK_X * 2f * massRatio;

        if (!isSpell) gameScreen.getKnight().gainSoul(damage);
        knockBackTimer = KNOCK_BACK_DURATION;
        flashTimer = FLASH_DURATION;

        AudioManager.getInstance().playSound(gameScreen.getGameAssets().pogoSound);

        if (state == EnemyState.STUN) {
            changeStrategy(new StunHitStrategy());
            hitTimer = HIT_DURATION;
            state = EnemyState.STUN_HIT;
            return;
        }
        if (currentHealth <= maxHealth / 2 && !isPhaseTwo) {
            isPhaseTwo = true;
            speedCoef = 1.3f;
            changeStrategy(new StunnedStrategy());
        }
        if (currentHealth <= 0) {
            currentHealth = 0;
            dead = true;
            state = EnemyState.DEAD_AIR;
            pendingStrategyAfterHit = null;
            changeStrategy(new DeathStrategy());
            gameScreen.getGame().currentGameData.isFalseKnightDefeated = true;
            return;
        }
        hitTimer = HIT_DURATION;

    }


    @Override
    protected void updateBehavior(float delta, List<Rectangle> platforms, Vector2 knightPos) {
        if (gameScreen.getTiledMap() == gameScreen.getGameAssets().bossfightRoom) bossFightStarted = true;
        if (hitTimer > 0f) hitTimer -= delta;
        if (flashTimer > 0f) flashTimer -= delta;
        currentStrategy.updateStrategy(this, delta, knightPos);
        FalseKnight.runTimer -= delta;
        if (state == EnemyState.CHARGE_RUN) {
            if (FalseKnight.runTimer <= 0f) {
                AudioManager.getInstance().playSound(gameScreen.getGameAssets().fkRoll);
                FalseKnight.runTimer = FalseKnight.RUN_DURATION_SOUND;
            }
        } else AudioManager.getInstance().stopSound(gameScreen.getGameAssets().fkRoll);

        for (int i = activeShockWaves.size - 1; i >= 0; i--) {
            ShockWave sw = activeShockWaves.get(i);
            sw.update(delta);
            if (sw.isDestroyed) activeShockWaves.removeIndex(i);
        }

        if (state == EnemyState.STUN && vulnerabilityHitbox != null) {
            float weakPointX = facingRight ? (position.x + width - 60f) : (position.x);
            vulnerabilityHitbox.setPosition(weakPointX, position.y + 10f);
        } else
            vulnerabilityHitbox.setPosition(10000f, 10000f);
    }

    public void changeStrategy(BossStrategy newStrategy) {
        float timer;
        if (currentStrategy instanceof StunnedStrategy && newStrategy instanceof StunHitStrategy) {
            timer = ((StunnedStrategy) currentStrategy).timer;
            currentStrategy.exitStrategy(this);
            currentStrategy = newStrategy;
            ((StunHitStrategy) currentStrategy).setStunTimer(timer);
        } else {
            if (currentStrategy != null) currentStrategy.exitStrategy(this);
            currentStrategy = newStrategy;
        }

        animTime = 0f;
        currentStrategy.enterStrategy(this);
    }

    @Override
    protected void resolveCollisions(List<Rectangle> platforms) {
        boolean wasOnGround = onGround;
        super.resolveCollisions(platforms);
        if (!wasOnGround && onGround && state != EnemyState.LAND) onLanding();
    }

    @Override
    public boolean isAttacking() {
        return state == EnemyState.MACE_SLAM || state == EnemyState.ATTACK_JUMP ||
            state == EnemyState.ATTACK || state == EnemyState.CHARGE_RUN || state == EnemyState.POWER_SLAM ||
            state == EnemyState.DEFENSIVE_LEAP;
    }

    private void onLanding() {
        state = EnemyState.LAND;
        AudioManager.getInstance().playSound(gameScreen.getGameAssets().fkLandSound);
    }


    public void spawnShockWave(boolean facingRightDir) {
        float spawnX = facingRightDir ? (position.x + width) : (position.x - 60f);
        activeShockWaves.add(new ShockWave(spawnX, position.y, facingRightDir));
    }

    @Override
    protected void buildAttackHitbox() {
    }

    public void buildMaceHitbox() {
        float attackRange = 140f;
        float attackX = facingRight ? (position.x + width) : (position.x - attackRange);
        attackHitbox = new Rectangle(attackX, position.y, attackRange, 90);
    }

    public void clearAttackHitbox() {
        attackHitbox = null;
    }

    public void setCorpse() {
        vX = 0;
        vY = 0;
        bossFightStarted = false;
        animTime = 9999f;
        currentHealth = 0;
        state = EnemyState.DEAD_GROUND;
    }

    public float getCloseRange() {
        return 160;
    }

    public float getMidRange() {
        return 420;
    }

    public GameScreen getGameScreen() {
        return gameScreen;
    }


    public boolean isFlashing() {
        return flashTimer > 0f && !(state == EnemyState.DEAD_AIR || state == EnemyState.DEAD_GROUND);
    }


}
