package net.madmenyo.beerunner;

import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for generating tracks on demand and keeping records.
 */
public class TrackGenerator {
    private StraightCurveGenerator curveGenerator;

    /** The current track **/
    private TrackSection currentTrackSection;

    private List<TrackSection> previousSections = new ArrayList<>();

    public TrackGenerator() {

        curveGenerator = new StraightCurveGenerator();

        currentTrackSection = new TrackSection(curveGenerator.getCurve());

        /*
        // Dummy track section
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
        currentTrackSection = new TrackSection(curveGenerator.nextCurve());
        return 0f;
    }

    public TrackSection getCurrentTrackSection() {
        return currentTrackSection;
    }

    public List<TrackSection> getPreviousSections() {
        return previousSections;
    }
}
