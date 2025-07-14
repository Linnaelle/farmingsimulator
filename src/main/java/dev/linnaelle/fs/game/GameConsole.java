package dev.linnaelle.fs.game;

import dev.linnaelle.fs.dao.*;
import dev.linnaelle.fs.entities.*;
import dev.linnaelle.fs.services.*;
import dev.linnaelle.fs.utils.*;
import java.util.Scanner;
import java.util.List;
import java.util.Map;

public class GameConsole {
    
    private Scanner scanner;
    private Joueur joueurActuel;
    private Ferme fermeActuelle;
    private EconomieService economie;
    private CatalogueService catalogue;
    private SauvegardeService sauvegarde;
    private FermeDao fermeDao;
    private ChampDao champDao;
    private AnimalDao animalDao;
    private JoueurDao joueurDao;
    
    public GameConsole() {
        this.scanner = new Scanner(System.in);
        this.economie = EconomieService.getInstance();
        this.catalogue = CatalogueService.getInstance();
        this.sauvegarde = SauvegardeService.getInstance();
        this.fermeDao = new FermeDao();
        this.champDao = new ChampDao();
        this.animalDao = new AnimalDao();
        this.joueurDao = new JoueurDao();
        
        // Activer l'affichage en temps réel
        System.setProperty("line.separator", System.getProperty("line.separator"));
        System.out.println("[INFO] Mode debug activé - Input visible en temps réel");
    }
    
    private void afficherSaisie(String input, String contexte) {
        System.out.println("┌─ SAISIE UTILISATEUR ─────────────────┐");
        System.out.println("│ Contexte: " + contexte.substring(0, Math.min(contexte.length(), 25)));
        System.out.println("│ Input: '" + input + "'");
        System.out.println("│ Longueur: " + input.length() + " caractères");
        System.out.println("└──────────────────────────────────────┘");
    }
    
    private String lireInput(String prompt, String contexte) {
        System.out.print(prompt);
        System.out.flush();
        
        // Affichage explicite pour debug
        System.out.print("[Tapez ici] ");
        System.out.flush();
        
        String input = scanner.nextLine();
        return input;
    }
    
    private int lireInt(String prompt, String contexte) {
        while (true) {
            String input = lireInput(prompt, contexte);
            try {
                return Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                System.out.println("[ERREUR] '" + input + "' n'est pas un nombre valide!");
                System.out.println("Veuillez réessayer...");
            }
        }
    }
    
    private int lireIntAvecLimites(String prompt, String contexte, int min, int max) {
        while (true) {
            int valeur = lireInt(prompt, contexte);
            if (valeur >= min && valeur <= max) {
                System.out.println("[OK] Choix valide: " + valeur);
                return valeur;
            } else {
                System.out.println("[ERREUR] " + valeur + " doit être entre " + min + " et " + max);
                System.out.println("Veuillez réessayer...");
            }
        }
    }
    
    private String lireInputNonVide(String prompt, String contexte) {
        while (true) {
            String input = lireInput(prompt, contexte);
            if (!input.trim().isEmpty()) {
                System.out.println("[OK] Input valide!");
                return input.trim();
            } else {
                System.out.println("[ERREUR] L'input ne peut pas être vide!");
                System.out.println("Veuillez réessayer...");
            }
        }
    }
    
    public void start() {
        System.out.println("=== FARM SIMULATOR ===");
        System.out.println();
        
        if (!selectionnerJoueur()) {
            return;
        }
        
        chargerFerme();
        boucleJeuPrincipale();
    }
    
    private boolean selectionnerJoueur() {
        System.out.println("1. Nouveau joueur");
        System.out.println("2. Charger partie");
        System.out.println("3. Supprimer un compte");
        System.out.println("0. Quitter");
        
        int choix = lireIntAvecLimites("Choix (0-3): ", "Selection joueur", 0, 3);
        
        switch (choix) {
            case 1:
                return creerNouveauJoueur();
            case 2:
                return chargerJoueur();
            case 3:
                supprimerCompte();
                return selectionnerJoueur(); // Retour au menu après suppression
            case 0:
                System.out.println("Au revoir!");
                return false;
            default:
                return false;
        }
    }
    
    private boolean creerNouveauJoueur() {
        String nom = lireInputNonVide("Nom du joueur: ", "Creation joueur");
        
        System.out.println("\nDifficultes disponibles:");
        DifficulteDao difficulteDao = new DifficulteDao();
        List<Difficulte> difficultes = difficulteDao.findAll();
        
        for (int i = 0; i < difficultes.size(); i++) {
            Difficulte d = difficultes.get(i);
            System.out.println((i + 1) + ". " + d.getNom() + " (" + d.getGoldDepart() + " gold)");
        }
        
        int choixDiff = lireIntAvecLimites("Choix difficulte (1-" + difficultes.size() + "): ", 
                                          "Selection difficulte", 1, difficultes.size()) - 1;
        
        Difficulte difficulte = difficultes.get(choixDiff);
        this.joueurActuel = new Joueur();
        joueurActuel.setName(nom);
        joueurActuel.setDifficulte(difficulte);
        joueurActuel.setTempsJeu(System.currentTimeMillis());
        
        boolean savedSuccessfully = joueurDao.save(joueurActuel) != null;
        if (savedSuccessfully) {
            creerFermeInitiale(difficulte);
            System.out.println("Joueur " + nom + " cree avec difficulte " + difficulte.getNom());
            return true;
        } else {
            System.out.println("Erreur lors de la creation du joueur!");
            return false;
        }
    }
    
