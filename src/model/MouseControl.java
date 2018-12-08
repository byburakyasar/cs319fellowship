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
    private double strictOldAngleX = 0;
    private double strictOldAngleY = 0;
    private DoubleProperty angleX;
    private DoubleProperty angleY;

    private SmartBox box;
    private SmartBox selectionBox;
    private SubScene scene;
    private CubeFaces cubeface = CubeFaces.FACE_FRONT;
    private int imageLoc = 2;

    public MouseControl(SmartBox box, SmartBox selectionBox, SubScene scene) {
        this.box = box;
        this.selectionBox = selectionBox;
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

        selectionBox.getTransforms().addAll(
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
                strictOldAngleX = angleX.get();
                strictOldAngleY = angleY.get();
            }
        });

        scene.setOnMouseDragged(event -> {
            if(event.getButton() == MouseButton.PRIMARY) {
                double angleXtoSet = 0;
                double angleYtoSet = 0;

                double dragAmountY = (anchorY - event.getSceneY()) / 4; // changes angleX
                double dragAmountX = (anchorX - event.getSceneX()) / 4; // changes angleY

                angleXtoSet = oldAngleX - dragAmountY;

                if(angleX.get() > 180) {
                    oldAngleX = -180;
                    anchorY = event.getSceneY();
                }
                if(angleX.get() < -180) {
                    oldAngleX = 180;
                    anchorY = event.getSceneY();
                }

                if (Math.abs(strictOldAngleX) >= 90) {
                    angleYtoSet = oldAngleY - dragAmountX;
                } else {
                    angleYtoSet = oldAngleY + dragAmountX;
                }

                if(angleY.get() > 180) {
                    oldAngleY = -180;
                    anchorX = event.getSceneX();
                }
                if(angleY.get() < -180) {
                    oldAngleY = 180;
                    anchorX = event.getSceneX();
                }

                angleX.set(angleXtoSet);
                angleY.set(angleYtoSet);

                Rotate rX = (Rotate) box.getTransforms().get(0);
                Rotate rY = (Rotate) box.getTransforms().get(1);
                //System.out.println(rX.getAngle() + "\t" + rY.getAngle());

                int addX = ((int) (angleXtoSet / Math.abs(angleXtoSet))) * 45;
                int addY = ((int) (angleYtoSet / Math.abs(angleYtoSet))) * 45;
                int extraXRotation = (int)((angleXtoSet + addX) / 90) * 90;
                int extraYRotation = (int)((angleYtoSet + addY) / 90) * 90;

                extraX.setAngle(-extraXRotation);
                extraY.setAngle(-extraYRotation);

                int locX = extraXRotation / 90;
                int locY = extraYRotation / 90;
                //System.out.println(locX + " " + locY);
                if((Math.abs(locX) == 0 && Math.abs(locY) == 0) || (Math.abs(locX) == 2 && Math.abs(locY) == 2)) {
                    System.out.println("front");
                    cubeface = CubeFaces.FACE_FRONT;
                    imageLoc = 2;
                } else if((Math.abs(locX) == 0 && Math.abs(locY) == 2) || (Math.abs(locX) == 2 && Math.abs(locY) == 0)) {
                    System.out.println("back");
                    cubeface = CubeFaces.FACE_BACK;
                    imageLoc = 5;
                } else if((Math.abs(locX) == 0 && locY == 1) || (Math.abs(locX) == 2 && locY == -1)) {
                    System.out.println("right");
                    cubeface = CubeFaces.FACE_RIGHT;
                    imageLoc = 4;
                } else if((Math.abs(locX) == 0 && locY == -1) || (Math.abs(locX) == 2 && locY == 1)) {
                    System.out.println("left");
                    cubeface = CubeFaces.FACE_LEFT;
                    imageLoc = 1;
                } else if(locX == 1) {
                    System.out.println("up");
                    cubeface = CubeFaces.FACE_UP;
                    imageLoc = 0;
                } else if(locX == -1) {
                    System.out.println("down");
                    cubeface = CubeFaces.FACE_DOWN;
                    imageLoc = 3;
                }
            }
        });

        scene.setOnScroll(event -> {
            box.translateZProperty().set(box.getTranslateZ() - event.getDeltaY());
        });
    }

    public CubeFaces getCubeFace() {
        return cubeface;
    }

    public int getImageLoc() {
        return imageLoc;
    }
}
