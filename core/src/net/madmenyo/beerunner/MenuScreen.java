package net.madmenyo.beerunner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MenuScreen extends ScreenAdapter {
    private AssetManager assetManager;
    private Skin skin;

    private Viewport vp;
    private Stage stage;

    private Image background;

    public MenuScreen(AssetManager assetManager) {
        this.assetManager = assetManager;
        skin = assetManager.get(Assets.skin);

        //vp = new StretchViewport(1280, 720);
        //vp = new FillViewport(1280, 720);
        vp = new ScreenViewport();
        stage = new Stage(vp);

    }

    @Override
    public void show() {
        background = new Image(assetManager.get(Assets.background));
        background.setFillParent(true);

        stage.addActor(background);

        addButtons();
    }

    private void addButtons() {
        TextButton playButton = new TextButton("PLAY", skin);
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