    private void creerFermeInitiale(Difficulte difficulte) {
        this.fermeActuelle = new Ferme("Ferme de " + joueurActuel.getName(), joueurActuel.getId());
        fermeActuelle.setRevenu(difficulte.getGoldDepart());
        
        StockPrincipal stock = new StockPrincipal();
        stock.setCapaciteMax(100000);
        fermeActuelle.setStockPrincipal(stock);
        
        Entrepot entrepot = new Entrepot();
        entrepot.setCapaciteMax(50000);
        fermeActuelle.setEntrepot(entrepot);
        
        ReservoirEau reservoir = new ReservoirEau();
        reservoir.setCapacite(20000);
        reservoir.setQuantite(20000);
        fermeActuelle.setReservoirEau(reservoir);
        
        fermeDao.save(fermeActuelle);
    }
    
    private void chargerFerme() {
        if (joueurActuel != null) {
            System.out.println("[DEBUG] Chargement ferme pour joueur ID: " + joueurActuel.getId());
            this.fermeActuelle = fermeDao.findByJoueurId(joueurActuel.getId());
            
            if (fermeActuelle == null) {
                System.out.println("[ERREUR] Ferme introuvable pour ce joueur! Création d'une ferme de base...");
                creerFermeInitiale(joueurActuel.getDifficulte());
            } else {
                System.out.println("[DEBUG] Ferme chargée: " + fermeActuelle.getName() + 
                                 " (Gold: " + fermeActuelle.getRevenu() + ")");
                
                // S'assurer que les listes sont initialisées
                if (fermeActuelle.getChamps() == null) {
                    fermeActuelle.setChamps(new java.util.ArrayList<>());
                }
                if (fermeActuelle.getStructures() == null) {
                    fermeActuelle.setStructures(new java.util.ArrayList<>());
                }
            }
        } else {
            System.out.println("[ERREUR] Aucun joueur actuel pour charger la ferme!");
        }
    }
    
    private boolean chargerJoueur() {
        List<String> sauvegardes = sauvegarde.listerSauvegardes();
        
        if (sauvegardes.isEmpty()) {
            System.out.println("Aucune sauvegarde trouvée!");
            return false;
        }
        
        System.out.println("Sauvegardes disponibles:");
        for (int i = 0; i < sauvegardes.size(); i++) {
            System.out.println((i + 1) + ". " + sauvegardes.get(i));
        }
        
        int choix = lireIntAvecLimites("Choix (1-" + sauvegardes.size() + "): ", 
                                      "Selection sauvegarde", 1, sauvegardes.size()) - 1;
        
        String nomJoueur = sauvegardes.get(choix);
        this.joueurActuel = sauvegarde.charger(nomJoueur);
        
        if (joueurActuel != null) {
            System.out.println("Partie de " + nomJoueur + " chargee!");
            return true;
        } else {
            System.out.println("Erreur lors du chargement!");
            return false;
        }
    }
    
    private void supprimerCompte() {
        System.out.println("\n=== SUPPRESSION DE COMPTE ===");
        
        List<String> sauvegardes = sauvegarde.listerSauvegardes();
        
        if (sauvegardes.isEmpty()) {
            System.out.println("Aucun compte trouvé à supprimer!");
            return;
        }
        
        System.out.println("Comptes disponibles:");
        for (int i = 0; i < sauvegardes.size(); i++) {
            System.out.println((i + 1) + ". " + sauvegardes.get(i));
        }
        System.out.println("0. Annuler");
        
        int choix = lireIntAvecLimites("Compte à supprimer (0 pour annuler): ", 
                                      "Selection compte suppression", 0, sauvegardes.size());
        
        if (choix == 0) {
            System.out.println("Suppression annulée.");
            return;
        }
        
        String nomJoueur = sauvegardes.get(choix - 1);
        
        // Double confirmation
        System.out.println("\n[ATTENTION] Vous allez supprimer le compte: " + nomJoueur);
        System.out.println("Cette action est IRREVERSIBLE!");
        System.out.println("Toutes les données de ce joueur seront perdues définitivement.");
        
        String confirmation1 = lireInput("Tapez 'SUPPRIMER' pour confirmer: ", "Confirmation suppression");
        
        if (!confirmation1.equals("SUPPRIMER")) {
            System.out.println("Suppression annulée - texte de confirmation incorrect.");
            return;
        }
        
        String confirmation2 = lireInput("Dernière chance! Tapez 'OUI' pour supprimer définitivement: ", 
                                        "Confirmation finale");
        
        if (!confirmation2.equalsIgnoreCase("OUI")) {
            System.out.println("Suppression annulée.");
            return;
        }
        
        // Procéder à la suppression
        System.out.println("\nSuppression en cours...");
        
        try {
            // Supprimer via le service de sauvegarde
            boolean success = sauvegarde.supprimerSauvegarde(nomJoueur);
            
            if (success) {
                System.out.println("[OK] Compte '" + nomJoueur + "' supprimé avec succès!");
                System.out.println("- Données de la base supprimées");
                System.out.println("- Fichiers de sauvegarde supprimés");
            } else {
                System.out.println("[ERREUR] Échec de la suppression du compte.");
                System.out.println("Le compte pourrait partiellement subsister.");
            }
            
        } catch (Exception e) {
            System.out.println("[ERREUR] Erreur durant la suppression: " + e.getMessage());
            System.out.println("Le compte pourrait ne pas être complètement supprimé.");
        }
        
        System.out.println("\nAppuyez sur Entrée pour continuer...");
        scanner.nextLine();
    }
    
