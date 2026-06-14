package com.parham.hollowknight.controller;

import com.parham.hollowknight.Main;

public class TextureManager {
    private final Main game;
    private static TextureManager textureManager;
    private float darkness;

    private TextureManager(Main game) {
        this.game = game;
        this.darkness = game.globalDarkness;
    }

    public static void init(Main game) {
        if (textureManager == null)
            textureManager = new TextureManager(game);
    }

    public static TextureManager getInstance() {
        return textureManager;
    }

    public void updateDarkness(float darkness) {
        this.darkness = darkness;
        game.globalDarkness = darkness * 0.7f;
        DeathController.setLastGlobalDarkness(darkness * 0.7f);
    }

    public float getDarkness() {
        return (1 - darkness) * 10;
    }

}
