package com.bifasico.soduko.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Singleton that owns the primary {@link Stage} and handles scene transitions.
 * Loading the stylesheet and switching scenes are its only responsibilities.
 */
public class SceneManager {

    private static SceneManager instance;

    private Stage mainStage;

    private SceneManager() {}

    public static SceneManager getInstance() {
        if (instance == null) instance = new SceneManager();
        return instance;
    }

    public void setMainStage(Stage stage) { this.mainStage = stage; }

    public void loadFonts() {
        Font.loadFont(getClass().getResourceAsStream("/fonts/BDOGrotesk-VF.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("/fonts/SF-Pro.ttf"), 14);
    }

    /**
     * Loads the given FXML file, attaches the global stylesheet, and displays the scene.
     *
     * @param fxmlFile filename inside /fxml/ (e.g. "game-view.fxml")
     * @return the controller instance created by the FXMLLoader
     */
    public <SceneController> SceneController switchScene(String fxmlFile) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxmlFile));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/css/styles.css")).toExternalForm()
        );

        mainStage.setScene(scene);
        mainStage.show();

        return loader.getController();
    }
}
