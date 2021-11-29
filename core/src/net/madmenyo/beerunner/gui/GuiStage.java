package net.madmenyo.beerunner.gui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
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
    //private ProgressBar energyBar;
    private EnergyBar energyBar;

    // Some form of currency in game?
    private Label honey;

    private Player player;

    private Array<Image> hearts = new Array<>();

    private Drawable heart;
    private Drawable damage;


    public GuiStage(Viewport viewport, Batch batch, Player player, Skin skin) {
        super(viewport, batch);
        this.player = player;
        this.skin = skin;

        heart = skin.getDrawable("heart");
        damage = skin.getDrawable("heart_out");


        distance = new Label("Distance:", skin);
        //energyBar = new ProgressBar(0, player.getMaxEnergy(), 1, false, skin);
        energyBar = new EnergyBar(skin, player);
        honey = new Label("Honey: ", skin);

        addActor(table);
        table.setFillParent(true);
        table.left().top();

        table.padLeft(10);

        //table.debugAll();

        table.add(distance).expandX().left().row();


        Table healthBar = new Table();
        table.add(healthBar).left().expandX().row();
        for (int i = 0; i < player.getMaxLives(); i++) {
            Image heart = new Image(skin.getDrawable("heart"));
            healthBar.add(heart).pad(2).size(18);
            hearts.add(heart);
        }
        table.add(energyBar).width(getWidth() * .2f).height(18).left().row();
        table.add(honey).expandX().left();
    }

    @Override
    public void act() {
        super.act();

        honey.setText("Flowers: " + player.getFlowers());
        distance.setText("Distance: " + (int)player.getTotalDistance());
        //energyBar.setValue(player.getEnergy());

        for (int i = 0; i < player.getMaxLives(); i++) {
            System.out.println(player.getLives());
            if (i < player.getLives()) hearts.get(i).setDrawable(heart);
            else hearts.get(i).setDrawable(damage);

        }

    }
}
