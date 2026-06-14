package com.parham.hollowknight.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

import static com.parham.hollowknight.model.enums.PhysicsConstants.MAX_SOUL;


public class SoulOrbActor extends Actor {

    private static final int SHELL_FRAME_COUNT = 6;
    private static final float SHELL_IMG_W = 257f;
    private static final float SHELL_IMG_H = 164f;
    private static final float SHELL_GROW_DURATION = 0.75f;

    private static final float HOLE_X = 20f;
    private static final float HOLE_Y = 46f;
    private static final float HOLE_W = 105f;
    private static final float HOLE_H = 107f;

    private static final float EYE_THRESHOLD = 66f;
    private static final float ORB_FRAME_DUR = 0.06f;

    private enum OrbState {GROW, IDLE, SHRINK}

    private final Animation<TextureRegion> shellGrowAnim;
    private final Animation<TextureRegion> fillGrowAnim;
    private final Animation<TextureRegion> fillIdleAnim;
    private final Animation<TextureRegion> fillShrinkAnim;
    private final TextureRegion eyeRegion;

    private final Texture circleMaskTex;
    private final ShaderProgram circleMaskShader;

    private OrbState fillState = OrbState.IDLE;
    private float fillTime = 0f;

    private boolean introPlaying = false;
    private float introTime = 0f;
    private float eyeAlpha = 0f;

    private float lastSoul = 0f;
    private float currentSoul = 0f;

    public SoulOrbActor(Texture shellSheet, TextureAtlas soulOrbAtlas, Texture eyeTex, Texture circleMaskTex) {
        shellGrowAnim = buildEqualStrip(shellSheet, SHELL_FRAME_COUNT,
            SHELL_GROW_DURATION / SHELL_FRAME_COUNT, Animation.PlayMode.NORMAL);

        fillGrowAnim = buildFromAtlas(soulOrbAtlas, "HUD_Soulorb_fills_soul_grow", 8, Animation.PlayMode.NORMAL);
        fillIdleAnim = buildFromAtlas(soulOrbAtlas, "HUD_Soulorb_fills_soul_idle", 6, Animation.PlayMode.LOOP);
        fillShrinkAnim = buildFromAtlas(soulOrbAtlas, "HUD_Soulorb_fills_soul_shrink", 6, Animation.PlayMode.NORMAL);

        eyeRegion = new TextureRegion(eyeTex);
        this.circleMaskTex = circleMaskTex;

        circleMaskShader = buildCircleMaskShader();

        setSize(SHELL_IMG_W, SHELL_IMG_H);
        introTime = SHELL_GROW_DURATION;
    }

    public void playShellGrowIntro() {
        introPlaying = true;
        introTime = 0f;
    }

