package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.Player;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * @author Mert Duman
 * @version 17.11.2018
 */
public class EndController {
    @FXML Label endGameLabel;
    @FXML Label endGameFirstLetter;
    @FXML Label finishedText;
    @FXML Button restartBtn;
    @FXML Button gameOptionsBtn;
    @FXML Button mainMenuBtn;

    private Player lastPlayer;
    private int lastDifficulty;
    private int lastPlayerCount;
    private int lastCubeDimension;
    private GameOptionsController.GameModes lastGameMode;
    private long lastGameTime;
    private Player lastWinner;
    private DateFormat dateFormatS = new SimpleDateFormat( "ss.SSS");
    private DateFormat dateFormatM = new SimpleDateFormat( "m");

    public EndController(Player lastPlayer, int lastDifficulty, int lastPlayerCount, int lastCubeDimension, GameOptionsController.GameModes lastGameMode, long lastGameTime, Player lastWinner) {
        this.lastPlayer = lastPlayer;
        this.lastDifficulty = lastDifficulty;
        this.lastPlayerCount = lastPlayerCount;
        this.lastCubeDimension = lastCubeDimension;
        this.lastGameMode = lastGameMode;
        this.lastGameTime = lastGameTime;
        this.lastWinner = lastWinner;
    }

    public void initialize() {
        if (lastWinner == null) {
            endGameFirstLetter.setText("Y");
            endGameLabel.setText("OU GAVE UP");
            finishedText.setText("");
            return;
        }

        endGameFirstLetter.setText("Y");
        endGameLabel.setText("OU LOST");
        String winner = lastWinner.getVisibleName();
        if (lastPlayer.getName().equals(lastWinner.getName())) {
            winner = "You";
            endGameFirstLetter.setText("C");
            endGameLabel.setText("ONGRATULATIONS");
        }

        if (!dateFormatM.format(lastGameTime).equals("0")) {
            if (dateFormatM.format(lastGameTime).equals("1")) {
                finishedText.setText(winner + " finished in: " + dateFormatM.format(lastGameTime)+ " minute and " + dateFormatS.format(lastGameTime) + " seconds!");
            } else {
                finishedText.setText(winner + " finished in: " + dateFormatM.format(lastGameTime)+ " minutes and " + dateFormatS.format(lastGameTime) + " seconds!");
            }
        } else {
            finishedText.setText(winner + " finished in: " + dateFormatS.format(lastGameTime) + " seconds!");
        }
    }

    @FXML
    public void restart() throws IOException {
        Stage current = (Stage) restartBtn.getScene().getWindow();

        FXMLLoader loader = new FXMLLoader();
        GameUIController gui = new GameUIController(lastPlayer, lastDifficulty, lastPlayerCount, lastCubeDimension, lastGameMode);
        loader.setController(gui);
        loader.setLocation(getClass().getResource("../view/GameUIStage.fxml"));

        BorderPane root = loader.load();

        current.getScene().setRoot(root);
    }

    @FXML
    public void loadGameOptions() throws IOException {
        Stage current = (Stage) restartBtn.getScene().getWindow();
        BorderPane root = FXMLLoader.load(getClass().getResource("../view/GameOptionsStage.fxml"));

        current.getScene().setRoot(root);
    }

    @FXML
    public void backToMainMenu() throws IOException {
        Stage current = (Stage) restartBtn.getScene().getWindow();
        BorderPane root = FXMLLoader.load(getClass().getResource("../view/MainMenuStage.fxml"));

        current.getScene().setRoot(root);
    }
}
