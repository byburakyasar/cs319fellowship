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
                                            "you can drag the selected face to the board. In the board, faces also can\n" +
                                            "be replaced with left click. Wrong faces can be removed by dragging them to\n" +
                                            "outside of the board We have different game modes. You can look at each of\n" +
                                            "it from the bar below where general info returns you back to this info.");
                        break;

                    case PATTERN_MATCHING:
                        howToLabel.setText( "Pattern Matching can be played both in single player and in multiplayer.\n" +
                                            "A pattern is shown on the left and the board you are going to fill is on\n" +
                                            "the right and the time starts to increment when the game start. If you\n" +
                                            "succeed to match the pattern you win otherwise you can give up and it\n" +
                                            "leads you to lost the game. In this case you can restart the same game\n" +
                                            "or you can play another game mode or go back to main menu");

                        break;

                    case MULTIPLAYER:
                        howToLabel.setText( "Game can be played up to 4 players. Each player can see other's move and\n" +
                                            "first player finishes the game wins other players lose. Also, when a player\n" +
                                            "gives up the game he/she directly loses");
                        break;

                    case FROM_MEMORY:
                        howToLabel.setText( "In this game mode the pattern shown on the left will disappear after a while.\n" +
                                            "In 3*3 board this time is 10 sec, in 4*4 it is 20sec and in 5*5 it is 40sec.\n" +
                                            "After the pattern is disappeared you can look at the pattern by click the\n" +
                                            "Reveal Solution button and the pattern is shown for a short time and disappear\n" +
                                            "again. You can click the Reveal Solution 3 times then the button is gone. You \n" +
                                            "can finish the game or you should click the give up button." );
                        break;

                    case MAXIMUM_PATTERNS:
                        howToLabel.setText("MAXIMUM_PATTERNS");
                        break;

                    case AGAINST_TIME:
                        howToLabel.setText( "In this game mode a countdown is started when the game starts and you have to\n" +
                                            "match the pattern before the time is up. This count down starts from 15 sec for\n" +
                                            "3*3 board and it starts from 30sec for 4*4 board and from 45 sec for 5*5 board\n");
                        break;

                    case DIFFERENT_CUBES:
                        howToLabel.setText("DIFFERENT_CUBES");
                        break;

                    case PAINTING_PUZZLE:
                        howToLabel.setText( "In this game mode a picture is provided as a pattern on the left and you try to\n" +
                                            "get this picture on your board. In other words you will paint your board by using\n" +
                                            "the cube. This time each face of the cube necessary to get the picture on your board");
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
