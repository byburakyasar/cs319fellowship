import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.ResourceLoader;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/view/MainMenuStage.fxml"));
        primaryStage.setTitle("QBitz");
        primaryStage.setScene(new Scene(root, 1920, 1080));
        primaryStage.getIcons().add(ResourceLoader.getInstance().getGameIcon());
        primaryStage.setMaximized(true);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
