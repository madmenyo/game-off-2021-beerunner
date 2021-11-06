package net.madmenyo.beerunner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.SphereShapeBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen extends ScreenAdapter {

    private PerspectiveCamera camera;
    private Viewport viewport;

    private FirstPersonCameraController fpsController;

    private SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;
    private Environment environment;
    private ModelBatch modelBatch;

    private AssetManager assetManager;

    // Tests, might need refactoring
    TrackGenerator trackGenerator;
    Player player;

    public GameScreen(AssetManager assetManager) {
        this.assetManager = assetManager;

        camera = new PerspectiveCamera(67, 1920, 1080);
        viewport = new ExtendViewport(1920, 1080, camera);

        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
        modelBatch = new ModelBatch();

        trackGenerator = new TrackGenerator();

        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        MeshPartBuilder meshBuilder;
        meshBuilder = modelBuilder.part("part1", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material());
        SphereShapeBuilder.build(meshBuilder, 2, 1, 2, 16, 12);
        player = new Player(new ModelInstance(modelBuilder.end()), trackGenerator);
    }


    @Override
    public void show() {
        setCamera();

        InputMultiplexer im = new InputMultiplexer(fpsController);
        Gdx.input.setInputProcessor(im);
    }

    private void setCamera() {
        camera.position.set(0, 5, -10);
        camera.lookAt(0, 0, 0);

        camera.near = .1f;
        camera.far = 600;

        camera.up.set(Vector3.Y);

        fpsController = new FirstPersonCameraController(camera);

        camera.update();
    }

    @Override
    public void render(float delta) {
        fpsController.update(delta * 20f);
        player.update(delta);

        ScreenUtils.clear(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();

        spriteBatch.end();

        modelBatch.begin(camera);
        modelBatch.render(player.getModelInstance(), environment);
        modelBatch.end();

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        // Debug drawing
        trackGenerator.getCurrentTrackSection().drawCurve(shapeRenderer);
        for (TrackSection ts : trackGenerator.getPreviousSections())
        {
            ts.drawCurve(shapeRenderer);
        }
        /*
        for (Vector3 p : trackGenerator.getCurrentTrackSection().dividePoints(10)){
            drawBox(p, 1);
        }*/
        shapeRenderer.end();

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    private void drawBox (Vector3 point, float size){
        shapeRenderer.box(point.x - size / 2f, point.y - size / 2f, point.z + size / 2f, size, size, size);
    }

}
