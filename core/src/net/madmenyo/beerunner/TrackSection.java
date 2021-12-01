package net.madmenyo.beerunner;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelCache;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.SphereShapeBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.sun.org.apache.xpath.internal.operations.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Tracksection maps the curve to the mesh and holds other data like sides and obstacles.
 */
public class TrackSection implements Disposable, Pool.Poolable {
    private Bezier<Vector3> curve;
    private float curveLength;

    private float trackWidth;

    private ModelInstance track;
    float[] verts;

    //private Array<ModelInstance> sideObjects = new Array<>();

    private Array<CollisionObject> collisionObjects = new Array<>();

    private ModelCache sideObjectCache = new ModelCache();

    Vector3 position = new Vector3();
    Vector3 derivative = new Vector3();

    Vector3 tmp1 = new Vector3();
    Vector3 tmp2 = new Vector3();

    /** map distance -> t **/
    private SortedMap<Float, Float> curveLookUp = new TreeMap<>();

    public TrackSection() {
    }

    public TrackSection(Bezier<Vector3> curve) {
        this.curve = curve;
        curveLength = curve.approxLength(500);
        trackWidth = 60;

        createLookup(curve);

        generateMesh(5);
    }

    public void init(Bezier<Vector3> curve){
        this.curve = curve;
        curveLength = curve.approxLength(500);
        trackWidth = 60;

        createLookup(curve);

        generateMesh(5);

    }


    /**
     * Creates a lookup table that maps distance to t on curve
     * This way I can lookup position and derivative of t.
     * If computation is small, consider mapping directly to position and derivative
     * @param curve
     */
    private void createLookup(Bezier<Vector3> curve) {
        curveLookUp.clear();
        curveLookUp.put(0f, 0f);
        curve.valueAt(tmp2, 0f);
        float dst = 0;
        for (float t = 0f; t < 1f; ){
            // Map detail (1000 looked very smooth, 500 shows some inconsistancies.
            t += 1f / 500;
            if (t > 1f){
                t = 1f;
            }
            curve.valueAt(tmp1, t);
            dst += tmp1.dst(tmp2);
            curveLookUp.put(dst, t);

            tmp2.set(tmp1);
        }
    }

    /**
     * Divides points over track, this needs tweaking since end point is not reached.
     * Should either push last point to end or do a second pass to even things out.
     *
     * Unless needing to draw debug stuff this does not need to be called every frame
     * There is a lot of optimization potential in this method.
     * @param amount
     * @return
     */
    public List<Vector3> divideByLookup(int amount){
        //long time = System.currentTimeMillis();
        List<Vector3> points = new ArrayList<>();

        float stepDistance = curveLength / (float) amount;
        //System.out.println(curveLength);
        //System.out.println(stepDistance);


        for (int i = 0; i <= amount; i++) {

            float distance = i * stepDistance;

            points.add(findPosition(distance).cpy());
            //points.add(curve.valueAt(tmp1, curveLookUp.get(distance)));

        }
        //System.out.println("Divide by lookup" + (System.currentTimeMillis() - time) + "ms.");

        return points;

    }

    /**
     * Divides the curve by derivative
     * Seems prone to bugs,something about derivative being zero then the vector points nowhere.
     * Creating a mesh this way creates some artifacts, I do not know if this method is the cause of
     * that.
     *
     * For now use divideByLookup()
     * @param amount
     * @return
     */
    public List<Vector3> divideByDerivative(int amount){
        List<Vector3> points = new ArrayList<>();

        float stepDistance = curveLength / (float) amount;

        for (float t = 0f; t <= 1f; ){
            curve.valueAt(position, t);
            curve.derivativeAt(derivative, t);

            float len = derivative.len();

            derivative.scl(1/ len);

            t += stepDistance / len;

            // This hack in the end point by just placing it on the end when near the end or past
            // the curve
            if (t > 1 || position.dst(curve.valueAt(tmp1, 1)) < stepDistance){
                points.add(tmp1);
                return points;
            }

            points.add(position.cpy());
        }

        return points;
    }

    /**
     * Returns the closest distance on the lookup table as position (V3)
     * @param distance
     * @return
     */
    public Vector3 findPosition(float distance) {
        return curve.valueAt(tmp1, findT(distance));
    }

