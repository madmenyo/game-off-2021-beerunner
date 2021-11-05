package net.madmenyo.beerunner;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Vector3;

public class TrackPiece {

    private Bezier<Vector3> curve;

    // previous curve to line it up, since we use world steps each mesh comes a little short
    private Bezier<Vector3> previousCurve;

    private Mesh mesh;

    private int sections;
    private int quadCount;
    private int vertCount;
    private int indexCount;
    private int vertComponents = 6;

    private int index = 0;
    float[] verts;

    private Vector3 position = new Vector3();
    private Vector3 derivative = new Vector3();
    private Vector3 tmp = new Vector3();

    public TrackPiece(Bezier<Vector3> curve) {
        this.curve = curve;

    }

    public ModelInstance getInstance(){
        generateMeshOverFullCurve(100, 5, 3);

        Material mat = new Material(new ColorAttribute(ColorAttribute.Diffuse, Color.GRAY));
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        modelBuilder.part("track", mesh, GL20.GL_TRIANGLES, mat);
        Model model = modelBuilder.end();

        return new ModelInstance(model);
    }

    /**
     * This generates a mesh from a curve. It iterates the curve by length using t and seems buggy
     * with extreme curves
     * @param samples
     * @param sectionSize
     * @param quadStrips
     * @return
     */
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



    /**
     * Adds a vert to the mesh
     * @param position position of the vert
     * @param normal the normal of the vert
     */
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

            /*
            shapeRenderer.box(verts[i + 0] - vertSize / 2f, verts[i + 1] - vertSize / 2f, verts[i + 2] - vertSize / 2f,
                    vertSize, vertSize, vertSize);

             */
            shapeRenderer.box(verts[i + 0] - vertSize / 2f, verts[i + 1] - vertSize / 2f, verts[i + 2] + vertSize / 2f,
                    vertSize, vertSize, vertSize);
        }
    }
}
