package dev.linnaelle.fs.dao;

import dev.linnaelle.fs.utils.DatabaseManager;
import dev.linnaelle.fs.entities.*;

import java.sql.*;
import java.util.*;

public class StockageDao {
    /**
     * Saves a StockPrincipal to the database.
     * @param stock The StockPrincipal to save.
     * @return The saved StockPrincipal with its ID set, or null if the save failed.
     */
    public StockPrincipal saveStockPrincipal(StockPrincipal stock) {
        String sqlStockage = "INSERT INTO Stockage (ferme_id, capacite_max) VALUES (?, ?)";
        String sqlStockPrincipal = "INSERT INTO StockPrincipal (stockage_id) VALUES (?)";
        
        try (Connection conn = DatabaseManager.get()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement stmtStockage = conn.prepareStatement(sqlStockage);
                 PreparedStatement stmtStock = conn.prepareStatement(sqlStockPrincipal)) {
                
                
                stmtStockage.setInt(1, stock.getFermeId());
                stmtStockage.setInt(2, stock.getCapaciteMax());
                stmtStockage.executeUpdate();
                
                int stockageId = 0;
                try (Statement lastIdStmt = conn.createStatement();
                    ResultSet rs = lastIdStmt.executeQuery("SELECT last_insert_rowid()")) {
                    if (rs.next()) {
                        stockageId = rs.getInt(1);
                        stock.setId(stockageId);
                    }
                }

                if(stockageId > 0) {
                    stmtStock.setInt(1, stockageId);
                    stmtStock.executeUpdate();
                    
                    conn.commit();
                    return stock;
                } else {
                    conn.rollback();
                    System.err.println("[ERROR] Erreur lors de la sauvegarde du StockPrincipal: ID non généré.");
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la sauvegarde du StockPrincipal: " + e.getMessage());
        }
        return null;
    }

    /**
     * Saves an Entrepot to the database.
     * @param entrepot The Entrepot to save.
     * @return The saved Entrepot with its ID set, or null if the save failed.
     */
    public Entrepot saveEntrepot(Entrepot entrepot) {
        String sqlStockage = "INSERT INTO Stockage (ferme_id, capacite_max) VALUES (?, ?)";
        String sqlEntrepot = "INSERT INTO Entrepot (stockage_id) VALUES (?)";
        
        try (Connection conn = DatabaseManager.get()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement stmtStockage = conn.prepareStatement(sqlStockage);
                 PreparedStatement stmtEntrepot = conn.prepareStatement(sqlEntrepot)) {
                
                stmtStockage.setInt(1, entrepot.getFermeId());
                stmtStockage.setInt(2, entrepot.getCapaciteMax());
                stmtStockage.executeUpdate();
                
                int stockageId = 0;
                try (Statement lastIdStmt = conn.createStatement();
                    ResultSet rs = lastIdStmt.executeQuery("SELECT last_insert_rowid()")) {
                    
                    if (rs.next()) {
                        stockageId = rs.getInt(1);
                        entrepot.setId(stockageId);
                    }
                }
                if (stockageId > 0) {
                    stmtEntrepot.setInt(1, stockageId);
                    stmtEntrepot.executeUpdate();
                    
                    conn.commit();
                    return entrepot;
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la sauvegarde de l'Entrepot: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Finds a StockPrincipal by ferme ID.
     * @param fermeId The ID of the ferme.
     * @return The StockPrincipal, or null if not found.
     */
    public StockPrincipal findStockPrincipalByFermeId(int fermeId) {
        String sql = """
            SELECT s.*, sp.stockage_id 
            FROM Stockage s 
            JOIN StockPrincipal sp ON s.id = sp.stockage_id 
            WHERE s.ferme_id = ?
            """;
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, fermeId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                StockPrincipal stock = new StockPrincipal();
                stock.setId(rs.getInt("id"));
                stock.setFermeId(rs.getInt("ferme_id"));
                stock.setCapaciteMax(rs.getInt("capacite_max"));
                
                
                stock.setArticles(loadArticles(stock.getId()));
                
                return stock;
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la recherche du StockPrincipal: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Finds an Entrepot by ferme ID.
     * @param fermeId The ID of the ferme.
     * @return The Entrepot, or null if not found.
     */
    public Entrepot findEntrepotByFermeId(int fermeId) {
        String sql = """
            SELECT s.*, e.stockage_id 
            FROM Stockage s 
            JOIN Entrepot e ON s.id = e.stockage_id 
            WHERE s.ferme_id = ?
            """;
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, fermeId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Entrepot entrepot = new Entrepot();
                entrepot.setId(rs.getInt("id"));
                entrepot.setFermeId(rs.getInt("ferme_id"));
                entrepot.setCapaciteMax(rs.getInt("capacite_max"));
                
                entrepot.setArticles(loadArticles(entrepot.getId()));
                
                return entrepot;
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la recherche de l'Entrepot: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Finds a ReservoirEau by ferme ID.
     * @param fermeId The ID of the ferme.
     * @return The ReservoirEau, or null if not found.
     */
    public ReservoirEau findReservoirEauByFermeId(int fermeId) {
        String sql = """
            SELECT s.*, r.capacite, r.quantite, r.dernier_remplissage 
            FROM Stockage s 
            JOIN ReservoirEau r ON s.id = r.stockage_id 
            WHERE s.ferme_id = ?
            """;
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, fermeId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                ReservoirEau reservoir = new ReservoirEau();
                reservoir.setId(rs.getInt("id"));
                reservoir.setFermeId(rs.getInt("ferme_id"));
                reservoir.setCapaciteMax(rs.getInt("capacite_max"));
                reservoir.setCapacite(rs.getInt("capacite"));
                reservoir.setQuantite(rs.getInt("quantite"));
                reservoir.setDernierRemplissage(rs.getLong("dernier_remplissage"));
                
                return reservoir;
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la recherche du ReservoirEau: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Updates the articles in a Stockage.
     * @param stockageId The ID of the Stockage.
     * @param articles A map of article names and their quantities.
     * @return true if the update was successful, false otherwise.
     */
    public boolean updateArticles(int stockageId, Map<String, Integer> articles) {
        String deleteSql = "DELETE FROM ArticlesStockage WHERE stockage_id = ?";
        String insertSql = "INSERT INTO ArticlesStockage (stockage_id, article, quantite) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseManager.get()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
                 PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                
                
                deleteStmt.setInt(1, stockageId);
                deleteStmt.executeUpdate();
                
                
                for (Map.Entry<String, Integer> entry : articles.entrySet()) {
                    if (entry.getValue() > 0) {
                        insertStmt.setInt(1, stockageId);
                        insertStmt.setString(2, entry.getKey());
                        insertStmt.setInt(3, entry.getValue());
                        insertStmt.addBatch();
                    }
                }
                
                insertStmt.executeBatch();
                conn.commit();
                return true;
                
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la mise à jour des articles: " + e.getMessage());
        }
        
        return false;
    }
    
    private Map<String, Integer> loadArticles(int stockageId) {
        Map<String, Integer> articles = new HashMap<>();
        String sql = "SELECT article, quantite FROM ArticlesStockage WHERE stockage_id = ?";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, stockageId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                articles.put(rs.getString("article"), rs.getInt("quantite"));
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors du chargement des articles: " + e.getMessage());
        }
        
        return articles;
    }
}