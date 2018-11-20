package controller;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.*;

import java.io.IOException;

/**
 * @author Mert Duman
 * @version 17.11.2018
 */
public class GameUIController {
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
    }

    @FXML
    public void backToMainMenu() throws IOException {
        Stage current = (Stage) boardPane.getScene().getWindow();
        BorderPane root = FXMLLoader.load(getClass().getResource("../view/MainMenuStage.fxml"));
        Scene scene = new Scene(root, 1920, 1000);

        current.setScene(scene);
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
        Image[] faceImages = {
                cube.get(CubeFaces.FACE_UP), cube.get(CubeFaces.FACE_LEFT), cube.get(CubeFaces.FACE_FRONT),
                cube.get(CubeFaces.FACE_DOWN), cube.get(CubeFaces.FACE_RIGHT), cube.get(CubeFaces.FACE_BACK)
        };

        for (int i = 0; i < cubeFacePanes.length; i++) {
            Pane pane = cubeFacePanes[i];
            Image img = faceImages[i];
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

                        playerPlayed(pane, Integer.parseInt(db.getString()));
                        event.setDropCompleted(true);
                        event.consume();
                    }
                });

                /*pane.setBackground(new Background(new BackgroundImage(new Image("/wood6.png"), BackgroundRepeat.NO_REPEAT,
                        BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(150,150,false,false,false,false))));*/
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

    public int getDifficulty() {
        return difficulty;
    }
}
