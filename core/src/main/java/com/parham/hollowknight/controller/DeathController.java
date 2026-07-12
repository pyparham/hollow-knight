package com.parham.hollowknight.controller;

import com.parham.hollowknight.Main;
import com.parham.hollowknight.controller.screens.GameScreen;
import com.parham.hollowknight.model.entities.Knight;
import com.parham.hollowknight.model.enums.KnightState;

public class DeathController {

    public enum Phase {NONE, FADING_OUT, FADING_IN}

    private Phase phase = Phase.NONE;
    private float alpha = 0f;
    private float lastGlobalDarkness = 0f;
    private static final float FADE_SPEED = 2f;
    private boolean isSpikeRespawn = false;

    private final Main game;
    private final GameScreen gameScreen;
    private final Knight knight;

    private float deathToIdleTimer = 0f;

    public DeathController(Main game, GameScreen gameScreen, Knight knight) {
        this.game = game;
        this.gameScreen = gameScreen;
        lastGlobalDarkness = game.globalDarkness;
        this.knight = knight;
    }

    public void update(float delta) {
        if (phase == Phase.NONE) {
            if (deathToIdleTimer > 0f) {
                deathToIdleTimer -= delta;
                knight.resetAllStates();
                if (deathToIdleTimer <= 0f) deathToIdleTimer = 0;
                return;
            }
            if (knight.currentState == KnightState.DEAD) {
                phase = Phase.FADING_OUT;
                isSpikeRespawn = false;
                knight.needsRespawn = false;
                return;
            }

            if (knight.needsRespawn &&
                knight.currentState != KnightState.HIT) {
                isSpikeRespawn = true;
                knight.needsRespawn = false;
                phase = Phase.FADING_OUT;
            }
            return;
        }

        if (phase == Phase.FADING_OUT) {
            alpha = Math.min(1f, alpha + FADE_SPEED * delta);
            game.globalDarkness = alpha;
            if (alpha >= 1f) {
                if (isSpikeRespawn) knight.position.set(knight.lastSafePosition);
                else {
                    knight.currentState = KnightState.DEAD;
                    gameScreen.respawnAtCheckpoint();
                }
                phase = Phase.FADING_IN;
            } else knight.currentState = KnightState.HIT;
        } else if (phase == Phase.FADING_IN) {
            alpha = Math.max(lastGlobalDarkness, alpha - FADE_SPEED * delta);
            game.globalDarkness = alpha;
            knight.currentState = KnightState.HIT;
            if (alpha <= lastGlobalDarkness) {
                phase = Phase.NONE;
                game.globalDarkness = lastGlobalDarkness;
                deathToIdleTimer = 0.2f;
            }
        }
    }

    public boolean isActive() {
        return phase != Phase.NONE;
    }

    public static void setLastGlobalDarkness(float lastGlobalDarkness) {
    }
}
