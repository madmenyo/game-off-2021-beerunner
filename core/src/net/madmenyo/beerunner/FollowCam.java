package net.madmenyo.beerunner;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;

public class FollowCam {
    private PerspectiveCamera camera;
    private Player player;
    private TrackGenerator trackGenerator;

    private Vector3 position = new Vector3();
    private Vector3 derivative = new Vector3();
    private Vector3 tmp = new Vector3();

    public FollowCam(PerspectiveCamera camera, Player player, TrackGenerator trackGenerator) {
        this.camera = camera;
        this.player = player;
        this.trackGenerator = trackGenerator;
    }

    /**
     * This still needs work, probably need a method to set it behind the player on the curve,
     * then depending on derivative offset and perhaps turn it.
     * @param delta
     */
    public void update(float delta){
        // Find t at player position
        float t = trackGenerator.getCurrentTrackSection().getCurve().approximate(player.getPosition());

        //get derative at player position
        trackGenerator.getCurrentTrackSection().getCurve().derivativeAt(derivative, t);

        // set position behind player


        tmp.set(derivative);

        //derivative.nor();
        //derivative.y = .2f;
        derivative.nor();
        derivative.y = -derivative.y;
        derivative.rotate(Vector3.Y, 180);
        derivative.scl(40);


        camera.position.set(position.set(player.getPosition()).add(derivative));
        camera.position.y += 15;
        camera.lookAt(player.getPosition());
        camera.up.set(Vector3.Y);
        camera.update();

    }
}
