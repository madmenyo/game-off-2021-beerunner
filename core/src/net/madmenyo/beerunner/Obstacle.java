package net.madmenyo.beerunner;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;

public class Obstacle extends CollisionObject{


    public Obstacle(ModelInstance modelInstance) {
        super(modelInstance);
    }

    @Override
    public void draw(ModelBatch modelBatch, Environment environment) {
        modelBatch.render(modelInstance, environment);
    }

    @Override
    public void onCollision(Player player) {
        // should crash and lose life or game over
        player.bump();

        System.out.println("Hit obstacle!");

    }
}
