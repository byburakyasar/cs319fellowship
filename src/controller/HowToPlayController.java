package controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * @author Mert Duman
 * @version 17.11.2018
 */
public class HowToPlayController {
    @FXML Button mainMenuBtn;
    @FXML ComboBox<String> gameModesComboBox;
    @FXML TextArea textArea;

    private String selection;
    private String generalText =
            "Q-Bitz is a game about building a pattern using a cube." +
            " Different patterns are chosen and mixed randomly." +
            " You can fill the board to match the pattern using the cube." +
            " The cube has 6 different faces and in 3D mode it can be rotated by holding left click." +
            " Currently selected face is marked with an orange frame." +
            " By holding right click you can drag the selected image to the board." +
            " Or you can double click on the board and the selected image will appear." +
            " On the board, you can use right click or left click to move an image." +
            " We have different game modes. You can look at each one from the menu below.";

    private String multiplayerText =
            "Game can be played up to 4 players." +
            " Each player can see the boards of other players and how they are doing." +
            " The first player to match the pattern correctly wins." +
            " You can also give up, in which you will lose.";

    private String patternMatchingText =
            "Pattern Matching mode can be played both in single player and in multiplayer." +
            " A pattern is shown on the left and the board you are going to fill is on the right." +
            " The timer starts when you start the game. You can see the timer on top." +
            " If you correctly match the pattern you win." +
            " If somebody finishes before you or if you give up, you lose.";

    private String fromMemoryText =
            "From Memory mode can be played both in single player and in multiplayer." +
            " In this game mode the pattern shown on the left will disappear after a while." +
            " In 3x3 board this time is 10 seconds, in 4x4 it is 20 seconds and in 5x5 it is 40 seconds." +
            " After the pattern is disappeared you can look at the pattern by click the" +
            " Reveal Solution button and the pattern is revealed again for a short time and disappear again." +
            " You can reveal the solution 3 times. If you haven't finished the pattern by then," +
            " you can give up or you can try to look at other people's boards and try to finish" +
            " by copying them before they do!";

    private String againstTimeText =
            "Against Time mode can only be played in single player." +
            " In this game mode a countdown starts when the game starts and you have to" +
            " match the pattern before the time is up." +
            " The countdown times are 15 seconds for 3x3 board, 30 seconds for 4x4 board and" +
            " 45 seconds for 5x5 board.";

    private String paintingPuzzleText =
            "Painting Puzzle mode can be played both in single player and in multiplayer." +
            " In this game mode a picture is provided as a pattern on the left and you try to" +
            " match this picture on your board. In other words you will paint your board by using" +
            " the cube. For instance you might need to match a Mona Lisa painting in order to win.";

    private String twoVsTwoText =
            "Two vs Two mode can only be played in multiplayer with 4 players." +
            " In this game mode you and another random player will try to match the given" +
            " pattern correctly before your opponents do. Both of you will be playing on the" +
            " same board so a good synergy is the key to win. For example, both of you might" +
            " try to match the same parts of the pattern at the same time, which will be a" +
            " waste of time for your team.";

    public void initialize() {
        textArea.setText(generalText);
        textArea.setPrefWidth(Screen.getPrimary().getVisualBounds().getWidth() / 2);
        textArea.setMaxWidth(Screen.getPrimary().getVisualBounds().getWidth() / 2);

        gameModesComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                selection = newValue.toUpperCase().replace(' ', '_').replace('İ', 'I');

                switch (selection){
                    case "GENERAL":
                        textArea.setText(generalText);
                        break;
                    case "MULTIPLAYER":
                        textArea.setText(multiplayerText);
                        break;
                    case "PATTERN_MATCHING":
                        textArea.setText(patternMatchingText);
                        break;
                    case "FROM_MEMORY":
                        textArea.setText(fromMemoryText);
                        break;
                    case "AGAINST_TIME":
                        textArea.setText(againstTimeText);
                        break;
                    case "PAINTING_PUZZLE":
                        textArea.setText(paintingPuzzleText);
                        break;
                    case "TWO_VS_TWO":
                        textArea.setText(twoVsTwoText);
                        break;

                }
            }
        });
    }

    private int getLineCount(String text) {
        return text.split("\n").length;
    }

    private int getLongestLineWidth(String text) {
        String[] rows = text.split("\n");
        double maxWidth = 0;

        for (String s : rows) {
            Text sample = new Text(textArea.getText());
            sample.setStyle("-fx-font-size: 30");
            new Scene(new Group(sample));
            sample.applyCss();
            double width = sample.getLayoutBounds().getWidth();
            if (width > maxWidth) {
                maxWidth = width;
            }
        }

        return (int)Math.ceil(maxWidth);
    }

    @FXML
    public void backToMainMenu() throws IOException {
        Stage current = (Stage) mainMenuBtn.getScene().getWindow();

        BorderPane root = FXMLLoader.load(getClass().getResource("/view/MainMenuStage.fxml"));

        current.getScene().setRoot(root);
    }
}
