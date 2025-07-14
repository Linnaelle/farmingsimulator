package dev.linnaelle.fs.services;

import dev.linnaelle.fs.utils.DatabaseManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DataInitializer {
    /**
     * Initialise les données de la base de données en insérant des informations
     */
    public static void initializeData() {
        try (Connection conn = DatabaseManager.get();
             Statement stmt = conn.createStatement()) {

            insertCultureData(stmt);
            insertAnimalData(stmt);
            insertUsineData(stmt);
            insertEquipementData(stmt);
            insertArticleData(stmt);
            insertDifficulteData(stmt);

            System.out.println("Initialisation du catalogue terminée avec succès.");

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'initialisation du catalogue: " + e.getMessage());
            throw new RuntimeException("Échec de l'initialisation du catalogue", e);
        }
    }

    private static void insertCultureData(Statement stmt) throws SQLException {
        String insertCultureData = """
            INSERT OR IGNORE INTO CultureInfo (nom, prix_achat, prix_vente, rendement, need_labour, article_produit, equipements) VALUES
            ('grains_ble', 0.5, 0.25, 1000, 1, 'ble', 'tracteur,semeuse,moissonneuse'),
            ('grains_orge', 0.5, 0.25, 1000, 1, 'orge', 'tracteur,semeuse,moissonneuse'),
            ('grains_avoine', 0.5, 0.25, 1000, 1, 'avoine', 'tracteur,semeuse,moissonneuse'),
            ('grains_canola', 0.6, 0.30, 1000, 1, 'canola', 'tracteur,semeuse,moissonneuse'),
            ('grains_soja', 0.6, 0.30, 1000, 1, 'soja', 'tracteur,semeuse,moissonneuse'),
            ('grains_mais', 0.3, 0.15, 3000, 1, 'mais', 'tracteur,semeuse,moissonneuse'),
            ('graines_tournesol', 0.3, 0.15, 3000, 1, 'tournesol', 'tracteur,semeuse,moissonneuse'),
            ('graines_betterave', 0.3, 0.15, 3500, 1, 'betterave', 'tracteur,semeuse,moissonneuse_betterave'),
            ('plants_pomme_de_terre', 0.2, 0.10, 5000, 1, 'pomme_de_terre', 'tracteur,planteuse_pomme_de_terre,moissonneuse_pomme_de_terre'),
            ('plants_raisin', 2.0, 1.0, 1500, 1, 'raisin', 'tracteur,semeuse,moissonneuse_raisin'),
            ('graines_herbe', 0.1, 0.05, 5000, 0, 'herbe', 'tracteur,semeuse,moissonneuse'),
            ('plants_cacao', 5.0, 2.5, 1000, 1, 'cacao', 'planteuse_arbre,moissonneuse_arbre'),
            ('graines_olive', 2.0, 1.0, 1500, 1, 'olive', 'tracteur,planteuse_arbre,moissonneuse_olive'),
            ('graines_coton', 0.4, 0.20, 750, 1, 'coton', 'tracteur,semeuse,moissonneuse_coton'),
            ('plants_canne_a_sucre', 1.0, 0.5, 5000, 1, 'canne_a_sucre', 'tracteur,planteuse_canne,moissonneuse_canne_a_sucre'),
            ('graines_peuplier', 3.0, 1.5, 1500, 1, 'peuplier', 'tracteur,planteuse_arbre,moissonneuse_arbre'),
            ('graines_legumes', 1.0, 0.5, 2500, 1, 'legumes', 'tracteur,planteuse_legumes,moissonneuse_legumes'),
            ('graines_epinard', 0.8, 0.4, 3000, 1, 'epinard', 'tracteur,semeuse,moissonneuse_epinard'),
            ('graines_pois', 1.5, 0.75, 7500, 1, 'pois', 'tracteur,semeuse,moissonneuse_pois'),
            ('graines_haricots_verts', 1.5, 0.75, 7500, 1, 'haricots_verts', 'tracteur,semeuse,moissonneuse_haricots');
            """;
        stmt.execute(insertCultureData);
        System.out.println("Données CultureInfo insérées.");
    }

    private static void insertAnimalData(Statement stmt) throws SQLException {
        String insertAnimalData = """
            INSERT OR IGNORE INTO AnimalInfo (nom, prix_achat, prix_vente, conso_eau, conso_herbe, stock_herbe, articles_produits) VALUES
            ('vache', 10.0, 5.0, 3, 3, 10, 'lait,fumier'),
            ('mouton', 5.0, 2.5, 2, 2, 10, 'laine,fumier'),
            ('poule', 1.0, 0.5, 1, 1, 10, 'oeufs');
            """;
        stmt.execute(insertAnimalData);
        System.out.println("Données AnimalInfo insérées.");
    }

    private static void insertUsineData(Statement stmt) throws SQLException {
        String insertUsineData = """
            INSERT OR IGNORE INTO UsineInfo (nom, prix_achat, prix_vente, intrants_requis, multiplicateur, article_produit) VALUES
            ('moulin_a_huile', 25000.0, 20000.0, 'tournesol:100,olive:100,canola:100', 2.0, 'huile'),
            ('scierie', 30000.0, 24000.0, 'peuplier:100', 2.0, 'planches'),
            ('fabrique_wagons', 80000.0, 64000.0, 'planches:100', 4.0, 'wagons'),
            ('usine_jouets', 60000.0, 48000.0, 'planches:100', 3.0, 'jouets'),
            ('moulin_a_grains', 20000.0, 16000.0, 'ble:100,orge:100', 2.0, 'farine'),
            ('raffinerie_sucre', 35000.0, 28000.0, 'betterave:100,canne_a_sucre:100', 2.0, 'sucre'),
            ('filature', 25000.0, 20000.0, 'coton:100', 2.0, 'tissu'),
            ('atelier_couture', 40000.0, 32000.0, 'tissu:100,laine:100', 4.0, 'vetements'),
            ('boulangerie', 50000.0, 40000.0, 'sucre:50,lait:50,farine:50,oeufs:50,beurre:50,chocolat:50,fraises:50', 18.0, 'gateau'),
            ('usine_chips', 45000.0, 36000.0, 'pommes_de_terre:100,huile:100', 6.0, 'chips'),
            ('cave_a_vin', 60000.0, 48000.0, 'raisin:100', 2.0, 'vin'),
            ('usine_fumier', 15000.0, 12000.0, 'fumier:100', 2.0, 'fertilisant'),
            ('laiterie', 30000.0, 24000.0, 'lait:100', 1.0, 'lait_sterilise'),
            ('chocolaterie', 70000.0, 56000.0, 'cacao:100,sucre:100,lait:100', 2.0, 'chocolat'),
            ('serre', 40000.0, 32000.0, 'eau:15', 1.0, 'fraises');
            """;
        stmt.execute(insertUsineData);
        System.out.println("Données UsineInfo insérées.");
    }

    private static void insertEquipementData(Statement stmt) throws SQLException {
        String insertEquipementData = """
            INSERT OR IGNORE INTO EquipementInfo (nom, prix_achat, prix_vente, type) VALUES
            ('tracteur', 50000.0, 25000.0, 'tracteur'),
            ('remorque', 15000.0, 7500.0, 'remorque'),
            ('moissonneuse', 80000.0, 40000.0, 'moissonneuse'),
            ('charrue', 8000.0, 4000.0, 'charrue'),
            ('fertilisateur', 12000.0, 6000.0, 'fertilisateur'),
            ('semeuse', 15000.0, 7500.0, 'semeuse'),
            ('moissonneuse_raisin', 90000.0, 45000.0, 'moissonneuse_raisin'),
            ('moissonneuse_olive', 95000.0, 47500.0, 'moissonneuse_olive'),
            ('moissonneuse_pomme_de_terre', 100000.0, 50000.0, 'moissonneuse_pomme_de_terre'),
            ('moissonneuse_betterave', 120000.0, 60000.0, 'moissonneuse_betterave'),
            ('moissonneuse_coton', 110000.0, 55000.0, 'moissonneuse_coton'),
            ('moissonneuse_canne_a_sucre', 125000.0, 62500.0, 'moissonneuse_canne_a_sucre'),
            ('moissonneuse_arbre', 85000.0, 42500.0, 'moissonneuse_arbre'),
            ('moissonneuse_epinard', 85000.0, 42500.0, 'moissonneuse_epinard'),
            ('moissonneuse_haricots', 90000.0, 45000.0, 'moissonneuse_haricots'),
            ('moissonneuse_pois', 88000.0, 44000.0, 'moissonneuse_pois'),
            ('moissonneuse_legumes', 95000.0, 47500.0, 'moissonneuse_legumes'),
            ('planteuse_arbre', 25000.0, 12500.0, 'planteuse_arbre'),
            ('planteuse_pomme_de_terre', 30000.0, 15000.0, 'planteuse_pomme_de_terre'),
            ('planteuse_canne_a_sucre', 35000.0, 17500.0, 'planteuse_canne_a_sucre'),
            ('planteuse_legumes', 20000.0, 10000.0, 'planteuse_legumes'),
            ('semi_remorque', 40000.0, 20000.0, 'semi_remorque');
            """;
        stmt.execute(insertEquipementData);
        System.out.println("Données EquipementInfo insérées.");
    }

    private static void insertArticleData(Statement stmt) throws SQLException {
        String insertArticleData = """
            INSERT OR IGNORE INTO ArticleInfo (nom, categorie, prix_vente) VALUES
            ('ble', 'cereale', 1.0),
            ('orge', 'cereale', 1.0),
            ('avoine', 'cereale', 1.0),
            ('canola', 'cereale', 1.0),
            ('soja', 'cereale', 1.0),
            ('mais', 'cereale', 1.0),
            ('tournesol', 'cereale', 1.0),
            ('betterave', 'legume', 1.0),
            ('pomme_de_terre', 'legume', 1.0),
            ('raisin', 'fruit', 1.5),
            ('herbe', 'fourrage', 0.5),
            ('cacao', 'fruit', 2.0),
            ('lait', 'produit_animal', 2.0),
            ('fumier', 'produit_animal', 1.0),
            ('laine', 'produit_animal', 1.5),
            ('oeufs', 'produit_animal', 1.0),
            ('huile', 'produit_transforme', 2.0),
            ('planches', 'materiau', 2.0),
            ('wagons', 'produit_fini', 8.0),
            ('jouets', 'produit_fini', 6.0),
            ('farine', 'produit_transforme', 2.0),
            ('sucre', 'produit_transforme', 2.0),
            ('tissu', 'materiau', 2.0),
            ('vetements', 'produit_fini', 8.0),
            ('gateau', 'produit_fini', 18.0),
            ('chips', 'produit_fini', 6.0),
            ('vin', 'produit_fini', 3.0),
            ('fertilisant', 'intrant', 2.0),
            ('lait_sterilise', 'produit_transforme', 2.0),
            ('beurre', 'produit_transforme', 2.0),
            ('chocolat', 'produit_transforme', 4.0),
            ('fraises', 'fruit', 3.0),
            ('olive', 'fruit', 1.5),
            ('coton', 'materiau', 1.0),
            ('canne_a_sucre', 'cereale', 1.0),
            ('peuplier', 'materiau', 1.5),
            ('legumes', 'legume', 1.5),
            ('epinard', 'legume', 2.0),
            ('pois', 'legume', 3.0),
            ('haricots_verts', 'legume', 3.0);
            """;
        stmt.execute(insertArticleData);
        System.out.println("Données ArticleInfo insérées.");
    }

    private static void insertDifficulteData(Statement stmt) throws SQLException {
        String insertDifficultesData = """
            INSERT OR IGNORE INTO Difficulte (nom, goldDepart, multiplicateurAchat, multiplicateurVente) VALUES
            ('facile', 200000, 0.75, 1.25),
            ('normal', 150000, 1.0, 1.0),
            ('difficile', 100000, 1.25, 0.75),
            ('expert', 75000, 1.5, 0.5);
            """;
        stmt.execute(insertDifficultesData);
        System.out.println("Données Difficultes insérées.");
    }
}