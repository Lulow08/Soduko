package com.bifasico.soduko.model;

import com.bifasico.soduko.view.SceneManager;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Launcher {

    private static final String APP_TITLE    = "Soduko";
    private static final String FAVICON_PATH = "/icons/favicon.png";
    private static final String ENTRY_SCENE  = "menu-view.fxml";

    public void start(Stage stage) throws IOException {
        /* Image favicon = new Image(Objects.requireNonNull(
                getClass().getResourceAsStream(FAVICON_PATH)
        )); */

        stage.setTitle(APP_TITLE);
        stage.setResizable(false);
        // stage.getIcons().add(favicon);

        SceneManager sceneManager = SceneManager.getInstance();
        sceneManager.setMainStage(stage);
        // sceneManager.loadFonts();
        sceneManager.switchScene(ENTRY_SCENE);
    }
}