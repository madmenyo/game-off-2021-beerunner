package net.madmenyo.beerunner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.ConeShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.SphereShapeBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen extends ScreenAdapter {

    private PerspectiveCamera camera;
    private Viewport viewport;

    private CameraInputController cameraInputController;

    private SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;

    private BezierGenerator curveGenerator;
    private TrackPiece trackPiece;

    private AssetManager assetManager;

    private Environment environment;
    private ModelBatch modelBatch;

    private ModelInstance track;

    ModelBuilder modelBuilder = new ModelBuilder();
    ModelInstance test;


    public GameScreen(AssetManager assetManager) {
        this.assetManager = assetManager;

        camera = new PerspectiveCamera(67, 1920, 1080);
        viewport = new ExtendViewport(1920, 1080, camera);

        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);


        curveGenerator = new BezierGenerator();
        trackPiece = new TrackPiece(curveGenerator.generateTrack());
        track = trackPiece.getInstance();
        //mesh = trackPiece.generateMesh(20, 5, 3);
        modelBatch = new ModelBatch();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        modelBuilder.begin();
        MeshPartBuilder meshBuilder;
        meshBuilder = modelBuilder.part("part1", GL20.GL_LINES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material());
        ConeShapeBuilder.build(meshBuilder, 5, 5, 5, 10);
        //meshBuilder.cone(5, 5, 5, 10);
        Node node = modelBuilder.node();
        node.translation.set(10,0,0);
        meshBuilder = modelBuilder.part("part2", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material());
        //meshBuilder.sphere(5, 5, 5, 10, 10);
        SphereShapeBuilder.build(meshBuilder,5, 5, 5, 10, 10);
        test = new ModelInstance(modelBuilder.end());

    }

    @Override
    public void show() {
        setCamera();

        Gdx.input.setInputProcessor(cameraInputController);
    }

    private void setCamera() {
        camera.position.set(0, 5, -10);
        camera.lookAt(0, 0, 0);

        camera.near = .1f;
        camera.far = 600;

        cameraInputController = new CameraInputController(camera);

        camera.update();
    }

    @Override
    public void render(float delta) {
        cameraInputController.update();

        ScreenUtils.clear(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        spriteBatch.setProjectionMatrix(camera.combined);

        modelBatch.begin(camera);
        //modelBatch.render(test, environment);
        modelBatch.render(track, environment);
        modelBatch.end();




        shapeRenderer.setProjectionMatrix(camera.combined);


        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        trackPiece.drawVerts(shapeRenderer);

        curveGenerator.drawCurve(curveGenerator.generateTrack(), 5, shapeRenderer);



        shapeRenderer.end();





    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

}
