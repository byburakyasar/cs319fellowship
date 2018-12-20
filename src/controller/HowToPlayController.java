package controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

import static controller.GameOptionsController.GameModes.*;

/**
 * @author Mert Duman
 * @version 17.11.2018
 */
public class HowToPlayController {
    @FXML Button mainMenuBtn;
    @FXML ComboBox<String> gameModesComboBox;
    @FXML BorderPane rootPane;
    @FXML Label howToLabel;

    private GameOptionsController.GameModes gameMode;


    public void initialize() {
        howToLabel.setText( "Q-bitz is a game about building a pattern using a cube.\n" +
                "Different patterns are chosen and mixed randomly.\n" +
                "You can fill the board to match the pattern using the cube.\n" +
                "Cube has 6 different face.Cube can be turned with the left click\n" +
                "Current selectable face is marked with yellow frame. With the right click\n" +
                "you can drag the selected face to the board.\n" +
                "In the board, faces also can be replaced with left click\n" +
                "We have different game modes. You can look at each of it from the bar\n" +
                "where general info returns you back to this info");
        gameModesComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                Text text = new Text(newValue);

                gameMode = Enum.valueOf(GameOptionsController.GameModes.class, newValue.toUpperCase().replace(' ', '_').replace('İ', 'I'));

                switch (gameMode){
                    case GENERAL_INFO:
                        howToLabel.setText( "Q-bitz is a game about building a pattern using a cube.\n" +
                                "Different patterns are chosen and mixed randomly.\n" +
                                "You can fill the board to match the pattern using the cube.\n" +
                                "Cube has 6 different face.Cube can be turned with the left click\n" +
                                "Current selectable face is marked with yellow frame. With the right click\n" +
                                "you can drag the selected face to the board.\n" +
                                "In the board, faces also can be replaced with left click\n" +
                                "We have different game modes. You can look at each of it from the bar\n" +
                                "where general info returns you back to this info");
                        break;

                    case PATTERN_MATCHING:
                        howToLabel.setText( "Pattern Matching can be played both in single player and in multiplayer.\n" +
                                "Different Patterns are shown when the game start. ");
                        break;

                    case RACING_AND_ROLLING:
                        howToLabel.setText( "Multiplayer");
                        break;

                    case FROM_MEMORY:
                        howToLabel.setText("FROM_MEMORY");
                        break;

                    case MAXIMUM_PATTERNS:
                        howToLabel.setText("MAXIMUM_PATTERNS");
                        break;

                    case AGAINST_TIME:
                        howToLabel.setText("AGAINST_TIME");
                        break;

                    case DIFFERENT_CUBES:
                        howToLabel.setText("DIFFERENT_CUBES");
                        break;

                    case PAINTING_PUZZLE:
                        howToLabel.setText("PAINTING_PUZZLE");
                        break;

                    case TWO_VS_TWO:
                        howToLabel.setText("TWO_VS_TWO");
                        break;

                }
            }
        });
    }


    @FXML
    public void backToMainMenu() throws IOException {
        Stage current = (Stage) mainMenuBtn.getScene().getWindow();
        BorderPane root = FXMLLoader.load(getClass().getResource("../view/MainMenuStage.fxml"));

        current.getScene().setRoot(root);
    }
}
