package dev.linnaelle.fs;

import dev.linnaelle.fs.services.*;
import dev.linnaelle.fs.utils.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Classe principale de l'application JavaFX pour le jeu Farming Simulator.
 * Gère le démarrage et l'arrêt de l'application, ainsi que la configuration initiale de la fenêtre.
 */
public class GameApplication extends Application {
    public static void main(String[] args) {
        // Initialisation du gestionnaire de base de données
        DatabaseManager.getInstance().initializeDatabase();
        // Remplissage du catalogue avec les données initiales
        DataInitializer.initializeData();

        // Lancement de l'application JavaFX
        launch(args);
    }
    
    /**
     * Méthode principale de l'application JavaFX.
     * Elle est appelée au démarrage de l'application.
     * @param primaryStage La fenêtre principale de l'application.
     */
    @Override
    public void start(Stage primaryStage) {
        // Initialisation de la fenêtre principale du jeu
        primaryStage.setResizable(ConfigManager.isWindowResizable());
        primaryStage.setFullScreen(ConfigManager.isFullscreen());
        primaryStage.setWidth(ConfigManager.getWindowWidth());
        primaryStage.setHeight(ConfigManager.getWindowHeight());
        primaryStage.setTitle(ConfigManager.getTitle());
        primaryStage.setScene(new Scene(new Label("Hello world !"), 1280, 720));

        // Affichage de la fenêtre
        primaryStage.show();

        // Gestion de la fermeture de l'application
        primaryStage.setOnCloseRequest(event -> {
            System.out.println("Fermeture de l'application " + ConfigManager.getTitle());
            DatabaseManager.closeInstance();
        });
    }
}