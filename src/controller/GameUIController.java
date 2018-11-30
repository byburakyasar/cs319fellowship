package controller;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.stage.Stage;
import model.*;

import java.io.IOException;

/**
 * @author Mert Duman
 * @version 17.11.2018
 */
public class GameUIController {
    @FXML private BorderPane mainBorderPane;
    @FXML private GridPane boardPane;
    @FXML private GridPane solutionGrid;
    @FXML private GridPane cubeFacesGrid;
    @FXML private AnchorPane boardAnchorPane;
    @FXML private AnchorPane solutionAnchorPane;

    private Pane[] cubeFacePanes = new Pane[6];
    private CubeFaces[] cubeFaces = {CubeFaces.FACE_UP, CubeFaces.FACE_LEFT, CubeFaces.FACE_FRONT,
                                        CubeFaces.FACE_DOWN, CubeFaces.FACE_RIGHT, CubeFaces.FACE_BACK};

    private int difficulty;
    private int playerCount;
    private Cube cube;
    private Pattern pattern;
    private Game game;

    public GameUIController(int difficulty, int playerCount) {
        this.difficulty = difficulty;
        this.playerCount = playerCount;
        this.cube = ResourceLoader.getInstance().getPatternPacks().get(1).getCube();
        this.game = Game.createRandomGame(1, difficulty);
        this.pattern = game.getPattern();
    }

    public void initialize() {
        game.addPlayer("Player 1");
        game.startGame();

        drawSolutionPattern();

        createCubeFaces();
        bindDragToCubes();
        loadBoardAndBindDrag();
        load3DCube();
    }

    @FXML
    public void backToMainMenu() throws IOException {
        Stage current = (Stage) boardPane.getScene().getWindow();
        BorderPane root = FXMLLoader.load(getClass().getResource("../view/MainMenuStage.fxml"));
        Scene scene = new Scene(root, 1920, 1000);

        current.setScene(scene);
    }

    private void load3DCube() {
        SmartBox box = new SmartBox(100, 100, 100);
        PhongMaterial boxMaterial = new PhongMaterial();
        Image img = generateFaces(cube.get(CubeFaces.FACE_UP), cube.get(CubeFaces.FACE_LEFT), cube.get(CubeFaces.FACE_FRONT),
                                  cube.get(CubeFaces.FACE_DOWN), cube.get(CubeFaces.FACE_RIGHT), cube.get(CubeFaces.FACE_BACK));
        boxMaterial.setDiffuseMap(img);
        box.setMaterial(boxMaterial);

        SmartBox selection = new SmartBox( 105, 105, 1);
        PhongMaterial selectionMaterial = new PhongMaterial();
        selectionMaterial.setDiffuseColor(Color.YELLOW);
        selectionMaterial.setSelfIlluminationMap(new Image(getClass().getResourceAsStream("/yellow.png")));
        selection.setMaterial(selectionMaterial);

        box.setDepthTest(DepthTest.ENABLE);
        selection.setDepthTest(DepthTest.ENABLE);

        Camera camera = new PerspectiveCamera();

        Group group = new Group();
        group.getChildren().add(box);
        group.getChildren().add(selection);

        SubScene scene = new SubScene(group, 400, 400, true, SceneAntialiasing.BALANCED);
        scene.setFill(Color.TRANSPARENT);
        scene.setCamera(camera);

        box.translateXProperty().bind(scene.widthProperty().divide(2));
        box.translateYProperty().bind(scene.heightProperty().divide(2));
        box.translateZProperty().set(-300);

        selection.translateXProperty().bind(scene.widthProperty().divide(2));
        selection.translateYProperty().bind(scene.heightProperty().divide(2));
        selection.translateZProperty().bind(box.translateZProperty().add(-48));

        MouseControl mc = new MouseControl(box, selection, scene);

        box.setOnMouseClicked(event -> {
            if(event.getClickCount() == 2) {
                System.out.println(mc.getSelectionXY()[0] + " " + mc.getSelectionXY()[1]);
            }
        });

        mainBorderPane.setCenter(scene);
    }

    private void drawSolutionPattern() {
        CubeFaces[][] solutionFaces = pattern.getPatternGrid();
        final int SOLUTION_SIZE = 150;

        for(int i = 0; i < difficulty; i++) {
            for(int j = 0; j < difficulty; j++) {
                Image img = cube.get(solutionFaces[i][j]);

                Pane pane = new Pane();
                pane.setPrefSize(SOLUTION_SIZE, SOLUTION_SIZE);
                pane.getStyleClass().add("pane");

                pane.setBackground(new Background(new BackgroundImage(img, BackgroundRepeat.NO_REPEAT,
                        BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(SOLUTION_SIZE, SOLUTION_SIZE, false, false, false, false))));

                solutionGrid.add(pane, j, i);
            }
        }

        solutionAnchorPane.setBackground(new Background(new BackgroundImage(new Image("/wood5.png"), BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(705,705,false,false,false,false))));
    }

