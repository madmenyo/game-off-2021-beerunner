package net.madmenyo.beerunner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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


    private float speed = 60;
    private float totalEnergy = 100;
    private float energy = totalEnergy;

    private int flowers = 0;


    private float t = 0;

    /** Horizontal offset from curve **/
    private float offset = 0;

    private final float minHeight = 2;
    private final float maxHeight = 6;
    /** Height offset from curve **/
    private float height = minHeight;

    /** Down force per second when flight is not used or energy depleted**/
    private float downForce = 1f;

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
        setBounds();

    }

    /**
     * Crude controls, clamping offset to track width
     * @param delta
     */
    private void controlls(float delta) {
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            offset -= 25 * delta;

        } else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            offset += 25 * delta;
        }
        offset = MathUtils.clamp(offset, -16, 16);

    }

    public ModelInstance getModelInstance() {
        return modelInstance;
    }


    public Vector3 getPosition() {
        return position;
    }

    public float getT() {
        return t;
    }

    public float getTotalDistance() {
        return totalDistance;
    }

    public void setBounds() {
        modelInstance.calculateBoundingBox(bounds);
        bounds.min.x += 1.4f;
        bounds.max.x -= 1.4f;
        bounds.min.z += 1.4f;
        bounds.max.z -= 1.4f;
        bounds.mul(modelInstance.transform);


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

    public void addFlower(){
        flowers++;
    }

    /**
     * Called when bumped into a obstacle
     */
    public void bump() {

    }

    public int getFlowers() {
        return flowers;
    }

    public void drawBounds(ShapeRenderer renderer){
        renderer.box(bounds.getCenterX() - bounds.getWidth() / 2, bounds.getCenterY() - bounds.getHeight() / 2, bounds.getCenterZ() + bounds.getDepth() / 2, bounds.getWidth(), bounds.getHeight(), bounds.getDepth());
    }
}
