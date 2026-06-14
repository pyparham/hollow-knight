package com.parham.hollowknight.controller.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.parham.hollowknight.Main;

public class SliderBar extends Slider {

    public SliderBar(float min, float max, float step, Main game) {
        super(min, max, step, false, buildHollowStyle(game));
    }

    private static SliderStyle buildHollowStyle(Main game) {
        SliderStyle style = new SliderStyle();

        Pixmap bgPixmap = new Pixmap(1, 4, Pixmap.Format.RGBA8888);
        bgPixmap.setColor(new Color(0.25f, 0.25f, 0.25f, 1f));
        bgPixmap.fill();
        Texture bgTexture = new Texture(bgPixmap);
        bgPixmap.dispose();
        style.background = new TextureRegionDrawable(new TextureRegion(bgTexture));

        Pixmap beforePixmap = new Pixmap(1, 4, Pixmap.Format.RGBA8888);
        beforePixmap.setColor(Color.WHITE);
        beforePixmap.fill();
        Texture beforeTexture = new Texture(beforePixmap);
        beforePixmap.dispose();
        style.knobBefore = new TextureRegionDrawable(new TextureRegion(beforeTexture));

        TextureRegionDrawable knobDrawable = new TextureRegionDrawable(new TextureRegion(game.assets.sliderArrow));

        knobDrawable.setBottomHeight(-12f);
        style.knob = knobDrawable;

        return style;
    }
}
