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
public class GameUIController {
    @FXML Button cubeBtn1;
    @FXML Button cubeBtn2;
    @FXML Button cubeBtn3;
    @FXML Button cubeBtn4;
    @FXML Button cubeBtn5;
    @FXML Button cubeBtn6;

    public void initialize() {

    }

    @FXML
    public void backToMainMenu() throws IOException {
        Stage current = (Stage) cubeBtn1.getScene().getWindow();
        BorderPane root = FXMLLoader.load(getClass().getResource("../view/MainMenuStage.fxml"));
        Scene scene = new Scene(root, 800, 600);

        current.setScene(scene);
    }
}
