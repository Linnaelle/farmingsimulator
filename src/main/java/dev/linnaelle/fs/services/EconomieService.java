package dev.linnaelle.fs.services;

import dev.linnaelle.fs.dao.*;
import dev.linnaelle.fs.entities.*;

public class EconomieService {
    
    private static EconomieService instance;
    
    private CatalogueService catalogueService;
    private FermeDao fermeDao;
    private ChampDao champDao;
    private AnimalDao animalDao;
    private EquipementDao equipementDao;
    private StructureProductionDao structureDao;
    private StockageDao stockageDao;
    
    private EconomieService() {
        this.catalogueService = CatalogueService.getInstance();
        this.fermeDao = new FermeDao();
        this.champDao = new ChampDao();
        this.animalDao = new AnimalDao();
        this.equipementDao = new EquipementDao();
        this.structureDao = new StructureProductionDao();
        this.stockageDao = new StockageDao();
    }
    
    public static EconomieService getInstance() {
        if (instance == null) {
            instance = new EconomieService();
        }
        return instance;
    }
    
    // === CALCUL DES PRIX ===
    
    public double getPrixVente(String article, Difficulte difficulte) {
        ArticleInfo info = catalogueService.getArticleInfo(article);
        if (info == null) {
            System.err.println("[ERROR] Article introuvable: " + article);
            return 0.0;
        }
        
        double prixBase = info.getPrixVente();
        return prixBase * difficulte.getMultiplicateurVente();
    }
    
    public double getPrixAchat(String item, Difficulte difficulte) {
        // Pour les animaux
        AnimalInfo animalInfo = catalogueService.getAnimalInfo(item);
        if (animalInfo != null) {
            return animalInfo.getPrixAchat() * difficulte.getMultiplicateurAchat();
        }
        
        // Pour les équipements
        EquipementInfo equipInfo = catalogueService.getEquipementInfo(item);
        if (equipInfo != null) {
            return equipInfo.getPrixAchat() * difficulte.getMultiplicateurAchat();
        }
        
        // Pour les cultures
        CultureInfo cultureInfo = catalogueService.getCultureInfo(item);
        if (cultureInfo != null) {
            return cultureInfo.getPrixAchat() * difficulte.getMultiplicateurAchat();
        }
        
        // Pour les usines
        UsineInfo usineInfo = catalogueService.getUsineInfo(item);
        if (usineInfo != null) {
            return usineInfo.getPrixAchat() * difficulte.getMultiplicateurAchat();
        }
        
        System.err.println("[ERROR] Item introuvable pour l'achat: " + item);
        return 0.0;
    }
    
    // === ACHATS ===
    
    public boolean acheterEquipement(Ferme ferme, String type, Difficulte difficulte) {
        if (!catalogueService.isEquipementValid(type)) {
            System.err.println("[ERROR] Type d'équipement invalide: " + type);
            return false;
        }
        
        double prix = getPrixAchat(type, difficulte);
        
        if (ferme.getRevenu() >= prix) {
            // Créer et sauvegarder le nouvel équipement
            Equipement equipement = new Equipement();
            equipement.setType(type);
            equipement.setEnUtilisation(false);
            // Associer à la ferme via le gestionnaire d'équipement
            
            if (equipementDao.save(equipement) != null) {
                // Débiter le prix et mettre à jour la ferme
                ferme.setRevenu(ferme.getRevenu() - prix);
                fermeDao.update(ferme);
                
                System.out.println("[INFO] Équipement acheté: " + type + " pour " + prix + "€");
                return true;
            }
        } else {
            System.err.println("[ERROR] Fonds insuffisants pour acheter " + type + " (prix: " + prix + "€, disponible: " + ferme.getRevenu() + "€)");
        }
        
        return false;
    }
    