    private void createCubeFaces() {
        final int FACE_SIZE = 120;

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                Pane pane = new Pane();
                pane.setPrefSize(FACE_SIZE, FACE_SIZE);
                pane.setMinSize(FACE_SIZE, FACE_SIZE);
                pane.getStyleClass().add("pane");

                cubeFacePanes[(i*3)+j] = pane;

                pane.setBackground(new Background(new BackgroundImage(cube.get(cubeFaces[(i*3)+j]), BackgroundRepeat.NO_REPEAT,
                        BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(FACE_SIZE, FACE_SIZE, false, false, false, false))));

                cubeFacesGrid.add(pane, j, i);
            }
        }
    }

    private void bindDragToCubes() {
        for (int i = 0; i < cubeFacePanes.length; i++) {
            Pane pane = cubeFacePanes[i];
            Image img = cube.get(cubeFaces[i]);
            int imageLoc = i;
            pane.setOnDragDetected(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    Dragboard db = pane.startDragAndDrop(TransferMode.ANY);
                    ClipboardContent content = new ClipboardContent();
                    content.putImage(img);
                    content.putString(imageLoc+"");
                    db.setDragView(img);
                    db.setContent(content);

                    event.consume();
                }
            });
        }
    }

    private void loadBoardAndBindDrag() {
        final int BOARD_PANE_SIZE = 150;
        for(int i = 0; i < difficulty; i++) {
            for(int j = 0; j < difficulty; j++) {
                Pane pane = new Pane();
                pane.setPrefSize(BOARD_PANE_SIZE, BOARD_PANE_SIZE);
                pane.getStyleClass().add("pane");

                pane.setOnDragDetected(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        Dragboard db = pane.startDragAndDrop(TransferMode.ANY);
                        ClipboardContent content = new ClipboardContent();
                        Image img = pane.getBackground().getImages().get(0).getImage();
                        if (img != null) {
                            content.putImage(img);
                            content.putString((String)pane.getUserData());

                            db.setDragView(img);
                            db.setContent(content);
                            pane.setBackground(null);

                            int row = boardPane.getRowIndex(pane);
                            int col = boardPane.getColumnIndex(pane);
                            game.playerMove("Player 1", row, col, null);
                        }

                        event.consume();
                    }
                });

                pane.setOnDragOver(new EventHandler<DragEvent>() {
                    @Override
                    public void handle(DragEvent event) {
                        event.acceptTransferModes(TransferMode.ANY);
                        event.consume();
                    }
                });

                pane.setOnDragDropped(new EventHandler<DragEvent>() {
                    @Override
                    public void handle(DragEvent event) {
                        Dragboard db = event.getDragboard();
                        pane.setBackground(new Background(new BackgroundImage(db.getImage(), BackgroundRepeat.NO_REPEAT,
                                BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(BOARD_PANE_SIZE,BOARD_PANE_SIZE,false,false,false,false))));

                        pane.setUserData(db.getString());
                        playerPlayed(pane, Integer.parseInt(db.getString()));
                        event.setDropCompleted(true);
                        event.consume();
                    }
                });

                boardPane.add(pane, i, j);
            }
        }

        boardPane.setBackground(new Background(new BackgroundImage(new Image("/wood6.png"), BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(705,705,false,false,false,false))));
        boardAnchorPane.setBackground(new Background(new BackgroundImage(new Image("/wood5.png"), BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(705,705,false,false,false,false))));
    }

    private void playerPlayed(Pane pane, int imageLoc) {
        int row = boardPane.getRowIndex(pane);
        int col = boardPane.getColumnIndex(pane);
        game.playerMove("Player 1", row, col, cubeFaces[imageLoc]);
        System.out.println(row + " " + col + " " + cubeFaces[imageLoc]);

        if(game.hasWinner()) {
            Player winner = game.getWinner();
            long winTime = winner.getEndTime() - game.getStartTime();

            System.out.println(winner);
            System.out.println(winTime/1000.0 + " seconds.");

            try {
                loadEndStage(winTime, winner);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadEndStage(long winTime, Player winner) throws IOException {
        Stage current = (Stage)boardPane.getScene().getWindow();

        FXMLLoader loader = new FXMLLoader();
        EndController endC = new EndController(difficulty, playerCount, winTime, winner);
        loader.setController(endC);
        loader.setLocation(getClass().getResource("../view/EndStage.fxml"));

        BorderPane root = loader.load();
        Scene scene = new Scene(root, 1920, 1000);

        current.setScene(scene);
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
        //tmpScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        return grid.snapshot(null, null);
    }

    public int getDifficulty() {
        return difficulty;
    }
}
