package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * @author Mert Duman
 * @version 17.11.2018
 */
public class GameOptionsController {
    @FXML Button startBtn;
    @FXML Button backToMenuBtn;

    private int difficulty;
    private int playerCount;

    public void initialize() {

    }

    @FXML
    public void backToMainMenu() throws IOException {
        Stage current = (Stage) startBtn.getScene().getWindow();
        BorderPane root = FXMLLoader.load(getClass().getResource("../view/MainMenuStage.fxml"));
        Scene scene = new Scene(root, 800, 600);

        current.setScene(scene);
    }

    @FXML
    public void startGame() throws IOException {
        Stage current = (Stage) startBtn.getScene().getWindow();
        BorderPane root = FXMLLoader.load(getClass().getResource("../view/GameUIStage.fxml"));
        Scene scene = new Scene(root, 800, 600);

        current.setScene(scene);
    }
}
