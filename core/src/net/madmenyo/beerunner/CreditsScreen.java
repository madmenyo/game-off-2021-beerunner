package net.madmenyo.beerunner;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class CreditsScreen extends ScreenAdapter {
    private BeeRunner beeRunner;
    private Viewport vp;
    private Stage stage;

    private Skin skin;

    private Table mainTable = new Table();

    public CreditsScreen(BeeRunner beeRunner) {
        this.beeRunner = beeRunner;

        skin = beeRunner.assetManager.get(Assets.skin);

        vp = new ScreenViewport();
        stage = new Stage(vp);
    }

    @Override
    public void show() {
        stage.addActor(mainTable);
        mainTable.setFillParent(true);

        addCredit(" You", " For playing my game <3", mainTable);
        addCredit(" Wooden button backgrounds ", " https://nl.freepik.com/vectoren/pijl ", mainTable);

        TextButton backButton = new TextButton("Back", skin, "main");
        mainTable.add(backButton).padTop(50);
        backButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                beeRunner.assetManager.get(Assets.click).play(0.5f);
                ((Game) Gdx.app.getApplicationListener()).setScreen(beeRunner.menuScreen);
            }
        });

        Gdx.input.setInputProcessor(stage);
    }

    private void addCredit(String asset, String info, Table creditsTable){


        Label assetLabel = new Label(asset, skin, "dark");
        Label infoLabel = new Label(info, skin, "dark");

        creditsTable.add(assetLabel).pad(5).fillX();
        creditsTable.add(infoLabel).pad(5).fillX();
        creditsTable.row();

    }

    @Override
    public void render(float delta) {
        stage.act();
        ScreenUtils.clear(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        vp.update(width, height);
    }

}