    public boolean acheterAnimal(Ferme ferme, String type, int quantite, Difficulte difficulte) {
        if (!catalogueService.isAnimalValid(type)) {
            System.err.println("[ERROR] Type d'animal invalide: " + type);
            return false;
        }
        
        double prixUnitaire = getPrixAchat(type, difficulte);
        double prixTotal = prixUnitaire * quantite;
        
        if (ferme.getRevenu() >= prixTotal) {
            int animauxAchetes = 0;
            
            for (int i = 0; i < quantite; i++) {
                // Trouver un champ avec ferme animale ayant de la place
                boolean place = false;
                for (Champ champ : ferme.getChamps()) {
                    if (champ.getFermeAnimale() != null && 
                        champ.getFermeAnimale().getTypeAnimal().equals(type) &&
                        champ.getFermeAnimale().getAnimaux().size() < champ.getFermeAnimale().getCapaciteMax()) {
                        
                        Animal animal = new Animal();
                        animal.setType(type);
                        animal.setVivant(true);
                        animal.setDeficit(false);
                        
                        AnimalInfo info = catalogueService.getAnimalInfo(type);
                        animal.setStockHerbe(info.getStockHerbe());
                        
                        if (animalDao.save(animal) != null) {
                            animauxAchetes++;
                            place = true;
                            break;
                        }
                    }
                }
                
                if (!place) {
                    System.err.println("[ERROR] Pas de place pour loger l'animal " + type);
                    break;
                }
            }
            
            if (animauxAchetes > 0) {
                double prixReel = prixUnitaire * animauxAchetes;
                ferme.setRevenu(ferme.getRevenu() - prixReel);
                fermeDao.update(ferme);
                
                System.out.println("[INFO] " + animauxAchetes + " " + type + "(s) acheté(s) pour " + prixReel + "€");
                return animauxAchetes == quantite;
            }
        } else {
            System.err.println("[ERROR] Fonds insuffisants pour acheter " + quantite + " " + type + "(s) (prix: " + prixTotal + "€, disponible: " + ferme.getRevenu() + "€)");
        }
        
        return false;
    }
    
    public boolean acheterStructure(Ferme ferme, String type, Difficulte difficulte) {
        if (!catalogueService.isUsineValid(type)) {
            System.err.println("[ERROR] Type de structure invalide: " + type);
            return false;
        }
        
        double prix = getPrixAchat(type, difficulte);
        
        if (ferme.getRevenu() >= prix) {
            StructureProduction structure;
            
            if (type.toLowerCase().contains("serre")) {
                structure = new Serre(ferme.getId(), type, prix);
                if (structureDao.saveSerre((Serre) structure) != null) {
                    ferme.setRevenu(ferme.getRevenu() - prix);
                    fermeDao.update(ferme);
                    
                    System.out.println("[INFO] Serre achetée: " + type + " pour " + prix + "€");
                    return true;
                }
            } else {
                structure = new Usine(ferme.getId(), type, prix);
                if (structureDao.saveUsine((Usine) structure) != null) {
                    ferme.setRevenu(ferme.getRevenu() - prix);
                    fermeDao.update(ferme);
                    
                    System.out.println("[INFO] Usine achetée: " + type + " pour " + prix + "€");
                    return true;
                }
            }
        } else {
            System.err.println("[ERROR] Fonds insuffisants pour acheter " + type + " (prix: " + prix + "€, disponible: " + ferme.getRevenu() + "€)");
        }
        
        return false;
    }
    
    public boolean acheterChamp(Ferme ferme, Difficulte difficulte) {
        // Prix fixe pour un champ selon la difficulté
        double prixChamp = 10000.0 * difficulte.getMultiplicateurAchat();
        
        if (ferme.getRevenu() >= prixChamp) {
            int numeroChamp = champDao.countByFermeId(ferme.getId()) + 1;
            
            Champ champ = new Champ();
            champ.setFermeId(ferme.getId());
            champ.setName("Champ " + numeroChamp);
            champ.setNumero(numeroChamp);
            champ.setEtat(EtatChamp.STANDBY);
            champ.setPrixAchat(prixChamp);
            champ.setTempsAction(0);
            
            if (champDao.save(champ) != null) {
                ferme.setRevenu(ferme.getRevenu() - prixChamp);
                fermeDao.update(ferme);
                
                System.out.println("[INFO] Nouveau champ acheté pour " + prixChamp + "€");
                return true;
            }
        } else {
            System.err.println("[ERROR] Fonds insuffisants pour acheter un champ (prix: " + prixChamp + "€, disponible: " + ferme.getRevenu() + "€)");
        }
        
        return false;
    }
    
