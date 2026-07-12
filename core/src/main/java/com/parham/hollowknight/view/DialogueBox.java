package com.parham.hollowknight.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;

public class DialogueBox {
    private final Texture bgTexture;
    private final BitmapFont font;

    private boolean isVisible = false;
    private String targetText = "";
    private String currentText = "";

    private float timer = 0f;
    private static final float CHAR_TIME = 0.03f;
    private int charIndex = 0;

    public DialogueBox(BitmapFont font) {
        this.font = font;

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0f, 0f, 0f, 0.8f));
        pixmap.fill();
        bgTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    public void show(String text) {
        this.targetText = text;
        this.currentText = "";
        this.charIndex = 0;
        this.timer = 0f;
        this.isVisible = true;
    }

    public void hide() {
        this.isVisible = false;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void update(float delta) {
        if (!isVisible || charIndex >= targetText.length()) return;

        timer += delta;
        if (timer >= CHAR_TIME) {
            timer = 0f;
            charIndex++;
            currentText = targetText.substring(0, charIndex);
        }
    }

    public void draw(SpriteBatch batch, float x, float y, float width, float height) {
        if (!isVisible) return;

        batch.draw(bgTexture, x, y, width, height);

        font.setColor(Color.WHITE);
        font.draw(batch, currentText, x + 30, y + height - 30, width - 60, Align.left, true);
    }

    public boolean handleAdvanceInput() {
        if (charIndex < targetText.length()) {
            charIndex = targetText.length();
            currentText = targetText;
            return false;
        }
        return true;
    }

    public void dispose() {
        if (bgTexture != null) bgTexture.dispose();
    }
}
