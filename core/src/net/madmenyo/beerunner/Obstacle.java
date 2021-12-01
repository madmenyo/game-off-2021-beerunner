package net.madmenyo.beerunner;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;

public class Obstacle extends CollisionObject{

    private boolean hit = false;

    /** initial speed it sinks when hit **/
    private float sinkSpeed = -5;

    /** accell it sinks with **/
    private float sinkAcceleration = 1.14f;

    public Obstacle(ModelInstance modelInstance) {
        super(modelInstance);
    }

    @Override
    public void update(float delta) {
        if (!hit) return;
        sinkSpeed *= sinkAcceleration;
        modelInstance.transform.translate(0, sinkSpeed * sinkAcceleration * delta, 0);
    }

    @Override
    public void draw(ModelBatch modelBatch, Environment environment) {
        modelBatch.render(modelInstance, environment);
    }

    @Override
    public void onCollision(Player player, AssetManager assetManager) {

        // Don't hit same obstacle twice
        if (hit) return;

        // If player flying max height give him a break :)
        if (player.flyingMaxHeight()) return;
        // should crash and lose life or game over
        hit = true;
        player.bump();

        assetManager.get(Assets.rock).play(1);

    }

    public boolean isHit() {
        return hit;
    }

}
