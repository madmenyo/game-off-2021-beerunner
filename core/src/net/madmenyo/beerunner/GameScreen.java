package net.madmenyo.beerunner;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen extends ScreenAdapter {

    private PerspectiveCamera camera;
    private Viewport viewport;

    private SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;

    private ICurveGenerator curveGenerator;

    private AssetManager assetManager;

    public GameScreen(AssetManager assetManager) {
        this.assetManager = assetManager;

        camera = new PerspectiveCamera(67, 1920, 1080);
        viewport = new ExtendViewport(1920, 1080, camera);
    }
}
