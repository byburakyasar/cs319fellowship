package controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * @author Mert Duman
 * @version 17.11.2018
 */
public class GameOptionsController {
    @FXML Button startBtn;
    @FXML Button backToMenuBtn;
    @FXML Button multiplayerBtn;
    @FXML HBox playersHBox;
    @FXML ComboBox<String> gameModesComboBox;
    @FXML ToggleGroup difficultyGroup;
    @FXML ToggleGroup playerGroup;


    private int difficulty = 4;
    private int playerCount = 1;

    public void initialize() {
        difficultyGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                difficulty = Integer.parseInt(difficultyGroup.getSelectedToggle().getUserData().toString());
            }
        });

        gameModesComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                Text text = new Text(newValue);
                gameModesComboBox.setMaxWidth(text.getLayoutBounds().getWidth() + 150);
                System.out.println(text.getLayoutBounds().getWidth() + 150);
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

    private boolean flipflop = true;
    @FXML
    public void loadPlayerChoices(ActionEvent event) {
        if(flipflop) {
            RadioButton[] btns = new RadioButton[3];
            for(int i = 0; i < 3; i++) {
                RadioButton btn = new RadioButton(String.valueOf(i+2));
                btn.setUserData(String.valueOf(i+2));
                btn.setToggleGroup(playerGroup);
                btns[i] = btn;
            }

            VBox vBox = new VBox();
            vBox.setAlignment(Pos.CENTER);
            vBox.setSpacing(10);

            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER);
            hBox.setSpacing(30);
            hBox.getChildren().addAll(btns);

            Button btn = (Button) playersHBox.getChildren().remove(2);
            vBox.getChildren().add(btn);
            vBox.getChildren().add(hBox);
            playersHBox.getChildren().add(vBox);
            playersHBox.setMargin(vBox, new Insets(-40,0,0,0));

            flipflop = false;
        } else {
            VBox vBox = (VBox)playersHBox.getChildren().remove(2);
            playersHBox.getChildren().add(multiplayerBtn);
            RadioButton btn = (RadioButton)(playersHBox.getChildren().get(1));
            btn.setSelected(true);
            flipflop = true;
        }
    }

    public int getDifficulty() {
        return difficulty;
    }
}
