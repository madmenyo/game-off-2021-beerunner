package net.madmenyo.beerunner;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for generating tracks on demand and keeping records.
 */
public class TrackGenerator {
    private AssetManager assetManager;
    private ICurveGenerator curveGenerator;

    /** The current track **/
    private TrackSection currentTrackSection;

    private TrackSection nextSection;

    private final List<TrackSection> previousSections = new ArrayList<>();

    private final List<PathObject> pathObjects = new ArrayList<>();


    /** Total distance of previous tracks **/

    private float lastObstacleDistance = 0;
    private float lastResourceDistance = 0;

    private Vector3 tmp1 = new Vector3();
    private Vector3 tmp2 = new Vector3();

    public TrackGenerator(AssetManager assetManager) {
        this.assetManager = assetManager;
        curveGenerator = new SimpleCurveGenerator();

        currentTrackSection = new TrackSection(curveGenerator.getCurve());
        placeObjects(currentTrackSection);
        nextSection = new TrackSection(curveGenerator.getCurve());
        placeObjects(nextSection);


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
        if (previousSections.size() > 3){
            System.out.println("removing");
            previousSections.get(2).dispose();
            previousSections.remove(2);
        }
        currentTrackSection = nextSection;
        nextSection = new TrackSection(curveGenerator.getCurve());

        placeObjects(nextSection);
        return 0f;
    }


    /**
     * No more time left, just hack in some objects to give the world some live
     * @param track
     */
    private void placeObjects(TrackSection track) {

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

    public List<PathObject> getPathObjects() {
        return pathObjects;
    }
}
