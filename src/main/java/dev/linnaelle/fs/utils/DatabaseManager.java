package dev.linnaelle.fs.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.linnaelle.fs.model.Config;
import java.sql.*;
import java.io.File;

public class DatabaseManager {
    private static DatabaseManager instance;
    private HikariDataSource dataSource;
    private String dbPath;

    private DatabaseManager() {}

    public static DatabaseManager getInstance() {
        if (instance == null) instance = new DatabaseManager();
        return instance;
    }

    public static Connection get() throws SQLException { 
        return getInstance().getConnection(); 
    }
    
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

    public Connection getConnection() throws SQLException { 
        return dataSource.getConnection(); 
    }

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

            String createDifficulteTable = """
                CREATE TABLE IF NOT EXISTS Difficulte (
                    nom TEXT PRIMARY KEY,
                    goldDepart REAL NOT NULL,
                    multiplicateurAchat REAL DEFAULT 1.0,
                    multiplicateurVente REAL DEFAULT 1.0
                );
                """;
            stmt.execute(createDifficulteTable);
            System.out.println("Table 'Difficulte' créée.");

            String createJoueurTable = """
                CREATE TABLE IF NOT EXISTS Joueur (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    temps_jeu INTEGER DEFAULT 0,
                    difficulte TEXT NOT NULL,
                    FOREIGN KEY (difficulte) REFERENCES Difficulte(nom)
                );
                """;
            stmt.execute(createJoueurTable);
            System.out.println("Table 'Joueur' créée.");

            String createFermeTable = """
                CREATE TABLE IF NOT EXISTS Ferme (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    joueur_id INTEGER NOT NULL,
                    name TEXT NOT NULL,
                    revenu REAL DEFAULT 0.0,
                    FOREIGN KEY (joueur_id) REFERENCES Joueur(id) ON DELETE CASCADE
                );
                """;
            stmt.execute(createFermeTable);
            System.out.println("Table 'Ferme' créée.");

            String createStockageTable = """
                CREATE TABLE IF NOT EXISTS Stockage (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    ferme_id INTEGER NOT NULL,
                    capacite_max INTEGER NOT NULL,
                    FOREIGN KEY (ferme_id) REFERENCES Ferme(id) ON DELETE CASCADE
                );
                """;
            stmt.execute(createStockageTable);
            System.out.println("Table 'Stockage' créée.");

            String createStockPrincipalTable = """
                CREATE TABLE IF NOT EXISTS StockPrincipal (
                    stockage_id INTEGER PRIMARY KEY,
                    FOREIGN KEY (stockage_id) REFERENCES Stockage(id) ON DELETE CASCADE
                );
                """;
            stmt.execute(createStockPrincipalTable);
            System.out.println("Table 'StockPrincipal' créée.");

            String createEntrepotTable = """
                CREATE TABLE IF NOT EXISTS Entrepot (
                    stockage_id INTEGER PRIMARY KEY,
                    FOREIGN KEY (stockage_id) REFERENCES Stockage(id) ON DELETE CASCADE
                );
                """;
            stmt.execute(createEntrepotTable);
            System.out.println("Table 'Entrepot' créée.");

            String createReservoirEauTable = """
                CREATE TABLE IF NOT EXISTS ReservoirEau (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    stockage_id INTEGER,
                    capacite INTEGER DEFAULT 20000,
                    quantite INTEGER DEFAULT 0,
                    dernier_remplissage INTEGER DEFAULT 0,
                    FOREIGN KEY (stockage_id) REFERENCES Stockage(id) ON DELETE CASCADE
                );
                """;
            stmt.execute(createReservoirEauTable);
            System.out.println("Table 'ReservoirEau' créée.");

            String createArticlesStockageTable = """
                CREATE TABLE IF NOT EXISTS ArticlesStockage (
                    stockage_id INTEGER NOT NULL,
                    article TEXT NOT NULL,
                    quantite INTEGER NOT NULL DEFAULT 0,
                    PRIMARY KEY (stockage_id, article),
                    FOREIGN KEY (stockage_id) REFERENCES Stockage(id) ON DELETE CASCADE
                );
                """;
            stmt.execute(createArticlesStockageTable);
            System.out.println("Table 'ArticlesStockage' créée.");

