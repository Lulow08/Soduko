package com.bifasico.soduko;

import com.bifasico.soduko.model.Launcher;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage mainStage) throws IOException {
        new Launcher().start(mainStage);
    }
}
