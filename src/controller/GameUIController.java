package controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.*;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * @author Mert Duman
 * @version 17.11.2018
 */
public class GameUIController {
    @FXML private BorderPane mainBorderPane;
    @FXML private GridPane boardGrid;
    @FXML private GridPane solutionGrid;
    @FXML private GridPane cubeFacesGrid;
    @FXML private AnchorPane boardAnchorPane;
    @FXML private AnchorPane solutionAnchorPane;
    @FXML private Label timeLabel;
    @FXML private Label numOfMovesLabel;
    @FXML private VBox centerVBox;
    @FXML private HBox multiplayerHBox;

    private CubeFaces[] cubeFaces = {CubeFaces.FACE_UP, CubeFaces.FACE_LEFT, CubeFaces.FACE_FRONT,
                                        CubeFaces.FACE_DOWN, CubeFaces.FACE_RIGHT, CubeFaces.FACE_BACK};

    private IntegerProperty numOfMoves = new SimpleIntegerProperty(0);
    private long curGameTime = 0;
    private DateFormat dateFormat = new SimpleDateFormat( "mm:ss.SSS");

    private int difficulty;
    private int playerCount;
    private int cubeDimension;
    private Cube cube;
    private Pattern pattern;
    private Game game;
    private CubeFaces[][] solutionFaces;

    private Player player;
    private Server server;
    private Client client;

    public GameUIController(Player player, int difficulty, int playerCount, int cubeDimension) {
        this.player = player;
        this.difficulty = difficulty;
        this.playerCount = playerCount;
        this.cubeDimension = cubeDimension;
        this.cube = ResourceLoader.getInstance().getPatternPacks().get(1).getCube();
        this.game = Game.createRandomGame(1, difficulty);
        this.pattern = game.getPattern();
        this.solutionFaces = this.pattern.getPatternGrid();
    }

    // Client
    public GameUIController(Player player, int difficulty, int playerCount, int cubeDimension, Client client, Game game) {
        this.player = player;
        this.difficulty = difficulty;
        this.playerCount = playerCount;
        this.cubeDimension = cubeDimension;
        this.cube = ResourceLoader.getInstance().getPatternPacks().get(1).getCube();
        this.game = game;
        this.pattern = game.getPattern();
        this.client = client;
        this.solutionFaces = this.pattern.getPatternGrid();
    }

    // Server
    public GameUIController(Player player, int difficulty, int playerCount, int cubeDimension, Server server, Client client, Game game) {
        this.player = player;
        this.difficulty = difficulty;
        this.playerCount = playerCount;
        this.cubeDimension = cubeDimension;
        this.cube = ResourceLoader.getInstance().getPatternPacks().get(1).getCube();
        this.game = game;
        this.pattern = game.getPattern();
        this.server = server;
        this.client = client;
        this.solutionFaces = this.pattern.getPatternGrid();
    }

    public void initialize() {
        game.addPlayer("Player 1");
        game.startGame();

        if (cubeDimension == 2) {
            load2DCube();
        } else if (cubeDimension == 3) {
            load3DCube();
        }

        loadSolutionPattern();
        loadBoard();

        /* This counts time and sets its label */
        Timeline keepTime = new Timeline(new KeyFrame(Duration.millis(10), event -> {
            curGameTime = System.currentTimeMillis() - game.getStartTime();
            timeLabel.setText( dateFormat.format(curGameTime));
        }));
        keepTime.setCycleCount(Animation.INDEFINITE);
        keepTime.play();

        /* This sets the number of moves label */
        numOfMovesLabel.textProperty().bind(numOfMoves.asString());

        if (playerCount != 1) {
            client.setGameUIController(this);
            client.readMessageNonBlockedAlways();

            centerVBox.setAlignment(Pos.TOP_CENTER);

            for (String name : client.getClientPlayerNames()) {
                if (!name.equals(player.getName())) {
                    loadMultiplayerBoards(name);
                }
            }
        }
    }

    /**
     * Loads the main menu scene onto the current stage.
     * @throws IOException May throw exception if the corresponding fxml is not found.
     */
    @FXML
    public void backToMainMenu() throws IOException {
        Stage current = (Stage) boardGrid.getScene().getWindow();
        BorderPane root = FXMLLoader.load(getClass().getResource("../view/MainMenuStage.fxml"));

        current.getScene().setRoot(root);
    }

