package controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
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
    @FXML ToggleGroup difficultyGroup;

    private int difficulty;
    private int playerCount;

    public void initialize() {
        difficultyGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                difficulty = Integer.parseInt(difficultyGroup.getSelectedToggle().getUserData().toString());
            }
        });
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

        FXMLLoader loader = new FXMLLoader();
        GameUIController gui = new GameUIController(this);
        loader.setController(gui);
        loader.setLocation(getClass().getResource("../view/GameUIStage.fxml"));

        BorderPane root = loader.load();
        Scene scene = new Scene(root, 800, 600);

        current.setScene(scene);

    }

    public GameOptionsController getSelf() {
        return this;
    }

    public int getDifficulty() {
        return difficulty;
    }
}
