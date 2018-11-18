package controller;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class MainMenuController {
    @FXML BorderPane mainBorderPane;
    @FXML Button playBtn;
    @FXML Button levelSelectBtn;
    @FXML Button howToPlayBtn;
    @FXML Button settingsBtn;
    @FXML Button creditsBtn;
    @FXML Button exitBtn;

    private int position = 0;

    public void initialize() {
        playButtonAnimations();
    }

    @FXML
    public void playBtnClicked() throws IOException {
        Stage current = (Stage) playBtn.getScene().getWindow();
        BorderPane root = FXMLLoader.load(getClass().getResource("../view/GameOptionsStage.fxml"));
        Scene scene = new Scene(root, 800, 600);

        current.setScene(scene);
    }

    @FXML
    public void settingsBtnClicked() throws IOException{
        Stage current = (Stage) creditsBtn.getScene().getWindow();
        BorderPane root = FXMLLoader.load(getClass().getResource("../view/SettingsStage.fxml"));
        Scene scene = new Scene(root, 800, 600);

        current.setScene(scene);
    }

    @FXML
    public void howToPlayBtnClicked() throws IOException{
        Stage current = (Stage) creditsBtn.getScene().getWindow();
        BorderPane root = FXMLLoader.load(getClass().getResource("../view/HowToPlayStage.fxml"));
        Scene scene = new Scene(root, 800, 600);

        current.setScene(scene);
    }

    @FXML
    public void creditsBtnClicked() throws IOException{
        Stage current = (Stage) creditsBtn.getScene().getWindow();
        BorderPane root = FXMLLoader.load(getClass().getResource("../view/CreditsStage.fxml"));
        Scene scene = new Scene(root, 800, 600);

        current.setScene(scene);
    }

    @FXML
    public void levelSelectBtnClicked() throws IOException{
        Stage current = (Stage) levelSelectBtn.getScene().getWindow();
        BorderPane root = FXMLLoader.load(getClass().getResource("../view/LevelSelectStage.fxml"));
        Scene scene = new Scene(root, 800, 600);

        current.setScene(scene);
    }

    @FXML
    public void exit() {
        Platform.exit();
        System.exit(0);
    }

    private void playButtonAnimations() {
        Button[] btnArr = {playBtn, levelSelectBtn, howToPlayBtn, settingsBtn, creditsBtn, exitBtn};

        setClips(btnArr);
        playTimeline(btnArr);
    }

    private void setClips(Button[] btnArr) {
        for (int i = 0; i < 6; i++) {
            btnArr[i].setTranslateX(-300);
            Rectangle clip = new Rectangle(210, 40);
            clip.translateXProperty().bind( btnArr[i].translateXProperty().negate());
            btnArr[i].setClip(clip);
        }
    }

    private void playTimeline(Button[] btnArr) {
        // additional delay needed otherwise animation acts unexpectedly on start.
        Timeline delay = new Timeline( new KeyFrame( Duration.millis(10)));
        delay.setCycleCount(1);
        delay.play();
        delay.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Timeline fiveSecondsT = new Timeline( new KeyFrame(Duration.ZERO, new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        playTransition(btnArr[position]);
                        position++;
                    }
                }), new KeyFrame( Duration.millis(150)));
                fiveSecondsT.setCycleCount(6);
                fiveSecondsT.play();
            }
        });
    }

    private void playTransition(Button btn) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(1000), btn);
        tt.setToX(0);
        tt.setInterpolator(Interpolator.EASE_BOTH);
        tt.play();

        btn.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(1700), btn);
        ft.setToValue(1);
        ft.setInterpolator(Interpolator.EASE_IN);
        ft.play();
    }
}