            String createChampTable = """
                CREATE TABLE IF NOT EXISTS Champ (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    ferme_id INTEGER NOT NULL,
                    name TEXT,
                    numero INTEGER NOT NULL,
                    type_culture TEXT,
                    temps_action INTEGER DEFAULT 0,
                    etat TEXT NOT NULL CHECK (etat IN ('STANDBY', 'LABOURE', 'SEME', 'FERTILISE', 'READY')),
                    prix_achat REAL DEFAULT 0.0,
                    FOREIGN KEY (ferme_id) REFERENCES Ferme(id) ON DELETE CASCADE
                );
                """;
            stmt.execute(createChampTable);
            System.out.println("Table 'Champ' créée.");

            String createFermeAnimaleTable = """
                CREATE TABLE IF NOT EXISTS FermeAnimale (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    champ_id INTEGER NOT NULL,
                    type_animal TEXT NOT NULL,
                    capacite_max INTEGER DEFAULT 10,
                    FOREIGN KEY (champ_id) REFERENCES Champ(id) ON DELETE CASCADE
                );
                """;
            stmt.execute(createFermeAnimaleTable);
            System.out.println("Table 'FermeAnimale' créée.");

            String createAnimalTable = """
                CREATE TABLE IF NOT EXISTS Animal (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    ferme_animale_id INTEGER NOT NULL,
                    type TEXT NOT NULL,
                    stock_herbe INTEGER DEFAULT 10,
                    vivant INTEGER DEFAULT 1,
                    deficit INTEGER DEFAULT 0,
                    FOREIGN KEY (ferme_animale_id) REFERENCES FermeAnimale(id) ON DELETE CASCADE
                );
                """;
            stmt.execute(createAnimalTable);
            System.out.println("Table 'Animal' créée.");

            String createGestionnaireEquipementTable = """
                CREATE TABLE IF NOT EXISTS GestionnaireEquipement (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    ferme_id INTEGER NOT NULL,
                    FOREIGN KEY (ferme_id) REFERENCES Ferme(id) ON DELETE CASCADE
                );
                """;
            stmt.execute(createGestionnaireEquipementTable);
            System.out.println("Table 'GestionnaireEquipement' créée.");

            String createEquipementTable = """
                CREATE TABLE IF NOT EXISTS Equipement (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    gestionnaire_id INTEGER NOT NULL,
                    type TEXT NOT NULL,
                    en_utilisation INTEGER DEFAULT 0,
                    FOREIGN KEY (gestionnaire_id) REFERENCES GestionnaireEquipement(id) ON DELETE CASCADE
                );
                """;
            stmt.execute(createEquipementTable);
            System.out.println("Table 'Equipement' créée.");

            String createStructureProductionTable = """
                CREATE TABLE IF NOT EXISTS StructureProduction (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    ferme_id INTEGER NOT NULL,
                    type TEXT NOT NULL,
                    active INTEGER DEFAULT 0,
                    prix_achat REAL DEFAULT 0.0,
                    en_pause INTEGER DEFAULT 0,
                    taux_traitement INTEGER DEFAULT 100,
                    FOREIGN KEY (ferme_id) REFERENCES Ferme(id) ON DELETE CASCADE
                );
                """;
            stmt.execute(createStructureProductionTable);
            System.out.println("Table 'StructureProduction' créée.");

            String createUsineTable = """
                CREATE TABLE IF NOT EXISTS Usine (
                    structure_id INTEGER PRIMARY KEY,
                    FOREIGN KEY (structure_id) REFERENCES StructureProduction(id) ON DELETE CASCADE
                );
                """;
            stmt.execute(createUsineTable);
            System.out.println("Table 'Usine' créée.");