    /**
     * Loads the 3DCube and its selectionBox onto a SubScene and makes that a
     * child of the mainBorderPane (BorderPane with id=mainBorderPane).
     * Also creates a perspective camera for the 3DCube and creates all of its (3DCube)
     * mouse control settings and drag events.
     */
    private void load3DCube() {
        SmartBox box = new SmartBox(100, 100, 100);
        PhongMaterial boxMaterial = new PhongMaterial();
        Image img = generateFaces(cube.get(CubeFaces.FACE_UP), cube.get(CubeFaces.FACE_RIGHT), cube.get(CubeFaces.FACE_BACK),
                                  cube.get(CubeFaces.FACE_LEFT), cube.get(CubeFaces.FACE_FRONT), cube.get(CubeFaces.FACE_DOWN));
        boxMaterial.setDiffuseMap(img);
        box.setMaterial(boxMaterial);

        SmartBox selection = new SmartBox( 105, 105, 1);
        PhongMaterial selectionMaterial = new PhongMaterial();
        selectionMaterial.setDiffuseColor(Color.rgb(255, 87, 0));
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
                System.out.println(mc.getCubeFace());
            }
        });

        box.setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton() == MouseButton.SECONDARY) {
                    Dragboard db = box.startDragAndDrop(TransferMode.ANY);
                    ClipboardContent content = new ClipboardContent();
                    CubeFaces cubeface = mc.getCubeFace();
                    int imageLoc = mc.getImageLoc();

                    content.putImage(cube.get(cubeface));
                    content.putString(imageLoc+"");

                    db.setDragView(cube.get(cubeface));
                    db.setContent(content);
                }
            }
        });

        centerVBox.getChildren().add(2, scene);
    }

    /**
     * Loads the 2DCube faces and adds them as the children of cubeFacesGrid (GridPane with id=cubeFacesGrid).
     * Gets the corresponding backgrounds for those faces from the Cube class and sets them.
     * Sets on drag events for each cube face.
     */
    private void load2DCube() {
        final int FACE_SIZE = 120;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                Pane pane = new Pane();
                pane.setPrefSize(FACE_SIZE, FACE_SIZE);
                pane.setMinSize(FACE_SIZE, FACE_SIZE);
                pane.getStyleClass().add("pane");

                int imageLoc = (i*2)+j;
                Image img = cube.get(cubeFaces[imageLoc]);

                pane.setBackground(new Background(new BackgroundImage(img, BackgroundRepeat.NO_REPEAT,
                        BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(FACE_SIZE, FACE_SIZE, false, false, false, false))));

                cubeFacesGrid.add(pane, j, i);

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
    }

    /**
     * Gets the current random pattern from the Pattern class and creates a
     * Pane for each pattern face (of size difficulty by difficulty).
     * Gets the corresponding backgrounds for those faces from the Cube class and sets them.
     * Sets the background for the solution board.
     */
    private void loadSolutionPattern() {
        final int SOLUTION_SIZE = 120;

        for (int i = 0; i < difficulty; i++) {
            for (int j = 0; j < difficulty; j++) {
                Image img = cube.get(solutionFaces[i][j]);

                Pane pane = new Pane();
                pane.setPrefSize(SOLUTION_SIZE, SOLUTION_SIZE);
                pane.getStyleClass().add("pane");

                pane.setBackground(new Background(new BackgroundImage(img, BackgroundRepeat.NO_REPEAT,
                        BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(SOLUTION_SIZE, SOLUTION_SIZE, false, false, false, false))));

                solutionGrid.add(pane, j, i);
            }
        }

//        solutionAnchorPane.setBackground(new Background(new BackgroundImage(new Image("/wood5.png"), BackgroundRepeat.NO_REPEAT,
//                BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(100,100,true,true,true,true))));

    }

    /**
     * Creates a pane for each pattern face (of size difficulty by difficulty)
     * and adds them to boardGrid (BoardPane with id=boardGrid).
     * Sets all of the drag bindings for the board faces. This includes
     * dropping an image onto the board from 2DCube, 3DCube or another face of the board.
     */
    private void loadBoard() {
        final int BOARD_PANE_SIZE = 120;
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

                        Background bg = pane.getBackground();
                        Image img = bg != null ? bg.getImages().get(0).getImage() : null;
                        if (img != null) {
                            content.putImage(img);
                            content.putString((String)pane.getUserData());

                            db.setDragView(img);
                            db.setContent(content);
                            pane.setBackground(null);

                            int row = boardGrid.getRowIndex(pane);
                            int col = boardGrid.getColumnIndex(pane);
                            game.playerMove(player.getName(), row, col, null);
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
                                BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(BOARD_PANE_SIZE,BOARD_PANE_SIZE,false,false,true,false))));

                        pane.setUserData(db.getString());


                        int row = GridPane.getRowIndex(pane);
                        int col = GridPane.getColumnIndex(pane);
                        CubeFaces cubeFace = cubeFaces[Integer.parseInt(db.getString())];
                        playerPlayed(row, col, cubeFace);
                        event.setDropCompleted(true);
                        event.consume();

                        numOfMoves.set(numOfMoves.get() + 1);
                        client.sendPlayerMove(player.getName(), row, col, cubeFace);
                    }
                });

                boardGrid.add(pane, i, j);
            }
        }

        boardGrid.setBackground(new Background(new BackgroundImage(new Image("/wood6.png"), BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
//        boardAnchorPane.setBackground(new Background(new BackgroundImage(new Image("/wood5.png"), BackgroundRepeat.NO_REPEAT,
//                BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(705,705,false,false,false,false))));
    }

    private void loadMultiplayerBoards(String playerName) {
        final int BOARD_PANE_SIZE = 50;
        GridPane multiplayerBoard = new GridPane();
        multiplayerBoard.setGridLinesVisible(true);
        multiplayerBoard.setAlignment(Pos.CENTER);
        for(int i = 0; i < difficulty; i++) {
            for(int j = 0; j < difficulty; j++) {
                Pane pane = new Pane();
                pane.setPrefSize(BOARD_PANE_SIZE, BOARD_PANE_SIZE);
                pane.getStyleClass().add("pane");

                multiplayerBoard.add(pane, i, j);
            }
        }

        multiplayerBoard.setId(playerName+"");
        multiplayerBoard.setBackground(new Background(new BackgroundImage(new Image("/wood6.png"), BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
        System.out.println("#"+multiplayerBoard.getId());
        multiplayerHBox.getChildren().add(multiplayerBoard);
    }

    /**
     * Calls Game.playerMove(..) and checks if there are any winners.
     * If there are any winners, loads the EndScene.
     * @param row The row of the game board on which the player drag-dropped an image.
     * @param col The col of the game board on which the player drag-dropped an image.
     * @param cubeFace The cube face the player dropped.
     */
    private void playerPlayed(int row, int col, CubeFaces cubeFace) {
        game.playerMove("Player 1", row, col, cubeFace);
        System.out.println(row + " " + col + " " + cubeFace);

        if(game.hasWinner()) {
            Player winner = game.getWinner();
            long winTime = winner.getEndTime() - game.getStartTime();

            try {
                loadEndScene(winTime, winner);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setBoardFace(String playerName, int row, int col, CubeFaces cubeFace, int clientNo) {
        Scene scene = multiplayerHBox.getScene();
        System.out.println("looking for id: #" + playerName);
        GridPane multiPane = (GridPane)scene.lookup("#"+playerName);
        Pane pane = (Pane)multiPane.getChildren().get(col*difficulty + row + 1);
        if (pane != null) {
            pane.setBackground(new Background(new BackgroundImage(cube.get(cubeFace), BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(100,100,true,true,true,false))));

            playerPlayed(row, col, cubeFace);
        }
    }

    /**
     * Loads the end scene onto the current stage.
     * @param winTime The time it took for a player to win the game.
     * @param winner The winning player.
     * @throws IOException May throw exception if the corresponding fxml is not found.
     */
    private void loadEndScene(long winTime, Player winner) throws IOException {
        Stage current = (Stage) boardGrid.getScene().getWindow();

        FXMLLoader loader = new FXMLLoader();
        EndController endC = new EndController(player, difficulty, playerCount, cubeDimension, winTime, winner);
        loader.setController(endC);
        loader.setLocation(getClass().getResource("../view/EndStage.fxml"));

        BorderPane root = loader.load();

        current.getScene().setRoot(root);
    }

    /**
     * Generates a grid with the input images in the shape of an open cube.
     * Then takes a snapshot of the full image and returns it.
     * @param face1
     * @param face2
     * @param face3
     * @param face4
     * @param face5
     * @param face6
     * @return Snapshot of the full image.
     */
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
}