    /**
     * Returns the closest distance on the lookup table as t 0...1
     * @param distance
     * @return
     */
    public float findT(float distance){
        float difference = Float.MAX_VALUE;
        for (Map.Entry<Float, Float> e : curveLookUp.entrySet()){
            float currentDiference = Math.abs(distance - e.getKey());
            if (currentDiference <= difference){
                difference = currentDiference;
            }
            else {
                return curveLookUp.get(e.getKey());
            }
        }

        return 1f;

    }

    /**
     * Gets position on curve based on travel distance and current t
     * @param t the current position on the curve (0...1)
     * @param distance the distance in world units needed to travel
     * @param out
     * @return the calculated t on the current curve (0...1) if > 1 need new track and calculate
     * distance left
     */
    public float getNextPosition(float t, float distance, Vector3 out){
        curve.derivativeAt(derivative, t);
        float len = derivative.len();

        t += distance / len;

        // If t has not reached end continue
        if (t <= 1 ){
            curve.valueAt(out, t);
            return t;
        }
        // Otherwise return the distance traveled
        curve.valueAt(out, 1);


        return t;
    }

    /**
     * Draws the curve of this track
      * @param shapeRenderer
     */
    public void drawCurve(ShapeRenderer shapeRenderer){

        curve.valueAt(position, 0);
        for (int i = 1; i <= 50; i++) {

            float t = i / 50f;
            curve.valueAt(derivative, t);

            shapeRenderer.line(position.x, position.y, position.z, derivative.x, derivative.y, derivative.z);

            position.set(derivative);
        }
    }

    public Bezier<Vector3> getCurve() {
        return curve;
    }

    @Override
    public void dispose() {
        track.model.dispose();
        sideObjectCache.dispose();

        // Currently there are spheres being generated that are not disposed. Eventually these will
        // be models that are reused every track piece and only need disposing after level finished
    }

    /**
     * Generates the terrain mesh of this section
     * @param sectionDistance
     */
    private void generateMesh(float sectionDistance){
        // This might need to be variable based on curve length
        final int sections = (int)(curveLength / sectionDistance);

        // knowing the sections I know the vert count
        final int quadCount = sections * 3;
        // Sharing quads? each section line had 4 verts, also has aditional end line at t = 1.
        final int vertCount = (sections + 1) * 4;
        final int indiceCount = quadCount * 6;
        final int attributes = 8;//8;

        Mesh mesh = new Mesh(true, vertCount, indiceCount,
                new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"),
                new VertexAttribute(VertexAttributes.Usage.Normal, 3, "a_normal"),
                new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "a_texCoord0")
        );

        // Generare verts
        int index = 0;
        verts = new float[vertCount * attributes];

        boolean even = true;
        for (int i = 0; i <= sections; i++) {
            position.set(findPosition(i * sectionDistance));
            float t = findT(i * sectionDistance);

            if (i == sections){
                t = 1;
            } else if (i == 0){
                t = 0;
            }

                //   For each section line
            // Add vert positions
            curve.valueAt(position, t);
            curve.derivativeAt(derivative, t);

            // Make derivate horizontal and normalize
            derivative.y = 0;
            derivative.nor();
            // turn derivative to make it perpendicular on curve
            derivative.rotate(Vector3.Y, 90);
            // scale derivate so it represents the width of a single quad
            derivative.scl(trackWidth / 3);

            // use derivate to offset verts from curve

            // hardcode 3 quads width so move it from -150% -> -50% -> +50% -> +150%
            position.add(tmp1.set(derivative).scl(1.5f));
            derivative.rotate(Vector3.Y,180);

            curve.derivativeAt(tmp1, t);
            tmp1.nor();
            tmp1.rotate(90, -tmp1.z, 0, tmp1.x);

            for (int y = 0; y < 4; y++) {
                verts[index++] = position.x;
                verts[index++] = position.y;
                verts[index++] = position.z;
                position.add(derivative);

                // calculate normal by turning derivative
                verts[index++] = tmp1.x;
                verts[index++] = tmp1.y;
                verts[index++] = tmp1.z;

                // UV
                verts[index++] = y / 3f;
                if (even) verts[index++] = 0;
                else verts[index++] = 1;

            }
            even = !even;
        }

