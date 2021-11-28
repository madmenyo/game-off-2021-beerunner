package net.madmenyo.beerunner;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.SphereShapeBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Responsible for generating tracks on demand and keeping records.
 *
 * Code starting to get one huge mesh but only 3 days left... need... more... time...
 */
public class TrackGenerator {
    private AssetManager assetManager;
    private ICurveGenerator curveGenerator;

    /** The current track **/
    private TrackSection currentTrackSection;

    private TrackSection nextSection;

    private final List<TrackSection> previousSections = new ArrayList<>();



    /** Total distance of previous tracks **/

    private float lastObstacleDistance = 0;
    private float lastResourceDistance = 0;

    private Vector3 tmp1 = new Vector3();
    private Vector3 tmp2 = new Vector3();


    // ---

    ModelBuilder modelBuilder = new ModelBuilder();

    public TrackGenerator(AssetManager assetManager) {
        this.assetManager = assetManager;
        curveGenerator = new SimpleCurveGenerator();

        currentTrackSection = new TrackSection(curveGenerator.getCurve());
        // Ridiculously repetitively... :)
        placeSideObjects(currentTrackSection);
        //placeCollisionObjects(currentTrackSection);
        placePickup(currentTrackSection);
        placeEdges(currentTrackSection);

        nextSection = new TrackSection(curveGenerator.getCurve());
        placeSideObjects(nextSection);
        //placeCollisionObjects(nextSection);
        placePickup(nextSection);
        placeEdges(nextSection);


        // Dummy track section
        /*
        currentTrackSection = new TrackSection(new Bezier<>(
                new Vector3(0,0,0),
                new Vector3(15,0,5),
                new Vector3(15,0,15),
                new Vector3(15,0,100)
        ));

         */

    }


    public float nextTrack() {
        // For now stay on current track and reset t
        previousSections.add(currentTrackSection);
        if (previousSections.size() > 2){
            previousSections.get(1).dispose();
            previousSections.remove(1);
        }
        currentTrackSection = nextSection;
        nextSection = new TrackSection(curveGenerator.getCurve());

        placeSideObjects(nextSection);
        placeEdges(nextSection);
        //placeCollisionObjects(nextSection);
        placePickup(nextSection);

        return 0f;
    }

    /**
     * Just add pickup flowers randomly
     * @param track
     */
    private void placePickup(TrackSection track) {
        for (float d = 0; d  < track.getCurve().approxLength(100); ) {

            boolean rock = MathUtils.random(100) < 30;

            d += MathUtils.random() * 60 + 2;

            ModelInstance ml;
            if (rock) {
                ml = new ModelInstance(assetManager.get(Assets.rocks.get(MathUtils.random(Assets.rocks.size() - 1))));
            } else
            {
                ml = new ModelInstance(assetManager.get(Assets.flowers.get(MathUtils.random(Assets.flowers.size() - 1))));
            }
            float t = track.findT(d);
            track.getCurve().valueAt(tmp1, t);
            track.getCurve().derivativeAt(tmp2, t);

            tmp2.y = 0;
            tmp2.nor();
            if (MathUtils.random(100) < 50)
                tmp2.rotate(Vector3.Y, 90);
            else
                tmp2.rotate(Vector3.Y, -90);

            tmp2.scl(MathUtils.random(0, 10));
            tmp1.add(tmp2);

            ml.transform.translate(tmp1);
            ml.transform.scl(MathUtils.random(.04f, .06f));
            ml.transform.rotate(Vector3.Y, MathUtils.random(360));

            CollisionObject co;
            if (rock) {
                co = new Obstacle(ml);
            } else {
                co = new Pickup(ml);
            }
            track.getCollisionObjects().add(co);
        }
    }

    /**
     * Hack in some object to dodge
     * @param trackSection
     */
    private void placeCollisionObjects(TrackSection trackSection) {
        modelBuilder.begin();
        MeshPartBuilder meshBuilder;
        meshBuilder = modelBuilder.part("part1", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material());
        SphereShapeBuilder.build(meshBuilder, 4, 4, 4, 12, 12);

        ModelInstance instance = new ModelInstance(modelBuilder.end());
        float t = trackSection.findT(100);

        trackSection.getCurve().valueAt(tmp1, t);
        tmp1.y += 6;
        instance.transform.translate(tmp1);

        Obstacle obstacle = new Obstacle(instance);


        trackSection.getCollisionObjects().add(obstacle);
    }


