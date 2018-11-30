package model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.SubScene;
import javafx.scene.input.MouseButton;
import javafx.scene.transform.Rotate;

/**
 * Responsible for controlling 3D Cubes rotation with mouse. Also highlights the selected face.
 */
public class MouseControl {
    private double anchorX = 0;
    private double anchorY = 0;
    private double oldAngleX = 0;
    private double oldAngleY = 0;
    private DoubleProperty angleX;
    private DoubleProperty angleY;

    private SmartBox box;
    private SmartBox selection;
    private int selectionX = 0;
    private int selectionY = 0;
    private SubScene scene;

    public MouseControl(SmartBox box, SmartBox selection, SubScene scene) {
        this.box = box;
        this.selection = selection;
        this.scene = scene;

        angleX = new SimpleDoubleProperty(0);
        angleY = new SimpleDoubleProperty(0);

        initBoxControl();
    }

    private void initBoxControl() {
        Rotate x = new Rotate(0, Rotate.X_AXIS);
        Rotate y = new Rotate(0, Rotate.Y_AXIS);
        Rotate selectionX = new Rotate(0, 0, 0, 50, Rotate.X_AXIS);
        Rotate selectionY = new Rotate(0, 0, 0, 50, Rotate.Y_AXIS);
        Rotate extraX = new Rotate(0, 0, 0, 50, Rotate.X_AXIS);
        Rotate extraY = new Rotate(0, 0, 0, 50, Rotate.Y_AXIS);

        box.getTransforms().addAll(
                x, y
        );

        selection.getTransforms().addAll(
                selectionX, selectionY, extraY, extraX
        );

        x.angleProperty().bind(angleX);
        y.angleProperty().bind(angleY);
        selectionX.angleProperty().bind(x.angleProperty());
        selectionY.angleProperty().bind(y.angleProperty());

        scene.setOnMousePressed(event -> {
            if(event.getButton() == MouseButton.PRIMARY) {
                anchorX = event.getSceneX();
                anchorY = event.getSceneY();
                oldAngleX = angleX.get();
                oldAngleY = angleY.get();
            }
        });

        scene.setOnMouseDragged(event -> {
            if(event.getButton() == MouseButton.PRIMARY) {
                double angleXtoSet = oldAngleX - ((anchorY - event.getSceneY()) / 4);
                double angleYtoSet;

                angleXtoSet = angleXtoSet % 360;

                if (Math.abs(oldAngleX) >= 90 && Math.abs(oldAngleX) <= 270) {  // -270 -- -90
                    angleYtoSet = oldAngleY - ((anchorX - event.getSceneX()) / 4);
                } else {
                    angleYtoSet = oldAngleY + ((anchorX - event.getSceneX()) / 4);
                }

                angleYtoSet = angleYtoSet % 360;

                angleX.set(angleXtoSet);
                angleY.set(angleYtoSet);

                System.out.println(box.getTransforms().get(0));
                System.out.println(box.getTransforms().get(1));

                int addX = ((int) (angleXtoSet / Math.abs(angleXtoSet))) * 45;
                int addY = ((int) (angleYtoSet / Math.abs(angleYtoSet))) * 45;
                angleXtoSet = angleXtoSet % 315;
                angleYtoSet = angleYtoSet % 315;
                int extraXRotation = (int)((angleXtoSet + addX) / 90) * 90;
                int extraYRotation = (int)((angleYtoSet + addY) / 90) * 90;

                extraX.setAngle(-extraXRotation);
                extraY.setAngle(-extraYRotation);

                setSelectionXY(extraXRotation, extraYRotation);
            }
        });

        scene.setOnScroll(event -> {
            box.translateZProperty().set(box.getTranslateZ() - event.getDeltaY());
        });
    }

    private void setSelectionXY(int extraXRotation, int extraYRotation) {
        if(extraXRotation < 0) {
            selectionX = -extraXRotation;
        } else {
            selectionX = extraXRotation;
        }

        if(extraYRotation < 0) {
            selectionY = -extraYRotation;
        } else {
            selectionY = extraYRotation;
        }
    }

    public int[] getSelectionXY() {
        return new int[]{selectionX, selectionY};
    }

}