    private void boucleJeuPrincipale() {
        boolean continuer = true;
        
        while (continuer) {
            afficherStatuts();
            afficherMenuPrincipal();
            
            int choix = lireInt("Choix: ", "Menu principal");
            
            switch (choix) {
                case 1:
                    gererChamps();
                    break;
                case 2:
                    gererAnimaux();
                    break;
                case 3:
                    gererStructures();
                    break;
                case 4:
                    gererEconomie();
                    break;
                case 5:
                    gererStockage();
                    break;
                case 6:
                    sauvegarderPartie();
                    break;
                case 0:
                    continuer = false;
                    break;
                default:
                    System.out.println("Choix invalide!");
            }
            
            simulerTemps();
        }
        
        System.out.println("Merci d'avoir joue!");
    }
    
    private void afficherStatuts() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("Joueur: " + (joueurActuel != null ? joueurActuel.getName() : "AUCUN"));
        
        if (fermeActuelle != null) {
            System.out.println("Gold: " + fermeActuelle.getRevenu());
            System.out.println("Ferme: " + fermeActuelle.getName());
            
            if (fermeActuelle.getStockPrincipal() != null) {
                System.out.println("Stock: " + fermeActuelle.getStockPrincipal().capaciteLibre() + "L libre");
            } else {
                System.out.println("Stock: NON INITIALISÉ");
            }
            
            if (fermeActuelle.getReservoirEau() != null) {
                System.out.println("Eau: " + fermeActuelle.getReservoirEau().getQuantite() + 
                                 "/" + fermeActuelle.getReservoirEau().getCapacite() + "L");
            } else {
                System.out.println("Eau: NON INITIALISÉ");
            }
            
            System.out.println("Champs: " + (fermeActuelle.getChamps() != null ? fermeActuelle.getChamps().size() : 0));
        } else {
            System.out.println("FERME: NON CHARGÉE");
        }
        
