package net.madmenyo.beerunner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.JsonReader;
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

    FollowCam followCam;

    BitmapFont font;

    DirectionalShadowLight shadowLight;

    ModelBatch shadowBatch;

    Vector3 tmp = new Vector3();

    public GameScreen(AssetManager assetManager) {
        this.assetManager = assetManager;

        camera = new PerspectiveCamera(67, 1920, 1080);
        viewport = new ExtendViewport(1920, 1080, camera);

        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));

        //DirectionalLight light = new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f);
        /*
        shadowLight = new DirectionalShadowLight(
                1048, 1048,
                viewport.getWorldWidth(), viewport.getWorldHeight(),
                1, 300);
         */

        environment.add((shadowLight = new DirectionalShadowLight(2048, 2048,
                600, 600,
                .1f, 2000f))
                .set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
                //.set(1f, 1f, 1f, 40.0f, -35f, -35f));
        environment.shadowMap = shadowLight;





        shadowBatch = new ModelBatch(new DepthShaderProvider());
        modelBatch = new ModelBatch();

        trackGenerator = new TrackGenerator(assetManager);

        /*
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        MeshPartBuilder meshBuilder;
        meshBuilder = modelBuilder.part("part1", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material());
        SphereShapeBuilder.build(meshBuilder, 2, 1, 2, 16, 12);
        player = new Player(new ModelInstance(modelBuilder.end()), trackGenerator);

         */

        G3dModelLoader modelLoader = new G3dModelLoader(new JsonReader());
        player = new Player(new ModelInstance(modelLoader.loadModel(Gdx.files.internal("models/bee.g3dj"))), trackGenerator);

        followCam = new FollowCam(camera, player, trackGenerator);

        font = new BitmapFont(Gdx.files.internal("gui/default.fnt"));
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
        //fpsController.update(delta * 20f);
        followCam.update(delta);
        player.update(delta);
        //trackGenerator.setCameraBehind(camera, player, shapeRenderer);

        ScreenUtils.clear(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);


        //create shadow texture
        shadowLight.begin(tmp.set(camera.position), camera.direction);
        shadowBatch.begin(shadowLight.getCamera());

        shadowBatch.render(trackGenerator.getCurrentTrackSection().getTrack(), environment);
        shadowBatch.render(player.getModelInstance(), environment);
        shadowBatch.render(trackGenerator.getNextSection().getTrack(), environment);


        for (ModelInstance modelInstance : trackGenerator.getCurrentTrackSection().getSideObjects()){
            shadowBatch.render(modelInstance, environment);
        }

        for (ModelInstance modelInstance : trackGenerator.getNextSection().getSideObjects()){
            shadowBatch.render(modelInstance, environment);
        }

        for (TrackSection track : trackGenerator.getPreviousSections()){
            shadowBatch.render(track.getTrack(), environment);
            for (ModelInstance modelInstance : track.getSideObjects()){
                shadowBatch.render(modelInstance, environment);
            }
        }
        //shadowBatch.render(instances);

        shadowBatch.end();
        shadowLight.end();

        modelBatch.begin(camera);
        modelBatch.render(trackGenerator.getCurrentTrackSection().getTrack(), environment);
        modelBatch.render(player.getModelInstance(), environment);
        modelBatch.render(trackGenerator.getNextSection().getTrack(), environment);

        for (ModelInstance modelInstance : trackGenerator.getCurrentTrackSection().getSideObjects()){
            modelBatch.render(modelInstance, environment);
        }

        for (ModelInstance modelInstance : trackGenerator.getNextSection().getSideObjects()){
            modelBatch.render(modelInstance, environment);
        }

        for (TrackSection track : trackGenerator.getPreviousSections()){
            modelBatch.render(track.getTrack(), environment);
            for (ModelInstance modelInstance : track.getSideObjects()){
                modelBatch.render(modelInstance, environment);
            }
        }



        for (CollisionObject object : trackGenerator.getCurrentTrackSection().getCollisionObjects()){

            if (player.getBounds().intersects(object.getBounds())){
                object.onCollision();

                System.out.println("Player: " + player.getBounds());
                System.out.println("Object: " + object.getBounds());
            }
            object.draw(modelBatch, environment);
            //modelBatch.render(object.getModelInstance(), environment);
        }



        modelBatch.end();

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        /*

        shapeRenderer.setColor(Color.WHITE);
        // Debug drawing
        trackGenerator.getCurrentTrackSection().drawCurve(shapeRenderer);
        for (TrackSection ts : trackGenerator.getPreviousSections())
        {
            ts.drawCurve(shapeRenderer);

            for (Vector3 p : ts.divideByLookup(10)){
                drawBox(p, 1);
            }
        }

        // Division of curves
        for (Vector3 p : trackGenerator.getCurrentTrackSection().divideByLookup(10)){
            drawBox(p, 1);
        }

        float t =  trackGenerator.getCurrentTrackSection().getCurve().approximate(tmp.set(0 , 0, -10));


        shapeRenderer.setColor(Color.GREEN);
        drawBox(trackGenerator.getCurrentTrackSection().getCurve().valueAt(tmp, t), 1);

        shapeRenderer.setColor(Color.CYAN);
        trackGenerator.getCurrentTrackSection().debugDrawVerts(shapeRenderer);

        for (TrackSection ts : trackGenerator.getPreviousSections())
        {
            ts.debugDrawVerts(shapeRenderer);
        }


        shapeRenderer.setColor(Color.GREEN);
        for (TrackSection ts : trackGenerator.getPreviousSections())
        {
            ts.debugDrawDerivative(shapeRenderer);
        }
         */

        shapeRenderer.setColor(Color.YELLOW);
        //player.getBounds().mul(player.getModelInstance().transform).getCorner000(tmp);
        player.getBounds().getCorner000(tmp);
        shapeRenderer.box(tmp.x, tmp.y, tmp.z, player.getBounds().getWidth(), player.getBounds().getHeight(), -player.getBounds().getDepth());

        shapeRenderer.end();

        spriteBatch.begin();

        //String formattedString = String.format("Distance: %.1f", (player.getTotalDistance() * .05f));
        //font.draw(spriteBatch, formattedString, 10, 30);
        font.draw(spriteBatch, "" + (int)(player.getTotalDistance() * .08f), 10, 30);

        spriteBatch.end();


    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    private void drawBox (Vector3 point, float size){
        shapeRenderer.box(point.x - size / 2f, point.y - size / 2f, point.z + size / 2f, size, size, size);
    }

}
