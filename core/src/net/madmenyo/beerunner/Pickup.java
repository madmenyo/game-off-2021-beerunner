package net.madmenyo.beerunner;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;

public class Pickup extends CollisionObject{

    private boolean pickedUp;

    public Pickup(ModelInstance modelInstance) {
        super(modelInstance);
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void draw(ModelBatch modelBatch, Environment environment) {
        if (pickedUp) return;
        modelBatch.render(modelInstance, environment);
    }

    @Override
    public void onCollision(Player player) {
        // add resource or initialize powerup
        if (pickedUp) return;
        pickedUp = true;
        player.addFlower();

    }
}
