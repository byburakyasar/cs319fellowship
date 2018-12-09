package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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
    @FXML Label finishedText;
    @FXML Button restartBtn;
    @FXML Button gameOptionsBtn;
    @FXML Button mainMenuBtn;

    private int lastDifficulty;
    private int lastPlayerCount;
    private int lastCubeDimension;
    private long lastGameTime;
    private Player lastWinner;
    private DateFormat dateFormatS = new SimpleDateFormat( "ss.SSS");
    private DateFormat dateFormatM = new SimpleDateFormat( "m");

    public EndController(int lastDifficulty, int lastPlayerCount, int lastCubeDimension, long lastGameTime, Player lastWinner) {
        this.lastDifficulty = lastDifficulty;
        this.lastPlayerCount = lastPlayerCount;
        this.lastCubeDimension = lastCubeDimension;
        this.lastGameTime = lastGameTime;
        this.lastWinner = lastWinner;
    }

    public void initialize() {
        if (!dateFormatM.format(lastGameTime).equals("0")) {
            if (dateFormatM.format(lastGameTime).equals("1")) {
                finishedText.setText("You finished in: " + dateFormatM.format(lastGameTime)+ " minute and " + dateFormatS.format(lastGameTime) + " seconds!");
            } else {
                finishedText.setText("You finished in: " + dateFormatM.format(lastGameTime)+ " minutes and " + dateFormatS.format(lastGameTime) + " seconds!");
            }
        } else {
            finishedText.setText("You finished in: " + dateFormatS.format(lastGameTime) + " seconds!");
        }
    }

    @FXML
    public void restart() throws IOException {
        Stage current = (Stage) restartBtn.getScene().getWindow();

        FXMLLoader loader = new FXMLLoader();
        GameUIController gui = new GameUIController(lastDifficulty, lastPlayerCount, lastCubeDimension);
        loader.setController(gui);
        loader.setLocation(getClass().getResource("../view/GameUIStage.fxml"));

        BorderPane root = loader.load();
        Scene scene = new Scene(root, 1920, 1000);

        current.setScene(scene);
    }

    @FXML
    public void loadGameOptions() throws IOException {
        Stage current = (Stage) restartBtn.getScene().getWindow();
        BorderPane root = FXMLLoader.load(getClass().getResource("../view/GameOptionsStage.fxml"));
        Scene scene = new Scene(root, 1920, 1000);

        current.setScene(scene);
    }

    @FXML
    public void backToMainMenu() throws IOException {
        Stage current = (Stage) restartBtn.getScene().getWindow();
        BorderPane root = FXMLLoader.load(getClass().getResource("../view/MainMenuStage.fxml"));
        Scene scene = new Scene(root, 1920, 1000);

        current.setScene(scene);
    }
}
