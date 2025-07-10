package dev.linnaelle.fs.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.linnaelle.fs.model.Config;
import dev.linnaelle.fs.utils.ConfigManager;

import java.sql.*;
import java.io.File;

/**
 * Classe utilitaire pour gérer la base de données SQLite avec HikariCP.
 * Implémente le pattern Singleton pour garantir une seule instance de DatabaseManager.
 * Gère l'initialisation du pool de connexions, la création des tables et la fermeture du pool.
 */
public class DatabaseManager {
    private static DatabaseManager instance;
    private HikariDataSource dataSource;
    private String dbPath;

    private DatabaseManager() {}

    /**
     * Retourne l'instance unique de DatabaseManager (Singleton).
     * @return L'instance de DatabaseManager.
     */
    public static DatabaseManager getInstance() {
        if (instance == null) instance = new DatabaseManager();
        return instance;
    }

    /**
     * Retourne une connexion à la base de données.
     * Utilise le pool de connexions HikariCP pour obtenir une connexion.
     * @return Une instance de Connection.
     * @throws SQLException Si une erreur SQL survient lors de l'obtention de la connexion.
     */
    public static Connection get() throws SQLException { return getInstance().getConnection(); }
    
    /**
     * Initialise le pool de connexions à la base de données et crée les tables si elles n'existent pas.
     * Cette méthode doit être appelée une fois au démarrage de l'application.
     */
    public void initializeDatabase() {
        Config.DatabaseConfig dbConfig = ConfigManager.getConfig().getDatabaseConfig();

        if (!dbConfig.isMemoryMode()) {
            File dbFile = new File(dbConfig.getPath());
            File parentDir = dbFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                if (parentDir.mkdirs()) {
                    System.out.println("Répertoire de base de données créé : " + parentDir.getAbsolutePath());
                } else {
                    System.err.println("Échec de la création du répertoire de base de données : " + parentDir.getAbsolutePath());
                    throw new RuntimeException("Échec de la création du répertoire de base de données.");
                }
            }
        }

        HikariConfig config = new HikariConfig();
        dbPath = dbConfig.isMemoryMode() ? "jdbc:sqlite::memory:" : "jdbc:sqlite:" + dbConfig.getPath();
        config.setJdbcUrl(dbPath);
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);

        try {
            dataSource = new HikariDataSource(config);
            System.out.println("Pool de connexions HikariCP pour SQLite initialisé.");
            createTables();
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation de la base de données: " + e.getMessage());
            throw new RuntimeException("Échec de l'initialisation de la base de données", e);
        }
    }

    /**
     * Retourne une connexion à la base de données depuis le pool.
     * @return Une instance de Connection.
     * @throws SQLException Si une erreur SQL survient lors de l'obtention de la connexion.
     */
    public Connection getConnection() throws SQLException { return dataSource.getConnection(); }

    /**
     * Ferme le pool de connexions à la base de données.
     * Cette méthode doit être appelée lors de la fermeture de l'application.
     */
    public static void closeInstance() {
        if (instance != null && instance.dataSource != null && !instance.dataSource.isClosed()) {
            instance.dataSource.close();
            System.out.println("Instance fermée.");
        } else {
            System.out.println("Instance déjà fermée ou non initialisée.");
        }
        instance = null;
    }

    private void createTables() {
        try (Connection conn = getConnection();
            Statement stmt = conn.createStatement()) {

            // Table Farm
            String createFarmTable = "CREATE TABLE IF NOT EXISTS Farm (" +
                                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                    "name TEXT DEFAULT 'Invite'," +
                                    "income REAL DEFAULT 10000.0," + // REAL = double
                                    "current_game_time INTEGER DEFAULT 0" +
                                    ");";
            stmt.execute(createFarmTable);
            System.out.println("Table 'Farm' créée.");

            // Table Champs
            String createChampsTable = "CREATE TABLE IF NOT EXISTS Champs (" +
                                       "numero INTEGER PRIMARY KEY," +
                                       "etat TEXT NOT NULL," +
                                       "type_culture TEXT," + // ENUM
                                       "groupe_lot TEXT," +
                                       "fertilise INTEGER NOT NULL" + // BOOLEAN = 0 ou 1
                                       ");";
            stmt.execute(createChampsTable);
            System.out.println("Table 'Champs' créée.");

            // Table Stockage (une seule instance pour toute la ferme)
            String createStockageTable = "CREATE TABLE IF NOT EXISTS Stockage (" +
                                         "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                         "capacite_maximale INTEGER NOT NULL," +
                                         "capacite_actuelle INTEGER NOT NULL" +
                                         ");";
            stmt.execute(createStockageTable);
            System.out.println("Table 'Stockage' créée.");

            // Table StorageItems (pour les articles stockés, liés à Stockage)
            String createStorageItemsTable = "CREATE TABLE IF NOT EXISTS StorageItems (" +
                                             "storage_id INTEGER NOT NULL," +
                                             "type_article TEXT NOT NULL," + // ENUM
                                             "quantity INTEGER NOT NULL," +
                                             "PRIMARY KEY (storage_id, type_article)," +
                                             "FOREIGN KEY (storage_id) REFERENCES Stockage(id)" +
                                             ");";
            stmt.execute(createStorageItemsTable);
            System.out.println("Table 'StorageItems' créée.");

            // Table Equipements
            String createEquipementsTable = "CREATE TABLE IF NOT EXISTS Equipements (" +
                                            "id TEXT PRIMARY KEY," +
                                            "type_equipement TEXT NOT NULL," + // ENUM
                                            "en_utilisation INTEGER NOT NULL" +
                                            ");";
            stmt.execute(createEquipementsTable);
            System.out.println("Table 'Equipements' créée.");

            // Table Usines
            String createUsinesTable = "CREATE TABLE IF NOT EXISTS Usines (" +
                                       "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                       "type_usine TEXT NOT NULL," + // ENUM
                                       "en_marche INTEGER NOT NULL" +
                                       ");";
            stmt.execute(createUsinesTable);
            System.out.println("Table 'Usines' créée.");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création des tables: " + e.getMessage());
            throw new RuntimeException("Échec de la création des tables de la base de données", e);
        }
    }
}