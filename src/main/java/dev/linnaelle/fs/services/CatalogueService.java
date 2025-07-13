package dev.linnaelle.fs.services;

import dev.linnaelle.fs.dao.CatalogueDao;
import dev.linnaelle.fs.entities.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class CatalogueService {
    
    private static CatalogueService instance;
    
    // Cache pour éviter les accès répétés à la base
    private Map<String, CultureInfo> cultureCache;
    private Map<String, AnimalInfo> animalCache;
    private Map<String, UsineInfo> usineCache;
    private Map<String, EquipementInfo> equipementCache;
    private Map<String, ArticleInfo> articleCache;
    
    // DAO pour accès aux données
    private CatalogueDao catalogueDao;
    
    private CatalogueService() {
        this.catalogueDao = new CatalogueDao();
        this.cultureCache = new HashMap<>();
        this.animalCache = new HashMap<>();
        this.usineCache = new HashMap<>();
        this.equipementCache = new HashMap<>();
        this.articleCache = new HashMap<>();
        
        // Chargement initial du cache
        loadCache();
    }
    
    public static CatalogueService getInstance() {
        if (instance == null) {
            instance = new CatalogueService();
        }
        return instance;
    }
    
    // === MÉTHODES PRINCIPALES ===
    
    public CultureInfo getCultureInfo(String type) {
        if (type == null || type.trim().isEmpty()) {
            return null;
        }
        
        return cultureCache.computeIfAbsent(type.toLowerCase(), 
            key -> catalogueDao.getCultureInfo(key));
    }
    
    public AnimalInfo getAnimalInfo(String type) {
        if (type == null || type.trim().isEmpty()) {
            return null;
        }
        
        return animalCache.computeIfAbsent(type.toLowerCase(), 
            key -> catalogueDao.getAnimalInfo(key));
    }
    
    public UsineInfo getUsineInfo(String type) {
        if (type == null || type.trim().isEmpty()) {
            return null;
        }
        
        return usineCache.computeIfAbsent(type.toLowerCase(), 
            key -> catalogueDao.getUsineInfo(key));
    }
    
    public EquipementInfo getEquipementInfo(String type) {
        if (type == null || type.trim().isEmpty()) {
            return null;
        }
        
        return equipementCache.computeIfAbsent(type.toLowerCase(), 
            key -> catalogueDao.getEquipementInfo(key));
    }
    
    public ArticleInfo getArticleInfo(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            return null;
        }
        
        return articleCache.computeIfAbsent(nom.toLowerCase(), 
            key -> catalogueDao.getArticleInfo(key));
    }
    
    // === MÉTHODES UTILITAIRES ===
    
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
    
    // === MÉTHODES DE VALIDATION ===
    
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
    
    // === MÉTHODES MÉTIER ===
    
    public boolean cultureNeedLabour(String typeCulture) {
        CultureInfo info = getCultureInfo(typeCulture);
        return info != null && info.isNeedLabour();
    }
    
    public List<String> getEquipementsRequiredForCulture(String typeCulture) {
        CultureInfo info = getCultureInfo(typeCulture);
        return info != null ? info.getEquipements() : new ArrayList<>();
    }
    
    public Map<String, Integer> getUsineIntrants(String typeUsine) {
        UsineInfo info = getUsineInfo(typeUsine);
        if (info != null && info.getIntrantsRequis() != null) {
            // Parse la chaîne "article1:quantite1,article2:quantite2"
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
    
    public String getUsineArticleProduit(String typeUsine) {
        UsineInfo info = getUsineInfo(typeUsine);
        return info != null ? info.getArticleProduit() : null;
    }
    
    public double getUsineMultiplicateur(String typeUsine) {
        UsineInfo info = getUsineInfo(typeUsine);
        return info != null ? info.getMultiplicateur() : 1.0;
    }
    
    public int getAnimalConsoEau(String typeAnimal) {
        AnimalInfo info = getAnimalInfo(typeAnimal);
        return info != null ? info.getConsoEau() : 0;
    }
    
    public int getAnimalConsoHerbe(String typeAnimal) {
        AnimalInfo info = getAnimalInfo(typeAnimal);
        return info != null ? info.getConsoHerbe() : 0;
    }
    
    public List<String> getAnimalArticlesProduits(String typeAnimal) {
        AnimalInfo info = getAnimalInfo(typeAnimal);
        return info != null ? info.getArticlesProduits() : new ArrayList<>();
    }
    
    // === GESTION DU CACHE ===
    
    private void loadCache() {
        try {
            // Chargement des cultures
            List<CultureInfo> cultures = catalogueDao.getAllCultures();
            for (CultureInfo culture : cultures) {
                cultureCache.put(culture.getNom().toLowerCase(), culture);
            }
            
            // Chargement des animaux
            List<AnimalInfo> animaux = catalogueDao.getAllAnimaux();
            for (AnimalInfo animal : animaux) {
                animalCache.put(animal.getNom().toLowerCase(), animal);
            }
            
            // Chargement des usines
            List<UsineInfo> usines = catalogueDao.getAllUsines();
            for (UsineInfo usine : usines) {
                usineCache.put(usine.getNom().toLowerCase(), usine);
            }
            
            // Chargement des équipements
            List<EquipementInfo> equipements = catalogueDao.getAllEquipements();
            for (EquipementInfo equipement : equipements) {
                equipementCache.put(equipement.getNom().toLowerCase(), equipement);
            }
            
            // Chargement des articles
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
    
    // === MÉTHODES DE DEBUG ===
    
    public void printCacheStats() {
        System.out.println("=== STATISTIQUES DU CATALOGUE ===");
        System.out.println("Cultures en cache: " + cultureCache.size());
        System.out.println("Animaux en cache: " + animalCache.size());
        System.out.println("Usines en cache: " + usineCache.size());
        System.out.println("Équipements en cache: " + equipementCache.size());
        System.out.println("Articles en cache: " + articleCache.size());
    }
}