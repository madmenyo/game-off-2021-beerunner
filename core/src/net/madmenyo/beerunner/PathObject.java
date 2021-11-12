package net.madmenyo.beerunner;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.collision.BoundingBox;

public class PathObject {

    private BoundingBox collisionBox = new BoundingBox();
    private ModelInstance modelInstance;

    // pickup or obstacle?

    // Store resource

    public PathObject(ModelInstance modelInstance) {
        this.modelInstance = modelInstance;

        modelInstance.calculateBoundingBox(collisionBox);
    }

    public void drawBounds(ShapeRenderer shapeRenderer){
        shapeRenderer.box(collisionBox.getCenterX() - collisionBox.getWidth() / 2, collisionBox.getCenterY() - collisionBox.getHeight() / 2, collisionBox.getCenterZ() + collisionBox.getDepth() / 2,
                collisionBox.getWidth(), collisionBox.getHeight(), collisionBox.getDepth());
    }

    public ModelInstance getModelInstance() {
        return modelInstance;
    }
}