    /**
     * No more time left, just hack in some objects to give the world some live
     * @param track
     */
    private void placeSideObjects(TrackSection track) {

        // Left side
        for (float d = 0; d  < track.getCurve().approxLength(100); ) {

            d += MathUtils.random() * 5 + 10;

            ModelInstance ml = new ModelInstance(assetManager.get(Assets.trees.get(MathUtils.random(Assets.trees.size() - 1))));
            float t = track.findT(d);
            track.getCurve().valueAt(tmp1, t);
            track.getCurve().derivativeAt(tmp2, t);

            tmp2.y = 0;
            tmp2.nor();
            tmp2.rotate(Vector3.Y, 90);
            tmp2.scl(track.getTrackWidth() / 3f + MathUtils.random() * track.getTrackWidth() * .2f);
            tmp1.add(tmp2);

            ml.transform.translate(tmp1);
            ml.transform.scl(MathUtils.random(.03f, .05f));
            ml.transform.rotate(Vector3.Y, MathUtils.random(360));

            track.getSideObjects().add(ml);
            //pathObjects.add(new PathObject(ml));
        }

        // right side
        for (float d = 0; d  < track.getCurve().approxLength(100); ) {

            d += MathUtils.random() * 2 + 5;

            ModelInstance ml = new ModelInstance(assetManager.get(Assets.trees.get(MathUtils.random(Assets.trees.size() - 1))));
            float t = track.findT(d);
            track.getCurve().valueAt(tmp1, t);
            track.getCurve().derivativeAt(tmp2, t);

            tmp2.y = 0;
            tmp2.nor();
            tmp2.rotate(Vector3.Y, 90);
            tmp2.scl(track.getTrackWidth() / -3f - MathUtils.random() * track.getTrackWidth() * .2f);
            tmp1.add(tmp2);

            ml.transform.translate(tmp1);
            ml.transform.scl(MathUtils.random(.03f, .05f));
            ml.transform.rotate(Vector3.Y, MathUtils.random(360));

            track.getSideObjects().add(ml);
            //pathObjects.add(new PathObject(ml));
        }
    }

    /**
     * No more time left, just hack in some objects to give the world some live
     * @param track
     */
    private void placeEdges(TrackSection track) {

        // Left side
        for (float d = 0; d  < track.getCurve().approxLength(100); ) {

            d += MathUtils.random() * 5 + 10;

            ModelInstance ml = new ModelInstance(assetManager.get(Assets.rocks.get(MathUtils.random(Assets.rocks.size() - 1))));
            float t = track.findT(d);
            track.getCurve().valueAt(tmp1, t);
            track.getCurve().derivativeAt(tmp2, t);

            tmp2.y = 0;
            tmp2.nor();
            tmp2.rotate(Vector3.Y, 90);
            tmp2.scl(MathUtils.random(40, 45));
            tmp1.add(tmp2);

            ml.transform.translate(tmp1);
            ml.transform.scl(MathUtils.random(.09f, .13f), MathUtils.random(.09f, .22f), MathUtils.random(.09f, .13f));
            ml.transform.rotate(Vector3.Y, MathUtils.random(360));

            track.getSideObjects().add(ml);
            //pathObjects.add(new PathObject(ml));
        }

        // right side
        for (float d = 0; d  < track.getCurve().approxLength(100); ) {

            d += MathUtils.random() * 3 + 6;

            ModelInstance ml = new ModelInstance(assetManager.get(Assets.rocks.get(MathUtils.random(Assets.rocks.size() - 1))));
            float t = track.findT(d);
            track.getCurve().valueAt(tmp1, t);
            track.getCurve().derivativeAt(tmp2, t);

            tmp2.y = 0;
            tmp2.nor();
            tmp2.rotate(Vector3.Y, -90);
            tmp2.scl(MathUtils.random(40, 45));
            tmp1.add(tmp2);

            ml.transform.translate(tmp1);
            ml.transform.scl(MathUtils.random(.09f, .13f), MathUtils.random(.09f, .22f), MathUtils.random(.09f, .13f));
            ml.transform.rotate(Vector3.Y, MathUtils.random(360));

            track.getSideObjects().add(ml);
            //pathObjects.add(new PathObject(ml));
        }

    }


    public TrackSection getCurrentTrackSection() {
        return currentTrackSection;
    }

    public TrackSection getNextSection() {
        return nextSection;
    }

    public List<TrackSection> getPreviousSections() {
        return previousSections;
    }

    private void addObstacle(){
        getCurrentTrackSection().findT(100);
    }

    public void setCameraBehind(PerspectiveCamera camera, Player player, ShapeRenderer shapeRenderer){
        /*
        float t = currentTrackSection.getCurve().approximate(player.getPosition());
        Vector3 v3 = new Vector3();
        currentTrackSection.getCurve().valueAt(v3, t);
        shapeRenderer.box(v3.x, v3.y, v3.z, 1, 1, 1);
         */

        float t = player.getT();
        t -= .2f;

        Vector3 v3 = new Vector3();
        TrackSection trackSection;

        if (t < 0){
            if (!previousSections.isEmpty()) {
                trackSection = previousSections.get(previousSections.size() - 1);

                t = 1 + t;
            } else {
                trackSection = currentTrackSection;
            }
        } else {
            trackSection = currentTrackSection;
        }

        trackSection.getCurve().valueAt(v3, t);
        v3.y += 8;
        camera.position.set(v3);
        camera.lookAt(player.getPosition());
        camera.up.set(Vector3.Y);
        camera.update();

    }


}
