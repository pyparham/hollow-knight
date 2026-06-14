package com.parham.hollowknight.controller.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.parham.hollowknight.Main;
import com.parham.hollowknight.controller.AudioManager;

public class MenuPointerManager {
    private Main game;
    private final Image leftPointer;
    private final Image rightPointer;
    private final float padding = 25f;

    public MenuPointerManager(Stage stage, Main game) {

        Texture pointerTex = game.assets.menuPointer;
        pointerTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        leftPointer = new Image(pointerTex);
        rightPointer = new Image(pointerTex);

        leftPointer.setSize(40, 40);
        rightPointer.setSize(40, 40);
        rightPointer.setOrigin(rightPointer.getWidth() / 2f, rightPointer.getHeight() / 2f);
        rightPointer.setScaleX(-1f);

        stage.addActor(leftPointer);
        stage.addActor(rightPointer);
        hide();
    }

    public void updatePosition(Actor activeBtn) {

        AudioManager.getInstance().playClickSound();

        float leftX = activeBtn.getX() - leftPointer.getWidth() - padding;
        float leftY = activeBtn.getY() + (activeBtn.getHeight() - leftPointer.getHeight()) / 2f;
        float rightX = activeBtn.getX() + activeBtn.getWidth() + padding;

        leftPointer.setPosition(leftX, leftY);
        rightPointer.setPosition(rightX, leftY);
        leftPointer.setVisible(true);
        rightPointer.setVisible(true);
    }

    public void hide() {
        leftPointer.setVisible(false);
        rightPointer.setVisible(false);
    }

}
