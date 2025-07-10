// Config.java
package dev.linnaelle.fs.model;

/**
 * Classe de configuration pour l'application Farming Simulator.
 * Contient des sous-classes pour la configuration de la base de données, du jeu et du joueur.
 * Utilisée pour initialiser les paramètres par défaut et accéder aux configurations.
 */
public class Config {
    private DatabaseConfig database;
    private GameConfig game;
    private PlayerConfig player;

    public Config() {
        this.database = new DatabaseConfig();
        this.game = new GameConfig();
        this.player = new PlayerConfig();
    }

    public DatabaseConfig getDatabaseConfig() { return database; }
    public GameConfig getGameConfig() { return game; }
    public PlayerConfig getPlayerConfig() { return player; }

    public static class DatabaseConfig {
        private String path;
        private boolean memoryMode;

        public DatabaseConfig() {
            this.path = "./data/db.sqlite";
            this.memoryMode = false;
        }

        public String getPath() { return path; }
        public boolean isMemoryMode() { return memoryMode; }
    }

    public static class GameConfig {
        private boolean fullscreen;
        private String title;
        private String version;
        private int windowWidth;
        private int windowHeight;
        private boolean windowResizable;
        private String language;
        private double volume;
        private boolean autoSave;
        private int autoSaveIntervalMinutes;

        public GameConfig() {
            this.fullscreen = false;
            this.title = "Farming Simulator";
            this.version = "1.0.0";
            this.windowWidth = 800;
            this.windowHeight = 600;
            this.windowResizable = true;
            this.language = "fr";
            this.volume = 0.7;
            this.autoSave = true;
            this.autoSaveIntervalMinutes = 5;
        }

        public int getWindowWidth() { return windowWidth; }
        public int getWindowHeight() { return windowHeight; }
        public int getAutoSaveIntervalMinutes() { return autoSaveIntervalMinutes; }
        public double getVolume() { return volume; }
        public boolean isAutoSave() { return autoSave; }
        public boolean isFullscreen() { return fullscreen; }
        public boolean isWindowResizable() { return windowResizable; }
        public String getTitle() { return title; }
        public String getVersion() { return version; }
        public String getLanguage() { return language; }
    }

    public static class PlayerConfig {
        private String defaultName;
        private int startMoney;

        public PlayerConfig() {
            this.defaultName = "Fermier";
            this.startMoney = 1500;
        }

        public String getDefaultName() { return defaultName; }
        public int getStartMoney() { return startMoney; }
    }
}
