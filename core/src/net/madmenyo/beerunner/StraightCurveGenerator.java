package net.madmenyo.beerunner;

import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.List;

/**
 * This generator creates a curve that always has it's controll points fixed in x direction on the
 * start and end point.
 */
public class StraightCurveGenerator {
    Vector3 lastPoint = new Vector3();
    Vector3 lastControl = new Vector3(0, 0, -50);

    List<Vector3> points = new ArrayList<>();


    public Bezier<Vector3> getCurve(){
        points.clear();

        // first point should match last point
        points.add(lastPoint.cpy());

        // first control should mirror lastControl
        points.add(new Vector3(lastPoint.x + lastPoint.x - lastControl.x, lastPoint.y + lastPoint.y - lastControl.y, lastPoint.z + lastPoint.z - lastControl.z));

        // Next control and point should be random with constraints
        float distance = MathUtils.random() * 200 + 100f; // 100 - 200

        // Create end point
        Vector3 endPoint = new Vector3(lastPoint.x + MathUtils.random() * 300 - 150, lastPoint.y + MathUtils.random() * 500 - 250, lastPoint.z + distance);

        // create end control
        Vector3 endControl = new Vector3(endPoint);
        endControl.z -= MathUtils.random() * 25 + 25;

        points.add(endControl);
        points.add(endPoint);

        lastPoint.set(endPoint);
        lastControl.set(endControl);

        return new Bezier<>(points.get(0).cpy(), points.get(1).cpy(), points.get(2).cpy(), points.get(3).cpy());
    }
}
