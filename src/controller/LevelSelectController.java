package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Player;

import java.io.IOException;

/**
 * @author Mert Duman
 * @version 17.11.2018
 */
public class LevelSelectController {
    @FXML Button backToMenuBtn;
    @FXML Button goLeftBtn;
    @FXML Button goRightBtn;
    @FXML VBox levelsVBox;
    @FXML GridPane levelsGridPane;

    private int currentPage = 1;
    private final int MIN_PAGE = 1;
    private final int MAX_PAGE = 2;

    public void initialize() {
        loadLevels((currentPage - 1) * 9 + 1);

        goLeftBtn.setOnAction(event -> {
            if (currentPage > MIN_PAGE) {
                levelsGridPane.getChildren().clear();
                currentPage--;
                loadLevels((currentPage - 1) * 9 + 1);
            }
        });

        goRightBtn.setOnAction(event -> {
            if (currentPage < MAX_PAGE) {
                levelsGridPane.getChildren().clear();
                currentPage++;
                loadLevels((currentPage - 1) * 9 + 1);
            }
        });
    }

    private void loadLevels(int startingLevel) {
        int start = startingLevel;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int currentLevel = start;   // variable used in lambda exp. must be final or eff. final
                Button button = new Button("Level " + currentLevel);
                button.setOnAction(event -> {
                    loadGameFromLevel(currentLevel);
                });
                levelsGridPane.add(button, j, i);
                start++;
            }
        }
    }

    public void loadGameFromLevel(int levelNo) {
        Player player;
        GameUIController gui;
        switch (levelNo) {
            case 1:
                player = new Player("single", 3);
                gui = new GameUIController(player, 3, 1, 3,
                        GameOptionsController.GameModes.PATTERN_MATCHING, 0);

                loadGame(gui);
                break;
            case 2:
                player = new Player("single", 3);
                gui = new GameUIController(player, 4, 1, 3,
                        GameOptionsController.GameModes.PATTERN_MATCHING, 0);

                loadGame(gui);
                break;
            case 3:
                player = new Player("single", 3);
                gui = new GameUIController(player, 5, 1, 3,
                        GameOptionsController.GameModes.PATTERN_MATCHING, 0);

                loadGame(gui);
                break;
            case 4:
                player = new Player("single", 3);
                gui = new GameUIController(player, 4, 1, 3,
                        GameOptionsController.GameModes.PATTERN_MATCHING, 1);

                loadGame(gui);
                break;
            case 5:
                player = new Player("single", 3);
                gui = new GameUIController(player, 4, 1, 3,
                        GameOptionsController.GameModes.AGAINST_TIME, 1);

                loadGame(gui);
                break;
            case 6:
                player = new Player("single", 3);
                gui = new GameUIController(player, 4, 1, 3,
                        GameOptionsController.GameModes.FROM_MEMORY, 1);

                loadGame(gui);
                break;
            case 7:
                player = new Player("single", 3);
                gui = new GameUIController(player, 4, 1, 3,
                        GameOptionsController.GameModes.AGAINST_TIME, 5);

                loadGame(gui);
                break;
            case 8:
                player = new Player("single", 3);
                gui = new GameUIController(player, 5, 1, 3,
                        GameOptionsController.GameModes.FROM_MEMORY, 1);

                loadGame(gui);
                break;
            case 9:
                player = new Player("single", 3);
                gui = new GameUIController(player, 5, 1, 3,
                        GameOptionsController.GameModes.FROM_MEMORY, 3);

                loadGame(gui);
                break;
            default:

        }
    }

    private void loadGame(GameUIController gui) {
        try {
            Stage current = (Stage)backToMenuBtn.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader();
            loader.setController(gui);
            loader.setLocation(getClass().getResource("/view/GameUIStage.fxml"));
            BorderPane root = loader.load();

            current.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void backToMainMenu() throws IOException {
        Stage current = (Stage) backToMenuBtn.getScene().getWindow();
        BorderPane root = FXMLLoader.load(getClass().getResource("/view/MainMenuStage.fxml"));

        current.getScene().setRoot(root);
    }
}
