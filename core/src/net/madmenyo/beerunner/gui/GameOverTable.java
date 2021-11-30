package net.madmenyo.beerunner.gui;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import net.madmenyo.beerunner.BeeRunner;
import net.madmenyo.beerunner.GameScreen;
import net.madmenyo.beerunner.Player;

public class GameOverTable extends Table {
    private Player player;
    private BeeRunner beeRunner;

    public GameOverTable(Skin skin, Player player, BeeRunner beeRunner) {
        super(skin);

        this.player = player;
        this.beeRunner = beeRunner;
    }

    public void show() {
        setVisible(true);

        Table t = new Table(getSkin());
        t.background("backtable");
        t.top();

        add(t);

        Label label = new Label("GAME OVER", getSkin(), "title");
        t.add(label).padBottom(50).padTop(20).row();

        Table infoTable = new Table(getSkin());
        infoTable.add("Distance: ", "dark");
        System.out.println(player.getTotalDistance());
        infoTable.add(new Label(315 + "m", getSkin(), "dark"));
        System.out.println(player.getTotalDistance());

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


        TextButton replayButton = new TextButton("Replay", getSkin(), "button");
        replayButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                beeRunner.gameMusic.setPosition(0);
                ((Game) Gdx.app.getApplicationListener()).setScreen(new GameScreen(beeRunner));
            }
        });


        navTable.add(exitButton).expandX();
        navTable.add(replayButton).expandX();

        t.add(navTable).padBottom(25).grow();



    }


}
