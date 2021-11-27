package net.madmenyo.beerunner;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;

public class Pickup extends CollisionObject{
    public Pickup(ModelInstance modelInstance) {
        super(modelInstance);
    }

    @Override
    public void draw(ModelBatch modelBatch, Environment environment) {

    }

    @Override
    public void onCollision() {
        // add resource or initialize powerup

        System.out.println("Hit pickup!");
    }
}
