package net.madmenyo.beerunner;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for generating tracks on demand and keeping records.
 */
public class TrackGenerator {
    private ICurveGenerator curveGenerator;

    /** The current track **/
    private TrackSection currentTrackSection;

    private TrackSection nextSection;

    private List<TrackSection> previousSections = new ArrayList<>();

    private List<PathObject> pathObjects = new ArrayList<>();


    /** distance prior to track **/
    private float distance;

    public TrackGenerator() {

        curveGenerator = new SimpleCurveGenerator();

        currentTrackSection = new TrackSection(curveGenerator.getCurve());
        nextSection = new TrackSection(curveGenerator.getCurve());


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
        currentTrackSection = nextSection;
        nextSection = new TrackSection(curveGenerator.getCurve());
        return 0f;
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

        System.out.println(t);
        trackSection.getCurve().valueAt(v3, t);
        v3.y += 8;
        camera.position.set(v3);
        camera.lookAt(player.getPosition());
        camera.up.set(Vector3.Y);
        camera.update();

    }
}
