package dev.linnaelle.fs;

import dev.linnaelle.fs.utils.DatabaseManager;
import dev.linnaelle.fs.service.CatalogueInitializer;
import dev.linnaelle.fs.dao.CatalogueDao;
import dev.linnaelle.fs.entities.*;

import java.util.List;

public class TestDao {
    
    public static void main(String[] args) {
        try {
            System.out.println("=== TEST CATALOGUE DAO ===\n");
            
            System.out.println("Initialisation de la base de donnees...");
            DatabaseManager.getInstance().initializeDatabase();
            CatalogueInitializer.initializeCatalogueData();
            
            CatalogueDao catalogueDAO = new CatalogueDao();
            
            System.out.println("\n=== TEST DES ENTITES ===\n");
            
            testCultureInfo(catalogueDAO);
            testAnimalInfo(catalogueDAO);
            testUsineInfo(catalogueDAO);
            testEquipementInfo(catalogueDAO);
            testArticleInfo(catalogueDAO);
            
            System.out.println("\n[SUCCESS] Tous les tests CatalogueDAO reussis !");
            
        } catch (Exception e) {
            System.err.println("[ERROR] Erreur lors du test: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseManager.closeInstance();
        }
    }
    
    private static void testCultureInfo(CatalogueDao dao) {
        System.out.println("[TEST] CultureInfo");
        
        CultureInfo ble = dao.getCultureInfo("grains_ble");
        if (ble != null) {
            System.out.println("  - Ble trouve: " + ble.getNom());
            System.out.println("    Prix achat: " + ble.getPrixAchat() + " gold");
            System.out.println("    Rendement: " + ble.getRendement() + "L/ha");
            System.out.println("    Article produit: " + ble.getArticleProduit());
            System.out.println("    Equipements: " + ble.getEquipements());
            System.out.println("    Need labour: " + ble.isNeedLabour());
        } else {
            System.err.println("  [ERROR] Ble non trouve");
        }
        
        List<CultureInfo> allCultures = dao.getAllCultures();
        System.out.println("  - Total cultures: " + allCultures.size());
        
        System.out.println();
    }
    
    private static void testAnimalInfo(CatalogueDao dao) {
        System.out.println("[TEST] AnimalInfo");
        
        AnimalInfo vache = dao.getAnimalInfo("vache");
        if (vache != null) {
            System.out.println("  - Vache trouvee: " + vache.getNom());
            System.out.println("    Prix achat: " + vache.getPrixAchat() + " gold");
            System.out.println("    Prix vente: " + vache.getPrixVente() + " gold");
            System.out.println("    Conso eau: " + vache.getConsoEau() + "L/s");
            System.out.println("    Conso herbe: " + vache.getConsoHerbe() + "L/s");
            System.out.println("    Articles produits: " + vache.getArticlesProduits());
        } else {
            System.err.println("  [ERROR] Vache non trouvee");
        }
        
        List<AnimalInfo> allAnimaux = dao.getAllAnimaux();
        System.out.println("  - Total animaux: " + allAnimaux.size());
        
        System.out.println();
    }
    
    private static void testUsineInfo(CatalogueDao dao) {
        System.out.println("[TEST] UsineInfo");
        
        UsineInfo moulin = dao.getUsineInfo("moulin_a_huile");
        if (moulin != null) {
            System.out.println("  - Moulin trouve: " + moulin.getNom());
            System.out.println("    Prix achat: " + moulin.getPrixAchat() + " gold");
            System.out.println("    Prix vente: " + moulin.getPrixVente() + " gold");
            System.out.println("    Multiplicateur: x" + moulin.getMultiplicateur());
            System.out.println("    Intrants: " + moulin.getIntrantsRequis());
            System.out.println("    Produit: " + moulin.getArticleProduit());
        } else {
            System.err.println("  [ERROR] Moulin non trouve");
        }
        
        List<UsineInfo> allUsines = dao.getAllUsines();
        System.out.println("  - Total usines: " + allUsines.size());
        
        System.out.println();
    }
    
    private static void testEquipementInfo(CatalogueDao dao) {
        System.out.println("[TEST] EquipementInfo");
        
        EquipementInfo tracteur = dao.getEquipementInfo("tracteur");
        if (tracteur != null) {
            System.out.println("  - Tracteur trouve: " + tracteur.getNom());
            System.out.println("    Prix achat: " + tracteur.getPrixAchat() + " gold");
            System.out.println("    Prix vente: " + tracteur.getPrixVente() + " gold");
            System.out.println("    Type: " + tracteur.getType());
            
            double perteValeur = tracteur.getPrixAchat() - tracteur.getPrixVente();
            double pourcentagePerte = (perteValeur / tracteur.getPrixAchat()) * 100;
            System.out.println("    Perte valeur: " + pourcentagePerte + "%");
        } else {
            System.err.println("  [ERROR] Tracteur non trouve");
        }
        
        List<EquipementInfo> allEquipements = dao.getAllEquipements();
        System.out.println("  - Total equipements: " + allEquipements.size());
        
        System.out.println();
    }
    
    private static void testArticleInfo(CatalogueDao dao) {
        System.out.println("[TEST] ArticleInfo");
        
        ArticleInfo lait = dao.getArticleInfo("lait");
        if (lait != null) {
            System.out.println("  - Lait trouve: " + lait.getNom());
            System.out.println("    Categorie: " + lait.getCategorie());
            System.out.println("    Prix vente: " + lait.getPrixVente() + " gold/L");
        } else {
            System.err.println("  [ERROR] Lait non trouve");
        }
        
        ArticleInfo gateau = dao.getArticleInfo("gateau");
        if (gateau != null) {
            System.out.println("  - Gateau trouve: " + gateau.getNom());
            System.out.println("    Categorie: " + gateau.getCategorie());
            System.out.println("    Prix vente: " + gateau.getPrixVente() + " gold/L");
        } else {
            System.err.println("  [ERROR] Gateau non trouve");
        }
        
        List<ArticleInfo> allArticles = dao.getAllArticles();
        System.out.println("  - Total articles: " + allArticles.size());
        
        System.out.println();
    }
}