            String createSerreTable = """
                CREATE TABLE IF NOT EXISTS Serre (
                    structure_id INTEGER PRIMARY KEY,
                    dernier_recolte INTEGER DEFAULT 0,
                    FOREIGN KEY (structure_id) REFERENCES StructureProduction(id) ON DELETE CASCADE
                );
                """;
            stmt.execute(createSerreTable);
            System.out.println("Table 'Serre' créée.");

            String createEconomieTable = """
                CREATE TABLE IF NOT EXISTS Economie (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    joueur_id INTEGER NOT NULL,
                    FOREIGN KEY (joueur_id) REFERENCES Joueur(id) ON DELETE CASCADE
                );
                """;
            stmt.execute(createEconomieTable);
            System.out.println("Table 'Economie' créée.");

            String createCultureInfoTable = """
                CREATE TABLE IF NOT EXISTS CultureInfo (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nom TEXT NOT NULL UNIQUE,
                    prix_achat REAL DEFAULT 0.0,
                    prix_vente REAL DEFAULT 0.0,
                    rendement INTEGER NOT NULL,
                    need_labour INTEGER DEFAULT 1,
                    article_produit TEXT NOT NULL,
                    equipements TEXT NOT NULL
                );
                """;
            stmt.execute(createCultureInfoTable);
            System.out.println("Table 'CultureInfo' créée.");

            String createAnimalInfoTable = """
                CREATE TABLE IF NOT EXISTS AnimalInfo (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nom TEXT NOT NULL UNIQUE,
                    prix_achat REAL DEFAULT 0.0,
                    prix_vente REAL DEFAULT 0.0,
                    conso_eau INTEGER NOT NULL,
                    conso_herbe INTEGER NOT NULL,
                    stock_herbe INTEGER NOT NULL,
                    articles_produits TEXT NOT NULL
                );
                """;
            stmt.execute(createAnimalInfoTable);
            System.out.println("Table 'AnimalInfo' créée.");

            String createUsineInfoTable = """
                CREATE TABLE IF NOT EXISTS UsineInfo (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nom TEXT NOT NULL UNIQUE,
                    prix_achat REAL DEFAULT 0.0,
                    prix_vente REAL DEFAULT 0.0,
                    intrants_requis TEXT NOT NULL,
                    multiplicateur REAL NOT NULL,
                    article_produit TEXT NOT NULL
                );
                """;
            stmt.execute(createUsineInfoTable);
            System.out.println("Table 'UsineInfo' créée.");

            String createEquipementInfoTable = """
                CREATE TABLE IF NOT EXISTS EquipementInfo (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nom TEXT NOT NULL UNIQUE,
                    prix_achat REAL DEFAULT 0.0,
                    prix_vente REAL DEFAULT 0.0,
                    type TEXT NOT NULL
                );
                """;
            stmt.execute(createEquipementInfoTable);
            System.out.println("Table 'EquipementInfo' créée.");

            String createArticleInfoTable = """
                CREATE TABLE IF NOT EXISTS ArticleInfo (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nom TEXT NOT NULL UNIQUE,
                    categorie TEXT NOT NULL,
                    prix_vente REAL DEFAULT 0.0
                );
                """;
            stmt.execute(createArticleInfoTable);
            System.out.println("Table 'ArticleInfo' créée.");

            String createSauvegardeTable = """
                CREATE TABLE IF NOT EXISTS Sauvegarde (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    joueur_id INTEGER NOT NULL,
                    chemin_fichier TEXT NOT NULL,
                    derniere_sauvegarde INTEGER DEFAULT 0,
                    FOREIGN KEY (joueur_id) REFERENCES Joueur(id) ON DELETE CASCADE
                );
                """;
            stmt.execute(createSauvegardeTable);
            System.out.println("Table 'Sauvegarde' créée.");

        } catch (SQLException e) {
            System.err.println("Erreur lors de la création des tables: " + e.getMessage());
            throw new RuntimeException("Échec de la création des tables de la base de données", e);
        }
    }
}