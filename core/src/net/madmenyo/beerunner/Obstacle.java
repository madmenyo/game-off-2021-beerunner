package net.madmenyo.beerunner;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;

public class Obstacle extends CollisionObject{

    private boolean collided;

    public Obstacle(ModelInstance modelInstance) {
        super(modelInstance);
    }

    @Override
    public void draw(ModelBatch modelBatch, Environment environment) {
        if (collided) return;
        modelBatch.render(modelInstance, environment);
    }

    @Override
    public void onCollision() {
        // should crash and lose life or game over
        if (collided) return;
        collided = true;
        System.out.println("Hit obstacle!");

    }
}
