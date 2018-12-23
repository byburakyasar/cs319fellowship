import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.MainClient;
import model.MainServer;
import model.ResourceLoader;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        try {
            createMainServer();

            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();

            Parent root = FXMLLoader.load(getClass().getResource("/view/MainMenuStage.fxml"));
            primaryStage.setTitle("QBitz");
            primaryStage.setScene(new Scene(root));
            primaryStage.getIcons().add(ResourceLoader.getInstance().getGameIcon());
            primaryStage.setX(bounds.getMinX());
            primaryStage.setY(bounds.getMinY());
            primaryStage.setMaximized(true);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            stop();
        }
    }

    public void createMainServer() {
        MainClient mainClient = new MainClient("139.179.211.250", 8000);
        if (!mainClient.joinServer()) {
            // The error thrown by this is caught to print that the server is running already.
            MainServer mainServer = new MainServer(8000);
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
