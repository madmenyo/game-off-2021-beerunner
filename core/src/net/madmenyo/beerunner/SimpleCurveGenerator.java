package net.madmenyo.beerunner;

import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.List;

/**
 * This generator creates a curve that always has it's control points fixed in x direction on the
 * start and end point.
 */
public class SimpleCurveGenerator implements ICurveGenerator {
    Vector3 lastPoint = new Vector3();
    Vector3 lastControl = new Vector3(0, 0, -50);

    List<Vector3> points = new ArrayList<>();

    Vector3 p1 = new Vector3();
    Vector3 p2 = new Vector3();
    Vector3 p3 = new Vector3();
    Vector3 p4 = new Vector3();

    Vector3 direction = new Vector3(0, 0, 1);
    //Quaternion quaternion = new Quaternion();
    float maxRotation = 45;

    float currentHeight = 0;
    float maxHeightDifferent = 50;

    public SimpleCurveGenerator() {
        //quaternion.transform(Vector3.Y);
    }


    public Bezier<Vector3> getCurve(){
        points.clear();

        // first point should match last point

        points.add(p1.set(lastPoint));

        // first control should mirror lastControl
        p2.set(lastPoint.x + lastPoint.x - lastControl.x, lastPoint.y + lastPoint.y - lastControl.y, lastPoint.z + lastPoint.z - lastControl.z);
        points.add(p2);

        // Set p3 randomly, but away from p2
        float distance = MathUtils.random() * 25 + 25f;
        float rotation = MathUtils.random(-maxRotation, maxRotation);

        direction.rotate(Vector3.Y, rotation);
        p3.set(p2).add(direction.nor().scl(distance));
        p3.y += MathUtils.random(-maxHeightDifferent, maxHeightDifferent);

        // Set p4 randomly, but away from p3
        distance = MathUtils.random() * 25 + 25f;
        rotation += MathUtils.random(-maxRotation, maxRotation);

        direction.rotate(Vector3.Y, rotation);
        p4.set(p3).add(direction.nor().scl(distance));
        p4.y += MathUtils.random(-maxHeightDifferent, maxHeightDifferent);

        // Add the points
        points.add(p3);
        points.add(p4);

        // set p3, p4 as last points to use them for mirroring in next curve
        lastPoint.set(p4);
        lastControl.set(p3);

        return new Bezier<>(points.get(0).cpy(), points.get(1).cpy(), points.get(2).cpy(), points.get(3).cpy());
    }
}
