package dev.linnaelle.fs.services;

import dev.linnaelle.fs.dao.CatalogueDao;
import dev.linnaelle.fs.entities.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class CatalogueService {
    
    private static CatalogueService instance;
    
    private Map<String, CultureInfo> cultureCache;
    private Map<String, AnimalInfo> animalCache;
    private Map<String, UsineInfo> usineCache;
    private Map<String, EquipementInfo> equipementCache;
    private Map<String, ArticleInfo> articleCache;
    
    private CatalogueDao catalogueDao;
    
    private CatalogueService() {
        this.catalogueDao = new CatalogueDao();
        this.cultureCache = new HashMap<>();
        this.animalCache = new HashMap<>();
        this.usineCache = new HashMap<>();
        this.equipementCache = new HashMap<>();
        this.articleCache = new HashMap<>();
        
        loadCache();
    }
    
    public static CatalogueService getInstance() {
        if (instance == null) {
            instance = new CatalogueService();
        }
        return instance;
    }
    
    /**
     * Récupérer les informations sur une culture par son type
     * @param type Le type de culture
     * @return Les informations sur la culture, ou null si non trouvées
     */
    public CultureInfo getCultureInfo(String type) {
        if (type == null || type.trim().isEmpty()) {
            return null;
        }
        
        return cultureCache.computeIfAbsent(type.toLowerCase(), 
            key -> catalogueDao.getCultureInfo(key));
    }
    
    /**
     * Récupérer les informations sur un animal par son type
     * @param type Le type d'animal
     * @return Les informations sur l'animal, ou null si non trouvées
     */
    public AnimalInfo getAnimalInfo(String type) {
        if (type == null || type.trim().isEmpty()) {
            return null;
        }
        
        return animalCache.computeIfAbsent(type.toLowerCase(), 
            key -> catalogueDao.getAnimalInfo(key));
    }
    
    /**
     * Récupérer les informations sur une usine par son type
     * @param type Le type d'usine
     * @return Les informations sur l'usine, ou null si non trouvées
     */
    public UsineInfo getUsineInfo(String type) {
        if (type == null || type.trim().isEmpty()) {
            return null;
        }
        
        return usineCache.computeIfAbsent(type.toLowerCase(), 
            key -> catalogueDao.getUsineInfo(key));
    }
    
    /**
     * Récupérer les informations sur un équipement par son type
     * @param type Le type d'équipement
     * @return Les informations sur l'équipement, ou null si non trouvées
     */
    public EquipementInfo getEquipementInfo(String type) {
        if (type == null || type.trim().isEmpty()) {
            return null;
        }
        
        return equipementCache.computeIfAbsent(type.toLowerCase(), 
            key -> catalogueDao.getEquipementInfo(key));
    }
    
    /**
     * Récupérer les informations sur un article par son nom
     * @param nom Le nom de l'article
     * @return Les informations sur l'article, ou null si non trouvées
     */
    public ArticleInfo getArticleInfo(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            return null;
        }
        
        return articleCache.computeIfAbsent(nom.toLowerCase(), 
            key -> catalogueDao.getArticleInfo(key));
    }
    
    public List<String> getAllCultureTypes() {
        return catalogueDao.getAllCultureTypes();
    }
    
    public List<String> getAllAnimalTypes() {
        return catalogueDao.getAllAnimalTypes();
    }
    
    public List<String> getAllUsineTypes() {
        return catalogueDao.getAllUsineTypes();
    }
    
    public List<String> getAllEquipementTypes() {
        return catalogueDao.getAllEquipementTypes();
    }
    
    public List<String> getArticlesByCategorie(String categorie) {
        return catalogueDao.getArticlesByCategorie(categorie);
    }
    
    public boolean isCultureValid(String type) {
        return getCultureInfo(type) != null;
    }
    
    public boolean isAnimalValid(String type) {
        return getAnimalInfo(type) != null;
    }
    
    public boolean isUsineValid(String type) {
        return getUsineInfo(type) != null;
    }
    
    public boolean isEquipementValid(String type) {
        return getEquipementInfo(type) != null;
    }
    
    public boolean isArticleValid(String nom) {
        return getArticleInfo(nom) != null;
    }
    
    /**
     * Vérifier si une culture nécessite du travail
     * @param typeCulture Le type de culture
     * @return true si la culture nécessite du travail, false sinon
     */
    public boolean cultureNeedLabour(String typeCulture) {
        CultureInfo info = getCultureInfo(typeCulture);
        return info != null && info.isNeedLabour();
    }
    
    /**
     * Récupérer les équipements requis pour une culture
     * @param typeCulture Le type de culture
     * @return La liste des équipements requis, ou une liste vide si aucun équipement n'est requis
     */
    public List<String> getEquipementsRequiredForCulture(String typeCulture) {
        CultureInfo info = getCultureInfo(typeCulture);
        return info != null ? info.getEquipements() : new ArrayList<>();
    }
    
    /**
     * Récupérer les intrants requis pour une usine
     * @param typeUsine Le type d'usine
     * @return Un map des articles et quantités requis, ou une map vide si aucun intrant n'est requis
     */
    public Map<String, Integer> getUsineIntrants(String typeUsine) {
        UsineInfo info = getUsineInfo(typeUsine);
        if (info != null && info.getIntrantsRequis() != null) {
            Map<String, Integer> intrants = new HashMap<>();
            String[] pairs = info.getIntrantsRequis().split(",");
            for (String pair : pairs) {
                String[] parts = pair.split(":");
                if (parts.length == 2) {
                    try {
                        intrants.put(parts[0].trim(), Integer.parseInt(parts[1].trim()));
                    } catch (NumberFormatException e) {
                        System.err.println("[ERROR] Format invalide pour les intrants: " + pair);
                    }
                }
            }
            return intrants;
        }
        return new HashMap<>();
    }
    
    /**
     * Récupérer les articles produits par une usine
     * @param typeUsine Le type d'usine
     * @return La liste des articles produits, ou une liste vide si aucun article n'est produit
     */
    public String getUsineArticleProduit(String typeUsine) {
        UsineInfo info = getUsineInfo(typeUsine);
        return info != null ? info.getArticleProduit() : null;
    }
    
    /**
     * Récupérer le multiplicateur de production d'une usine
     * @param typeUsine Le type d'usine
     * @return Le multiplicateur de production, ou 1.0 si non spécifié
     */
    public double getUsineMultiplicateur(String typeUsine) {
        UsineInfo info = getUsineInfo(typeUsine);
        return info != null ? info.getMultiplicateur() : 1.0;
    }
    
    /**
     * Récupérer la consommation d'eau d'un animal
     * @param typeAnimal Le type d'animal
     * @return La consommation d'eau en litres, ou 0 si non spécifié
     */
    public int getAnimalConsoEau(String typeAnimal) {
        AnimalInfo info = getAnimalInfo(typeAnimal);
        return info != null ? info.getConsoEau() : 0;
    }
    
    /**
     * Récupérer la consommation de nourriture d'un animal
     * @param typeAnimal Le type d'animal
     * @return La consommation de nourriture en kg, ou 0 si non spécifié
     */
    public int getAnimalConsoHerbe(String typeAnimal) {
        AnimalInfo info = getAnimalInfo(typeAnimal);
        return info != null ? info.getConsoHerbe() : 0;
    }
    
    /**
     * Récupérer les articles produits par un animal
     * @param typeAnimal Le type d'animal
     * @return La liste des articles produits, ou une liste vide si aucun article n'est produit
     */
    public List<String> getAnimalArticlesProduits(String typeAnimal) {
        AnimalInfo info = getAnimalInfo(typeAnimal);
        return info != null ? info.getArticlesProduits() : new ArrayList<>();
    }
    
    private void loadCache() {
        try {
            List<CultureInfo> cultures = catalogueDao.getAllCultures();
            for (CultureInfo culture : cultures) {
                cultureCache.put(culture.getNom().toLowerCase(), culture);
            }
            
            List<AnimalInfo> animaux = catalogueDao.getAllAnimaux();
            for (AnimalInfo animal : animaux) {
                animalCache.put(animal.getNom().toLowerCase(), animal);
            }
            
            List<UsineInfo> usines = catalogueDao.getAllUsines();
            for (UsineInfo usine : usines) {
                usineCache.put(usine.getNom().toLowerCase(), usine);
            }
            
            List<EquipementInfo> equipements = catalogueDao.getAllEquipements();
            for (EquipementInfo equipement : equipements) {
                equipementCache.put(equipement.getNom().toLowerCase(), equipement);
            }
            
            List<ArticleInfo> articles = catalogueDao.getAllArticles();
            for (ArticleInfo article : articles) {
                articleCache.put(article.getNom().toLowerCase(), article);
            }
            
            System.out.println("[INFO] Catalogue chargé avec succès (" + 
                cultureCache.size() + " cultures, " +
                animalCache.size() + " animaux, " +
                usineCache.size() + " usines, " +
                equipementCache.size() + " équipements, " +
                articleCache.size() + " articles)");
                
        } catch (Exception e) {
            System.err.println("[ERROR] Erreur lors du chargement du catalogue: " + e.getMessage());
        }
    }
    
    public void reloadCache() {
        clearCache();
        loadCache();
    }
    
    public void clearCache() {
        cultureCache.clear();
        animalCache.clear();
        usineCache.clear();
        equipementCache.clear();
        articleCache.clear();
    }
    
    public void printCacheStats() {
        System.out.println("=== STATISTIQUES DU CATALOGUE ===");
        System.out.println("Cultures en cache: " + cultureCache.size());
        System.out.println("Animaux en cache: " + animalCache.size());
        System.out.println("Usines en cache: " + usineCache.size());
        System.out.println("Équipements en cache: " + equipementCache.size());
        System.out.println("Articles en cache: " + articleCache.size());
    }
}