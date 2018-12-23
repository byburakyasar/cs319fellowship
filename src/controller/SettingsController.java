package controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;

import java.io.IOException;

import static javafx.scene.media.AudioClip.INDEFINITE;


public class SettingsController {
    @FXML Button mainMenuBtn;
    @FXML CheckBox musicBtn;
    @FXML CheckBox soundBtn;

    private static AudioClip music = new AudioClip(ClassLoader.getSystemClassLoader().getResource("res/background_music.wav").toString());

    public void initialize(){
        if (music.isPlaying()) {
            musicBtn.selectedProperty().setValue(true);
        } else {
            musicBtn.selectedProperty().setValue(false);
        }

        EventHandler musicHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(event.getSource() instanceof CheckBox){
                    if(musicBtn.isSelected()){
                        music.setCycleCount(INDEFINITE);
                        music.stop();
                        music.play();
                    }
                    else{
                        music.stop();
                    }
                }
            }
        };
        musicBtn.setOnAction(musicHandler);
    }


    @FXML
    public void backToMainMenu() throws IOException {
        Stage current = (Stage) mainMenuBtn.getScene().getWindow();

        BorderPane root = FXMLLoader.load(getClass().getResource("/view/MainMenuStage.fxml"));

        current.getScene().setRoot(root);
    }
}
