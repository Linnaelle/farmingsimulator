package dev.linnaelle.fs.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.linnaelle.fs.model.Config;

import java.io.*;

public class ConfigManager {
    private static final String CONFIG_FILE = "config.json";
    private static Config config;

    private static void loadConfig() {
        File configFile = new File(CONFIG_FILE);

        if (!configFile.exists()) {
            System.out.println("Fichier de configuration non trouvé. Création d'une configuration par défaut.");
            config = new Config();
            saveConfig(config);
            return;
        }

        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            Gson gson = new Gson();
            config = gson.fromJson(reader, Config.class);
            System.out.println("Configuration chargée.");
        } catch (IOException e) {
            System.err.println("Impossible de lire le fichier de configuration : " + e.getMessage());
            throw new RuntimeException("Échec du chargement de la configuration");
        }
    }

    /**
     * Enregistre la configuration actuelle dans le fichier config.json.
     * @param newConfig La nouvelle configuration à enregistrer.
     */
    public static void saveConfig(Config newConfig) {
        config = newConfig;
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(config, writer);
            System.out.println("Configuration sauvegardée.");
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde de la configuration : " + e.getMessage());
        }
    }

    /**
     * Retourne la configuration actuelle.
     * Charge la configuration depuis le fichier si elle n'est pas déjà chargée.
     * @return L'objet Config contenant la configuration du jeu.
     */
    public static Config getConfig() {
        if (config == null) loadConfig();
        return config;
    }

    public static boolean isFullscreen() { return getConfig().getGameConfig().isFullscreen(); }
    public static String getTitle() { return getConfig().getGameConfig().getTitle(); }
    public static String getVersion() { return getConfig().getGameConfig().getVersion(); }
    public static int getWindowWidth() { return getConfig().getGameConfig().getWindowWidth(); }
    public static int getWindowHeight() { return getConfig().getGameConfig().getWindowHeight(); }
    public static boolean isWindowResizable() { return getConfig().getGameConfig().isWindowResizable(); }
    public static String getLanguage() { return getConfig().getGameConfig().getLanguage(); }
    public static double getVolume() { return getConfig().getGameConfig().getVolume(); }
    public static boolean isAutoSave() { return getConfig().getGameConfig().isAutoSave(); }
    public static int getAutoSaveIntervalMinutes() { return getConfig().getGameConfig().getAutoSaveIntervalMinutes(); }
    public static String getDefaultPlayerName() { return getConfig().getPlayerConfig().getDefaultName(); }
    public static int getStartMoney() { return getConfig().getPlayerConfig().getStartMoney(); }
    public static String getDatabasePath() { return getConfig().getDatabaseConfig().getPath(); }
    public static boolean isMemoryMode() { return getConfig().getDatabaseConfig().isMemoryMode(); }
}
