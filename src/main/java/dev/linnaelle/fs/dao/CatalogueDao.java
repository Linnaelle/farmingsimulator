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
    
    private ArticleInfo mapToArticleInfo(ResultSet rs) throws SQLException {
        ArticleInfo article = new ArticleInfo();
        article.setNom(rs.getString("nom"));
        article.setCategorie(rs.getString("categorie"));
        article.setPrixVente(rs.getDouble("prix_vente"));
        
        return article;
    }
}