    public boolean acheterCulture(Ferme ferme, String typeCulture, int champId, Difficulte difficulte) {
        if (!catalogueService.isCultureValid(typeCulture)) {
            System.err.println("[ERROR] Type de culture invalide: " + typeCulture);
            return false;
        }
        
        double prix = getPrixAchat(typeCulture, difficulte);
        
        if (ferme.getRevenu() >= prix) {
            Champ champ = champDao.findById(champId);
            if (champ != null && champ.getEtat() == EtatChamp.LABOURE) {
                champ.setTypeCulture(typeCulture);
                champ.setEtat(EtatChamp.SEME);
                champ.setTempsAction(System.currentTimeMillis());
                
                if (champDao.update(champ)) {
                    ferme.setRevenu(ferme.getRevenu() - prix);
                    fermeDao.update(ferme);
                    
                    System.out.println("[INFO] Culture " + typeCulture + " semée dans le champ " + champId + " pour " + prix + "€");
                    return true;
                }
            } else {
                System.err.println("[ERROR] Le champ doit être labouré avant de semer");
            }
        } else {
            System.err.println("[ERROR] Fonds insuffisants pour acheter " + typeCulture + " (prix: " + prix + "€, disponible: " + ferme.getRevenu() + "€)");
        }
        
        return false;
    }
    
    // === VENTES ===
    
    public double vendreArticle(Ferme ferme, String article, int quantite, Difficulte difficulte) {
        if (ferme.getStockPrincipal().getQuantite(article) >= quantite) {
            double prixUnitaire = getPrixVente(article, difficulte);
            double gain = prixUnitaire * quantite;
            
            if (ferme.getStockPrincipal().retirer(article, quantite)) {
                ferme.setRevenu(ferme.getRevenu() + gain);
                fermeDao.update(ferme);
                stockageDao.updateArticles(ferme.getStockPrincipal().getId(), ferme.getStockPrincipal().getArticles());
                
                System.out.println("[INFO] Vendu " + quantite + " " + article + " pour " + gain + "€");
                return gain;
            }
        } else {
            System.err.println("[ERROR] Stock insuffisant pour vendre " + quantite + " " + article);
        }
        
        return 0.0;
    }
    
    public double vendreCulture(Ferme ferme, String culture, int quantite, Difficulte difficulte) {
        return vendreArticle(ferme, culture, quantite, difficulte);
    }
    
    public double vendreAnimal(Ferme ferme, Animal animal, Difficulte difficulte) {
        if (animal.isVivant()) {
            double prix = getPrixVente(animal.getType(), difficulte);
            
            if (animalDao.delete(animal.getId())) {
                ferme.setRevenu(ferme.getRevenu() + prix);
                fermeDao.update(ferme);
                
                System.out.println("[INFO] Animal " + animal.getType() + " vendu pour " + prix + "€");
                return prix;
            }
        } else {
            System.err.println("[ERROR] Impossible de vendre un animal mort");
        }
        
        return 0.0;
    }
    
    public boolean vendreEquipement(Ferme ferme, Equipement equipement, Difficulte difficulte) {
        if (!equipement.isEnUtilisation()) {
            // Prix de revente = 70% du prix d'achat
            double prixRevente = getPrixAchat(equipement.getType(), difficulte) * 0.7;
            
            if (equipementDao.delete(equipement.getId())) {
                ferme.setRevenu(ferme.getRevenu() + prixRevente);
                fermeDao.update(ferme);
                
                System.out.println("[INFO] Équipement " + equipement.getType() + " vendu pour " + prixRevente + "€");
                return true;
            }
        } else {
            System.err.println("[ERROR] Impossible de vendre un équipement en cours d'utilisation");
        }
        
        return false;
    }
    
    // === MÉTHODES UTILITAIRES ===
    
    public double calculerValeurFerme(Ferme ferme, Difficulte difficulte) {
        double valeur = ferme.getRevenu();
        
        // Valeur des champs
        for (Champ champ : ferme.getChamps()) {
            valeur += champ.getPrixAchat();
        }
        
        // Valeur des structures
        for (StructureProduction structure : ferme.getStructures()) {
            valeur += structure.getPrixAchat();
        }
        
        // TODO: Ajouter valeur des équipements et animaux
        
        return valeur;
    }
    
    public boolean peutAcheter(Ferme ferme, String item, int quantite, Difficulte difficulte) {
        double prix = getPrixAchat(item, difficulte) * quantite;
        return ferme.getRevenu() >= prix;
    }
}