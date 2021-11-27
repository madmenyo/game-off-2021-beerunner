package net.madmenyo.beerunner;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.collision.BoundingBox;

public abstract class CollisionObject {

    private BoundingBox bounds = new BoundingBox();
    protected ModelInstance modelInstance;

    // pickup or obstacle?

    // Store resource

    public CollisionObject(ModelInstance modelInstance) {
        this.modelInstance = modelInstance;

        modelInstance.calculateBoundingBox(bounds);
        bounds.mul(modelInstance.transform);
    }

    public void drawBounds(ShapeRenderer shapeRenderer){
        shapeRenderer.box(bounds.getCenterX() - bounds.getWidth() / 2, bounds.getCenterY() - bounds.getHeight() / 2, bounds.getCenterZ() + bounds.getDepth() / 2,
                bounds.getWidth(), bounds.getHeight(), bounds.getDepth());
    }

    public ModelInstance getModelInstance() {
        return modelInstance;
    }

    public abstract void onCollision();

    public abstract void draw(ModelBatch modelBatch, Environment environment);

    public BoundingBox getBounds() {
        return bounds;
    }
}
