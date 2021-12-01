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

import net.madmenyo.beerunner.Assets;
import net.madmenyo.beerunner.BeeRunner;
import net.madmenyo.beerunner.GameScreen;
import net.madmenyo.beerunner.Player;

public class GuiStage extends Stage {

    private Skin skin;
    private BeeRunner beeRunner;
    private GameScreen gameScreen;

    private Table mainTable = new Table();

    // Distance traveled, need to record for high scores
    private Label distance;

    // flying higher time (and shooting if I can hack it in in time) costs energy which renews
    // slowly automaticaly and by picking up flowers
    //private Label energy;
    //private ProgressBar energyBar;
    private EnergyBar energyBar;

    // Some form of currency in game?
    private Label flowers;

    private Player player;

    private Array<Image> hearts = new Array<>();

    private Drawable heart;
    private Drawable damage;

    private PopupTable popupTable;

    private ProfilerOverlay profilerOverlay;


    public GuiStage(Viewport viewport, Batch batch, Player player, BeeRunner beeRunner, GameScreen gameScreen) {
        super(viewport, batch);
        this.player = player;
        this.skin = beeRunner.assetManager.get(Assets.skin);
        this.beeRunner = beeRunner;
        this.gameScreen = gameScreen;

        addHud(player);
        addPopup(player, beeRunner);
        addProfileOverlay();
    }

    private void addHud(Player player) {
        heart = skin.getDrawable("heart");
        damage = skin.getDrawable("heart_out");

        addActor(mainTable);
        mainTable.setFillParent(true);
        mainTable.top();

        Table healthBar = new Table(skin);
        healthBar.background("panel");
        mainTable.add(healthBar).size(200, 60).expandX();
        for (int i = 0; i < player.getMaxLives(); i++) {
            Image heart = new Image(skin.getDrawable("heart"));
            healthBar.add(heart).pad(2).size(24);
            hearts.add(heart);
        }

        Table distanceTable = new Table(skin);
        distanceTable.background("panel");
        distanceTable.add("Distance: ", "yellow");
        distance = new Label("Distance:", skin, "yellow");
        distanceTable.add(distance);
        mainTable.add(distanceTable).size(getWidth() * .2f, 60).padTop(5).expandX();
        mainTable.padLeft(10);

        //table.debugAll();


        flowers = new Label("Flower: ", skin, "yellow");
        Table flowerTable = new Table(skin);
        flowerTable.background("panel");
        flowerTable.add("Flowers: ", "yellow");
        flowerTable.add(flowers);
        mainTable.add(flowerTable).expandX().size(200, 60).row();


        //energyBar = new ProgressBar(0, player.getMaxEnergy(), 1, false, skin);
        energyBar = new EnergyBar(skin, player);
        mainTable.add(energyBar).width(getWidth() * .2f).height(24).colspan(3).padTop(10).expandX();
    }

    private void addPopup(Player player, BeeRunner beeRunner) {
        popupTable = new PopupTable(skin, player, beeRunner);
        addActor(popupTable);
        popupTable.setFillParent(true);
        popupTable.setVisible(false);
    }

    private void addProfileOverlay() {
        profilerOverlay = new ProfilerOverlay(skin);
        profilerOverlay.setFillParent(true);
        addActor(profilerOverlay);
    }

    private boolean gameOver = false;

    @Override
    public void act() {
        super.act();

        flowers.setText(player.getFlowers());
        distance.setText((int)player.getTotalDistance());
        //energyBar.setValue(player.getEnergy());

        for (int i = 0; i < player.getMaxLives(); i++) {
            if (i < player.getLives()) hearts.get(i).setDrawable(heart);
            else hearts.get(i).setDrawable(damage);

        }

        if (!gameOver && player.getLives() <= 0){
            gameOver = true;
            popupTable.show("Game Over", true, this);
        }
    }

    public void setPause(boolean pause){
        gameScreen.setPause(pause);
        if (pause){
            popupTable.show("PAUSE", false, this);
        }else {
            popupTable.setVisible(false);
        }
    }

    public boolean isPaused(){
        return gameScreen.isPause();
    }
}
