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
    
    private JoueurDao joueurDao;
    private FermeDao fermeDao;
    private DifficulteDao difficulteDao;
    private ChampDao champDao;
    private StockageDao stockageDao;
    private ReservoirEauDao reservoirDao;
    private StructureProductionDao structureDao;
    
    private SauvegardeService() {
        this.cheminSauvegardes = "saves/";
        this.joueurDao = new JoueurDao();
        this.fermeDao = new FermeDao();
        this.difficulteDao = new DifficulteDao();
        this.champDao = new ChampDao();
        this.stockageDao = new StockageDao();
        this.reservoirDao = new ReservoirEauDao();
        this.structureDao = new StructureProductionDao();
        
        creerDossierSauvegardes();
    }
    
    public static SauvegardeService getInstance() {
        if (instance == null) {
            instance = new SauvegardeService();
        }
        return instance;
    }
    
    /**
     * Sauvegarde l'état actuel du joueur et de sa ferme
     * @param joueur
     * @return
     */
    public boolean sauvegarder(Joueur joueur) {
        if (joueur == null) {
            System.err.println("[ERROR] Impossible de sauvegarder un joueur null");
            return false;
        }
        
        try {
            boolean successDB = sauvegarderEnBase(joueur);
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
            joueur.setTempsJeu(System.currentTimeMillis() - joueur.getTempsJeu());
            boolean joueurSaved = joueurDao.update(joueur);
            Ferme ferme = fermeDao.findByJoueurId(joueur.getId());

            if (ferme == null) {
                System.err.println("[ERROR] Ferme introuvable pour le joueur: " + joueur.getName());
                return false;
            }
            
            boolean fermeSaved = fermeDao.update(ferme);
            boolean composantsSaved = sauvegarderComposantsFerme(ferme);
            
            return joueurSaved && fermeSaved && composantsSaved;
        } catch (Exception e) {
            System.err.println("[ERROR] Erreur lors de la sauvegarde en base: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Sauvegarde les composants de la ferme (champs, structures, stockage, etc.)
     * @param ferme La ferme à sauvegarder
     * @return true si la sauvegarde a réussi, false sinon
     */
    public boolean sauvegarderComposantsFerme(Ferme ferme) {
        try {
            boolean success = true;
            
            if (ferme.getStockPrincipal() != null) {
                success &= stockageDao.updateArticles(ferme.getStockPrincipal().getId(), ferme.getStockPrincipal().getArticles());
            }
            
            if (ferme.getEntrepot() != null) {
                success &= stockageDao.updateArticles(ferme.getEntrepot().getId(), ferme.getEntrepot().getArticles());
            }
            
            if (ferme.getReservoirEau() != null) {
                success &= reservoirDao.update(ferme.getReservoirEau());
            }
            
            if (ferme.getChamps() != null) {
                for (Champ champ : ferme.getChamps()) {
                    success &= champDao.update(champ);
                    
                    if (champ.getFermeAnimale() != null && champ.getFermeAnimale().getAnimaux() != null) {
                        AnimalDao animalDao = new AnimalDao();
                        for (Animal animal : champ.getFermeAnimale().getAnimaux()) {
                            if (ferme.getReservoirEau() != null) {
                                animal.mettreAJour(System.currentTimeMillis(), ferme.getReservoirEau());
                            }
                            success &= animalDao.update(animal);
                        }
                    }
                }
            }
            
            if (ferme.getStructures() != null) {
                for (StructureProduction structure : ferme.getStructures()) {
                    if (structure instanceof Serre) {
                        success &= structureDao.updateSerre((Serre) structure);
                    } else if (structure instanceof Usine) {
                        success &= structureDao.updateUsine((Usine) structure);
                    }
                }
            }
            
            return success;
        } catch (Exception e) {
            System.err.println("[ERROR] Erreur lors de la sauvegarde des composants: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private boolean sauvegarderEnFichier(Joueur joueur) {
        try {
            String nomFichier = genererNomFichier(joueur.getName());
            String cheminComplet = cheminSauvegardes + nomFichier;
            Ferme ferme = fermeDao.findByJoueurId(joueur.getId());

            if (ferme == null) {
                System.err.println("[ERROR] Ferme introuvable pour la sauvegarde fichier");
                return false;
            }
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(cheminComplet))) {
                writer.println("# Sauvegarde Farm Simulator");
                writer.println("# Joueur: " + joueur.getName());
                writer.println("# Date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                writer.println("# Version: 1.0");
                writer.println();

                writer.println("[JOUEUR]");
                writer.println("nom=" + joueur.getName());
                writer.println("difficulte=" + joueur.getDifficulte().getNom());
                writer.println("tempsJeu=" + joueur.getTempsJeu());
                writer.println();

                writer.println("[FERME]");
                writer.println("nom=" + ferme.getName());
                writer.println("revenu=" + ferme.getRevenu());
                if (ferme.getChamps() != null) {
                    writer.println("nombreChamps=" + ferme.getChamps().size());
                }
                if (ferme.getStructures() != null) {
                    writer.println("nombreStructures=" + ferme.getStructures().size());
                }
                writer.println();

                writer.println("[STOCKAGE]");
                if (ferme.getStockPrincipal() != null) {
                    writer.println("stockPrincipal_capacite=" + ferme.getStockPrincipal().getCapaciteMax());
                }
                if (ferme.getEntrepot() != null) {
                    writer.println("entrepot_capacite=" + ferme.getEntrepot().getCapaciteMax());
                }
                if (ferme.getReservoirEau() != null) {
                    writer.println("reservoirEau_quantite=" + ferme.getReservoirEau().getQuantite());
                    writer.println("reservoirEau_capacite=" + ferme.getReservoirEau().getCapacite());
                }
                writer.println();

                writer.println("[CHAMPS]");
                if (ferme.getChamps() != null) {
                    for (Champ champ : ferme.getChamps()) {
                        writer.println("champ_" + champ.getNumero() + "=" + champ.getEtat().name() + 
                                     "," + (champ.getTypeCulture() != null ? champ.getTypeCulture() : "vide"));
                        if (champ.getFermeAnimale() != null) {
                            FermeAnimale fermeAnimale = champ.getFermeAnimale();
                            int nbAnimaux = fermeAnimale.getAnimaux() != null ? fermeAnimale.getAnimaux().size() : 0;
                            writer.println("champ_" + champ.getNumero() + "_animaux=" + 
                                         fermeAnimale.getTypeAnimal() + "," + 
                                         nbAnimaux + "/" + fermeAnimale.getCapaciteMax());
                        }
                    }
                }
                writer.println();
                
                writer.println("[STRUCTURES]");
                if (ferme.getStructures() != null) {
                    for (StructureProduction structure : ferme.getStructures()) {
                        writer.println("structure_" + structure.getType() + "=" + 
                                     (structure.isActive() ? "active" : "inactive") + 
                                     "," + (structure.isEnPause() ? "pause" : "marche"));
                    }
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

    /**
     * Charge une sauvegarde depuis la base de données
     * @param nomJoueur Nom du joueur à charger
     * @return Joueur chargé ou null si erreur
     */    
    public Joueur charger(String nomJoueur) {
        if (nomJoueur == null || nomJoueur.trim().isEmpty()) {
            System.err.println("[ERROR] Nom de joueur invalide");
            return null;
        }
        
        try {
            Joueur joueur = joueurDao.findByName(nomJoueur.trim());
            
            if (joueur != null) {
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
    
    /**
     * Implémente le chargement depuis fichier
     * @param nomFichier Nom du fichier de sauvegarde
     * @return Joueur chargé ou null si erreur
     */
    public Joueur chargerDepuisFichier(String nomFichier) {
        try {
            Path fichier = Paths.get(cheminSauvegardes + nomFichier);
            if (!Files.exists(fichier)) {
                System.err.println("[ERROR] Fichier de sauvegarde introuvable: " + nomFichier);
                return null;
            }
            
            Joueur joueur = parserFichierSauvegarde(fichier.toString());
            
            if (joueur != null) {
                System.out.println("[INFO] Joueur chargé depuis le fichier: " + joueur.getName());
                
                Ferme ferme = fermeDao.findByJoueurId(joueur.getId());
                if (ferme != null) {
                    System.out.println("[INFO] Ferme associée trouvée: " + ferme.getName());
                }
                
                return joueur;
            } else {
                System.err.println("[ERROR] Impossible de parser le fichier de sauvegarde");
            }
            
        } catch (Exception e) {
            System.err.println("[ERROR] Erreur lors du chargement depuis fichier: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Démarre une sauvegarde automatique à intervalle régulier
     * @param joueur Joueur pour lequel la sauvegarde est effectuée
     * @param intervalleMinutes Intervalle en minutes entre chaque sauvegarde automatique
     */
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
    
    /**
     * Arrête la sauvegarde automatique si elle est en cours
     */
    public void arreterSauvegardeAutomatique() {
        if (timerSauvegardeAuto != null) {
            timerSauvegardeAuto.cancel();
            timerSauvegardeAuto = null;
            System.out.println("[INFO] Sauvegarde automatique désactivée");
        }
    }
    
    /**
     * Effectue une sauvegarde automatique du joueur
     * @param joueur Joueur à sauvegarder
     */
    public void sauvegardeAutomatique(Joueur joueur) {
        System.out.println("[INFO] Sauvegarde automatique en cours...");
        boolean success = sauvegarder(joueur);
        
        if (success) {
            System.out.println("[INFO] Sauvegarde automatique réussie");
        } else {
            System.err.println("[ERROR] Échec de la sauvegarde automatique");
        }
    }

    /**
     * Supprime une sauvegarde du joueur
     * @param nomJoueur Nom du joueur dont la sauvegarde doit être supprimée
     * @return true si la suppression a réussi, false sinon
     */    
    public boolean supprimerSauvegarde(String nomJoueur) {
        try {
            Joueur joueur = joueurDao.findByName(nomJoueur);
            if (joueur != null) {
                boolean deleted = joueurDao.delete(joueur.getId());
                
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
    
    /**
     * Vérifie si une sauvegarde existe pour le joueur donné
     * @param nomJoueur Nom du joueur à vérifier
     * @return true si la sauvegarde existe, false sinon
     */
    public boolean existeSauvegarde(String nomJoueur) {
        return joueurDao.findByName(nomJoueur) != null;
    }
    
    /**
     * Liste les sauvegardes disponibles
     * @return Liste des noms de joueurs ayant une sauvegarde
     */
    public List<String> listerSauvegardes() {
        List<String> noms = new ArrayList<>();
        try {
            List<Joueur> joueurs = joueurDao.findAll();
            for (Joueur joueur : joueurs) {
                noms.add(joueur.getName());
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Erreur lors de la récupération des sauvegardes: " + e.getMessage());
        }
        return noms;
    }
    
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
    
    /**
     * Nettoie les anciens fichiers de sauvegarde
     * @param joursMax Nombre de jours maximum pour conserver les sauvegardes
     */
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
    
    /**
     * Définit le chemin des sauvegardes
     * @param nouveauChemin Le nouveau chemin pour les sauvegardes
     */
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
    
    public void shutdown() {
        arreterSauvegardeAutomatique();
        System.out.println("[INFO] Service de sauvegarde arrêté");
    }

    private Joueur parserFichierSauvegarde(String cheminFichier) {
        try {
            Path fichier = Paths.get(cheminFichier);
            List<String> lignes = Files.readAllLines(fichier);
            
            String nomJoueur = null;
            String difficulteNom = null;
            long tempsJeu = 0;
            
            for (String ligne : lignes) {
                ligne = ligne.trim();
                
                if (ligne.startsWith("nom=")) {
                    nomJoueur = ligne.substring(4);
                } else if (ligne.startsWith("difficulte=")) {
                    difficulteNom = ligne.substring(11);
                } else if (ligne.startsWith("tempsJeu=")) {
                    try {
                        tempsJeu = Long.parseLong(ligne.substring(9));
                    } catch (NumberFormatException e) {
                        System.err.println("[WARN] Format temps de jeu invalide: " + ligne);
                    }
                }
            }
            
            if (nomJoueur != null && difficulteNom != null) {
                Difficulte difficulte = difficulteDao.findByNom(difficulteNom);
                
                if (difficulte != null) {
                    Joueur joueur = new Joueur();
                    joueur.setName(nomJoueur);
                    joueur.setDifficulte(difficulte);
                    joueur.setTempsJeu(tempsJeu);
                    
                    return joueur;
                }
            }
            
        } catch (IOException e) {
            System.err.println("[ERROR] Erreur lecture fichier: " + e.getMessage());
        }
        
        return null;
    }
}