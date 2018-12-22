package controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;

import java.io.IOException;

import static javafx.scene.media.AudioClip.INDEFINITE;

/**
 * @author Mert Duman
 * @version 17.11.2018
 */
public class SettingsController {
    @FXML Button mainMenuBtn;
    @FXML CheckBox musicBtn;
    @FXML CheckBox soundBtn;

    public void initialize(){
        AudioClip music = new AudioClip(getClass().getResource("background_music.wav").toString());

        EventHandler eventHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(event.getSource() instanceof CheckBox){
                    if(musicBtn.isSelected()){
                        music.setCycleCount(INDEFINITE);
                        music.play();
                    }
                    else{
                        music.stop();
                    }
                }
            }
        };
        musicBtn.setOnAction(eventHandler);
    }


    @FXML
    public void backToMainMenu() throws IOException {
        Stage current = (Stage) mainMenuBtn.getScene().getWindow();

        BorderPane root = FXMLLoader.load(getClass().getResource("/view/MainMenuStage.fxml"));

        current.getScene().setRoot(root);
    }
}