        // Set indices
        short[] indices = new short[indiceCount];
        for (int i = 0; i < sections; i++) {
            indices[i * 6 * 3 + 0] = (short) (i * 4 + 0);
            indices[i * 6 * 3 + 1] = (short) (i * 4 + 1);
            indices[i * 6 * 3 + 2] = (short) ((i + 1) * 4 + 0);

            indices[i * 6 * 3 + 3] = (short) (i  * 4 + 1);
            indices[i * 6 * 3 + 4] = (short) ((i + 1) * 4 + 1);
            indices[i * 6 * 3 + 5] = (short) ((i + 1) * 4 + 0);

            indices[i * 6 * 3 + 6] = (short) (i * 4 + 1);
            indices[i * 6 * 3 + 7] = (short) (i * 4 + 2);
            indices[i * 6 * 3 + 8] = (short) ((i + 1) * 4 + 1);

            indices[i * 6 * 3 + 9] = (short) (i  * 4 + 2);
            indices[i * 6 * 3 + 10] = (short) ((i + 1) * 4 + 2);
            indices[i * 6 * 3 + 11] = (short) ((i + 1) * 4 + 1);

            indices[i * 6 * 3 + 12] = (short) (i * 4 + 2);
            indices[i * 6 * 3 + 13] = (short) (i * 4 + 3);
            indices[i * 6 * 3 + 14] = (short) ((i + 1) * 4 + 2);

            indices[i * 6 * 3 + 15] = (short) (i  * 4 + 3);
            indices[i * 6 * 3 + 16] = (short) ((i + 1) * 4 + 3);
            indices[i * 6 * 3 + 17] = (short) ((i + 1) * 4 + 2);
        }

        mesh.setVertices(verts);
        mesh.setIndices(indices);

        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        //modelBuilder.part("Track", mesh, GL20.GL_TRIANGLES, new Material(new ColorAttribute(ColorAttribute.Diffuse, Color.GRAY)));
        modelBuilder.part("Track", mesh, GL20.GL_TRIANGLES, new Material(new TextureAttribute(TextureAttribute.Diffuse, new Texture("models/path.png"))));
        track = new ModelInstance(modelBuilder.end());
        // Create model from mesh and add it to the track instance.
    }

    public ModelInstance getTrack() {
        return track;
    }

    /**
     * Debug method to visualize verts
     * @param renderer
     */
    public void debugDrawVerts(ShapeRenderer renderer){
        for (int i = 0; i < verts.length; i+= 3) {
            renderer.box(verts[i] - .1f, verts[i + 1] - .1f, verts[i + 2] + .1f, .2f, .2f, .2f);
        }
    }

    /**
     * Debug method to visualize derivative
     * @param renderer
     */
    public void debugDrawDerivative(ShapeRenderer renderer){
        float stepDistance = curveLength / 10;


        for (float t = 0f; t <= 1f; ) {
            curve.valueAt(position, t);
            curve.derivativeAt(derivative, t);

            float len = derivative.len();

            //derivative.scl(1/ len);

            t += stepDistance / len;

            derivative.nor().scl(stepDistance * .25f);

            derivative.rotate(90, -derivative.z,0,  derivative.x);

            renderer.line(position.x, position.y, position.z, position.x + derivative.x, position.y + derivative.y, position.z + derivative.z);
        }

    }


    public float getTrackWidth() {
        return trackWidth;
    }

    public Array<CollisionObject> getCollisionObjects() {
        return collisionObjects;
    }

    public void render(ModelBatch modelBatch, Environment environment){
        // Render everything for now
        modelBatch.render(track, environment);

        modelBatch.render(sideObjectCache, environment);


        for (CollisionObject object : collisionObjects){
            object.draw(modelBatch, environment);
        }


    }

    public void handleCollisions(Player player){
        for (CollisionObject object : collisionObjects){

            if (player.getBounds().intersects(object.getBounds())){
                object.onCollision(player);
            }
        }
    }

    public ModelCache getSideObjectCache() {
        return sideObjectCache;
    }

    public float getCurveLength() {
        return curveLength;
    }

    @Override
    public void reset() {
        collisionObjects.clear();
    }
}
