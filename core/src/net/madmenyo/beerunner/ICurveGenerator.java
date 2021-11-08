package net.madmenyo.beerunner;

import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Vector3;

public interface ICurveGenerator {
    Bezier<Vector3> getCurve();
}
