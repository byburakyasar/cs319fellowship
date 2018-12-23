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
import model.*;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;


public class GameOptionsController {
    @FXML Button startBtn;
    @FXML Button backToMenuBtn;
    @FXML Button multiplayerBtn;
    @FXML HBox playersHBox;
    @FXML HBox difficultyHBox;
    @FXML HBox dimensionsHBox;
    @FXML ComboBox<String> gameModesComboBox;
    @FXML ToggleGroup difficultyGroup;
    @FXML ToggleGroup playerGroup;
    @FXML ToggleGroup dimensionGroup;

    MainClient mainClient;
    GameClient gameClient;
    HostServer hostServer;

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
                switch (gameMode) {
                    case TWO_VS_TWO:
                        for (int i = 0; i < playerGroup.getToggles().size(); i++) {
                            if (i != 3) {
                                ((RadioButton) playerGroup.getToggles().get(i)).setDisable(true);
                            } else {
                                ((RadioButton) playerGroup.getToggles().get(i)).setDisable(false);
                                ((RadioButton) playerGroup.getToggles().get(i)).setSelected(true);
                            }
                            playerCount = 4;
                        }
                        break;
                    case AGAINST_TIME:
                        for (int i = 0; i < playerGroup.getToggles().size(); i++) {
                            if (i != 0) {
                                ((RadioButton) playerGroup.getToggles().get(i)).setDisable(true);
                            } else {
                                ((RadioButton) playerGroup.getToggles().get(i)).setDisable(false);
                                ((RadioButton) playerGroup.getToggles().get(i)).setSelected(true);
                            }
                            playerCount = 1;
                        }
                        break;
                    default:
                        for (int i = 0; i < playerGroup.getToggles().size(); i++) {
                            ((RadioButton) playerGroup.getToggles().get(i)).setDisable(false);
                        }
                        break;
                }
            }
        });
    }

    @FXML
    public void backToMainMenu() throws IOException {
        Stage current = (Stage) startBtn.getScene().getWindow();

        BorderPane root = FXMLLoader.load(getClass().getResource("/view/MainMenuStage.fxml"));

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
            loader.setLocation(getClass().getResource("/view/GameUIStage.fxml"));
            BorderPane root = loader.load();

            current.getScene().setRoot(root);
            return;
        }

        startBtn.setText("Looking for players...");
        startBtn.setDisable(true);

        mainClient = new MainClient("localhost", 50000);
        mainClient.joinServer();
        Vector<ServerInfo> matchingServers = mainClient.getMatchingServers(playerCount, difficulty, cubeDimension, String.valueOf(gameMode));

        boolean foundServer = false;
        for (ServerInfo si : matchingServers) {
            System.out.println(si);
        }

        if (matchingServers.size() == 0) {
            int port = ThreadLocalRandom.current().nextInt(15000, 64000);
            hostServer = new HostServer(port, playerCount, difficulty, cubeDimension, gameMode, mainClient);
            mainClient.setHostServer(hostServer);
            mainClient.sendServerInfo();
            gameClient = new GameClient("localhost", port, player);
            gameClient.joinServer();
            foundServer = false;
        } else {
            String serverAddress = matchingServers.firstElement().getServerAddress();
            int port = matchingServers.firstElement().getServerPort();

            gameClient = new GameClient(serverAddress, port, player);
            foundServer = gameClient.joinServer();
        }

        if (foundServer) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    gameClient.sendPlayerProperties(player.getName(), player.getVisibleName(), difficulty);
                    String serverMessage = gameClient.waitUntilGameReady();
                    System.out.println("GameClient ready with no: " + serverMessage);
                    gameClient.waitClientPlayers();

                    Game obj = gameClient.readObjectBlocked();

                    if (serverMessage != null) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    FXMLLoader loader = new FXMLLoader();
                                    GameUIController gui = new GameUIController(player, difficulty, playerCount, cubeDimension, gameMode, gameClient, obj);
                                    loader.setController(gui);
                                    loader.setLocation(getClass().getResource("/view/GameUIStage.fxml"));
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
            //hostServer = new HostServer(8000, playerCount, difficulty, cubeDimension, gameMode, mainClient);
            //gameClient.joinServer();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    gameClient.sendPlayerProperties(player.getName(), player.getVisibleName(), difficulty);
                    String serverMessage = gameClient.waitUntilGameReady(); // make it so that everyone exits at the same time
                    System.out.println("GameClient ready with no: " + serverMessage);
                    gameClient.distributeClientPlayers();

                    Game game = Game.createRandomGame(playerCount, difficulty);
                    Game obj = gameClient.sendObjectBlocked(game);
                    if (serverMessage != null) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    FXMLLoader loader = new FXMLLoader();
                                    GameUIController gui = new GameUIController(player, difficulty, playerCount, cubeDimension, gameMode, hostServer, gameClient, obj);
                                    loader.setController(gui);
                                    loader.setLocation(getClass().getResource("/view/GameUIStage.fxml"));

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
        PATTERN_MATCHING,
        FROM_MEMORY,
        MAXIMUM_PATTERNS,
        AGAINST_TIME,
        DIFFERENT_CUBES,
        PAINTING_PUZZLE,
        TWO_VS_TWO
    }
}
