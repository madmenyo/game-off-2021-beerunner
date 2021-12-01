package net.madmenyo.beerunner;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
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

        // Resize bounds
        bounds.min.x += (bounds.max.x - bounds.min.x) * .25f;
        bounds.max.x -= (bounds.max.x - bounds.min.x) * .25f;
        bounds.min.z += (bounds.max.z - bounds.min.z) * .25f;
        bounds.max.z -= (bounds.max.z - bounds.min.z) * .25f;

        // set to transform
        bounds.mul(modelInstance.transform);
    }

    public void drawBounds(ShapeRenderer shapeRenderer){
        shapeRenderer.box(bounds.getCenterX() - bounds.getWidth() / 2, bounds.getCenterY() - bounds.getHeight() / 2, bounds.getCenterZ() + bounds.getDepth() / 2,
                bounds.getWidth(), bounds.getHeight(), bounds.getDepth());
    }

    public ModelInstance getModelInstance() {
        return modelInstance;
    }

    public abstract void onCollision(Player player, AssetManager assetManager);

    public abstract void update(float delta);

    public abstract void draw(ModelBatch modelBatch, Environment environment);

    public BoundingBox getBounds() {
        return bounds;
    }

    public abstract boolean shouldRender();
}
