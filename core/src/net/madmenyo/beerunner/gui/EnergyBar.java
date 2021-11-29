package net.madmenyo.beerunner.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;

import net.madmenyo.beerunner.Player;


public class EnergyBar extends Actor {

    private NinePatch frame;
    private NinePatch fill;

    private Player player;
    private float progress = 0.4f;

    private Color lowOnEnergyColor = new Color(.4f, .4f,.1f, 1);

    public EnergyBar(Skin skin, Player player) {
        this.player = player;

        fill  = skin.getPatch("white");
        frame = skin.getPatch("frame");


    }

    @Override
    public void act(float delta) {
        super.act(delta);
        progress = player.getEnergy() / player.getMaxEnergy();

        if (player.isDepleted()) fill.setColor(lowOnEnergyColor);
        else fill.setColor(Color.YELLOW);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        fill.draw(batch, getX() + 2, getY() + 2,  (getWidth() - 4) * progress * getScaleX(), (getHeight() - 4) * getScaleY());
        frame.draw(batch, getX(), getY(), getWidth() * getScaleX(), getHeight() * getScaleY());
    }

}