        if (joueurActuel != null) {
            System.out.println("Temps de jeu: " + ((System.currentTimeMillis() - joueurActuel.getTempsJeu()) / 1000) + "s");
        }
        System.out.println("=".repeat(50));
    }
    
    private void afficherMenuPrincipal() {
        System.out.println("\n=== MENU PRINCIPAL ===");
        System.out.println("1. Gerer les champs");
        System.out.println("2. Gerer les animaux");
        System.out.println("3. Gerer les structures");
        System.out.println("4. Economie (acheter/vendre)");
        System.out.println("5. Gerer le stockage");
        System.out.println("6. Sauvegarder");
        System.out.println("0. Quitter");
    }
    
    private void gererChamps() {
        System.out.println("\n=== GESTION DES CHAMPS ===");
        
        List<Champ> champs = fermeActuelle.getChamps();
        if (champs == null || champs.isEmpty()) {
            System.out.println("Aucun champ disponible. Achetez un champ d'abord!");
            return;
        }
        
        System.out.println("Champs disponibles:");
        for (int i = 0; i < champs.size(); i++) {
            Champ champ = champs.get(i);
            System.out.println((i + 1) + ". Champ " + champ.getNumero() + 
                             " - État: " + champ.getEtat() + 
                             " - Culture: " + (champ.getTypeCulture() != null ? champ.getTypeCulture() : "vide"));
        }
        
        System.out.println("\nActions:");
        System.out.println("1. Labourer un champ");
        System.out.println("2. Semer");
        System.out.println("3. Fertiliser");
        System.out.println("4. Récolter");
        System.out.println("0. Retour");
        
        int action = lireInt("Choix: ", "Action champ");
        
        if (action == 0) return;
        
        int numChamp = lireInt("Numéro du champ: ", "Selection champ") - 1;
        
        if (numChamp < 0 || numChamp >= champs.size()) {
            System.out.println("Champ invalide!");
            return;
        }
        
        Champ champ = champs.get(numChamp);
        
        switch (action) {
            case 1:
                if (champ.labourer(System.currentTimeMillis())) {
                    System.out.println("Champ labouré!");
                    champDao.update(champ);
                } else {
                    System.out.println("Impossible de labourer ce champ!");
                }
                break;
            case 2:
                semer(champ);
                break;
            case 3:
                if (champ.fertiliser(System.currentTimeMillis())) {
                    System.out.println("Champ fertilisé!");
                    champDao.update(champ);
                } else {
                    System.out.println("Impossible de fertiliser ce champ!");
                }
                break;
            case 4:
                recolter(champ);
                break;
        }
    }
    
    private void semer(Champ champ) {
        System.out.println("Cultures disponibles:");
        
        List<String> typesDesCultures = catalogue.getAllCultureTypes();
        
        if (typesDesCultures.isEmpty()) {
            System.out.println("Aucune culture disponible dans le catalogue!");
            return;
        }
        
        for (int i = 0; i < typesDesCultures.size(); i++) {
            String typeCulture = typesDesCultures.get(i);
            CultureInfo cultureInfo = catalogue.getCultureInfo(typeCulture);
            
            if (cultureInfo != null) {
                System.out.println((i + 1) + ". " + cultureInfo.getNom() + 
                                 " - Rendement: " + cultureInfo.getRendement() + "L/ha" +
                                 " - Prix: " + cultureInfo.getPrixAchat() + " gold");
            } else {
                System.out.println((i + 1) + ". " + typeCulture + " (infos indisponibles)");
            }
        }
        
        int choixCulture = lireInt("Choix culture: ", "Selection culture") - 1;
        
        if (choixCulture >= 0 && choixCulture < typesDesCultures.size()) {
            String nomCulture = typesDesCultures.get(choixCulture);
            
            if (champ.semer(nomCulture, System.currentTimeMillis())) {
                System.out.println("Culture " + nomCulture + " semee!");
                champDao.update(champ);
            } else {
                System.out.println("Impossible de semer!");
            }
        } else {
            System.out.println("Choix invalide!");
        }
    }
    
    private void recolter(Champ champ) {
        if (champ.getEtat() == EtatChamp.READY) {
            int quantite = champ.recolter(System.currentTimeMillis());
            
            if (quantite > 0) {
                String culture = champ.getTypeCulture();
                if (fermeActuelle.getStockPrincipal().ajouter(culture, quantite)) {
                    System.out.println("Récolté " + quantite + "L de " + culture + "!");
                    champDao.update(champ);
                } else {
                    System.out.println("Stock plein! Impossible de stocker la récolte!");
                }
            } else {
                System.out.println("Aucune récolte disponible!");
            }
        } else {
            System.out.println("Ce champ n'est pas prêt à être récolté! État actuel: " + champ.getEtat());
        }
    }
    
    private void gererAnimaux() {
        System.out.println("\n=== GESTION DES ANIMAUX ===");
        
        List<Champ> champs = fermeActuelle.getChamps();
        boolean animauxTrouves = false;
        
        for (Champ champ : champs) {
            if (champ.getFermeAnimale() != null && champ.getFermeAnimale().getAnimaux() != null) {
                FermeAnimale fermeAnimale = champ.getFermeAnimale();
                System.out.println("Champ " + champ.getNumero() + " - " + fermeAnimale.getTypeAnimal() + ":");
                
                for (int i = 0; i < fermeAnimale.getAnimaux().size(); i++) {
                    Animal animal = fermeAnimale.getAnimaux().get(i);
                    System.out.println("  " + (i + 1) + ". " + animal.getType() + 
                                     " - Herbe: " + animal.getStockHerbe() + "L" +
                                     " - Vivant: " + (animal.isVivant() ? "Oui" : "Non") +
                                     " - Déficit: " + (animal.isDeficit() ? "Oui" : "Non"));
                }
                animauxTrouves = true;
            }
        }
        
        if (!animauxTrouves) {
            System.out.println("Aucun animal trouvé! Achetez des animaux d'abord!");
            return;
        }
        
        System.out.println("\nActions:");
        System.out.println("1. Nourrir les animaux");
        System.out.println("2. Récolter les produits");
        System.out.println("3. Soigner un animal");
        System.out.println("0. Retour");
        
        int action = lireInt("Choix: ", "Action animaux");
        
        switch (action) {
            case 1:
                nourrirAnimaux();
                break;
            case 2:
                recolterProduitsAnimaux();
                break;
            case 3:
                soignerAnimal();
                break;
        }
    }
    
    private void nourrirAnimaux() {
        int herbeDisponible = fermeActuelle.getStockPrincipal().getQuantite("herbe");
        
        System.out.println("Herbe disponible en stock: " + herbeDisponible + "L");
        int quantite = lireInt("Quantité d'herbe à distribuer: ", "Nourrir animaux");
        
        if (quantite <= herbeDisponible) {
            for (Champ champ : fermeActuelle.getChamps()) {
                if (champ.getFermeAnimale() != null) {
                    for (Animal animal : champ.getFermeAnimale().getAnimaux()) {
                        if (animal.isVivant() && quantite > 0) {
                            int ration = Math.min(quantite, 10 - animal.getStockHerbe());
                            animal.setStockHerbe(animal.getStockHerbe() + ration);
                            quantite -= ration;
                            animalDao.update(animal);
                        }
                    }
                }
            }
            
            fermeActuelle.getStockPrincipal().retirer("herbe", quantite);
            System.out.println("Animaux nourris!");
        } else {
            System.out.println("Pas assez d'herbe en stock!");
        }
    }
    
    private void recolterProduitsAnimaux() {
        System.out.println("Récolte des produits animaux...");
        
        for (Champ champ : fermeActuelle.getChamps()) {
            if (champ.getFermeAnimale() != null) {
                for (Animal animal : champ.getFermeAnimale().getAnimaux()) {
                    Map<String, Integer> production = animal.mettreAJour(System.currentTimeMillis(), 
                                                                        fermeActuelle.getReservoirEau());
                    
                    for (Map.Entry<String, Integer> produit : production.entrySet()) {
                        String article = produit.getKey();
                        int quantite = produit.getValue();
                        
                        if (fermeActuelle.getStockPrincipal().ajouter(article, quantite)) {
                            System.out.println("Récolté " + quantite + "L de " + article);
                        } else {
                            System.out.println("Stock plein! " + article + " perdu!");
                        }
                    }
                    
                    animalDao.update(animal);
                }
            }
        }
    }
    
    private void soignerAnimal() {
        System.out.println("Fonction de soin à implémenter selon vos règles de jeu");
    }
    
    private void gererStructures() {
        System.out.println("\n=== GESTION DES STRUCTURES ===");
        
        List<StructureProduction> structures = fermeActuelle.getStructures();
        if (structures == null || structures.isEmpty()) {
            System.out.println("Aucune structure disponible!");
            return;
        }
        
        for (int i = 0; i < structures.size(); i++) {
            StructureProduction structure = structures.get(i);
            System.out.println((i + 1) + ". " + structure.getType() + 
                             " - Active: " + (structure.isActive() ? "Oui" : "Non") +
                             " - En pause: " + (structure.isEnPause() ? "Oui" : "Non"));
        }
        
        System.out.println("\nActions:");
        System.out.println("1. Démarrer/arrêter production");
        System.out.println("2. Voir détails production");
        System.out.println("0. Retour");
        
        int action = lireInt("Choix: ", "Action structures");
        
        if (action == 1) {
            int numStructure = lireInt("Numéro de structure: ", "Selection structure") - 1;
            
            if (numStructure >= 0 && numStructure < structures.size()) {
                StructureProduction structure = structures.get(numStructure);
                
                if (structure.isActive()) {
                    structure.arreter();
                    System.out.println("Production arrêtée!");
                } else {
                    if (structure.demarrer()) {
                        System.out.println("Production démarrée!");
                    } else {
                        System.out.println("Impossible de démarrer!");
                    }
                }
            }
        }
    }
    
    private void gererEconomie() {
        System.out.println("\n=== ÉCONOMIE ===");
        System.out.println("1. Acheter");
        System.out.println("2. Vendre");
        
        int choix = lireInt("Choix: ", "Menu economie");
        
        switch (choix) {
            case 1:
                menuAcheter();
                break;
            case 2:
                menuVendre();
                break;
        }
    }
    
    private void menuAcheter() {
        System.out.println("\nQue voulez-vous acheter ?");
        System.out.println("1. Graines");
        System.out.println("2. Animaux");
        System.out.println("3. Équipements");
        System.out.println("4. Structures");
        System.out.println("5. Nouveau champ");
        
        int choix = lireInt("Choix: ", "Menu achat");
        
        switch (choix) {
            case 1:
                acheterGraines();
                break;
            case 2:
                acheterAnimaux();
                break;
            case 3:
                acheterEquipements();
                break;
            case 4:
                acheterStructures();
                break;
            case 5:
                acheterChamp();
                break;
        }
    }
    
    private void acheterGraines() {
        System.out.println("Achat de graines à implémenter selon votre système économique");
    }
    
    private void acheterAnimaux() {
        System.out.println("Types d'animaux disponibles:");
        System.out.println("1. Vache - 10 gold");
        System.out.println("2. Mouton - 5 gold");
        System.out.println("3. Poule - 1 gold");
        
        int choix = lireInt("Choix: ", "Type animal");
        
        String typeAnimal = "";
        double prix = 0;
        
        switch (choix) {
            case 1: typeAnimal = "vache"; prix = 10; break;
            case 2: typeAnimal = "mouton"; prix = 5; break;
            case 3: typeAnimal = "poule"; prix = 1; break;
            default: 
                System.out.println("Choix invalide!");
                return;
        }
        
        int quantite = lireInt("Quantité: ", "Quantite animaux");
        
        double coutTotal = prix * quantite;
        
        if (fermeActuelle.getRevenu() >= coutTotal) {
            fermeActuelle.setRevenu(fermeActuelle.getRevenu() - coutTotal);
            System.out.println("Acheté " + quantite + " " + typeAnimal + "(s) pour " + coutTotal + " gold!");
            
            if (economie.acheterAnimal(fermeActuelle, typeAnimal, quantite, joueurActuel.getDifficulte())) {
                System.out.println("Animaux achetés avec succès!");
            } else {
                fermeActuelle.setRevenu(fermeActuelle.getRevenu() + coutTotal);
                System.out.println("Erreur lors de l'achat des animaux!");
            }
        } else {
            System.out.println("Pas assez d'argent! Coût: " + coutTotal + " gold");
        }
    }
    
    private void acheterEquipements() {
        System.out.println("\n=== ACHAT D'EQUIPEMENTS ===");
        
        List<String> typesEquipements = catalogue.getAllEquipementTypes();
        
        if (typesEquipements.isEmpty()) {
            System.out.println("Aucun equipement disponible dans le catalogue!");
            return;
        }
        
        System.out.println("Equipements disponibles:");
        for (int i = 0; i < typesEquipements.size(); i++) {
            String typeEquipement = typesEquipements.get(i);
            EquipementInfo equipInfo = catalogue.getEquipementInfo(typeEquipement);
            
            if (equipInfo != null) {
                double prix = economie.getPrixAchat(typeEquipement, joueurActuel.getDifficulte());
                System.out.println((i + 1) + ". " + equipInfo.getNom() + 
                                    " - Prix: " + prix + " gold");
            } else {
                System.out.println((i + 1) + ". " + typeEquipement + " (infos indisponibles)");
            }
        }
        System.out.println("0. Retour");
        
        int choix = lireIntAvecLimites("Choix (0-" + typesEquipements.size() + "): ", 
                                        "Selection equipement", 0, typesEquipements.size());
        
        if (choix == 0) return;
        
        String typeEquipement = typesEquipements.get(choix - 1);
        double prix = economie.getPrixAchat(typeEquipement, joueurActuel.getDifficulte());
        
        System.out.println("Prix: " + prix + " gold");
        System.out.println("Votre argent: " + fermeActuelle.getRevenu() + " gold");
        
        if (fermeActuelle.getRevenu() >= prix) {
            String confirmation = lireInput("Confirmer l'achat? (oui/non): ", "Confirmation achat equipement");
            
            if (confirmation.equalsIgnoreCase("oui") || confirmation.equalsIgnoreCase("o")) {
                if (economie.acheterEquipement(fermeActuelle, typeEquipement, joueurActuel.getDifficulte())) {
                    System.out.println("Equipement " + typeEquipement + " achete avec succes!");
                } else {
                    System.out.println("Erreur lors de l'achat de l'equipement!");
                }
            } else {
                System.out.println("Achat annule.");
            }
        } else {
            System.out.println("Pas assez d'argent! Il vous manque " + (prix - fermeActuelle.getRevenu()) + " gold.");
        }
    }

    private void acheterStructures() {
        System.out.println("\n=== ACHAT DE STRUCTURES ===");
        
        List<String> typesUsines = catalogue.getAllUsineTypes();
        
        if (typesUsines.isEmpty()) {
            System.out.println("Aucune structure disponible dans le catalogue!");
            return;
        }
        
        System.out.println("Structures disponibles:");
        for (int i = 0; i < typesUsines.size(); i++) {
            String typeUsine = typesUsines.get(i);
            UsineInfo usineInfo = catalogue.getUsineInfo(typeUsine);
            
            if (usineInfo != null) {
                double prix = economie.getPrixAchat(typeUsine, joueurActuel.getDifficulte());
                System.out.println((i + 1) + ". " + usineInfo.getNom() + 
                                    " - Prix: " + prix + " gold");
                System.out.println("    Produit: " + usineInfo.getArticleProduit() + 
                                    " (x" + usineInfo.getMultiplicateur() + ")");
            } else {
                System.out.println((i + 1) + ". " + typeUsine + " (infos indisponibles)");
            }
        }
        System.out.println("0. Retour");
        
        int choix = lireIntAvecLimites("Choix (0-" + typesUsines.size() + "): ", 
                                        "Selection structure", 0, typesUsines.size());
        
        if (choix == 0) return;
        
        String typeStructure = typesUsines.get(choix - 1);
        double prix = economie.getPrixAchat(typeStructure, joueurActuel.getDifficulte());
        
        UsineInfo usineInfo = catalogue.getUsineInfo(typeStructure);
        if (usineInfo != null) {
            System.out.println("\nDetails de la structure:");
            System.out.println("Nom: " + usineInfo.getNom());
            System.out.println("Prix: " + prix + " gold");
            System.out.println("Produit: " + usineInfo.getArticleProduit());
            System.out.println("Multiplicateur: x" + usineInfo.getMultiplicateur());
            if (usineInfo.getIntrantsRequis() != null && !usineInfo.getIntrantsRequis().isEmpty()) {
                System.out.println("Intrants requis: " + usineInfo.getIntrantsRequis());
            }
        }
        
        System.out.println("Votre argent: " + fermeActuelle.getRevenu() + " gold");
        
        if (fermeActuelle.getRevenu() >= prix) {
            String confirmation = lireInput("Confirmer l'achat? (oui/non): ", "Confirmation achat structure");
            
            if (confirmation.equalsIgnoreCase("oui") || confirmation.equalsIgnoreCase("o")) {
                if (economie.acheterStructure(fermeActuelle, typeStructure, joueurActuel.getDifficulte())) {
                    System.out.println("Structure " + typeStructure + " achetee avec succes!");
                    System.out.println("Elle sera ajoutee a votre ferme.");
                } else {
                    System.out.println("Erreur lors de l'achat de la structure!");
                }
            } else {
                System.out.println("Achat annule.");
            }
        } else {
            System.out.println("Pas assez d'argent! Il vous manque " + (prix - fermeActuelle.getRevenu()) + " gold.");
        }
    }
    
    private void acheterChamp() {
        if (fermeActuelle == null) {
            System.out.println("[ERREUR] Ferme non chargée! Tentative de rechargement...");
            chargerFerme();
            
            if (fermeActuelle == null) {
                System.out.println("[ERREUR] Impossible de charger la ferme!");
                return;
            }
        }
        
        System.out.println("Prix d'un nouveau champ: 1000 gold");
        System.out.println("Votre argent actuel: " + fermeActuelle.getRevenu() + " gold");
        
        if (fermeActuelle.getRevenu() >= 1000) {
            Champ nouveauChamp = new Champ();
            
            if (fermeActuelle.getChamps() == null) {
                fermeActuelle.setChamps(new java.util.ArrayList<>());
            }
            
            nouveauChamp.setNumero(fermeActuelle.getChamps().size() + 1);
            nouveauChamp.setEtat(EtatChamp.STANDBY);
            nouveauChamp.setFermeId(fermeActuelle.getId());
            
            if (champDao.save(nouveauChamp) != null) {
                fermeActuelle.getChamps().add(nouveauChamp);
                fermeActuelle.setRevenu(fermeActuelle.getRevenu() - 1000);
                fermeDao.update(fermeActuelle);
                System.out.println("Nouveau champ acheté!");
            } else {
                System.out.println("Erreur lors de l'achat du champ!");
            }
        } else {
            System.out.println("Pas assez d'argent! Il vous faut 1000 gold.");
        }
    }
    
    private void menuVendre() {
        System.out.println("\n=== VENTE ===");
        
        Map<String, Integer> articles = fermeActuelle.getStockPrincipal().getArticles();
        
        if (articles.isEmpty()) {
            System.out.println("Aucun article en stock à vendre!");
            return;
        }
        
        System.out.println("Articles disponibles:");
        String[] nomsArticles = articles.keySet().toArray(new String[0]);
        
        for (int i = 0; i < nomsArticles.length; i++) {
            String article = nomsArticles[i];
            int quantite = articles.get(article);
            double prixVente = economie.getPrixVente(article, joueurActuel.getDifficulte());
            
            System.out.println((i + 1) + ". " + article + " - " + quantite + "L - " + 
                             prixVente + " gold/L");
        }
        
        int choix = lireInt("Article à vendre: ", "Selection article") - 1;
        
        if (choix >= 0 && choix < nomsArticles.length) {
            String article = nomsArticles[choix];
            int stockDisponible = articles.get(article);
            
            int quantite = lireInt("Quantité à vendre (max " + stockDisponible + "): ", "Quantite vente");
            
            if (quantite > 0 && quantite <= stockDisponible) {
                double revenu = economie.vendreArticle(fermeActuelle, article, quantite, joueurActuel.getDifficulte());
                System.out.println("Vendu " + quantite + "L de " + article + " pour " + revenu + " gold!");
            } else {
                System.out.println("Quantité invalide!");
            }
        }
    }
    
    private void gererStockage() {
        System.out.println("\n=== STOCKAGE ===");
        
        System.out.println("Stock Principal:");
        System.out.println("  Capacité: " + fermeActuelle.getStockPrincipal().getCapaciteMax() + "L");
        System.out.println("  Libre: " + fermeActuelle.getStockPrincipal().capaciteLibre() + "L");
        
        Map<String, Integer> articles = fermeActuelle.getStockPrincipal().getArticles();
        if (!articles.isEmpty()) {
            System.out.println("  Contenu:");
            for (Map.Entry<String, Integer> entry : articles.entrySet()) {
                System.out.println("    - " + entry.getKey() + ": " + entry.getValue() + "L");
            }
        }
        
        if (fermeActuelle.getEntrepot() != null) {
            System.out.println("\nEntrepôt:");
            System.out.println("  Capacité: " + fermeActuelle.getEntrepot().getCapaciteMax() + "L");
            System.out.println("  Libre: " + fermeActuelle.getEntrepot().capaciteLibre() + "L");
            
            Map<String, Integer> articlesEntrepot = fermeActuelle.getEntrepot().getArticles();
            if (!articlesEntrepot.isEmpty()) {
                System.out.println("  Contenu:");
                for (Map.Entry<String, Integer> entry : articlesEntrepot.entrySet()) {
                    System.out.println("    - " + entry.getKey() + ": " + entry.getValue() + "L");
                }
            }
        }
        
        System.out.println("\nActions:");
        System.out.println("1. Transférer entre stockages");
        System.out.println("2. Vider un stockage");
        System.out.println("0. Retour");
        
        int action = lireInt("Choix: ", "Action stockage");
        
        switch (action) {
            case 1:
                transfererEntreStockages();
                break;
            case 2:
                viderStockage();
                break;
        }
    }
    
    private void transfererEntreStockages() {
        System.out.println("Transferts entre stockages:");
        System.out.println("1. Stock Principal → Entrepôt");
        System.out.println("2. Entrepôt → Stock Principal");
        
        int choix = lireInt("Choix: ", "Direction transfert");
        
        Stockage source, destination;
        
        if (choix == 1) {
            source = fermeActuelle.getStockPrincipal();
            destination = fermeActuelle.getEntrepot();
        } else if (choix == 2) {
            source = fermeActuelle.getEntrepot();
            destination = fermeActuelle.getStockPrincipal();
        } else {
            System.out.println("Choix invalide!");
            return;
        }
        
        Map<String, Integer> articlesSource = source.getArticles();
        if (articlesSource.isEmpty()) {
            System.out.println("Aucun article à transférer!");
            return;
        }
        
        System.out.println("Articles disponibles:");
        String[] nomsArticles = articlesSource.keySet().toArray(new String[0]);
        
        for (int i = 0; i < nomsArticles.length; i++) {
            String article = nomsArticles[i];
            int quantite = articlesSource.get(article);
            System.out.println((i + 1) + ". " + article + " - " + quantite + "L");
        }
        
        int choixArticle = lireInt("Article à transférer: ", "Selection article transfert") - 1;
        
        if (choixArticle >= 0 && choixArticle < nomsArticles.length) {
            String article = nomsArticles[choixArticle];
            int quantiteDisponible = articlesSource.get(article);
            
            int quantite = lireInt("Quantité à transférer (max " + quantiteDisponible + "): ", "Quantite transfert");
            
            if (quantite > 0 && quantite <= quantiteDisponible) {
                if (destination.peutStocker(article) && destination.capaciteLibre() >= quantite) {
                    if (source.retirer(article, quantite) && destination.ajouter(article, quantite)) {
                        System.out.println("Transfert réussi: " + quantite + "L de " + article);
                    } else {
                        System.out.println("Erreur lors du transfert!");
                    }
                } else {
                    System.out.println("Impossible de stocker dans la destination!");
                }
            } else {
                System.out.println("Quantité invalide!");
            }
        }
    }
    
    private void viderStockage() {
        System.out.println("Quel stockage vider ?");
        System.out.println("1. Stock Principal");
        System.out.println("2. Entrepôt");
        
        int choix = lireInt("Choix: ", "Selection stockage vider");
        
        Stockage stockage;
        
        if (choix == 1) {
            stockage = fermeActuelle.getStockPrincipal();
        } else if (choix == 2) {
            stockage = fermeActuelle.getEntrepot();
        } else {
            System.out.println("Choix invalide!");
            return;
        }
        
        String confirmation = lireInput("Êtes-vous sûr de vouloir vider ce stockage ? (oui/non): ", "Confirmation vidage");
        
        if (confirmation.toLowerCase().equals("oui") || confirmation.toLowerCase().equals("o")) {
            stockage.getArticles().clear();
            System.out.println("Stockage vidé!");
        } else {
            System.out.println("Opération annulée.");
        }
    }
    
    private void sauvegarderPartie() {
        boolean success = sauvegarde.sauvegarder(joueurActuel);
        if (success) {
            System.out.println("Partie sauvegardée!");
        } else {
            System.out.println("Erreur lors de la sauvegarde!");
        }
    }
    
    private void simulerTemps() {
        long tempsCourant = System.currentTimeMillis();
        
        for (Champ champ : fermeActuelle.getChamps()) {
            if (champ.getFermeAnimale() != null && champ.getFermeAnimale().getAnimaux() != null) {
                for (Animal animal : champ.getFermeAnimale().getAnimaux()) {
                    Map<String, Integer> production = animal.mettreAJour(tempsCourant, fermeActuelle.getReservoirEau());
                    
                    for (Map.Entry<String, Integer> produit : production.entrySet()) {
                        String article = produit.getKey();
                        int quantite = produit.getValue();
                        
                        if (!fermeActuelle.getStockPrincipal().ajouter(article, quantite)) {
                            if (fermeActuelle.getEntrepot().peutStocker(article)) {
                                fermeActuelle.getEntrepot().ajouter(article, quantite);
                            }
                        }
                    }
                }
            }
        }
        
        if (fermeActuelle.getReservoirEau() != null) {
            fermeActuelle.getReservoirEau().remplir(tempsCourant);
        }
        
        if (fermeActuelle.getStructures() != null) {
            for (StructureProduction structure : fermeActuelle.getStructures()) {
                if (structure.isActive() && !structure.isEnPause()) {
                    try {
                        structure.produire(tempsCourant, fermeActuelle.getReservoirEau(), 
                                         fermeActuelle.getStockPrincipal());
                    } catch (Exception e) {
                        System.err.println("Erreur lors de la production de " + structure.getType() + ": " + e.getMessage());
                    }
                }
            }
        }
    }
    
    public void recupererSauvegarde() {
        System.out.println("=== RÉCUPÉRATION DE SAUVEGARDE ===");
        
        String nomJoueur = lireInput("Nom du joueur à récupérer: ", "Recovery nom joueur");
        
        Joueur joueur = sauvegarde.charger(nomJoueur);
        
        if (joueur == null) {
            System.out.println("Erreur DB - Fichiers de récupération disponibles :");
            
            try {
                java.io.File dossier = new java.io.File(sauvegarde.getCheminSauvegardes());
                java.io.File[] fichiers = dossier.listFiles();
                
                if (fichiers != null) {
                    List<String> fichiersSauvegarde = new java.util.ArrayList<>();
                    
                    for (java.io.File fichier : fichiers) {
                        if (fichier.getName().endsWith(".fsave")) {
                            fichiersSauvegarde.add(fichier.getName());
                            System.out.println((fichiersSauvegarde.size()) + ". " + fichier.getName());
                        }
                    }
                    
                    if (!fichiersSauvegarde.isEmpty()) {
                        int choix = lireInt("Choisir fichier à restaurer (numéro): ", "Recovery fichier") - 1;
                        
                        if (choix >= 0 && choix < fichiersSauvegarde.size()) {
                            String nomFichier = fichiersSauvegarde.get(choix);
                            joueur = sauvegarde.chargerDepuisFichier(nomFichier);
                            
                            if (joueur != null) {
                                System.out.println("Sauvegarde récupérée depuis le fichier!");
                                this.joueurActuel = joueur;
                                chargerFerme();
                            } else {
                                System.out.println("Impossible de récupérer la sauvegarde.");
                            }
                        } else {
                            System.out.println("Choix invalide!");
                        }
                    } else {
                        System.out.println("Aucun fichier de sauvegarde trouvé.");
                    }
                } else {
                    System.out.println("Dossier de sauvegarde introuvable.");
                }
            } catch (Exception e) {
                System.err.println("Erreur lors de la récupération: " + e.getMessage());
            }
        } else {
            System.out.println("Sauvegarde récupérée depuis la base de données!");
            this.joueurActuel = joueur;
            chargerFerme();
        }
    }
    
    public static void main(String[] args) {
        DatabaseManager.getInstance().initializeDatabase();
        DataInitializer.initializeData();
        
        GameConsole game = new GameConsole();
        
        if (args.length > 0 && args[0].equals("--recover")) {
            game.recupererSauvegarde();
        } else {
            game.start();
        }
        
        DatabaseManager.closeInstance();
    }
}