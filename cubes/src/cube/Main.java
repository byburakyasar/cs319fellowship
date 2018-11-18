package cube;

import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;
import jfxtras.labs.scene.control.window.Window;
import jfxtras.labs.scene.layout.ScalableContentPane;
import jfxtras.labs.util.event.MouseControlUtil;
import org.fxyz3d.geometry.Point3D;
import org.fxyz3d.shapes.primitives.CuboidMesh;


public class Main extends Application {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 800;

    @Override
    public void start(Stage mainStage) {


        /*
        ScalableContentPane scaledPane = new ScalableContentPane();

        Pane rootPane = scaledPane.getContentPane();
        rootPane.setStyle("-fx-background-color: darkblue");
        */

        // cuboid is like open form of a cube
        CuboidMesh cuboid = new CuboidMesh(50f, 50f, 50f);

        // Point3D point = new Point3D(0,0,0);
        // cuboid.setCenter(point);
        MouseControlUtil.makeDraggable(cuboid);

        // cube is covered with the material
        PhongMaterial mat = new PhongMaterial();

        // faces of a cube
        Image img1 = new Image(getClass().getResourceAsStream("/face1.jpeg"));
        Image img2 = new Image(getClass().getResourceAsStream("/face2.jpeg"));
        Image img3 = new Image(getClass().getResourceAsStream("/face3.jpeg"));
        Image img4 = new Image(getClass().getResourceAsStream("/face4.jpeg"));
        Image img5 = new Image(getClass().getResourceAsStream("/face5.jpeg"));
        Image img6 = new Image(getClass().getResourceAsStream("/face6.jpeg"));

        //
        Image faces = generateFaces(img1,img2,img3,img4,img5,img6);
        mat.setDiffuseMap(faces);
        cuboid.setMaterial(mat);

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
                    group.rotateByZ(60);
                    break;
                case Q:
                    group.rotateByZ(-60);
                    break;
                case S:
                    group.rotateByX(60);
                    break;
                case W:
                    group.rotateByX(-60);
                    break;
                case A:
                    group.rotateByY(60);
                    break;
                case D:
                    group.rotateByY(-60);
                    break;
            }
        });



        mainStage.setTitle("Qbitz");
        mainStage.setScene(myScene);
        mainStage.show();

    }

    private Image generateFaces(Image face1, Image face2, Image face3, Image face4, Image face5, Image face6) {

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);

        ImageView imageView1 = new ImageView(face1);
        imageView1.setRotate(90);
        imageView1.setFitHeight(200);
        imageView1.setFitWidth(200);
        GridPane.setHalignment(imageView1, HPos.CENTER);

        //label1.fitHeightProperty().bind(grid.getColumnConstraints().get(0).maxWidthProperty());

        ImageView imageView2 = new ImageView(face2);
        imageView2.setFitHeight(200);
        imageView2.setFitWidth(200);
        GridPane.setHalignment(imageView2, HPos.CENTER);

        ImageView imageView3 = new ImageView(face3);
        imageView3.setFitHeight(200);
        imageView3.setFitWidth(200);
        GridPane.setHalignment(imageView3, HPos.CENTER);

        ImageView imageView4 = new ImageView(face4);
        imageView4.setFitHeight(200);
        imageView4.setFitWidth(200);
        GridPane.setHalignment(imageView4, HPos.CENTER);

        ImageView imageView5 = new ImageView(face5);
        imageView5.setFitHeight(200);
        imageView5.setFitWidth(200);
        GridPane.setHalignment(imageView5, HPos.CENTER);

        ImageView imageView6 = new ImageView(face6);
        imageView6.setFitHeight(200);
        imageView6.setFitWidth(200);
        imageView6.setRotate(90);
        GridPane.setHalignment(imageView6, HPos.CENTER);

        grid.add(imageView1, 1, 0);
        grid.add(imageView2, 0, 1);
        grid.add(imageView3, 1, 1);
        grid.add(imageView4, 2, 1);
        grid.add(imageView5, 3, 1);
        grid.add(imageView6, 1, 2);

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