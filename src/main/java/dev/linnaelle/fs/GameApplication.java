package dev.linnaelle.fs;

import dev.linnaelle.fs.utils.*;
import dev.linnaelle.fs.service.*;
import javafx.application.Application;
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
        CatalogueInitializer.initializeCatalogueData();

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

        // Affichage de la fenêtre
        primaryStage.show();

        // Gestion de la fermeture de l'application
        primaryStage.setOnCloseRequest(event -> {
            System.out.println("Fermeture de l'application " + ConfigManager.getTitle());
            DatabaseManager.closeInstance();
        });
    }
}