package dev.linnaelle.fs.services;

import dev.linnaelle.fs.dao.*;
import dev.linnaelle.fs.entities.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;
import java.util.List;
import java.util.ArrayList;

public class SauvegardeService {
    
    private static SauvegardeService instance;
    private String cheminSauvegardes;
    private Timer timerSauvegardeAuto;
    
    // DAO nécessaires
    private JoueurDao joueurDao;
    private FermeDao fermeDao;
    private DifficulteDao difficulteDao;
    private ChampDao champDao;
    private StockageDao stockageDao;
    private ReservoirEauDao reservoirDao;
    private StructureProductionDao structureDao;
    // private GestionnaireEquipementDao equipementDao;
    
    private SauvegardeService() {
        this.cheminSauvegardes = "saves/";
        this.joueurDao = new JoueurDao();
        this.fermeDao = new FermeDao();
        this.difficulteDao = new DifficulteDao();
        this.champDao = new ChampDao();
        this.stockageDao = new StockageDao();
        this.reservoirDao = new ReservoirEauDao();
        this.structureDao = new StructureProductionDao();
        // this.equipementDao = new GestionnaireEquipementDao();
        
        // Créer le dossier de sauvegarde s'il n'existe pas
        creerDossierSauvegardes();
    }
    
    public static SauvegardeService getInstance() {
        if (instance == null) {
            instance = new SauvegardeService();
        }
        return instance;
    }
    
    // === SAUVEGARDE MANUELLE ===
    
