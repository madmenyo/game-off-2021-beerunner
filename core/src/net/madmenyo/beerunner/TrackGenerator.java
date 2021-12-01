package net.madmenyo.beerunner;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelCache;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pools;

import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for generating tracks on demand and keeping records.
 *
 * Code starting to get one huge mesh but only 3 days left... need... more... time...
 *
 * The current track is tracksections(1), previous (o) or it disapears and at least one more track to see further.
 */
public class TrackGenerator {
    private AssetManager assetManager;
    private ICurveGenerator curveGenerator;

    /** The current track **/
    private TrackSection currentTrackSection;

    private TrackSection nextSection;

    private final List<TrackSection> trackSections = new ArrayList<>();


    // Catch lists
    private List<ModelInstance> treeInstances = new ArrayList<>();
    private List<ModelInstance> rockInstances = new ArrayList<>();


    /** Total distance of previous tracks **/

    private float lastObstacleDistance = 0;
    private float lastResourceDistance = 0;

    private Vector3 tmp1 = new Vector3();
    private Vector3 tmp2 = new Vector3();

    // ---

    public TrackGenerator(AssetManager assetManager) {
        this.assetManager = assetManager;
        curveGenerator = new SimpleCurveGenerator();

        fillCacheInstances();


        //currentTrackSection = new TrackSection(curveGenerator.getCurve());

        //generateStartTrack();

        nextTrack();
        nextTrack();
        nextTrack();
        nextTrack();

    }

    private void generateStartTrack() {
        currentTrackSection = Pools.obtain(TrackSection.class);
        currentTrackSection.init(curveGenerator.getCurve());

        ModelCache modelCache = currentTrackSection.getSideObjectCache();
        modelCache.begin();
        // Ridiculously repetitively... :)
        placeSideTrees(currentTrackSection, modelCache);
        //placeCollisionObjects(currentTrackSection);
        //placePickup(currentTrackSection);
        placeSideRocks(currentTrackSection, modelCache);
        modelCache.end();

        //nextSection = new TrackSection(curveGenerator.getCurve());
        nextSection = Pools.obtain(TrackSection.class);
        nextSection.init(curveGenerator.getCurve());

        modelCache = nextSection.getSideObjectCache();
        modelCache.begin();

        placeSideTrees(nextSection, modelCache);
        //placeCollisionObjects(nextSection);
        placePickup(nextSection);
        placeSideRocks(nextSection, modelCache);

        modelCache.end();
    }

    /**
     * Improvement on creating new modelinstances from the AssetDescriptors I pass to assetmanager.
     */
    private void fillCacheInstances() {
        for (AssetDescriptor<Model> rock : Assets.rocks){
            rockInstances.add(new ModelInstance(assetManager.get(rock)));
        }
        for (AssetDescriptor<Model> tree : Assets.trees){
            treeInstances.add(new ModelInstance(assetManager.get(tree)));
        }
    }

    /**
     * This generates a track, takes +- 7mms on desktop system and showing a big hickup on the
     * already slow webGL
     *
     * Added pooling to reuse track pieces
     * @return
     */
    public float nextTrack() {
        long time = System.currentTimeMillis();
        // For now stay on current track and reset t
        //trackSections.add(currentTrackSection);
        if (trackSections.size() > 3){
            trackSections.get(0).dispose();

            TrackSection t = trackSections.remove(0);
            Pools.free(t);
        }

        currentTrackSection = nextSection;
        //nextSection = new TrackSection(curveGenerator.getCurve());
        TrackSection nextSection = Pools.obtain(TrackSection.class);
        trackSections.add(nextSection);
        nextSection.init(curveGenerator.getCurve());


        ModelCache modelCache = nextSection.getSideObjectCache();
        modelCache.begin();
        placeSideTrees(nextSection, modelCache);
        placeSideRocks(nextSection, modelCache);
        modelCache.end();

        // If first track do not load pickups
        if (trackSections.size() <3) return 0f;
        placePickup(nextSection);

        System.out.println("Generated track in: " + (System.currentTimeMillis() - time) + "ms.");


        return 0f;
    }

