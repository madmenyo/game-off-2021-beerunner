package net.madmenyo.beerunner;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class BezierGenerator {

    private Bezier<Vector3> currentCurve = new Bezier<>();

    private Array<Vector3> points = new Array();

    // Calculating V3's
    private Vector3 derivate = new Vector3();
    private Vector3 position = new Vector3();
    private Vector3 previousPosition = new Vector3();


    public BezierGenerator() {

        // Create dummy arc curve points
        points.add(new Vector3(0, -20, 0));
        points.add(new Vector3(0, 40, 50));
        points.add(new Vector3(50, 40, 100));
        points.add(new Vector3(100, -20, 100));
    }

    public Bezier generateTrack() {
        currentCurve.set(points, 0 ,4);

        return currentCurve;
    }

    /**
     *
     * @param curve the curve to draw
     * @param stepSize step size in world units
     * @param shapeRenderer the renderer to draw
     */
    public void drawCurve(Bezier<Vector3> curve, float stepSize, ShapeRenderer shapeRenderer){
        // set prev pos to start of curve
        previousPosition.set(curve.points.get(0));

        for (int i = 0; i <= 100; i++){
            curve.valueAt(position, i / 100f);


            shapeRenderer.setColor(Color.GREEN);
            shapeRenderer.line(previousPosition.x, previousPosition.y, previousPosition.z,
                    position.x, position.y, position.z);
            previousPosition.set(position);

        }


        /*
        for (float t = 0f; t <= 1f; ){
            curve.valueAt(position, t);
            if (position.dst(previousPosition) > stepSize / 5){
                shapeRenderer.setColor(Color.CYAN);
                shapeRenderer.line(previousPosition.x, previousPosition.y, previousPosition.z,
                        position.x, position.y, position.z);
            }

            previousPosition.set(position);

            curve.derivativeAt(derivate, t);

            // Get derivate length and normalize it
            float length = derivate.len();
            derivate.scl(1 / length);

            t += stepSize / length;

        }

         */

    }



}
