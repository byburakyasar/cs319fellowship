package cube;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;
import org.fxyz3d.shapes.primitives.CuboidMesh;


public class Main extends Application {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 800;

    @Override
    public void start(Stage mainStage) {
        CuboidMesh cuboid = new CuboidMesh(100, 100, 100);
        PhongMaterial mat = new PhongMaterial();

        Image img1 = new Image(getClass().getResourceAsStream("/face1.jpeg"));
        Image img2 = new Image(getClass().getResourceAsStream("/face2.jpeg"));
        Image img3 = new Image(getClass().getResourceAsStream("/face3.jpeg"));
        Image img4 = new Image(getClass().getResourceAsStream("/face4.jpeg"));
        Image img5 = new Image(getClass().getResourceAsStream("/face5.jpeg"));
        Image img6 = new Image(getClass().getResourceAsStream("/face6.jpeg"));

        Image net = generateNet(img1,img2,img3,img4,img5,img6);
        mat.setDiffuseMap(net);
        cuboid.setMaterial(mat);

        /*
        //Create box
        Box myBox = new Box(40, 40, 40);
        myBox.setMaterial(material);

        Image img = new Image(getClass().getResourceAsStream("/dice.jpeg"));
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseMap(img);

        */

        //Prepare transformable Group container
        SmartGroup group = new SmartGroup();
        group.getChildren().add(cuboid);

        Camera myCamera = new PerspectiveCamera();
        Scene myScene = new Scene(group, WIDTH, HEIGHT);
        myScene.setFill(Color.BLUE);
        myScene.setCamera(myCamera);

        //Move to center of the screen
        group.translateXProperty().set(WIDTH / 2);
        group.translateYProperty().set(HEIGHT / 2);
        group.translateZProperty().set(-1000);

        //Add keyboard control.
        mainStage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            switch (event.getCode()) {
                case E:
                    group.rotateByZ(20);
                    break;
                case Q:
                    group.rotateByZ(-20);
                    break;
                case S:
                    group.rotateByX(20);
                    break;
                case W:
                    group.rotateByX(-20);
                    break;
                case A:
                    group.rotateByY(20);
                    break;
                case D:
                    group.rotateByY(-20);
                    break;
            }
        });

        mainStage.setTitle("Qbitz");
        mainStage.setScene(myScene);
        mainStage.show();
    }

    private Image generateNet(Image face1, Image face2, Image face3, Image face4, Image face5, Image face6) {

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);

        ImageView label1 = new ImageView(face1);
        label1.setRotate(90);
        label1.setFitHeight(200);
        label1.setFitWidth(200);
        //label1.fitHeightProperty().bind(grid.getColumnConstraints().get(0).maxWidthProperty());
        GridPane.setHalignment(label1, HPos.CENTER);

        ImageView label2 = new ImageView(face2);
        label2.setFitHeight(200);
        label2.setFitWidth(200);
        GridPane.setHalignment(label2, HPos.CENTER);

        ImageView label3 = new ImageView(face3);
        label3.setFitHeight(200);
        label3.setFitWidth(200);
        GridPane.setHalignment(label3, HPos.CENTER);

        ImageView label4 = new ImageView(face4);
        label4.setFitHeight(200);
        label4.setFitWidth(200);
        GridPane.setHalignment(label4, HPos.CENTER);

        ImageView label5 = new ImageView(face5);
        label5.setFitHeight(200);
        label5.setFitWidth(200);
        GridPane.setHalignment(label5, HPos.CENTER);

        ImageView label6 = new ImageView(face6);
        label6.setFitHeight(200);
        label6.setFitWidth(200);
        label6.setRotate(90);
        GridPane.setHalignment(label6, HPos.CENTER);

        grid.add(label1, 1, 0);
        grid.add(label2, 0, 1);
        grid.add(label3, 1, 1);
        grid.add(label4, 2, 1);
        grid.add(label5, 3, 1);
        grid.add(label6, 1, 2);

        grid.setGridLinesVisible(true);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(25);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(25);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(25);
        ColumnConstraints col4 = new ColumnConstraints();
        col4.setPercentWidth(25);
        grid.getColumnConstraints().addAll(col1, col2, col3, col4);

        RowConstraints row1 = new RowConstraints();
        row1.setPercentHeight(33.33);
        RowConstraints row2 = new RowConstraints();
        row2.setPercentHeight(33.33);
        RowConstraints row3 = new RowConstraints();
        row3.setPercentHeight(33.33);
        grid.getRowConstraints().addAll(row1, row2, row3);
        grid.setPrefSize(600, 450);

        Scene tmpScene = new Scene(grid);
        tmpScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        return grid.snapshot(null, null);
    }

    public static void main(String[] args) {
        launch(args);
    }

    class SmartGroup extends Group {

        Rotate rotate;
        Transform transform = new Rotate();

        void rotateByX(int angle) {
            rotate = new Rotate(angle, Rotate.X_AXIS);
            transform = transform.createConcatenation(rotate);
            this.getTransforms().clear();
            this.getTransforms().addAll(transform);
        }

        void rotateByY(int angle) {
            rotate = new Rotate(angle, Rotate.Y_AXIS);
            transform = transform.createConcatenation(rotate);
            this.getTransforms().clear();
            this.getTransforms().addAll(transform);
        }

        void rotateByZ(int angle) {
            rotate = new Rotate(angle, Rotate.Z_AXIS);
            transform = transform.createConcatenation(rotate);
            this.getTransforms().clear();
            this.getTransforms().addAll(transform);
        }
    }
}