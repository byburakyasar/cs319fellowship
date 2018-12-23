package controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.*;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.Vector;


public class GameOptionsController {
    @FXML Button startBtn;
    @FXML Button backToMenuBtn;
    @FXML Button multiplayerBtn;
    @FXML HBox playersHBox;
    @FXML HBox difficultyHBox;
    @FXML HBox dimensionsHBox;
    @FXML ComboBox<String> gameModesComboBox;
    @FXML ComboBox<String> patternComboBox;
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
    private int patternNo = 0;
    private ObservableList<String> paintingList = FXCollections.observableArrayList();
    private ObservableList<String> patternList = FXCollections.observableArrayList();

    public void initialize() {
        paintingList.addAll("Mona Lisa", "Starry Night", "Blossoms", "Convergence", "Pearl Earring");
        patternList.addAll( "Legacy Q-Bitz", "Mona Lisa", "Pepee", "Flowers", "Masa Ile Koca Ayi", "Lord of the Rings", "Starry Night", "Dice", "Blossoms", "Convergence", "Pearl Earring");

        patternComboBox.setItems(patternList);

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

        patternComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue == null) {
                    return;
                }
                String value = newValue.toUpperCase().replace(' ', '_').replace('İ', 'I');
                System.out.println(value);
                switch (value) {
                    case "LEGACY_Q-BITZ": patternNo = 0;
                        break;
                    case "MONA_LISA": patternNo = 1;
                        break;
                    case "PEPEE": patternNo = 2;
                        break;
                    case "FLOWERS": patternNo = 3;
                        break;
                    case "MASA_ILE_KOCA_AYI": patternNo = 4;
                        break;
                    case "LORD_OF_THE_RINGS": patternNo = 5;
                        break;
                    case "STARRY_NIGHT": patternNo = 6;
                        break;
                    case "DICE": patternNo = 7;
                        break;
                    case "BLOSSOMS": patternNo = 8;
                        break;
                    case "CONVERGENCE": patternNo = 9;
                        break;
                    case "PEARL_EARRING": patternNo = 10;
                        break;
                }
            }
        });

        gameModesComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
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
                    case PAINTING_PUZZLE:
                        patternComboBox.setItems(paintingList);
                        patternComboBox.valueProperty().setValue(paintingList.get(0));
                        break;
                    default:
                        for (int i = 0; i < playerGroup.getToggles().size(); i++) {
                            ((RadioButton) playerGroup.getToggles().get(i)).setDisable(false);
                        }

                        patternComboBox.setItems(patternList);
                        patternComboBox.valueProperty().setValue(patternList.get(0));
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
            GameUIController gui = new GameUIController(player, difficulty, playerCount, cubeDimension, gameMode, patternNo);
            loader.setController(gui);
            loader.setLocation(getClass().getResource("/view/GameUIStage.fxml"));
            BorderPane root = loader.load();

            current.getScene().setRoot(root);
            return;
        }

        startBtn.setText("Looking for players...");
        startBtn.setDisable(true);

        mainClient = new MainClient("localhost", 8000);
        mainClient.joinServer();
        Vector<ServerInfo> matchingServers = mainClient.getMatchingServers(playerCount, difficulty, cubeDimension, String.valueOf(gameMode), patternNo);

        boolean foundServer = false;
        for (ServerInfo si : matchingServers) {
            System.out.println(si);
        }

        if (matchingServers.size() == 0) {
            hostServer = new HostServer(playerCount, difficulty, cubeDimension, gameMode, patternNo, mainClient);
            mainClient.setHostServer(hostServer);
            mainClient.sendServerInfo();
            gameClient = new GameClient("localhost", hostServer.getServerPort(), player);
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
                                    GameUIController gui = new GameUIController(player, difficulty, playerCount, cubeDimension, gameMode, patternNo, gameClient, obj);
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
            new Thread(new Runnable() {
                @Override
                public void run() {
                    gameClient.sendPlayerProperties(player.getName(), player.getVisibleName(), difficulty);
                    String serverMessage = gameClient.waitUntilGameReady();
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
                                    GameUIController gui = new GameUIController(player, difficulty, playerCount, cubeDimension, gameMode, patternNo, hostServer, gameClient, obj);
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
        AGAINST_TIME,
        PAINTING_PUZZLE,
        TWO_VS_TWO
    }
}
