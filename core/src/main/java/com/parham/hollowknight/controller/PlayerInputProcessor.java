package com.parham.hollowknight.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.parham.hollowknight.model.KeySetting;
import com.parham.hollowknight.model.entities.Knight;

public class PlayerInputProcessor implements InputProcessor {

    private final Knight knight;
    private final KeySetting keys = KeySetting.getInstance();

    public PlayerInputProcessor(Knight knight) {
        this.knight = knight;
    }


    @Override
    public boolean keyDown(int keycode) {
        if (keycode == keys.getLeftKey()) knight.wantsLeft = true;
        if (keycode == keys.getRightKey()) knight.wantsRight = true;
        if (keycode == keys.getJumpKey()) {
            knight.wantsJump = true;
            knight.holdingJump = true;
        }
        if (keycode == keys.getDashKey()) knight.wantsDash = true;
        if (keycode == keys.getAttackKey()) {
            knight.wantsAttack = true;
            if (Gdx.input.isKeyPressed(keys.getUpKey()))
                knight.wantsAttackUp = true;
            else if (Gdx.input.isKeyPressed(keys.getDownKey()) && !knight.onGround)
                knight.wantsAttackDown = true;

        }
        if (keycode == keys.getFocusKey()) knight.wantsHeal = true;
        if (keycode == keys.getUpKey()) knight.wantsLookUp = true;
        if (keycode == keys.getDownKey()) knight.wantsLookDown = true;
        if (keycode == keys.getCastKey()) knight.wantsCast = true;
        return false;
    }


    @Override
    public boolean keyUp(int keycode) {
        if (keycode == keys.getLeftKey()) knight.wantsLeft = false;
        if (keycode == keys.getRightKey()) knight.wantsRight = false;
        if (keycode == keys.getJumpKey()) knight.holdingJump = false;
        if (keycode == keys.getFocusKey()) knight.wantsHeal = false;
        if (keycode == keys.getUpKey()) knight.wantsLookUp = false;
        if (keycode == keys.getDownKey()) knight.wantsLookDown = false;
        return false;
    }

    @Override
    public boolean keyTyped(char c) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == keys.getLeftKey()) knight.wantsLeft = true;
        if (button == keys.getRightKey()) knight.wantsRight = true;
        if (button == keys.getJumpKey()) {
            knight.wantsJump = true;
            knight.holdingJump = true;
        }
        if (button == keys.getDashKey()) knight.wantsDash = true;
        if (button == keys.getAttackKey()) {
            knight.wantsAttack = true;
            if (Gdx.input.isKeyPressed(keys.getUpKey()))
                knight.wantsAttackUp = true;
            else if (Gdx.input.isKeyPressed(keys.getDownKey()) && !knight.onGround)
                knight.wantsAttackDown = true;
        }
        if (button == keys.getFocusKey()) knight.wantsHeal = true;
        if (button == keys.getUpKey()) knight.wantsLookUp = true;
        if (button == keys.getDownKey()) knight.wantsLookDown = true;
        if (button == keys.getCastKey()) knight.wantsCast = true;


        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == keys.getLeftKey()) knight.wantsLeft = false;
        if (button == keys.getRightKey()) knight.wantsRight = false;
        if (button == keys.getJumpKey()) knight.holdingJump = false;
        if (button == keys.getFocusKey()) knight.wantsHeal = false;
        if (button == keys.getUpKey()) knight.wantsLookUp = false;
        if (button == keys.getDownKey()) knight.wantsLookDown = false;
        if (button == keys.getCastKey()) knight.wantsCast = false;

        return true;
    }

    @Override
    public boolean touchCancelled(int x, int y, int p, int b) {
        return false;
    }

    @Override
    public boolean touchDragged(int x, int y, int p) {
        return false;
    }

    @Override
    public boolean mouseMoved(int x, int y) {
        return false;
    }

    @Override
    public boolean scrolled(float ax, float ay) {
        return false;
    }
}
