package net.madmenyo.beerunner;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;
import java.util.List;

/**
 * Tracksection maps the curve to the mesh and holds other data like sides and obstacles.
 */
public class TrackSection implements Disposable {
    private Bezier<Vector3> curve;
    float curveLength;


    private ModelInstance track;

    private Array<ModelInstance> sideObjects = new Array<>();

    Vector3 position = new Vector3();
    Vector3 derivative = new Vector3();

    public TrackSection(Bezier<Vector3> curve) {
        this.curve = curve;
        curveLength = curve.approxLength(100);

    }

    /**
     * Divides points over track, this needs tweaking since end point is not reached.
     * Should either push last point to end or do a second pass to even things out.
     * @param amount
     * @return
     */
    public List<Vector3> dividePoints(int amount){
        List<Vector3> points = new ArrayList<>();

        float stepDistance = curveLength / (float) amount;

        for (float t = 0f; t <= 1f; ){
            curve.valueAt(position, t);
            curve.derivativeAt(derivative, t);

            float len = derivative.len();

            derivative.scl(1/ len);

            t += stepDistance / len;


            points.add(position.cpy());
        }

        return points;
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
        for (int i = 1; i <= 100; i++) {

            float t = i / 100f;
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
    }


    // Somewhat hacky try out of creating a mesh, contains bugs using for reference
    /*
    public Mesh generateMeshOverFullCurve(int samples, float sectionSize, int quadStrips){

        // Should probably store verts in list since I iteratre over curve differently
        sections = (int)(curve.approxLength(samples) / sectionSize);

        System.out.println("Precalc results");
        System.out.println("Length  : " + curve.approxLength(samples));
        System.out.println("Sections: " + sections);

        quadCount = sections * quadStrips;
        // Each line has 4 verts
        vertCount = (sections + 1) * 4;
        indexCount = quadCount * 6;

        mesh = new Mesh(true, vertCount, indexCount,
                new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"),
                new VertexAttribute(VertexAttributes.Usage.Normal, 3, "a_normal"));
        verts = new float[vertCount * vertComponents];

        // float t should start at a offset of previous
        int n = 0;
        float l = 0;
        for (float t = 0; t < 1; ){
            n++;
            // Set position and derivative at t
            curve.valueAt(position, t);
            curve.derivativeAt(derivative, t);

            // normalize derivate and increase t
            float length = derivative.len();
            derivative.scl(1/ length);

            l += length;

            // step t using section size

            t += sectionSize / length;
            if (t > 1){
                t = 1;
                curve.valueAt(position, 1);
                curve.derivativeAt(derivative, 1);
            }

            System.out.println(t);
            // set vertices of first step
            //for now hardcode for 3 quads 4 verts

            // should flatten out derivative horizontally
            derivative.y = 0;
            derivative.nor();

            derivative.rotate(Vector3.Y, 90);
            // since the path is centered we should offset 1,5
            position.add(tmp.set(derivative).scl(sectionSize * 1.5f));

            derivative.rotate(Vector3.Y, 180);

            // now loop and use derivative and offset it each time by derivative
            // Possible to create a curvature over the width of the path by raising the outer verts
            for (int i = 0; i < 4; i++) {
                addVert(position, Vector3.Y);
                position.add(tmp.set(derivative).scl(sectionSize));
            }

        }
        System.out.println("Looping results");
        System.out.println("Length: " + l);
        System.out.println("Sections " + (n - 1));
        mesh.setVertices(verts);

        short[] indices = new short[indexCount];
        // Each section has 4 bottom and 4 top verts
        // Each section has 3 quads and thus needs 6 * 3 indices
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
        mesh.setIndices(indices);

        System.out.println(sections);
        System.out.println(n);
        System.out.println(vertCount);
        System.out.println(index);
        return mesh;
    }


    private void addVert(Vector3 position, Vector3 normal) {
        if (index >= vertCount * vertComponents) return;
        verts[index++] = position.x;
        verts[index++] = position.y;
        verts[index++] = position.z;

        verts[index++] = normal.x;
        verts[index++] = normal.y;
        verts[index++] = normal.z;
    }

    public void drawVerts(ShapeRenderer shapeRenderer){
        shapeRenderer.setColor(Color.CYAN);
        float vertSize = .2f;
        for (int i = 0; i < verts.length; i += vertComponents) {


            //shapeRenderer.box(verts[i + 0] - vertSize / 2f, verts[i + 1] - vertSize / 2f, verts[i + 2] - vertSize / 2f,
            //        vertSize, vertSize, vertSize);

            shapeRenderer.box(verts[i + 0] - vertSize / 2f, verts[i + 1] - vertSize / 2f, verts[i + 2] + vertSize / 2f,
                    vertSize, vertSize, vertSize);
        }
    }
     */
}
