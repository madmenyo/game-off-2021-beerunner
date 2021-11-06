package net.madmenyo.beerunner;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class Player {

    private ModelInstance modelInstance;
    private Vector3 position = new Vector3();
    private Quaternion quaternion = new Quaternion();

    /** Just a score keeper **/
    private float totalDistance;

    private Bezier<Vector3> currentCurve;
    private float curveLength;

    private float distanceOnCurve;

    private StraightCurveGenerator curveGenerator;


    private float speed = 50;

    /** Height offset from curve **/
    private float height;
    /** Horizontal offset from curve **/
    private float offset;

    public Player(ModelInstance modelInstance, StraightCurveGenerator curveGenerator) {
        this.modelInstance = modelInstance;
        this.curveGenerator = curveGenerator;


        nextCurve();

    }

    private void nextCurve() {
        currentCurve = curveGenerator.nextCurve();
        distanceOnCurve = 0;
        curveLength = currentCurve.approxLength(100);

    }

    public void update(float delta){

        // Calculate the distance we need to travel;
        float distanceToTravel = speed * delta;
        distanceOnCurve += distanceToTravel;

        System.out.println("distance to travel: " + distanceToTravel);

        if (distanceOnCurve >= curveLength){
            distanceOnCurve -= curveLength;
            nextCurve();
        }

        float t = distanceOnCurve / curveLength;

        System.out.println("T: " + t);


        currentCurve.valueAt(position, t);


        System.out.println("Pos: " + position);
        modelInstance.transform.set(position, quaternion);

    }

    public ModelInstance getModelInstance() {
        return modelInstance;
    }

    public void drawCurve(ShapeRenderer shapeRenderer){


        Vector3 prev = new Vector3();

        Vector3 cur = new Vector3();

        System.out.println(currentCurve.points.get(0));

        currentCurve.valueAt(prev, 0);

        for (int i = 1; i <= 100; i++){
            currentCurve.valueAt(cur, i / 100f);



            shapeRenderer.setColor(Color.BLUE);
            shapeRenderer.line(prev.x, prev.y, prev.z,
                    cur.x, cur.y, cur.z);
            prev.set(cur);

        }
    }
}
