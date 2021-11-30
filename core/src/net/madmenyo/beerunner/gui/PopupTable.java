package net.madmenyo.beerunner.gui;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import net.madmenyo.beerunner.BeeRunner;
import net.madmenyo.beerunner.GameScreen;
import net.madmenyo.beerunner.Player;

public class PopupTable extends Table {
    private Player player;
    private BeeRunner beeRunner;

    public PopupTable(Skin skin, Player player, BeeRunner beeRunner) {
        super(skin);

        this.player = player;
        this.beeRunner = beeRunner;
    }

    public void show(String title, boolean gameOver, GuiStage guiStage) {
        clear();
        setVisible(true);

        Table t = new Table(getSkin());
        t.background("backtable");
        t.top();

        add(t);

        Label label = new Label(title, getSkin(), "title");
        t.add(label).padBottom(50).padTop(20).row();

        Table infoTable = new Table(getSkin());
        infoTable.add("Distance: ", "dark");
        infoTable.add(new Label((int)player.getTotalDistance() + "m", getSkin(), "dark"));

        infoTable.row().pad(5);

        infoTable.add("Flowers: ", "dark");
        infoTable.add(player.getFlowers() + "", "dark");

        t.add(infoTable).row();

        Table navTable = new Table();
        navTable.bottom();

        TextButton exitButton = new TextButton("Back", getSkin(), "button");
        exitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                beeRunner.gameMusic.stop();
                beeRunner.menuMusic.play();
                ((Game) Gdx.app.getApplicationListener()).setScreen(beeRunner.menuScreen);
            }
        });

        if (!gameOver){
            TextButton continueButton = new TextButton("continue", getSkin(), "button");
            continueButton.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    guiStage.setPause(false);
                    setVisible(false);
                }
            });
            navTable.add(continueButton).expandX();
        }



        TextButton replayButton = new TextButton("Replay", getSkin(), "button");
        replayButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                beeRunner.gameMusic.setPosition(0);
                ((Game) Gdx.app.getApplicationListener()).setScreen(new GameScreen(beeRunner));
            }
        });

        if (!gameOver){
            replayButton.setText("Restart");
        }



        navTable.add(exitButton).expandX();
        navTable.add(replayButton).expandX();

        t.add(navTable).padBottom(25).grow();



    }


}
