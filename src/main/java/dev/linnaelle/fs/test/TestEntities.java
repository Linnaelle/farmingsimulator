package dev.linnaelle.fs.test;

import dev.linnaelle.fs.utils.DatabaseManager;
import dev.linnaelle.fs.dao.*;
import dev.linnaelle.fs.entities.*;
import dev.linnaelle.fs.services.*;

/**
 * Test complet des entités, DAO et Services
 * Style similaire au TestDao existant
 */
public class TestEntities {
    
    public static void main(String[] args) {
        try {
            System.out.println("=== TEST COMPLET DES ENTITES ===\n");
            
            System.out.println("Initialisation de la base de donnees...");
            DatabaseManager.getInstance().initializeDatabase();
            DataInitializer.initializeData();
            
            System.out.println("\n=== TEST DES ENTITES SIMPLES ===\n");
            
            testDifficulte();
            testStockageEntities();
            testChampEntity();
            testAnimalEntity();
            testEquipementEntity();
            testStructureProductionEntities();
            testJoueurFermeEntities();
            
            System.out.println("\n=== TEST DES DAO ===\n");
            
            testDifficulteDao();
            testReservoirEauDao();
            testUsineSerreDao();
            
            System.out.println("\n=== TEST DES SERVICES ===\n");
            
            testCatalogueService();
            testEconomieService();
            testSauvegardeService();
            
            System.out.println("\n[SUCCESS] Tous les tests des entites reussis !");
            
        } catch (Exception e) {
            System.err.println("[ERROR] Erreur lors du test: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseManager.closeInstance();
        }
    }
    
    private static void testDifficulte() {
        System.out.println("[TEST] Difficulte Entity");
        
        Difficulte facile = new Difficulte("Facile", 50000.0, 1.0, 1.2);
        System.out.println("  - Difficulte creee: " + facile.getNom());
        System.out.println("    Gold depart: " + facile.getGoldDepart() + " gold");
        System.out.println("    Multiplicateur achat: x" + facile.getMultiplicateurAchat());
        System.out.println("    Multiplicateur vente: x" + facile.getMultiplicateurVente());
        
        // Test des setters
        facile.setNom("Très Facile");
        if ("Très Facile".equals(facile.getNom())) {
            System.out.println("  - Modification nom: OK");
        } else {
            System.err.println("  [ERROR] Modification nom echouee");
        }
        
        // Test toString
        String desc = facile.toString();
        if (desc != null && desc.contains("Très Facile")) {
            System.out.println("  - ToString: OK");
        } else {
            System.err.println("  [ERROR] ToString invalide");
        }
        
        System.out.println();
    }
    
    private static void testStockageEntities() {
        System.out.println("[TEST] Stockage Entities");
        
        // Test StockPrincipal
        StockPrincipal stock = new StockPrincipal(1);
        System.out.println("  - StockPrincipal cree");
        System.out.println("    Capacite max: " + stock.getCapaciteMax() + "L");
        System.out.println("    Ferme ID: " + stock.getFermeId());
        System.out.println("    Peut stocker ble: " + stock.peutStocker("ble"));
        System.out.println("    Peut stocker eau: " + stock.peutStocker("eau"));
        
        // Test ajout/retrait
        boolean ajoutOk = stock.ajouter("ble", 1000);
        System.out.println("    Ajout 1000L ble: " + (ajoutOk ? "OK" : "ECHEC"));
        System.out.println("    Quantite ble: " + stock.getQuantite("ble") + "L");
        
        boolean retraitOk = stock.retirer("ble", 500);
        System.out.println("    Retrait 500L ble: " + (retraitOk ? "OK" : "ECHEC"));
        System.out.println("    Quantite finale ble: " + stock.getQuantite("ble") + "L");
        System.out.println("    Capacite libre: " + stock.capaciteLibre() + "L");
        
        // Test Entrepot
        Entrepot entrepot = new Entrepot(1);
        System.out.println("  - Entrepot cree");
        System.out.println("    Capacite max: " + entrepot.getCapaciteMax() + "L");
        System.out.println("    Peut stocker gateau: " + entrepot.peutStocker("gateau"));
        System.out.println("    Peut stocker ble: " + entrepot.peutStocker("ble"));
        
        // Test ReservoirEau
        ReservoirEau reservoir = new ReservoirEau(1, 10000);
        System.out.println("  - ReservoirEau cree");
        System.out.println("    Capacite: " + reservoir.getCapacite() + "L");
        System.out.println("    Quantite initiale: " + reservoir.getQuantite() + "L");
        System.out.println("    Peut stocker eau seulement: " + reservoir.peutStocker("eau"));
        System.out.println("    Ne peut pas stocker ble: " + !reservoir.peutStocker("ble"));
        
        boolean consoOk = reservoir.consommer(5000);
        System.out.println("    Consommation 5000L: " + (consoOk ? "OK" : "ECHEC"));
        System.out.println("    Quantite apres conso: " + reservoir.getQuantite() + "L");
        
        System.out.println();
    }
    
    private static void testChampEntity() {
        System.out.println("[TEST] Champ Entity");
        
        Champ champ = new Champ();
        champ.setFermeId(1);
        champ.setName("Champ Test");
        champ.setNumero(1);
        champ.setEtat(EtatChamp.STANDBY);
        champ.setPrixAchat(10000.0);
        champ.setTempsAction(System.currentTimeMillis());
        
        System.out.println("  - Champ cree: " + champ.getName());
        System.out.println("    Numero: " + champ.getNumero());
        System.out.println("    Ferme ID: " + champ.getFermeId());
        System.out.println("    Etat initial: " + champ.getEtat());
        System.out.println("    Prix achat: " + champ.getPrixAchat() + " gold");
        
        // Test progression des états
        champ.setEtat(EtatChamp.LABOURE);
        System.out.println("    Etat apres labour: " + champ.getEtat());
        
        champ.setTypeCulture("grains_ble");
        champ.setEtat(EtatChamp.SEME);
        System.out.println("    Culture semee: " + champ.getTypeCulture());
        System.out.println("    Etat apres semis: " + champ.getEtat());
        
        champ.setEtat(EtatChamp.FERTILISE);
        System.out.println("    Etat apres fertilisation: " + champ.getEtat());
        
        champ.setEtat(EtatChamp.READY);
        System.out.println("    Etat pret a recolter: " + champ.getEtat());
        
        System.out.println();
    }
    
    private static void testAnimalEntity() {
        System.out.println("[TEST] Animal Entity");
        
        Animal vache = new Animal();
        vache.setType("vache");
        vache.setVivant(true);
        vache.setDeficit(false);
        vache.setStockHerbe(100);
        
        System.out.println("  - Animal cree: " + vache.getType());
        System.out.println("    Vivant: " + vache.isVivant());
        System.out.println("    En deficit: " + vache.isDeficit());
        System.out.println("    Stock herbe initial: " + vache.getStockHerbe() + "L");
        
        // Test consommation
        vache.setStockHerbe(50);
        System.out.println("    Stock apres consommation: " + vache.getStockHerbe() + "L");
        
        // Test déficit
        vache.setStockHerbe(0);
        vache.setDeficit(true);
        System.out.println("    Stock epuise: " + vache.getStockHerbe() + "L");
        System.out.println("    En deficit: " + vache.isDeficit());
        
        // Test FermeAnimale
        FermeAnimale fermeAnimale = new FermeAnimale();
        fermeAnimale.setTypeAnimal("vache");
        fermeAnimale.setCapaciteMax(10);
        
        System.out.println("  - FermeAnimale creee");
        System.out.println("    Type animal: " + fermeAnimale.getTypeAnimal());
        System.out.println("    Capacite max: " + fermeAnimale.getCapaciteMax() + " animaux");
        
        System.out.println();
    }
    
    private static void testEquipementEntity() {
        System.out.println("[TEST] Equipement Entity");
        
        Equipement tracteur = new Equipement();
        tracteur.setType("tracteur");
        tracteur.setEnUtilisation(false);
        
        System.out.println("  - Equipement cree: " + tracteur.getType());
        System.out.println("    En utilisation: " + tracteur.isEnUtilisation());
        
        // Test utilisation
        long tempsCourant = System.currentTimeMillis();
        tracteur.utiliser(60000, tempsCourant); // 1 minute
        System.out.println("    Utilisation 1min: " + tracteur.isEnUtilisation());
        
        // Test GestionnaireEquipement
        GestionnaireEquipement gestionnaire = new GestionnaireEquipement(1);
        System.out.println("  - GestionnaireEquipement cree");
        System.out.println("    Ferme ID: " + gestionnaire.getFermeId());
        System.out.println("    Inventaire initial: " + gestionnaire.getInventaire().size() + " types");
        
        System.out.println();
    }
    
    private static void testStructureProductionEntities() {
        System.out.println("[TEST] StructureProduction Entities");
        
        // Test Serre
        Serre serre = new Serre(1, "serre_fraises", 25000.0);
        System.out.println("  - Serre creee: " + serre.getType());
        System.out.println("    Ferme ID: " + serre.getFermeId());
        System.out.println("    Prix achat: " + serre.getPrixAchat() + " gold");
        System.out.println("    Active: " + serre.isActive());
        System.out.println("    En pause: " + serre.isEnPause());
        System.out.println("    Taux traitement: " + serre.getTauxTraitement());
        System.out.println("    Dernier recolte: " + serre.getDernierRecolte());
        
        // Test arrêt/redémarrage
        serre.arreter();
        System.out.println("    Apres arret - Active: " + serre.isActive());
        
        boolean redemarrage = serre.demarrer();
        System.out.println("    Redemarrage: " + (redemarrage ? "OK" : "ECHEC"));
        System.out.println("    Apres redemarrage - Active: " + serre.isActive());
        
        // Test Usine
        Usine moulin = new Usine(1, "moulin_a_huile", 35000.0);
        System.out.println("  - Usine creee: " + moulin.getType());
        System.out.println("    Ferme ID: " + moulin.getFermeId());
        System.out.println("    Prix achat: " + moulin.getPrixAchat() + " gold");
        System.out.println("    Active: " + moulin.isActive());
        System.out.println("    En pause: " + moulin.isEnPause());
        
        System.out.println();
    }
    
    private static void testJoueurFermeEntities() {
        System.out.println("[TEST] Joueur et Ferme Entities");
        
        // Test Difficulté
        Difficulte facile = new Difficulte("Facile", 50000.0, 1.0, 1.2);
        
        // Test Joueur
        Joueur joueur = new Joueur();
        joueur.setName("TestPlayer");
        joueur.setDifficulte(facile);
        joueur.setTempsJeu(0);
        
        System.out.println("  - Joueur cree: " + joueur.getName());
        System.out.println("    Difficulte: " + joueur.getDifficulte().getNom());
        System.out.println("    Gold depart: " + joueur.getDifficulte().getGoldDepart() + " gold");
        System.out.println("    Temps jeu initial: " + joueur.getTempsJeu() + "ms");
        
        // Test Ferme
        Ferme ferme = new Ferme();
        ferme.setName("Ferme de Test");
        ferme.setRevenu(facile.getGoldDepart());
        
        System.out.println("  - Ferme creee: " + ferme.getName());
        System.out.println("    Revenu initial: " + ferme.getRevenu() + " gold");
        
        // Simuler du temps de jeu
        long tempsDebut = System.currentTimeMillis();
        try { Thread.sleep(100); } catch (InterruptedException e) {}
        long tempsFin = System.currentTimeMillis();
        
        joueur.setTempsJeu(tempsFin - tempsDebut);
        System.out.println("    Temps jeu simule: " + joueur.getTempsJeu() + "ms");
        
        System.out.println();
    }
    
    // === TESTS DAO ===
    
    private static void testDifficulteDao() {
        System.out.println("[TEST] DifficulteDao");
        
        DifficulteDao dao = new DifficulteDao();
        
        // Test recherche par nom
        Difficulte facile = dao.findByNom("facile");
        if (facile != null) {
            System.out.println("  - Difficulte trouvee: " + facile.getNom());
            System.out.println("    Gold depart: " + facile.getGoldDepart() + " gold");
            System.out.println("    Multiplicateur achat: x" + facile.getMultiplicateurAchat());
            System.out.println("    Multiplicateur vente: x" + facile.getMultiplicateurVente());
        } else {
            System.err.println("  [ERROR] Difficulte 'Facile' non trouvee");
        }
        
        // Test liste complète
        java.util.List<Difficulte> difficultes = dao.findAll();
        System.out.println("  - Total difficultes: " + difficultes.size());
        for (Difficulte d : difficultes) {
            System.out.println("    > " + d.getNom() + " (" + d.getGoldDepart() + " gold)");
        }
        
        System.out.println();
    }
    
    private static void testReservoirEauDao() {
        System.out.println("[TEST] ReservoirEauDao");
        
        ReservoirEauDao dao = new ReservoirEauDao();
        
        // Test création
        ReservoirEau reservoir = new ReservoirEau(999, 10000); // Ferme test
        reservoir.setQuantite(15000);
        
        ReservoirEau saved = dao.save(reservoir);
        if (saved != null && saved.getId() > 0) {
            System.out.println("  - Reservoir sauvegarde: ID " + saved.getId());
            System.out.println("    Ferme ID: " + saved.getFermeId());
            System.out.println("    Capacite: " + saved.getCapacite() + "L");
            System.out.println("    Quantite: " + saved.getQuantite() + "L");
            
            // Test mise à jour
            boolean updateOk = dao.updateQuantite(saved.getId(), 18000);
            System.out.println("    Mise a jour quantite: " + (updateOk ? "OK" : "ECHEC"));
            
            // Test suppression
            boolean deleteOk = dao.delete(saved.getId());
            System.out.println("    Suppression: " + (deleteOk ? "OK" : "ECHEC"));
        } else {
            System.err.println("  [ERROR] Echec sauvegarde reservoir");
        }
        
        System.out.println();
    }
    
    private static void testUsineSerreDao() {
        System.out.println("[TEST] Usine et Serre DAO");
        
        StructureProductionDao dao = new StructureProductionDao();
        
        // Test Usine
        Usine moulin = new Usine(999, "moulin_test", 30000.0);
        Usine savedUsine = dao.saveUsine(moulin);
        if (savedUsine != null && savedUsine.getId() > 0) {
            System.out.println("  - Usine sauvegardee: ID " + savedUsine.getId());
            System.out.println("    Type: " + savedUsine.getType());
            System.out.println("    Prix: " + savedUsine.getPrixAchat() + " gold");
            
            // Test mise à jour
            savedUsine.setActive(false);
            boolean updateUsineOk = dao.updateUsine(savedUsine);
            System.out.println("    Mise a jour usine: " + (updateUsineOk ? "OK" : "ECHEC"));
            
            // Test suppression
            boolean deleteUsineOk = dao.delete(savedUsine.getId());
            System.out.println("    Suppression usine: " + (deleteUsineOk ? "OK" : "ECHEC"));
        } else {
            System.err.println("  [ERROR] Echec sauvegarde usine");
        }
        
        // Test Serre
        Serre serre = new Serre(999, "serre_test", 25000.0);
        Serre savedSerre = dao.saveSerre(serre);
        if (savedSerre != null && savedSerre.getId() > 0) {
            System.out.println("  - Serre sauvegardee: ID " + savedSerre.getId());
            System.out.println("    Type: " + savedSerre.getType());
            System.out.println("    Prix: " + savedSerre.getPrixAchat() + " gold");
            System.out.println("    Dernier recolte: " + savedSerre.getDernierRecolte());
            
            // Test mise à jour
            savedSerre.setDernierRecolte(System.currentTimeMillis());
            boolean updateSerreOk = dao.updateSerre(savedSerre);
            System.out.println("    Mise a jour serre: " + (updateSerreOk ? "OK" : "ECHEC"));
            
            // Test suppression
            boolean deleteSerreOk = dao.delete(savedSerre.getId());
            System.out.println("    Suppression serre: " + (deleteSerreOk ? "OK" : "ECHEC"));
        } else {
            System.err.println("  [ERROR] Echec sauvegarde serre");
        }
        
        System.out.println();
    }
    
    // === TESTS SERVICES ===
    
    private static void testCatalogueService() {
        System.out.println("[TEST] CatalogueService");
        
        CatalogueService service = CatalogueService.getInstance();
        
        // Test singleton
        CatalogueService service2 = CatalogueService.getInstance();
        System.out.println("  - Singleton: " + (service == service2 ? "OK" : "ECHEC"));
        
        // Test récupération d'infos
        CultureInfo ble = service.getCultureInfo("grains_ble");
        if (ble != null) {
            System.out.println("  - Culture ble trouvee: " + ble.getNom());
            System.out.println("    Rendement: " + ble.getRendement() + "L/ha");
            System.out.println("    Need labour: " + ble.isNeedLabour());
        }
        
        AnimalInfo vache = service.getAnimalInfo("vache");
        if (vache != null) {
            System.out.println("  - Animal vache trouve: " + vache.getNom());
            System.out.println("    Conso eau: " + vache.getConsoEau() + "L/s");
            System.out.println("    Articles produits: " + vache.getArticlesProduits());
        }
        
        // Test validations
        System.out.println("  - Validation culture 'ble': " + service.isCultureValid("grains_ble"));
        System.out.println("  - Validation animal 'licorne': " + service.isAnimalValid("licorne"));
        
        // Test méthodes métier
        System.out.println("  - Ble need labour: " + service.cultureNeedLabour("grains_ble"));
        System.out.println("  - Conso eau vache: " + service.getAnimalConsoEau("vache") + "L/s");
        
        // Stats du cache
        service.printCacheStats();
        
        System.out.println();
    }
    
    private static void testEconomieService() {
        System.out.println("[TEST] EconomieService");
        
        EconomieService service = EconomieService.getInstance();
        
        // Test singleton
        System.out.println("  - Singleton: " + (service == EconomieService.getInstance() ? "OK" : "ECHEC"));
        
        Difficulte facile = new Difficulte("Facile", 50000.0, 1.0, 1.2);
        
        // Test calculs de prix
        double prixVenteLait = service.getPrixVente("lait", facile);
        System.out.println("  - Prix vente lait: " + prixVenteLait + " gold/L");
        
        double prixAchatVache = service.getPrixAchat("vache", facile);
        System.out.println("  - Prix achat vache: " + prixAchatVache + " gold");
        
        double prixAchatTracteur = service.getPrixAchat("tracteur", facile);
        System.out.println("  - Prix achat tracteur: " + prixAchatTracteur + " gold");
        
        // Test capacité d'achat
        Ferme ferme = new Ferme();
        ferme.setRevenu(100000.0);
        
        boolean peutAcheterVache = service.peutAcheter(ferme, "vache", 1, facile);
        System.out.println("  - Peut acheter 1 vache (100k gold): " + peutAcheterVache);
        
        boolean peutAcheter10Vaches = service.peutAcheter(ferme, "vache", 10, facile);
        System.out.println("  - Peut acheter 10 vaches (100k gold): " + peutAcheter10Vaches);
        
        System.out.println();
    }
    
    private static void testSauvegardeService() {
        System.out.println("[TEST] SauvegardeService");
        
        SauvegardeService service = SauvegardeService.getInstance();
        
        // Test singleton
        System.out.println("  - Singleton: " + (service == SauvegardeService.getInstance() ? "OK" : "ECHEC"));
        
        // Test configuration
        String cheminOriginal = service.getCheminSauvegardes();
        System.out.println("  - Chemin sauvegardes: " + cheminOriginal);
        
        service.setCheminSauvegardes("test_saves/");
        System.out.println("  - Nouveau chemin: " + service.getCheminSauvegardes());
        
        // Remettre le chemin original
        service.setCheminSauvegardes(cheminOriginal);
        System.out.println("  - Chemin restaure: " + service.getCheminSauvegardes());
        
        // Test liste des sauvegardes
        try {
            java.util.List<String> sauvegardes = service.listerSauvegardes();
            System.out.println("  - Nombre de sauvegardes: " + sauvegardes.size());
            for (String nom : sauvegardes) {
                System.out.println("    > " + nom);
            }
        } catch (Exception e) {
            System.err.println("  [ERROR] Impossible de lister les sauvegardes: " + e.getMessage());
        }
        
        // Test existence
        boolean existeTest = service.existeSauvegarde("TestPlayer");
        System.out.println("  - Sauvegarde 'TestPlayer' existe: " + existeTest);
        
        System.out.println();
    }
}