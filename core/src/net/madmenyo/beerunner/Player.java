package net.madmenyo.beerunner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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


    private float tAccelaration;
    private float fromSpeed = 0;
    private float maxSpeed = 60;
    private float speed = maxSpeed;

    private float moveSpeed = 25;
    private float flySpeed = 25;
    private float flyCost = 10;

    private int maxLives = 3;
    private int lives = maxLives;

    private float maxEnergy = 50;
    private float energyTreshHold = 10;
    private boolean depleted;
    private float energy = maxEnergy;

    private float energyRegen = 1;

    private int flowers = 0;


    private float tCurve = 0;

    /** Horizontal offset from curve **/
    private float offset = 0;

    private final float minHeight = 2;
    private final float maxHeight = 13;
    /** Height offset from curve **/
    private float height = minHeight;

    /** Down force per second when flight is not used or energy depleted**/
    private float downForce = 1f;

    private boolean bump = false;

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
        // If bumped we need acceleration
        if (speed < maxSpeed){
            if (tAccelaration >= 1){
                speed = maxSpeed;
            }else {
                tAccelaration += delta;
                speed = MathUtils.lerp(fromSpeed, maxSpeed, tAccelaration);
            }
        }

        float distanceToTravel = speed * delta;
        // increment total distance for score keeping
        totalDistance += distanceToTravel;

        tCurve = trackGenerator.getCurrentTrackSection().getNextPosition(tCurve, distanceToTravel, position);
        if (tCurve > 1) // we need a new track
        {
            trackGenerator.nextTrack();
            // Can I get away with this? If each track is about the same length I probably can.
            tCurve -= 1;
            // Otherwise need to distill the left over distance and calculate it on the new track
            tCurve = trackGenerator.getCurrentTrackSection().getNextPosition(tCurve, distanceToTravel, position);
        }

        // Get derivative and offset
        trackGenerator.getCurrentTrackSection().getCurve().derivativeAt(derivative, tCurve);

        derivative.nor();
        //modelInstance.transform.rotateTowardDirection(derivative.cpy(), Vector3.Y);
        derivative.y = 0;
        derivative.rotate(Vector3.Y, -90);

        // Set the position
        modelInstance.transform.set(position, quaternion);


        // Translate position to current offsets
        modelInstance.transform.translate(derivative.scl(offset));
        modelInstance.transform.translate(0, height, 0);
        trackGenerator.getCurrentTrackSection().getCurve().derivativeAt(derivative, tCurve);
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
            offset -= moveSpeed * delta;

        } else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            offset += moveSpeed * delta;
        }
        offset = MathUtils.clamp(offset, -16, 16);

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && energy > 0 && !depleted){
            height += flySpeed * delta;
            height = MathUtils.clamp(height, minHeight, maxHeight);

            energy -= flyCost * delta;
            energy = MathUtils.clamp(energy, 0, maxEnergy);

            if (energy == 0){
                depleted = true;
            }

        } else {
            if (depleted){
                depleted = energy < energyTreshHold;
            }
            height -= 20 * delta;
            height = MathUtils.clamp(height, minHeight, maxHeight);

            energy += energyRegen * delta;
            energy = MathUtils.clamp(energy, 0, maxEnergy);

        }

    }

    public ModelInstance getModelInstance() {
        return modelInstance;
    }


    public Vector3 getPosition() {
        return position;
    }

    public float gettCurve() {
        return tCurve;
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

    public int getMaxLives() {
        return maxLives;
    }

    public int getLives() {
        return lives;
    }

    public float getMaxEnergy() {
        return maxEnergy;
    }

    public float getEnergy() {
        return energy;
    }

    public boolean isDepleted() {
        return depleted;
    }

    public void addFlower(){
        flowers++;
    }

    /**
     * Called when bumped into a obstacle
     */
    public void bump() {
        tAccelaration = 0;
        fromSpeed = -speed * .8f;
        speed = fromSpeed;
        lives -= 1;
    }

    public int getFlowers() {
        return flowers;
    }

    public void drawBounds(ShapeRenderer renderer){
        renderer.box(bounds.getCenterX() - bounds.getWidth() / 2, bounds.getCenterY() - bounds.getHeight() / 2, bounds.getCenterZ() + bounds.getDepth() / 2, bounds.getWidth(), bounds.getHeight(), bounds.getDepth());
    }

    public boolean flyingMaxHeight(){
        return height == maxHeight;
    }
}
