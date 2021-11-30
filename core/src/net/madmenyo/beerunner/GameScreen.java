package net.madmenyo.beerunner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.CubemapAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.madmenyo.beerunner.gui.GuiStage;

public class GameScreen extends ScreenAdapter {

    private PerspectiveCamera camera;
    private Viewport viewport;

    private FirstPersonCameraController fpsController;

    private SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;
    private Environment environment;
    private ModelBatch modelBatch;

    private BeeRunner beeRunner;

    private GuiStage gui;

    private PlayerController playerController;

    private Sound beeSound;
    private long beeSoundId;

    private boolean pause= false;

    // Tests, might need refactoring
    TrackGenerator trackGenerator;
    Player player;

    FollowCam followCam;

    BitmapFont font;

    DirectionalShadowLight shadowLight;

    ModelBatch shadowBatch;

    Vector3 tmp = new Vector3();

    EnvironmentCubemap cube;

    ModelBuilder mb = new ModelBuilder();
    ModelInstance envMap;

    public GameScreen(BeeRunner beeRunner) {
        this.beeRunner = beeRunner;

        beeSound = beeRunner.assetManager.get(Assets.bee);
        beeSoundId = beeSound.loop(.06f);


        camera = new PerspectiveCamera(67, 1920, 1080);
        viewport = new ExtendViewport(1920, 1080, camera);

        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);

