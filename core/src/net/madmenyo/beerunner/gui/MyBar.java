package net.madmenyo.beerunner.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

public class MyBar extends WidgetGroup {

    private Skin skin;

    private Image frame;
    private Image fill;

    public MyBar(Skin skin) {
        this.skin = skin;

        fill  = new Image(skin.getDrawable("white"));
        fill.setColor(Color.TEAL);
        frame = new Image(skin.getDrawable("bar_back"));

    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

}
