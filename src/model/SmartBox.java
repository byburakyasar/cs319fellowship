package model;

import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import org.fxyz3d.shapes.primitives.CuboidMesh;

/**
 * For easily rotating the CuboidMesh (with keyboard)
 */
public class SmartBox extends CuboidMesh {
    Rotate r;
    Transform t = new Rotate();

    public SmartBox(int width, int height, int depth) {
        super(width, height, depth);
    }

    public SmartBox() {
        super();
    }

    public void rotateByX(int angle) {
        r = new Rotate(angle, Rotate.X_AXIS);
        t = t.createConcatenation(r);
        this.getTransforms().clear();
        this.getTransforms().add(t);
    }

    public void rotateByY(int angle) {
        r = new Rotate(angle, Rotate.Y_AXIS);
        t = t.createConcatenation(r);
        this.getTransforms().clear();
        this.getTransforms().add(t);
    }

    public void rotateByZ(int angle) {
        r = new Rotate(angle, Rotate.Z_AXIS);
        t = t.createConcatenation(r);
        this.getTransforms().clear();
        this.getTransforms().add(t);
    }
}
