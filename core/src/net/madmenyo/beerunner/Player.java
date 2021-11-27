package net.madmenyo.beerunner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class Player {
    private ModelInstance modelInstance;
    private Vector3 position = new Vector3();
    private Vector3 derivative = new Vector3();
    private Quaternion quaternion = new Quaternion();

    /** Just a score keeper **/
    private float totalDistance;

    private TrackGenerator trackGenerator;


    private float speed = 80;
    private float totalEnergy = 100;
    private float energy = totalEnergy;



    private float t = 0;

    /** Height offset from curve **/
    private float height = 2;
    /** Horizontal offset from curve **/
    private float offset = 0;

    private BoundingBox bounds = new BoundingBox();

    public Player(ModelInstance modelInstance, TrackGenerator trackGenerator) {
        this.modelInstance = modelInstance;
        this.trackGenerator = trackGenerator;

    }

    public void update(float delta){
        controlls(delta);
        movement(delta);
    }

    /**
     * Moves based on current T on track. It asks the track generator for a position, the track
     * generator is responsible for providing a new track and returning a proper T and position
     * @param delta
     */
    private void movement(float delta) {
        float distanceToTravel = speed * delta;
        // increment total distance for score keeping
        totalDistance += distanceToTravel;

        t = trackGenerator.getCurrentTrackSection().getNextPosition(t, distanceToTravel, position);
        if (t > 1) // we need a new track
        {
            trackGenerator.nextTrack();
            // Can I get away with this? If each track is about the same length I probably can.
            t -= 1;
            // Otherwise need to distill the left over distance and calculate it on the new track
        }

        // Get derivative and offset
        trackGenerator.getCurrentTrackSection().getCurve().derivativeAt(derivative, t);

        derivative.nor();
        //modelInstance.transform.rotateTowardDirection(derivative.cpy(), Vector3.Y);
        derivative.y = 0;
        derivative.rotate(Vector3.Y, -90);

        // Set the position
        modelInstance.transform.set(position, quaternion);


        // Translate position to current offsets
        modelInstance.transform.translate(derivative.scl(offset));
        modelInstance.transform.translate(0, height, 0);
        trackGenerator.getCurrentTrackSection().getCurve().derivativeAt(derivative, t);
        modelInstance.transform.rotateTowardDirection(derivative.scl(-1), Vector3.Y);

        // set new bounds
        //modelInstance.calculateBoundingBox(bounds);
        modelInstance.calculateBoundingBox(bounds);
        bounds.mul(modelInstance.transform);

    }

    /**
     * Crude controls, should clamp offset to track width
     * @param delta
     */
    private void controlls(float delta) {
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            offset -= 15 * delta;

        } else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            offset += 15 * delta;
        }
        MathUtils.clamp(offset, -5, 5);
    }

    public ModelInstance getModelInstance() {
        return modelInstance;
    }

    /*
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
    }*/

    public Vector3 getPosition() {
        return position;
    }

    public float getT() {
        return t;
    }

    public float getTotalDistance() {
        return totalDistance;
    }

    public BoundingBox getBounds() {
        return bounds;
    }

    public float getTotalEnergy() {
        return totalEnergy;
    }

    public float getEnergy() {
        return energy;
    }
}
