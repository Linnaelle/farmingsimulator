package dev.linnaelle.fs.dao;

import dev.linnaelle.fs.utils.DatabaseManager;
import dev.linnaelle.fs.entities.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * DAO pour accéder aux données du catalogue.
 * Fournit des méthodes pour récupérer les informations sur les cultures, animaux, usines, équipements et articles.
 */
public class CatalogueDao {
    /**
     * Récupère les informations sur une culture par son nom.
     * @param nom Le nom de la culture.
     * @return Les informations sur la culture, ou null si aucune culture n'est trouvée.
     */
    public CultureInfo getCultureInfo(String nom) {
        String sql = "SELECT * FROM CultureInfo WHERE nom = ?";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nom);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapToCultureInfo(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la récupération de CultureInfo: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Récupère toutes les cultures disponibles.
     * @return Une liste de toutes les cultures.
     */
    public List<CultureInfo> getAllCultures() {
        List<CultureInfo> cultures = new ArrayList<>();
        String sql = "SELECT * FROM CultureInfo ORDER BY nom";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                cultures.add(mapToCultureInfo(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la récupération des cultures: " + e.getMessage());
        }
        
        return cultures;
    }
    
    /**
     * Récupère les informations sur un animal par son nom.
     * @param nom Le nom de l'animal.
     * @return Les informations sur l'animal, ou null si aucun animal n'est trouvé.
     */
    public AnimalInfo getAnimalInfo(String nom) {
        String sql = "SELECT * FROM AnimalInfo WHERE nom = ?";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nom);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapToAnimalInfo(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la récupération de AnimalInfo: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Récupère tous les animaux disponibles.
     * @return Une liste de tous les animaux.
     */
    public List<AnimalInfo> getAllAnimaux() {
        List<AnimalInfo> animaux = new ArrayList<>();
        String sql = "SELECT * FROM AnimalInfo ORDER BY nom";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                animaux.add(mapToAnimalInfo(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la récupération des animaux: " + e.getMessage());
        }
        
        return animaux;
    }
    
    /**
     * Récupère les informations sur une usine par son nom.
     * @param nom Le nom de l'usine.
     * @return Les informations sur l'usine, ou null si aucune usine n'est trouvée.
     */
    public UsineInfo getUsineInfo(String nom) {
        String sql = "SELECT * FROM UsineInfo WHERE nom = ?";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nom);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapToUsineInfo(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la récupération de UsineInfo: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Récupère toutes les usines disponibles.
     * @return Une liste de toutes les usines.
     */
    public List<UsineInfo> getAllUsines() {
        List<UsineInfo> usines = new ArrayList<>();
        String sql = "SELECT * FROM UsineInfo ORDER BY nom";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                usines.add(mapToUsineInfo(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la récupération des usines: " + e.getMessage());
        }
        
        return usines;
    }
    
    /**
     * Récupère les informations sur un équipement par son nom.
     * @param nom Le nom de l'équipement.
     * @return Les informations sur l'équipement, ou null si aucun équipement n'est trouvé.
     */
    public EquipementInfo getEquipementInfo(String nom) {
        String sql = "SELECT * FROM EquipementInfo WHERE nom = ?";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nom);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapToEquipementInfo(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la récupération de EquipementInfo: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Récupère tous les équipements disponibles.
     * @return Une liste de tous les équipements.
     */
    public List<EquipementInfo> getAllEquipements() {
        List<EquipementInfo> equipements = new ArrayList<>();
        String sql = "SELECT * FROM EquipementInfo ORDER BY nom";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                equipements.add(mapToEquipementInfo(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la récupération des équipements: " + e.getMessage());
        }
        
        return equipements;
    }
    
    /**
     * Récupère les informations sur un article par son nom.
     * @param nom Le nom de l'article.
     * @return Les informations sur l'article, ou null si aucun article n'est trouvé.
     */
    public ArticleInfo getArticleInfo(String nom) {
        String sql = "SELECT * FROM ArticleInfo WHERE nom = ?";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nom);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapToArticleInfo(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la récupération de ArticleInfo: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Récupère tous les articles disponibles.
     * @return Une liste de tous les articles.
     */
    public List<ArticleInfo> getAllArticles() {
        List<ArticleInfo> articles = new ArrayList<>();
        String sql = "SELECT * FROM ArticleInfo ORDER BY nom";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                articles.add(mapToArticleInfo(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la récupération des articles: " + e.getMessage());
        }
        
        return articles;
    }
    
    private CultureInfo mapToCultureInfo(ResultSet rs) throws SQLException {
        CultureInfo culture = new CultureInfo();
        culture.setNom(rs.getString("nom"));
        culture.setPrixAchat(rs.getDouble("prix_achat"));
        culture.setPrixVente(rs.getDouble("prix_vente"));
        culture.setRendement(rs.getInt("rendement"));
        culture.setNeedLabour(rs.getInt("need_labour") == 1);
        culture.setArticleProduit(rs.getString("article_produit"));
        
        String equipementsStr = rs.getString("equipements");
        List<String> equipements = Arrays.asList(equipementsStr.split(","));
        culture.setEquipements(equipements);
        
        return culture;
    }
    
    private AnimalInfo mapToAnimalInfo(ResultSet rs) throws SQLException {
        AnimalInfo animal = new AnimalInfo();
        animal.setNom(rs.getString("nom"));
        animal.setPrixAchat(rs.getDouble("prix_achat"));
        animal.setPrixVente(rs.getDouble("prix_vente"));
        animal.setConsoEau(rs.getInt("conso_eau"));
        animal.setConsoHerbe(rs.getInt("conso_herbe"));
        animal.setStockHerbe(rs.getInt("stock_herbe"));
        
        String produitsStr = rs.getString("articles_produits");
        List<String> produits = Arrays.asList(produitsStr.split(","));
        animal.setArticlesProduits(produits);
        
        return animal;
    }
    
    private UsineInfo mapToUsineInfo(ResultSet rs) throws SQLException {
        UsineInfo usine = new UsineInfo();
        usine.setNom(rs.getString("nom"));
        usine.setPrixAchat(rs.getDouble("prix_achat"));
        usine.setPrixVente(rs.getDouble("prix_vente"));
        usine.setIntrantsRequis(rs.getString("intrants_requis"));
        usine.setMultiplicateur(rs.getDouble("multiplicateur"));
        usine.setArticleProduit(rs.getString("article_produit"));
        
        return usine;
    }
    
    private EquipementInfo mapToEquipementInfo(ResultSet rs) throws SQLException {
        EquipementInfo equipement = new EquipementInfo();
        equipement.setNom(rs.getString("nom"));
        equipement.setPrixAchat(rs.getDouble("prix_achat"));
        equipement.setPrixVente(rs.getDouble("prix_vente"));
        equipement.setType(rs.getString("type"));
        
        return equipement;
    }

    public List<String> getAllCultureTypes() {
        List<String> types = new ArrayList<>();
        String sql = "SELECT nom FROM CultureInfo ORDER BY nom";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                types.add(rs.getString("nom"));
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la récupération des types de cultures: " + e.getMessage());
        }
        
        return types;
    }
    
    public List<String> getAllAnimalTypes() {
        List<String> types = new ArrayList<>();
        String sql = "SELECT nom FROM AnimalInfo ORDER BY nom";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                types.add(rs.getString("nom"));
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la récupération des types d'animaux: " + e.getMessage());
        }
        
        return types;
    }
    
    public List<String> getAllUsineTypes() {
        List<String> types = new ArrayList<>();
        String sql = "SELECT nom FROM UsineInfo ORDER BY nom";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                types.add(rs.getString("nom"));
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la récupération des types d'usines: " + e.getMessage());
        }
        
        return types;
    }
    
    public List<String> getAllEquipementTypes() {
        List<String> types = new ArrayList<>();
        String sql = "SELECT nom FROM EquipementInfo ORDER BY nom";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                types.add(rs.getString("nom"));
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la récupération des types d'équipements: " + e.getMessage());
        }
        
        return types;
    }
    
    public List<String> getArticlesByCategorie(String categorie) {
        List<String> articles = new ArrayList<>();
        String sql = "SELECT nom FROM ArticleInfo WHERE categorie = ? ORDER BY nom";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, categorie);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                articles.add(rs.getString("nom"));
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la récupération des articles par catégorie: " + e.getMessage());
        }
        
        return articles;
    }
    
    private ArticleInfo mapToArticleInfo(ResultSet rs) throws SQLException {
        ArticleInfo article = new ArticleInfo();
        article.setNom(rs.getString("nom"));
        article.setCategorie(rs.getString("categorie"));
        article.setPrixVente(rs.getDouble("prix_vente"));
        
        return article;
    }
}