        Cubemap cubemap = getCubemap();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f),
                new CubemapAttribute(CubemapAttribute.EnvironmentMap,  cubemap));

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

        trackGenerator = new TrackGenerator(beeRunner.assetManager);


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

        gui = new GuiStage(new ExtendViewport(1280, 720), spriteBatch, player, beeRunner, this);

        playerController = new PlayerController(player, gui);

        font = new BitmapFont(Gdx.files.internal("gui/default.fnt"));
    }

    private Cubemap getCubemap() {
        int attr = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates;
        mb.begin();
        mb.part("front", GL20.GL_TRIANGLES, attr, new Material(TextureAttribute.createDiffuse(new Texture("models/cubemap/front.png"))))
                .rect(
                        -2f,-2f,-2f,
                        2f,-2f,-2,
                        2f,2f,-2f,
                        -2f,2f,-2f,
                        0,0,1);
        mb.part("back", GL20.GL_TRIANGLES, attr, new Material(TextureAttribute.createDiffuse(new Texture("models/cubemap/back.png"))))
                .rect(
                        2f,-2f,2f,
                        -2f,-2f,2f,
                        -2f,2f,2f,
                        2f,2f,2f,
                        0,0,1);
        mb.part("bottom", GL20.GL_TRIANGLES, attr, new Material(TextureAttribute.createDiffuse(new Texture("models/cubemap/bottom.png"))))
                .rect(
                        2f,-2f,2f,
                        2f,-2f,-2f,
                        -2f,-2f,-2f,
                        -2f,-2f,2f,
                        0,1,0);
        mb.part("top", GL20.GL_TRIANGLES, attr, new Material(TextureAttribute.createDiffuse(new Texture("models/cubemap/top.png"))))
                .rect(
                        2f,2f,2f,
                        -2f,2f,2f,
                        -2f,2f,-2f,
                        2f,2f,-2f,
                        0,-1,0);
        mb.part("right", GL20.GL_TRIANGLES, attr, new Material(TextureAttribute.createDiffuse(new Texture("models/cubemap/right.png"))))
                .rect(
                        -2f,-2f,2f,
                        -2f,-2f,-2f,
                        -2f,2f,-2f,
                        -2f,2f,2f,
                        1,0,0);
        mb.part("left", GL20.GL_TRIANGLES, attr, new Material(TextureAttribute.createDiffuse(new Texture("models/cubemap/left.png"))))
                .rect(
                        2f,-2f,-2f,
                        2f,-2f,2f,
                        2f,2f,2f,
                        2f,2f,-2f,
                        -1,0,0);
        envMap = new ModelInstance(mb.end());
        envMap.transform.scl(200);

        cube = new EnvironmentCubemap(
                Gdx.files.internal("models/cubemap/left.png"),
                Gdx.files.internal("models/cubemap/right.png"),
                Gdx.files.internal("models/cubemap/top.png"),
                Gdx.files.internal("models/cubemap/bottom.png"),
                Gdx.files.internal("models/cubemap/back.png"),
                Gdx.files.internal("models/cubemap/front.png")
        );

        Cubemap cubemap = new Cubemap(
                Gdx.files.internal("models/cubemap/left.png"),
                Gdx.files.internal("models/cubemap/right.png"),
                Gdx.files.internal("models/cubemap/top.png"),
                Gdx.files.internal("models/cubemap/bottom.png"),
                Gdx.files.internal("models/cubemap/back.png"),
                Gdx.files.internal("models/cubemap/front.png")
        );
        return cubemap;
    }


    @Override
    public void show() {
        setCamera();

        //InputMultiplexer im = new InputMultiplexer(fpsController);
        //Gdx.input.setInputProcessor(im);

        beeRunner.menuMusic.setVolume(0);
        beeRunner.gameMusic.setPosition(0);
        beeRunner.gameMusic.setVolume(.6f);
        beeRunner.gameMusic.play();

        InputMultiplexer im = new InputMultiplexer(gui, playerController, new GestureDetector(playerController));
        Gdx.input.setInputProcessor(im);
    }


    private void setCamera() {
        camera.position.set(0, 5, -10);
        camera.lookAt(0, 0, 0);

        camera.near = .1f;
        camera.far = 2000;

        camera.up.set(Vector3.Y);

        fpsController = new FirstPersonCameraController(camera);

        camera.update();
    }

    @Override
    public void render(float delta) {
        if (!pause) {
            //fpsController.update(delta * 20f);
            followCam.update(delta);
            player.update(delta);
            envMap.transform.set(camera.position, new Quaternion(), new Vector3(500, 500, 500));
            //trackGenerator.setCameraBehind(camera, player, shapeRenderer);

            float volume = player.getHeight() / player.getMaxHeight();
            beeSound.setVolume(beeSoundId, volume * .6f);

            playerController.update(delta);

            for (CollisionObject colObjects : trackGenerator.getCurrentTrackSection().getCollisionObjects()) {
                colObjects.update(delta);
            }


            // Handle collisions
            trackCollision();
        } else {
            beeSound.setVolume(beeSoundId, 0);
        }

        //Update gui after game logic update and also on pause
        gui.act();

        ScreenUtils.clear(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        //cube.render(camera);

        modelBatch.begin(camera);

        modelBatch.render(envMap);

        modelBatch.end();

        renderShadowPass();

        renderTrackPass();



        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        boolean hit = false;

        //player.getBounds().mul(player.getModelInstance().transform).getCorner000(tmp);
        shapeRenderer.setColor(Color.YELLOW);
        for (CollisionObject object : trackGenerator.getCurrentTrackSection().getCollisionObjects()){
            //object.drawBounds(shapeRenderer);
            //object.getBounds().getCorner000(tmp);
            //shapeRenderer.box(tmp.x, tmp.y, tmp.z, object.getBounds().getWidth(), object.getBounds().getHeight(), -object.getBounds().getDepth());

            if (player.getBounds().intersects(object.getBounds())){
                if (object instanceof Obstacle && !((Obstacle)object).isHit())
                    hit = true;
            }

        }

        if (hit) shapeRenderer.setColor(Color.RED);
        else {
            shapeRenderer.setColor(Color.GREEN);
        }
        //player.drawBounds(shapeRenderer);

        /*
        player.getBounds().getCorner000(tmp);
        shapeRenderer.box(tmp.x, tmp.y, tmp.z, player.getBounds().getWidth(), player.getBounds().getHeight(), -player.getBounds().getDepth());

         */



        shapeRenderer.end();

        gui.draw();
    }

    private void renderTrackPass() {
        modelBatch.begin(camera);
        modelBatch.render(player.getModelInstance(), environment);

        // Render tracks
        // Render previous
        for (TrackSection track : trackGenerator.getPreviousSections()) {
            track.render(modelBatch, environment);
        }

        // render current
        trackGenerator.getCurrentTrackSection().render(modelBatch, environment);

        // render next
        trackGenerator.getNextSection().render(modelBatch, environment);


        /*
            for (CollisionObject object : track.getCollisionObjects()){

                if (player.getBounds().intersects(object.getBounds())){
                    object.onCollision();

                    System.out.println("Player: " + player.getBounds());
                    System.out.println("Object: " + object.getBounds());
                }
                // Let the object handle drawing itself so it changes on state change
                object.draw(modelBatch, environment);
            }

        }

        for (CollisionObject object : trackGenerator.getCurrentTrackSection().getCollisionObjects()){

            if (player.getBounds().intersects(object.getBounds())){
                object.onCollision();

                System.out.println("Player: " + player.getBounds());
                System.out.println("Object: " + object.getBounds());
            }
            // Let the object handle drawing itself so it changes on state change
            object.draw(modelBatch, environment);
        }
         */

        modelBatch.end();
    }

    private void renderShadowPass() {
        //create shadow texture
        shadowLight.begin(tmp.set(camera.position), camera.direction);
        shadowBatch.begin(shadowLight.getCamera());

        shadowBatch.render(player.getModelInstance(), environment);
        // Shadow Render tracks
        // Render previous
        for (TrackSection track : trackGenerator.getPreviousSections()){
            track.render(shadowBatch, environment);
        }

        // render current
        trackGenerator.getCurrentTrackSection().render(shadowBatch, environment);

        // render next
        trackGenerator.getNextSection().render(shadowBatch, environment);

        shadowBatch.end();
        shadowLight.end();
    }

    private void trackCollision() {
        for (TrackSection track : trackGenerator.getPreviousSections()){
            track.handleCollisions(player);
        }

        // render current
        trackGenerator.getCurrentTrackSection().handleCollisions(player);

        // render next
        trackGenerator.getNextSection().handleCollisions(player);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    private void drawBox (Vector3 point, float size){
        shapeRenderer.box(point.x - size / 2f, point.y - size / 2f, point.z + size / 2f, size, size, size);
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    public boolean isPause() {
        return pause;
    }
}
