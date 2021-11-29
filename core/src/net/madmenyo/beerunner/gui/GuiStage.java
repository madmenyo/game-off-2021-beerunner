package net.madmenyo.beerunner.gui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.madmenyo.beerunner.Player;

public class GuiStage extends Stage {

    private Skin skin;

    Table table = new Table();

    // Distance traveled, need to record for high scores
    private Label distance;

    // flying higher time (and shooting if I can hack it in in time) costs energy which renews
    // slowly automaticaly and by picking up flowers
    //private Label energy;
    private ProgressBar energyBar;

    // Some form of currency in game?
    private Label honey;

    private Player player;

    public GuiStage(Viewport viewport, Batch batch, Player player, Skin skin) {
        super(viewport, batch);
        this.player = player;
        this.skin = skin;



        distance = new Label("Distance:", skin);
        energyBar = new ProgressBar(0, player.getMaxEnergy(), 1, false, skin);
        honey = new Label("Honey: ", skin);

        addActor(table);
        table.setFillParent(true);
        table.left().top();

        //table.debugAll();

        table.add(distance).expandX().left().row();
        table.add(energyBar).width(getWidth() * .2f).height(40).left().row();
        table.add(honey).expandX().left();
    }

    @Override
    public void act() {
        super.act();

        honey.setText("Flowers: " + player.getFlowers());
        distance.setText("Distance: " + (int)player.getTotalDistance());
        energyBar.setValue(player.getEnergy());
    }
}
