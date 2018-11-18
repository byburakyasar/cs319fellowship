import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import model.ResourceLoader;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/view/MainMenuStage.fxml"));
        primaryStage.setTitle("QBitz");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.getIcons().add(ResourceLoader.getInstance().getGameIcon());
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