    private void addInstance(ModelInstance instance, ModelCache modelCache){
        modelCache.add(instance);
        instance.transform.idt();
    }

    /**
     * Just add pickup flowers randomly
     *
     * Pickups are not being cached by a modelcache because I need to remove or move them at any time
     * @param track
     */
    private void placePickup(TrackSection track) {
        for (float d = 0; d  < track.getCurveLength(); ) {

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
            if (rock) ml.transform.scl(MathUtils.random(.04f, .06f));
            else ml.transform.scl(MathUtils.random(.08f, .12f));
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
     * No more time left, just hack in some objects to give the world some live
     *
     * Sideobjects are cached by the modelCache, eventually modelCache just has 1 renderable
     *
     * rocks and trees could be combined as wel as left and right side in a single loop.
     * @param track
     */
    private void placeSideTrees(TrackSection track, ModelCache modelCache) {

        // Left side
        for (float d = 0; d  < track.getCurveLength(); ) {

            d += MathUtils.random() * 5 + 10;

            //ModelInstance ml = new ModelInstance(assetManager.get(Assets.trees.get(MathUtils.random(Assets.trees.size() - 1))));
            ModelInstance ml = treeInstances.get(MathUtils.random(treeInstances.size() - 1));
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

            modelCache.add(ml);
            ml.transform.idt();
            //track.getSideObjects().add(ml);
            //pathObjects.add(new PathObject(ml));
        }

        // right side
        for (float d = 0; d  < track.getCurveLength(); ) {

            d += MathUtils.random() * 2 + 5;

            //ModelInstance ml = new ModelInstance(assetManager.get(Assets.trees.get(MathUtils.random(Assets.trees.size() - 1))));
            ModelInstance ml = treeInstances.get(MathUtils.random(treeInstances.size() - 1));
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

            modelCache.add(ml);
            ml.transform.idt();
            //track.getSideObjects().add(ml);
            //pathObjects.add(new PathObject(ml));
        }
    }

    /**
     * No more time left, just hack in some objects to give the world some live
     *
     * edge rocks are cached by the modelCache, eventually modelCache just has 1 renderable
     *
     * rocks and trees could be combined as wel as left and right side in a single loop.
     * @param track
     */
    private void placeSideRocks(TrackSection track, ModelCache modelCache) {

        // Left side
        for (float d = 0; d  < track.getCurveLength(); ) {

            d += MathUtils.random() * 5 + 10;

            //ModelInstance ml = new ModelInstance(assetManager.get(Assets.rocks.get(MathUtils.random(Assets.rocks.size() - 1))));
            ModelInstance ml = rockInstances.get(MathUtils.random(rockInstances.size() - 1));
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

            modelCache.add(ml);
            ml.transform.idt();
            //track.getSideObjects().add(ml);
            //pathObjects.add(new PathObject(ml));
        }

        // right side
        for (float d = 0; d  < track.getCurveLength(); ) {

            d += MathUtils.random() * 3 + 6;

            //ModelInstance ml = new ModelInstance(assetManager.get(Assets.rocks.get(MathUtils.random(Assets.rocks.size() - 1))));
            ModelInstance ml = rockInstances.get(MathUtils.random(rockInstances.size() - 1));
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

            modelCache.add(ml);
            ml.transform.idt();
            //track.getSideObjects().add(ml);
            //pathObjects.add(new PathObject(ml));
        }

    }


    public TrackSection getCurrentTrackSection() {
        return trackSections.get(1);
        //return currentTrackSection;
    }

    public TrackSection getNextSection() {
        return trackSections.get(2);

        //return nextSection;
    }

    public List<TrackSection> getTrackSections() {
        return trackSections;
    }

    private void addObstacle(){
        getCurrentTrackSection().findT(100);
    }

    public void setCameraBehind(PerspectiveCamera camera, Player player, ShapeRenderer shapeRenderer){

        float t = player.gettCurve();
        t -= .2f;

        Vector3 v3 = new Vector3();
        TrackSection trackSection;

        if (t < 0){
            if (!trackSections.isEmpty()) {
                trackSection = trackSections.get(trackSections.size() - 1);

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
