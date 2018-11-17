package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("MainMenuStage.fxml"));
        primaryStage.setTitle("QBitz");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
        primaryStage.getIcons().add(new Image("./qbitz.png"));
    }


    public static void main(String[] args) {
        launch(args);
    }
}
