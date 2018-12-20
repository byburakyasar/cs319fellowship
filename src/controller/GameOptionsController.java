package controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Client;
import model.Game;
import model.Player;
import model.Server;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;


public class GameOptionsController {
    @FXML Button startBtn;
    @FXML Button backToMenuBtn;
    @FXML Button multiplayerBtn;
    @FXML HBox playersHBox;
    @FXML ComboBox<String> gameModesComboBox;
    @FXML ToggleGroup difficultyGroup;
    @FXML ToggleGroup playerGroup;
    @FXML ToggleGroup dimensionGroup;

    Client client;
    Server server;

    private int difficulty = 4;
    private int playerCount = 1;
    private int cubeDimension = 3;
    private GameModes gameMode = GameModes.PATTERN_MATCHING;

    public void initialize() {
        difficultyGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                difficulty = Integer.parseInt(difficultyGroup.getSelectedToggle().getUserData().toString());
            }
        });

        playerGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                playerCount = Integer.parseInt(playerGroup.getSelectedToggle().getUserData().toString());
            }
        });

        dimensionGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                cubeDimension = Integer.parseInt(dimensionGroup.getSelectedToggle().getUserData().toString());
            }
        });

        gameModesComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                Text text = new Text(newValue);

                gameMode = Enum.valueOf(GameModes.class, newValue.toUpperCase().replace(' ', '_').replace('İ', 'I'));
                System.out.println(gameMode);
            }
        });
    }

    @FXML
    public void backToMainMenu() throws IOException {
        Stage current = (Stage) startBtn.getScene().getWindow();
        BorderPane root = FXMLLoader.load(getClass().getResource("../view/MainMenuStage.fxml"));

        current.getScene().setRoot(root);
    }

    @FXML
    public void startGame() throws IOException {
        Stage current = (Stage) startBtn.getScene().getWindow();
        Player player = new Player(UUID.randomUUID().toString(), difficulty);

        TextInputDialog dialog = new TextInputDialog("Player");
        dialog.setTitle(null);
        dialog.setHeaderText(null);
        dialog.setGraphic(null);
        dialog.setContentText("Pick a username:");
        dialog.getDialogPane().lookupButton(ButtonType.CANCEL).setManaged(false);

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(player::setVisibleName);

        if (playerCount == 1) {
            FXMLLoader loader = new FXMLLoader();
            GameUIController gui = new GameUIController(player, difficulty, playerCount, cubeDimension, gameMode);
            loader.setController(gui);
            loader.setLocation(getClass().getResource("../view/GameUIStage.fxml"));
            BorderPane root = loader.load();

            current.getScene().setRoot(root);
            return;
        }

        startBtn.setText("Looking for players...");
        startBtn.setDisable(true);

        client = new Client("localhost", 8000, player);
        boolean foundServer = client.joinServer();

        if (foundServer) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    client.sendPlayer(player);
                    String serverMessage = client.waitUntilGameReady();
                    System.out.println("Client ready with no: " + serverMessage);
                    client.requestClientPlayers();

                    Game obj = client.readObjectBlocked();

                    if (serverMessage != null) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    FXMLLoader loader = new FXMLLoader();
                                    GameUIController gui = new GameUIController(player, difficulty, playerCount, cubeDimension, gameMode, client, obj);
                                    loader.setController(gui);
                                    loader.setLocation(getClass().getResource("../view/GameUIStage.fxml"));
                                    BorderPane root = loader.load();

                                    current.getScene().setRoot(root);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
            }).start();
        } else {
            server = new Server(8000, playerCount, difficulty, cubeDimension, gameMode);
            client.joinServer();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    client.sendPlayer(player);
                    String serverMessage = client.waitUntilGameReady(); // make it so that everyone exits at the same time
                    System.out.println("Client ready with no: " + serverMessage);
                    client.requestClientPlayers();

                    Game game = Game.createRandomGame(playerCount, difficulty);
                    Game obj = client.sendObjectBlocked(game);
                    if (serverMessage != null) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    FXMLLoader loader = new FXMLLoader();
                                    GameUIController gui = new GameUIController(player, difficulty, playerCount, cubeDimension, gameMode, server, client, obj);
                                    loader.setController(gui);
                                    loader.setLocation(getClass().getResource("../view/GameUIStage.fxml"));

                                    BorderPane root = loader.load();

                                    current.getScene().setRoot(root);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
            }).start();
        }
    }
    public enum GameModes{
        GENERAL_INFO,
        PATTERN_MATCHING,
        RACING_AND_ROLLING,
        FROM_MEMORY,
        MAXIMUM_PATTERNS,
        AGAINST_TIME,
        DIFFERENT_CUBES,
        PAINTING_PUZZLE,
        TWO_VS_TWO;
    }

    public void startClientSideThread() {

    }
}