    public void setSoul(float soul) {
        currentSoul = soul;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (introPlaying) {
            introTime += delta;
            if (shellGrowAnim.isAnimationFinished(introTime)) introPlaying = false;
        }

        if (currentSoul > lastSoul + 0.01f) {
            if (fillState != OrbState.GROW) {
                fillState = OrbState.GROW;
                fillTime = 0f;
            }
        } else if (currentSoul < lastSoul - 0.01f) {
            if (fillState != OrbState.SHRINK) {
                fillState = OrbState.SHRINK;
                fillTime = 0f;
            }
        }
        lastSoul = currentSoul;

        fillTime += delta;
        if (fillState == OrbState.GROW && fillGrowAnim.isAnimationFinished(fillTime)) {
            fillState = OrbState.IDLE;
            fillTime = 0f;
        } else if (fillState == OrbState.SHRINK && fillShrinkAnim.isAnimationFinished(fillTime)) {
            fillState = OrbState.IDLE;
            fillTime = 0f;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float x = getX();
        float y = getY();
        float w = getWidth();
        float h = getHeight();
        float scaleX = w / SHELL_IMG_W;
        float scaleY = h / SHELL_IMG_H;

        batch.setColor(1f, 1f, 1f, parentAlpha);

        TextureRegion shellFrame = shellGrowAnim.getKeyFrame(introTime);
        batch.draw(shellFrame, x, y, w, h);

        Animation<TextureRegion> fillAnim = switch (fillState) {
            case GROW -> fillGrowAnim;
            case SHRINK -> fillShrinkAnim;
            default -> fillIdleAnim;
        };
        TextureRegion fillFrame = fillAnim.getKeyFrame(fillTime);

        float holeDrawW = HOLE_W * scaleX;
        float holeDrawH = HOLE_H * scaleY;
        float holeDrawX = x + HOLE_X * scaleX + 2f * scaleX;
        float holeDrawY = y + (SHELL_IMG_H - HOLE_Y - HOLE_H) * scaleY;

        float liquidW = holeDrawW * 1.58f;
        float liquidH = holeDrawH * 1.45f;

        float liquidX = holeDrawX - (liquidW - holeDrawW) / 2f;
        float liquidY = holeDrawY - (liquidH - holeDrawH) / 2f;

        float soulPercent = Math.max(0f, Math.min(1f, currentSoul / MAX_SOUL));
        if (currentSoul > 1f)
            drawMaskedLiquid(batch, fillFrame, liquidX, liquidY, liquidW, liquidH, parentAlpha, soulPercent);

        float eyeDrawW = 87f * scaleX;
        float eyeDrawH = 33f * scaleY;
        float eyeDrawX = holeDrawX + (holeDrawW - eyeDrawW) / 2f;
        float eyeDrawY = holeDrawY + 22f * scaleY;

        eyeAlpha = Math.max(0f, eyeAlpha - 0.02f);
        if (currentSoul >= EYE_THRESHOLD) eyeAlpha = Math.min(1f, eyeAlpha + 0.04f);

        batch.setColor(1f, 1f, 1f, eyeAlpha);
        batch.draw(eyeRegion, eyeDrawX, eyeDrawY, eyeDrawW, eyeDrawH);
        batch.setColor(1f, 1f, 1f, parentAlpha);

    }

    private void drawMaskedLiquid(
        Batch batch, TextureRegion liquidFrame,
        float x, float y, float w, float h, float parentAlpha, float soulPercentage
    ) {
        ShaderProgram previousShader = batch.getShader();

        batch.flush();
        batch.setShader(circleMaskShader);

        circleMaskShader.setUniformi("u_mask", 1);
        circleMaskShader.setUniformf("u_minU", liquidFrame.getU());
        circleMaskShader.setUniformf("u_maxU", liquidFrame.getU2());
        circleMaskShader.setUniformf("u_minV", liquidFrame.getV());
        circleMaskShader.setUniformf("u_maxV", liquidFrame.getV2());
        circleMaskShader.setUniformf("u_soulPercentage", soulPercentage);
        circleMaskShader.setUniformf("u_glowWeight", 0.0f);

        circleMaskShader.setUniformf("u_maskScaleX", 1.3f);
        circleMaskShader.setUniformf("u_maskScaleY", 1.3f);

        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE1);
        circleMaskTex.bind();
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);

        batch.setColor(1f, 1f, 1f, parentAlpha);
        batch.draw(liquidFrame, x, y, w, h);

