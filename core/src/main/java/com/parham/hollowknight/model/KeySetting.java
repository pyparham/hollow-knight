package com.parham.hollowknight.model;

import com.badlogic.gdx.Input;

public class KeySetting {

    private static KeySetting instance;

    private int upKey = Input.Keys.W;
    private int downKey = Input.Keys.S;
    private int jumpKey = Input.Keys.SPACE;
    private int attackKey = Input.Buttons.LEFT;
    private int dashKey = Input.Keys.SHIFT_LEFT;
    private int focusKey = Input.Buttons.RIGHT;
    private int leftKey = Input.Keys.A;
    private int rightKey = Input.Keys.D;
    private int quickMapKey = Input.Keys.M;
    private int superDashKey = Input.Keys.X;
    private int dreamNailKey = Input.Keys.N;
    private int castKey = Input.Keys.C;
    private int inventoryKey = Input.Keys.I;

    private KeySetting() {
    }

    public static KeySetting getInstance() {
        if (instance == null) instance = new KeySetting();
        return instance;
    }

    public void resetDefaults() {
        upKey = Input.Keys.W;
        downKey = Input.Keys.S;
        jumpKey = Input.Keys.SPACE;
        attackKey = Input.Buttons.LEFT;
        dashKey = Input.Keys.SHIFT_LEFT;
        focusKey = Input.Buttons.RIGHT;
        leftKey = Input.Keys.A;
        rightKey = Input.Keys.D;
        quickMapKey = Input.Keys.M;
        superDashKey = Input.Keys.X;
        dreamNailKey = Input.Keys.N;
        castKey = Input.Keys.C;
        inventoryKey = Input.Keys.I;
    }

    public int getUpKey() {
        return upKey;
    }

    public void setUpKey(int upKey) {
        this.upKey = upKey;
    }

    public int getDownKey() {
        return downKey;
    }

    public void setDownKey(int downKey) {
        this.downKey = downKey;
    }

    public int getJumpKey() {
        return jumpKey;
    }

    public void setJumpKey(int jumpKey) {
        this.jumpKey = jumpKey;
    }

    public int getAttackKey() {
        return attackKey;
    }

    public void setAttackKey(int attackKey) {
        this.attackKey = attackKey;
    }

    public int getDashKey() {
        return dashKey;
    }

    public void setDashKey(int dashKey) {
        this.dashKey = dashKey;
    }

    public int getFocusKey() {
        return focusKey;
    }

    public void setFocusKey(int focusKey) {
        this.focusKey = focusKey;
    }

    public int getLeftKey() {
        return leftKey;
    }

    public void setLeftKey(int leftKey) {
        this.leftKey = leftKey;
    }

    public int getRightKey() {
        return rightKey;
    }

    public void setRightKey(int rightKey) {
        this.rightKey = rightKey;
    }

    public int getQuickMapKey() {
        return quickMapKey;
    }

    public void setQuickMapKey(int quickMapKey) {
        this.quickMapKey = quickMapKey;
    }

    public int getSuperDashKey() {
        return superDashKey;
    }

    public void setSuperDashKey(int superDashKey) {
        this.superDashKey = superDashKey;
    }

    public int getDreamNailKey() {
        return dreamNailKey;
    }

    public void setDreamNailKey(int dreamNailKey) {
        this.dreamNailKey = dreamNailKey;
    }

    public int getCastKey() {
        return castKey;
    }

    public void setCastKey(int castKey) {
        this.castKey = castKey;
    }

    public int getInventoryKey() {
        return inventoryKey;
    }

    public void setInventoryKey(int inventoryKey) {
        this.inventoryKey = inventoryKey;
    }
}