    public boolean sauvegarder(Joueur joueur) {
        if (joueur == null) {
            System.err.println("[ERROR] Impossible de sauvegarder un joueur null");
            return false;
        }
        
        try {
            // Sauvegarder en base de données
            boolean successDB = sauvegarderEnBase(joueur);
            
            // Sauvegarder en fichier de backup
            sauvegarderEnFichier(joueur);
            
            if (successDB) {
                System.out.println("[INFO] Partie de " + joueur.getName() + " sauvegardée avec succès");
                return true;
            } else {
                System.err.println("[ERROR] Échec de la sauvegarde en base de données");
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("[ERROR] Erreur lors de la sauvegarde: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private boolean sauvegarderEnBase(Joueur joueur) {
        try {
            // Mettre à jour le temps de jeu
            joueur.setTempsJeu(System.currentTimeMillis() - joueur.getTempsJeu());
            
            // Sauvegarder le joueur
            boolean joueurSaved = joueurDao.update(joueur);
            
            // Récupérer la ferme du joueur
            Ferme ferme = fermeDao.findByJoueurId(joueur.getId());
            if (ferme == null) {
                System.err.println("[ERROR] Ferme introuvable pour le joueur: " + joueur.getName());
                return false;
            }
            
            // Sauvegarder la ferme
            boolean fermeSaved = fermeDao.update(ferme);
            
            // Sauvegarder tous les composants de la ferme
            boolean composantsSaved = sauvegarderComposantsFerme(ferme);
            
            return joueurSaved && fermeSaved && composantsSaved;
            
        } catch (Exception e) {
            System.err.println("[ERROR] Erreur lors de la sauvegarde en base: " + e.getMessage());
            return false;
        }
    }
    
    private boolean sauvegarderComposantsFerme(Ferme ferme) {
        try {
            // Sauvegarder les stockages
            stockageDao.updateArticles(ferme.getStockPrincipal().getId(), ferme.getStockPrincipal().getArticles());
            stockageDao.updateArticles(ferme.getEntrepot().getId(), ferme.getEntrepot().getArticles());
            reservoirDao.update(ferme.getReservoirEau());
            
            // Sauvegarder les champs
            for (Champ champ : ferme.getChamps()) {
                champDao.update(champ);
                
                // Sauvegarder les animaux s'il y en a
                if (champ.getFermeAnimale() != null) {
                    // TODO: Implémenter FermeAnimaleDao si nécessaire
                    // fermeAnimaleDao.update(champ.getFermeAnimale());
                    
                    AnimalDao animalDao = new AnimalDao();
                    for (Animal animal : champ.getFermeAnimale().getAnimaux()) {
                        animalDao.update(animal);
                    }
                }
            }
            
            // Sauvegarder les structures de production
            for (StructureProduction structure : ferme.getStructures()) {
                if (structure instanceof Serre) {
                    structureDao.updateSerre((Serre) structure);
                } else if (structure instanceof Usine) {
                    structureDao.updateUsine((Usine) structure);
                }
            }
            
            // Sauvegarder le gestionnaire d'équipement
            // TODO: Implémenter update dans GestionnaireEquipementDao si nécessaire
            // equipementDao.update(ferme.getEquipements());
            
            return true;
            
        } catch (Exception e) {
            System.err.println("[ERROR] Erreur lors de la sauvegarde des composants: " + e.getMessage());
            return false;
        }
    }
    
    private boolean sauvegarderEnFichier(Joueur joueur) {
        try {
            String nomFichier = genererNomFichier(joueur.getName());
            String cheminComplet = cheminSauvegardes + nomFichier;
            
            // Récupérer la ferme du joueur
            Ferme ferme = fermeDao.findByJoueurId(joueur.getId());
            if (ferme == null) {
                System.err.println("[ERROR] Ferme introuvable pour la sauvegarde fichier");
                return false;
            }
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(cheminComplet))) {
                // En-tête du fichier
                writer.println("# Sauvegarde Farm Simulator");
                writer.println("# Joueur: " + joueur.getName());
                writer.println("# Date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                writer.println("# Version: 1.0");
                writer.println();
                
                // Informations du joueur
                writer.println("[JOUEUR]");
                writer.println("nom=" + joueur.getName());
                writer.println("difficulte=" + joueur.getDifficulte().getNom());
                writer.println("tempsJeu=" + joueur.getTempsJeu());
                writer.println();
                
                // Informations de la ferme
                writer.println("[FERME]");
                writer.println("nom=" + ferme.getName());
                writer.println("revenu=" + ferme.getRevenu());
                writer.println("nombreChamps=" + ferme.getChamps().size());
                writer.println("nombreStructures=" + ferme.getStructures().size());
                writer.println();
                
                // Statistiques de stockage
                writer.println("[STOCKAGE]");
                writer.println("stockPrincipal_capacite=" + ferme.getStockPrincipal().getCapaciteMax());
                writer.println("entrepot_capacite=" + ferme.getEntrepot().getCapaciteMax());
                writer.println("reservoirEau_quantite=" + ferme.getReservoirEau().getQuantite());
                writer.println("reservoirEau_capacite=" + ferme.getReservoirEau().getCapacite());
                writer.println();
                
                // Champs
                writer.println("[CHAMPS]");
                for (Champ champ : ferme.getChamps()) {
                    writer.println("champ_" + champ.getNumero() + "=" + champ.getEtat().name() + 
                                 "," + (champ.getTypeCulture() != null ? champ.getTypeCulture() : "vide"));
                }
                writer.println();
                
                System.out.println("[INFO] Fichier de sauvegarde créé: " + cheminComplet);
                return true;
            }
            
        } catch (IOException e) {
            System.err.println("[ERROR] Erreur lors de la sauvegarde en fichier: " + e.getMessage());
            return false;
        }
    }
    
    // === CHARGEMENT ===
    
    public Joueur charger(String nomJoueur) {
        if (nomJoueur == null || nomJoueur.trim().isEmpty()) {
            System.err.println("[ERROR] Nom de joueur invalide");
            return null;
        }
        
        try {
            // Charger depuis la base de données
            Joueur joueur = joueurDao.findByName(nomJoueur.trim());
            
            if (joueur != null) {
                // Charger la ferme complète
                // Ferme ferme = fermeDao.findByJoueurId(joueur.getId());
                // Note: On ne peut pas faire joueur.setFerme() car la méthode n'existe pas
                // La ferme sera accessible via fermeDao.findByJoueurId() quand nécessaire
                
                // Charger la difficulté
                Difficulte difficulte = difficulteDao.findByNom(joueur.getDifficulte().getNom());
                joueur.setDifficulte(difficulte);
                
                System.out.println("[INFO] Partie de " + nomJoueur + " chargée avec succès");
                return joueur;
            } else {
                System.err.println("[ERROR] Aucune sauvegarde trouvée pour le joueur: " + nomJoueur);
                return null;
            }
            
        } catch (Exception e) {
            System.err.println("[ERROR] Erreur lors du chargement: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    public Joueur chargerDepuisFichier(String nomFichier) {
        try {
            Path fichier = Paths.get(cheminSauvegardes + nomFichier);
            if (!Files.exists(fichier)) {
                System.err.println("[ERROR] Fichier de sauvegarde introuvable: " + nomFichier);
                return null;
            }
            
            // TODO: Implémenter le parsing du fichier de sauvegarde
            // Pour l'instant, on renvoie null car la priorité est la base de données
            System.out.println("[INFO] Chargement depuis fichier non implémenté (utilise la base de données)");
            return null;
            
        } catch (Exception e) {
            System.err.println("[ERROR] Erreur lors du chargement depuis fichier: " + e.getMessage());
            return null;
        }
    }
    
    // === SAUVEGARDE AUTOMATIQUE ===
    
    public void demarrerSauvegardeAutomatique(Joueur joueur, long intervalleMinutes) {
        arreterSauvegardeAutomatique();
        
        timerSauvegardeAuto = new Timer("SauvegardeAuto", true);
        
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                sauvegardeAutomatique(joueur);
            }
        };
        
        long intervalleMs = intervalleMinutes * 60 * 1000;
        timerSauvegardeAuto.scheduleAtFixedRate(task, intervalleMs, intervalleMs);
        
        System.out.println("[INFO] Sauvegarde automatique activée (toutes les " + intervalleMinutes + " minutes)");
    }
    
    public void arreterSauvegardeAutomatique() {
        if (timerSauvegardeAuto != null) {
            timerSauvegardeAuto.cancel();
            timerSauvegardeAuto = null;
            System.out.println("[INFO] Sauvegarde automatique désactivée");
        }
    }
    
    public void sauvegardeAutomatique(Joueur joueur) {
        System.out.println("[INFO] Sauvegarde automatique en cours...");
        boolean success = sauvegarder(joueur);
        
        if (success) {
            System.out.println("[INFO] Sauvegarde automatique réussie");
        } else {
            System.err.println("[ERROR] Échec de la sauvegarde automatique");
        }
    }
    
    // === GESTION DES FICHIERS ===
    
    public boolean supprimerSauvegarde(String nomJoueur) {
        try {
            // Supprimer de la base de données
            Joueur joueur = joueurDao.findByName(nomJoueur);
            if (joueur != null) {
                boolean deleted = joueurDao.delete(joueur.getId());
                
                // Supprimer aussi le fichier backup s'il existe
                String nomFichier = genererNomFichier(nomJoueur);
                Path fichier = Paths.get(cheminSauvegardes + nomFichier);
                
                if (Files.exists(fichier)) {
                    Files.delete(fichier);
                }
                
                if (deleted) {
                    System.out.println("[INFO] Sauvegarde supprimée: " + nomJoueur);
                    return true;
                }
            }
            
        } catch (Exception e) {
            System.err.println("[ERROR] Erreur lors de la suppression: " + e.getMessage());
        }
        
        return false;
    }
    
    public boolean existeSauvegarde(String nomJoueur) {
        return joueurDao.findByName(nomJoueur) != null;
    }
    
    public List<String> listerSauvegardes() {
        // Créer une liste des noms de joueurs en utilisant findAll()
        List<String> noms = new ArrayList<>();
        try {
            List<Joueur> joueurs = joueurDao.findAll(); // Assuming this method exists
            for (Joueur joueur : joueurs) {
                noms.add(joueur.getName());
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Erreur lors de la récupération des sauvegardes: " + e.getMessage());
        }
        return noms;
    }
    
    // === UTILITAIRES ===
    
    private void creerDossierSauvegardes() {
        try {
            Path dossier = Paths.get(cheminSauvegardes);
            if (!Files.exists(dossier)) {
                Files.createDirectories(dossier);
                System.out.println("[INFO] Dossier de sauvegardes créé: " + cheminSauvegardes);
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Impossible de créer le dossier de sauvegardes: " + e.getMessage());
        }
    }
    
    private String genererNomFichier(String nomJoueur) {
        String nomNettoye = nomJoueur.replaceAll("[^a-zA-Z0-9_-]", "_");
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return nomNettoye + "_" + timestamp + ".fsave";
    }
    
    public void nettoyerVieuxFichiers(int joursMax) {
        try {
            Path dossier = Paths.get(cheminSauvegardes);
            if (!Files.exists(dossier)) return;
            
            long limitTemps = System.currentTimeMillis() - (joursMax * 24L * 60 * 60 * 1000);
            
            Files.walk(dossier)
                 .filter(Files::isRegularFile)
                 .filter(path -> path.toString().endsWith(".fsave"))
                 .filter(path -> {
                     try {
                         return Files.getLastModifiedTime(path).toMillis() < limitTemps;
                     } catch (IOException e) {
                         return false;
                     }
                 })
                 .forEach(path -> {
                     try {
                         Files.delete(path);
                         System.out.println("[INFO] Ancien fichier supprimé: " + path.getFileName());
                     } catch (IOException e) {
                         System.err.println("[ERROR] Impossible de supprimer: " + path.getFileName());
                     }
                 });
                 
        } catch (IOException e) {
            System.err.println("[ERROR] Erreur lors du nettoyage: " + e.getMessage());
        }
    }
    
    // === MÉTHODES DE CONFIGURATION ===
    
    public void setCheminSauvegardes(String nouveauChemin) {
        this.cheminSauvegardes = nouveauChemin;
        if (!nouveauChemin.endsWith("/")) {
            this.cheminSauvegardes += "/";
        }
        creerDossierSauvegardes();
    }
    
    public String getCheminSauvegardes() {
        return cheminSauvegardes;
    }
    
    // === NETTOYAGE ===
    
    public void shutdown() {
        arreterSauvegardeAutomatique();
        System.out.println("[INFO] Service de sauvegarde arrêté");
    }
}