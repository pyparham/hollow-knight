package com.parham.hollowknight.view;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.parham.hollowknight.model.entities.VengefulSpirit;

public class SpellAnimationManager {

    private final Animation<TextureRegion> ballAnim;
    private final Animation<TextureRegion> blastAnim;

    private static final float BALL_DRAW_WIDTH = 317f;
    private static final float BALL_DRAW_HEIGHT = 143;

    private static final float BLAST_DRAW_WIDTH = 306f;
    private static final float BLAST_DRAW_HEIGHT = 289f;

    public SpellAnimationManager(Texture ballSheet, int ballFrames,
                                 Texture blastSheet, int blastFrames) {
        ballAnim = buildAnim(ballSheet, ballFrames, 0.08f, Animation.PlayMode.NORMAL);
        blastAnim = buildAnim(blastSheet, blastFrames, 0.06f, Animation.PlayMode.NORMAL);
    }

    public void draw(SpriteBatch batch, VengefulSpirit spell) {
        if (!spell.blastFinished) {
            TextureRegion blastFrame = blastAnim.getKeyFrame(spell.blastAnimTime);
            float bx = spell.blastPosition.x;
            float by = spell.blastPosition.y;

            if (spell.facingRight)
                batch.draw(blastFrame, bx, by, BLAST_DRAW_WIDTH, BLAST_DRAW_HEIGHT);
            else
                batch.draw(blastFrame, bx + BLAST_DRAW_WIDTH, by, -BLAST_DRAW_WIDTH, BLAST_DRAW_HEIGHT);

        }

        TextureRegion ballFrame = ballAnim.getKeyFrame(spell.ballAnimTime);
        float x = spell.position.x;
        float y = spell.position.y;

        if (spell.facingRight)
            batch.draw(ballFrame, x, y, BALL_DRAW_WIDTH, BALL_DRAW_HEIGHT);
        else
            batch.draw(ballFrame, x + BALL_DRAW_WIDTH, y, -BALL_DRAW_WIDTH, BALL_DRAW_HEIGHT);

    }

    private Animation<TextureRegion> buildAnim(Texture sheet, int frameCount,
                                               float frameDuration, Animation.PlayMode mode) {
        int fw = sheet.getWidth() / frameCount;
        int fh = sheet.getHeight();
        TextureRegion[] frames = new TextureRegion[frameCount];
        for (int i = 0; i < frameCount; i++)
            frames[i] = new TextureRegion(sheet, i * fw, 0, fw, fh);
        Animation<TextureRegion> anim = new Animation<>(frameDuration, frames);
        anim.setPlayMode(mode);
        return anim;
    }
}
