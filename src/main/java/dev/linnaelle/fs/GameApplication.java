package dev.linnaelle.fs;

import dev.linnaelle.fs.services.*;
import dev.linnaelle.fs.utils.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class GameApplication extends Application {
    public static void main(String[] args) {
        DatabaseManager.getInstance().initializeDatabase();
        DataInitializer.initializeData();

        launch(args);
    }
    
    /**
     * Méthode principale de l'application JavaFX.
     * Elle est appelée au démarrage de l'application.
     * @param primaryStage La fenêtre principale de l'application.
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setResizable(ConfigManager.isWindowResizable());
        primaryStage.setFullScreen(ConfigManager.isFullscreen());
        primaryStage.setWidth(ConfigManager.getWindowWidth());
        primaryStage.setHeight(ConfigManager.getWindowHeight());
        primaryStage.setTitle(ConfigManager.getTitle());
        primaryStage.setScene(new Scene(new Label("Hello world !"), 1280, 720));

        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            System.out.println("Fermeture de l'application " + ConfigManager.getTitle());
            DatabaseManager.closeInstance();
        });
    }
}