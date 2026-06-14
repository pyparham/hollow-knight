package com.parham.hollowknight.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

// Big shoutout to Elyas Hajinezhad

public class BackGroundDust {
    private final Texture particleTexture;
    private final Array<DustParticle> particles;
    private Color dustColor = new Color(1f, 1f, 1f, 1f);

    public BackGroundDust() {

        Pixmap pixmap = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fillCircle(8, 8, 6);
        particleTexture = new Texture(pixmap);
        pixmap.dispose();

        particles = new Array<>();
        for (int i = 0; i < 1000; i++) {
            particles.add(new DustParticle());
        }
    }

    public void updateAndDraw(SpriteBatch batch, float delta, float brightness) {
        for (DustParticle p : particles) {
            p.update(delta);

            float findAlpha = p.alpha * brightness;
            batch.setColor(dustColor.r, dustColor.g, dustColor.b, findAlpha);
            batch.draw(particleTexture, p.x, p.y, p.size, p.size);
        }

        batch.setColor(Color.WHITE);
    }

    public void dispose() {
        if (particleTexture != null) particleTexture.dispose();
    }

    private static class DustParticle {
        float x, y;
        float speedY;
        float wobbleSpeed, wobbleAmount;
        float stateTime;
        float size;
        float maxAlpha;
        float alpha;

        public DustParticle() {
            reset(true);
        }

        public void reset(boolean randomY) {
            this.x = MathUtils.random(0, 1920 * 5);
            this.y = MathUtils.random(0, 1080 * 2);
            this.size = MathUtils.random(4f, 12f);
            this.speedY = MathUtils.random(10f, 40f);
            this.wobbleSpeed = MathUtils.random(0.5f, 2f);
            this.wobbleAmount = MathUtils.random(10f, 30f);
            this.maxAlpha = MathUtils.random(0.1f, 0.6f);
            this.stateTime = MathUtils.random(0f, 10f);
        }

        public void update(float delta) {
            stateTime += delta;
            y += speedY * delta;
            x += MathUtils.sin(stateTime * wobbleSpeed) * wobbleAmount * delta;

            alpha = maxAlpha * (0.5f + 0.5f * MathUtils.sin(stateTime * 1.5f));
//
//            if (y > 1100) {
//                reset(false);
//            }
        }
    }

    public void setDustColor(Color color) {
        this.dustColor.set(color);
    }

}
