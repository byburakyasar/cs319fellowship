package controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Button;
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
import java.util.Vector;

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
    @FXML private VBox leftVBox;
    @FXML private VBox rightVBox;
    @FXML private HBox multiplayerHBox;

    // Technical
    private IntegerProperty numOfMoves = new SimpleIntegerProperty(0);
    private long curGameTime = 0;
    private DateFormat dateFormat = new SimpleDateFormat( "mm:ss.SSS");

    // General
    private Player player;
    private int difficulty;
    private int playerCount;
    private int cubeDimension;
    private GameOptionsController.GameModes gameMode;
    private Cube cube;
    private Pattern pattern;
    private Game game;
    private MouseControl mc;
    private CubeFaces[][] solutionFaces;
    private final int PATTERN_NO = 6;
    private CubeFaces[] cubeFaces = {CubeFaces.FACE_UP, CubeFaces.FACE_LEFT, CubeFaces.FACE_FRONT,
            CubeFaces.FACE_DOWN, CubeFaces.FACE_RIGHT, CubeFaces.FACE_BACK};

    // Online related
    private Server server;
    private Client client;

    // From Memory
    private int remainingReveals = 3;

    // Against Time
    private int againstTimeLimit = 30000;

    /**
     * Constructs a GameUIController object for single player use.
     * @param player The player will play the game.
     * @param difficulty The board size (3, 4, 5 for 3x3, 4x4, 5x5).
     * @param playerCount The number of players in the game.
     * @param cubeDimension The dimensions of the cube (2, 3 for 2D, 3D)
     */
    public GameUIController(Player player, int difficulty, int playerCount, int cubeDimension, GameOptionsController.GameModes gameMode) {
        this.player = player;
        this.difficulty = difficulty;
        this.playerCount = playerCount;
        this.cubeDimension = cubeDimension;
        this.gameMode = gameMode;
        this.cube = ResourceLoader.getInstance().getPatternPacks().get(PATTERN_NO).getCube();
        this.game = Game.createRandomGame(1, difficulty);
        this.pattern = game.getPattern();
        this.solutionFaces = this.pattern.getPatternGrid();
    }

    /**
     * Constructs a GameUIController object for multi player use for client machine.
     * @param player The player will play the game.
     * @param difficulty The board size (3, 4, 5 for 3x3, 4x4, 5x5).
     * @param playerCount The number of players in the game.
     * @param cubeDimension The dimensions of the cube (2, 3 for 2D, 3D)
     * @param client The client that will play the game.
     * @param game The game object shared between all people in the server.
     */
    public GameUIController(Player player, int difficulty, int playerCount, int cubeDimension, GameOptionsController.GameModes gameMode, Client client, Game game) {
        this.player = player;
        this.difficulty = difficulty;
        this.playerCount = playerCount;
        this.cubeDimension = cubeDimension;
        this.gameMode = gameMode;
        this.cube = ResourceLoader.getInstance().getPatternPacks().get(PATTERN_NO).getCube();
        this.game = game;
        this.pattern = game.getPattern();
        this.client = client;
        this.solutionFaces = this.pattern.getPatternGrid();
    }

    /**
     * Constructs a GameUIController object for multi player use for server machine that also registers as a client.
     * @param player The player will play the game.
     * @param difficulty The board size (3, 4, 5 for 3x3, 4x4, 5x5).
     * @param playerCount The number of players in the game.
     * @param cubeDimension The dimensions of the cube (2, 3 for 2D, 3D)
     * @param server The server that is responsible for handling the clients to play this game.
     * @param client The client that will play the game.
     * @param game The game object shared between all people in the server.
     */
    public GameUIController(Player player, int difficulty, int playerCount, int cubeDimension, GameOptionsController.GameModes gameMode, Server server, Client client, Game game) {
        this.player = player;
        this.difficulty = difficulty;
        this.playerCount = playerCount;
        this.cubeDimension = cubeDimension;
        this.gameMode = gameMode;
        this.cube = ResourceLoader.getInstance().getPatternPacks().get(PATTERN_NO).getCube();
        this.game = game;
        this.pattern = game.getPattern();
        this.server = server;
        this.client = client;
        this.solutionFaces = this.pattern.getPatternGrid();
    }

    public void initialize() {
        switch (gameMode)
        {
            case PATTERN_MATCHING:
                loadPatternMatching(false);
                break;
            case MULTIPLAYER:
                // something like loadRacingAndRolling();
                break;
            case FROM_MEMORY:
                loadPatternMatching(true);
                break;
            case MAXIMUM_PATTERNS:
                break;
            case AGAINST_TIME:
                if (difficulty == 3) againstTimeLimit = 15000;
                else if (difficulty == 4) againstTimeLimit = 30000;
                else if (difficulty == 5) againstTimeLimit = 45000;

                loadPatternMatching(false);
                break;
            case PAINTING_PUZZLE:
                break;
            case DIFFERENT_CUBES:
                break;
            case TWO_VS_TWO:
                break;
        }

    }

    /*private void loadAgainstTime(boolean isFromMemory) {
        // Load the cube based on dimensions
        if (cubeDimension == 2) {
            load2DCube();
        } else if (cubeDimension == 3) {
            load3DCube();
        }



        // Load the game and solution boards
        loadSolutionBoard(isFromMemory);
        loadBoard();

        // This counts time and sets its label
        bindGameTime();

        // This sets the number of moves label
        numOfMovesLabel.textProperty().bind(numOfMoves.asString());

        if (playerCount != 1) {
            // If this is a multiplayer game, make client ready
            client.setGameUIController(this);
            client.readMessageNonBlockedAlways();

            centerVBox.setAlignment(Pos.TOP_CENTER);

            Vector<Player> players = client.getClientPlayers();
            for (int i = 0; i < players.size(); i++) {
                if (!players.get(i).getName().equals(player.getName())) {
                    // Load boards for every other player
                    loadMultiplayerBoards(players.get(i).getName(), players.get(i).getVisibleName());

                    // Add all other players to the game
                    game.addPlayer(players.get(i));
                }
            }
        }

        // Add yourself and start
        game.addPlayer(player);
        game.startGame();
    }*/

    private void loadPatternMatching(boolean isFromMemory) {
        // Load the cube based on dimensions
        if (cubeDimension == 2) {
            load2DCube();
        } else if (cubeDimension == 3) {
            load3DCube();
        }

        // Load the game and solution boards
        loadSolutionBoard(isFromMemory);
        loadBoard();

        // This counts time and sets its label
        bindGameTime();

        // This sets the number of moves label
        numOfMovesLabel.textProperty().bind(numOfMoves.asString());

        if (playerCount != 1) {
            // If this is a multiplayer game, make client ready
            client.setGameUIController(this);
            client.readMessageNonBlockedAlways();

            centerVBox.setAlignment(Pos.TOP_CENTER);

            Vector<Player> players = client.getClientPlayers();
            for (int i = 0; i < players.size(); i++) {
                if (!players.get(i).getName().equals(player.getName())) {
                    // Load boards for every other player
                    loadMultiplayerBoards(players.get(i).getName(), players.get(i).getVisibleName());

                    // Add all other players to the game
                    game.addPlayer(players.get(i));
                }
            }
        }

        // Add yourself and start
        game.addPlayer(player);
        game.startGame();
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

        mc = new MouseControl(box, selection, scene);

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
    private void loadSolutionBoard(boolean isFromMemory) {
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

        if (isFromMemory) {
            loadSolutionForFromMemory();
        }
    }

    /**
     * Handles everything related to from memory game mode, such as:
     * The solution board disappears after X seconds.
     * A reveal button now appears.
     * The player can give up if he can't find the solution.
     */
    private void loadSolutionForFromMemory() {
        Button button = new Button("Reveal Solution (" + remainingReveals + ")");
        button.setId("revealBtn");
        button.setVisible(false);
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (remainingReveals > 0) {
                    setChildenVisibility(solutionGrid, true);
                    remainingReveals--;
                    if (remainingReveals == 0) {
                        button.setVisible(false);
                    } else {
                        button.setText("Reveal Solution (" + remainingReveals + ")");
                    }

                    long timeOnClick = curGameTime / 1000L;

                    Timeline timer = new Timeline();
                    timer.getKeyFrames().add(new KeyFrame(
                            Duration.millis(100), new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            long gameTimeSeconds = curGameTime / 1000L;
                            if (gameTimeSeconds >= timeOnClick + 2) {
                                setChildenVisibility(solutionGrid, false);
                                timer.stop();
                            }
                        }
                    }));

                    timer.setCycleCount(Animation.INDEFINITE);
                    timer.play();
                }
            }
        });

        Timeline timer = new Timeline();
        timer.getKeyFrames().add(new KeyFrame(
                Duration.millis(100), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                long gameTimeSeconds = curGameTime / 1000L;

                // for 3 --> 10s, 4 --> 20s, 5 --> 40s
                if (gameTimeSeconds >= Math.pow(2, difficulty) * (5.0 / 4.0)) {
                    setChildenVisibility(solutionGrid, false);
                    button.setVisible(true);
                    timer.stop();
                }
            }
        }));

        timer.setCycleCount(Animation.INDEFINITE);
        timer.play();

        centerVBox.getChildren().add(button);
    }

    /**
     * Sets the visibility of all children of node {@param node} to {@param visibility}
     * @param node The node whose children will be changed.
     * @param visibility The visibility value to set to.
     */
    private void setChildenVisibility(Pane node, boolean visibility) {
        for (Node n : node.getChildren()) {
            n.setVisible(visibility);
        }
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
                            content.putString(String.valueOf(pane.getUserData()));

                            db.setDragView(img);
                            db.setContent(content);
                            pane.setBackground(null);

                            int row = boardGrid.getRowIndex(pane);
                            int col = boardGrid.getColumnIndex(pane);
                            if (client != null) {
                                client.sendPlayerMove(player.getName(), row, col, null);
                            }
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
                        Image img = db.getImage();
                        int imageLoc = Integer.parseInt(db.getString());

                        pane.setBackground(new Background(new BackgroundImage(img, BackgroundRepeat.NO_REPEAT,
                                BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(BOARD_PANE_SIZE,BOARD_PANE_SIZE,false,false,true,false))));

                        pane.setUserData(db.getString());


                        int row = GridPane.getRowIndex(pane);
                        int col = GridPane.getColumnIndex(pane);
                        CubeFaces cubeFace = cubeFaces[imageLoc];

                        numOfMoves.set(numOfMoves.get() + 1);
                        if (client != null) {
                            client.sendPlayerMove(player.getName(), row, col, cubeFace);
                        }
                        playerPlayed(player.getName(), row, col, cubeFace);

                        event.setDropCompleted(true);
                        event.consume();
                    }
                });

                pane.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (event.getClickCount() == 2) {
                            CubeFaces cubeFace = mc.getCubeFace();
                            Image img = cube.get(cubeFace);
                            int imageLoc = mc.getImageLoc();

                            pane.setBackground(new Background(new BackgroundImage(img, BackgroundRepeat.NO_REPEAT,
                                    BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(BOARD_PANE_SIZE,BOARD_PANE_SIZE,false,false,true,false))));

                            pane.setUserData(imageLoc);

                            int row = GridPane.getRowIndex(pane);
                            int col = GridPane.getColumnIndex(pane);

                            numOfMoves.set(numOfMoves.get() + 1);
                            if (client != null) {
                                client.sendPlayerMove(player.getName(), row, col, cubeFace);
                            }
                            playerPlayed(player.getName(), row, col, cubeFace);
                        }
                    }
                });

                boardGrid.add(pane, i, j);
            }
        }

        boardGrid.setBackground(new Background(new BackgroundImage(new Image("/wood6.png"), BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
    }

    /**
     * Loads the boards of other clients connected to this game.
     * @param playerName The name of the other client
     * @param playerVisibleName The visible (username) name of the other client
     */
    private void loadMultiplayerBoards(String playerName, String playerVisibleName) {
        final int BOARD_PANE_SIZE;
        if (difficulty == 4) {
            BOARD_PANE_SIZE = 50;
        } else if (difficulty == 3) {
            BOARD_PANE_SIZE = 65;
        } else {
            BOARD_PANE_SIZE = 60 - playerCount*7;
        }
        GridPane multiplayerBoard = new GridPane();
        multiplayerBoard.setGridLinesVisible(true);
        multiplayerBoard.setAlignment(Pos.CENTER);
        multiplayerBoard.getStyleClass().add("bordered");

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
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(100, 100, true, true, true, false))));

        Label label = new Label(playerVisibleName);

        VBox multiplayerVBox = new VBox();
        multiplayerVBox.setAlignment(Pos.CENTER);
        multiplayerVBox.getChildren().add(multiplayerBoard);
        multiplayerVBox.getChildren().add(label);
        multiplayerVBox.getStyleClass().add("bordered");

        multiplayerHBox.getChildren().add(multiplayerVBox);
    }

    /**
     * Calls Game.playerMove(..) and checks if there are any winners.
     * If there are any winners, calls handleGameEndMultiplayer().
     * @param playerName Name of the player that made the move.
     * @param row The row of the game board on which the player drag-dropped an image.
     * @param col The col of the game board on which the player drag-dropped an image.
     * @param cubeFace The cube face the player dropped.
     */
    public void playerPlayed(String playerName, int row, int col, CubeFaces cubeFace) {
        game.playerMove(playerName, row, col, cubeFace);

        if(game.hasWinner()) {
            Player winner = game.getWinner();
            long winTime = winner.getEndTime() - game.getStartTime();

            if (playerCount != 1) {
                handleGameEndMultiplayer(winTime, winner, EndController.EndType.NORMAL);
            } else {
                try {
                    loadEndScene(winTime, winner, EndController.EndType.NORMAL);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Loads the end game scene and sends/receives the end time of the winning player
     * to other clients if there is a winner.
     * Closes the always waiting thread of all other clients.
     * Closes the client and server.
     */
    public void handleGameEndMultiplayer(long winTime, Player winner, EndController.EndType endType) {
        if (winner == null) {
        }
        else if (winner.getName().equals(player.getName())) {
            // If you are the winner send your end time to others and close their always waiting thread.
            client.sendPlayerEndTime(winTime);
            long returnedVal = Long.valueOf(client.readMessageBlocked());
        } else {
            // If you are not the winner, wait for the winner player's end time.
            winTime = Long.valueOf(client.readMessageBlocked());
        }

        try {
            client.close();
            if (server != null) {
                server.close();
            }

            loadEndScene(winTime, winner, endType);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Called from the client to set the board face when another client makes a move.
     * Sets the multiplayer board face at row, col with the given cubeFace, for the given player.
     * @param playerName Name of the player to set the board for.
     * @param row Row of the board face.
     * @param col Column of the board face.
     * @param cubeFace Cube face to set the board to.
     * @param clientNo No of the client who made the move.
     */
    public void setBoardFace(String playerName, int row, int col, CubeFaces cubeFace, int clientNo) {
        Scene scene = multiplayerHBox.getScene();
        GridPane multiPane = (GridPane)scene.lookup("#"+playerName);
        System.out.println(playerName + " " + row + " " + col + " " + cubeFace);
        Pane pane = (Pane)multiPane.getChildren().get(col*difficulty + row + 1);
        if (pane != null) {
            if (cubeFace == null) {
                pane.setBackground(null);
            } else {
                pane.setBackground(new Background(new BackgroundImage(cube.get(cubeFace), BackgroundRepeat.NO_REPEAT,
                        BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(100,100,true,true,true,false))));
            }
        }
    }

    /**
     * Loads the end scene onto the current stage.
     * @param winTime The time it took for a player to win the game.
     * @param winner The winning player.
     * @throws IOException May throw exception if the corresponding fxml is not found.
     */
    public void loadEndScene(long winTime, Player winner, EndController.EndType endType) throws IOException {
        Stage current = (Stage) boardGrid.getScene().getWindow();

        FXMLLoader loader = new FXMLLoader();
        EndController endC = new EndController(player, difficulty, playerCount, cubeDimension, gameMode, winTime, winner, endType);
        loader.setController(endC);
        loader.setLocation(getClass().getResource("../view/EndStage.fxml"));

        BorderPane root = loader.load();

        current.getScene().setRoot(root);
    }

    /**
     * Bind the timeLabel with the game time.
     */
    private void bindGameTime() {
        Timeline keepTime = new Timeline();
        keepTime.getKeyFrames().add(new KeyFrame(Duration.millis(10), event -> {
            if (GameOptionsController.GameModes.AGAINST_TIME == gameMode){
                curGameTime = System.currentTimeMillis() - game.getStartTime();
                timeLabel.setText( dateFormat.format( againstTimeLimit - curGameTime));

                if( curGameTime > againstTimeLimit){
                    keepTime.stop();
                    lostAgainstTime();
                }
            }
            else{
                curGameTime = System.currentTimeMillis() - game.getStartTime();
                timeLabel.setText( dateFormat.format(curGameTime));
            }
        }));
        keepTime.setCycleCount(Animation.INDEFINITE);
        keepTime.play();
    }

    /**
     *
     */
    private void lostAgainstTime(){
        try {
            player.lostAgainstTime();
            loadEndScene(curGameTime, null, EndController.EndType.LOST_AGAINST_TIME);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    @FXML
    public void giveUp() {
        if (client != null) {
            client.sendPlayerGiveUp(player.getName());
        } else {
            try {
                loadEndScene(0, null, EndController.EndType.GIVE_UP);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