        batch.flush();
        batch.setShader(previousShader);
    }

    private Animation<TextureRegion> buildFromAtlas(
        TextureAtlas atlas, String namePrefix, int frameCount, Animation.PlayMode mode
    ) {
        Array<TextureAtlas.AtlasRegion> frames = new Array<>();
        for (int i = 0; i < frameCount; i++) {
            String name = namePrefix + String.format("%04d", i);
            TextureAtlas.AtlasRegion region = atlas.findRegion(name);
            if (region == null) {
                throw new IllegalStateException("Atlas region not found: " + name +
                    " — check Soulorb.atlas contains this exact name.");
            }
            frames.add(region);
        }
        return new Animation<>(ORB_FRAME_DUR, frames, mode);
    }

    private Animation<TextureRegion> buildEqualStrip(
        Texture tex, int frameCount, float frameDur, Animation.PlayMode mode
    ) {
        TextureRegion[] frames = new TextureRegion[frameCount];
        int fw = tex.getWidth() / frameCount;
        int fh = tex.getHeight();
        for (int i = 0; i < frameCount; i++)
            frames[i] = new TextureRegion(tex, i * fw, 0, fw, fh);
        Animation<TextureRegion> anim = new Animation<>(frameDur, frames);
        anim.setPlayMode(mode);
        return anim;
    }

    private static final String VERTEX_SHADER =
        "attribute vec4 a_position;\n" +
            "attribute vec4 a_color;\n" +
            "attribute vec2 a_texCoord0;\n" +
            "uniform mat4 u_projTrans;\n" +
            "varying vec4 v_color;\n" +
            "varying vec2 v_texCoords;\n" +
            "void main() {\n" +
            "    v_color = a_color;\n" +
            "    v_texCoords = a_texCoord0;\n" +
            "    gl_Position = u_projTrans * a_position;\n" +
            "}\n";

    private static final String FRAGMENT_SHADER =
        "#ifdef GL_ES\n" +
            "precision mediump float;\n" +
            "#endif\n" +
            "\n" +
            "varying vec4 v_color;\n" +
            "varying vec2 v_texCoords;\n" +
            "\n" +
            "uniform sampler2D u_texture;\n" +
            "uniform sampler2D u_mask;\n" +
            "\n" +
            "uniform float u_minU;\n" +
            "uniform float u_maxU;\n" +
            "uniform float u_minV;\n" +
            "uniform float u_maxV;\n" +
            "uniform float u_soulPercentage;\n" +
            "uniform float u_glowWeight; \n" +
            "uniform float u_maskScaleX;\n" +
            "uniform float u_maskScaleY;\n" +
            "\n" +
            "void main() {\n" +
            "    float localX = (v_texCoords.x - u_minU) / (u_maxU - u_minU);\n" +
            "    float localY = (u_maxV - v_texCoords.y) / (u_maxV - u_minV);\n" +
            "    \n" +
            "    float maskX = (localX - 0.5) * u_maskScaleX + 0.5;\n" +
            "    float maskY = (localY - 0.5) * u_maskScaleY + 0.5;\n" +
            "    \n" +
            "    vec4 maskColor = vec4(0.0);\n" +
            "    if(maskX >= 0.0 && maskX <= 1.0 && maskY >= 0.0 && maskY <= 1.0) {\n" +
            "        maskColor = texture2D(u_mask, vec2(maskX, 1.0 - maskY));\n" +
            "    }\n" +
            "\n" +
            "    float adjustedPercentage = 0.25 + 0.75 * u_soulPercentage;\n" +
            "    float shiftedLocalY = localY + (1.0 - adjustedPercentage);\n" +
            "\n" +
            "    if (u_soulPercentage < 0.99) {\n" +
            "        if (shiftedLocalY > 1.0 && u_glowWeight < 1.0) {\n" +
            "            if (u_glowWeight <= 0.0) {\n" +
            "                discard;\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "\n" +
            "    float shiftedV = u_maxV - (shiftedLocalY * (u_maxV - u_minV));\n" +
            "    shiftedV = clamp(shiftedV, u_minV, u_maxV);\n" +
            "\n" +
            "    vec4 liquidColor = texture2D(u_texture, vec2(v_texCoords.x, shiftedV));\n" +
            "    vec4 finalLiquid = v_color * vec4(liquidColor.rgb, liquidColor.a * maskColor.a);\n" +
            "    vec4 finalFullOrb = v_color * maskColor;\n" +
            "\n" +
            "    gl_FragColor = mix(finalLiquid, finalFullOrb, u_glowWeight);\n" +
            "}";

    private ShaderProgram buildCircleMaskShader() {
        ShaderProgram.pedantic = false;
        ShaderProgram shader = new ShaderProgram(VERTEX_SHADER, FRAGMENT_SHADER);
        if (!shader.isCompiled()) {
            throw new IllegalStateException("Soul orb mask shader failed to compile: " + shader.getLog());
        }
        return shader;
    }

    public void dispose() {
        circleMaskShader.dispose();
    }
}
