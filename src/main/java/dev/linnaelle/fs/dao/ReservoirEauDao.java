package dev.linnaelle.fs.dao;

import dev.linnaelle.fs.utils.DatabaseManager;
import dev.linnaelle.fs.entities.ReservoirEau;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ReservoirEauDao {
    /**
     * Saves a ReservoirEau to the database.
     * This method creates a parent record in Stockage and an associated child record in ReservoirEau.
     * @param reservoir The ReservoirEau to save.
     * @return The saved ReservoirEau with its ID set, or null if the save failed.
     */
    public ReservoirEau save(ReservoirEau reservoir) {
        String sqlStockage = "INSERT INTO Stockage (ferme_id, capacite_max) VALUES (?, ?)";
        String sqlReservoir = "INSERT INTO ReservoirEau (stockage_id, capacite, quantite, dernier_remplissage) VALUES (?, ?, ?, ?)";
        String sqlGetLastId = "SELECT last_insert_rowid()";
        
        try (Connection conn = DatabaseManager.get()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement stmtParent = conn.prepareStatement(sqlStockage, Statement.RETURN_GENERATED_KEYS);
                PreparedStatement stmtEnfant = conn.prepareStatement(sqlReservoir);
                PreparedStatement stmtLastId = conn.prepareStatement(sqlGetLastId)) {
                
                stmtParent.setInt(1, reservoir.getFermeId());
                stmtParent.setInt(2, reservoir.getCapaciteMax());
                stmtParent.executeUpdate();
                
                ResultSet rs = stmtLastId.executeQuery();
                if (rs.next()) {
                    int stockageId = rs.getInt(1);
                    
                    stmtEnfant.setInt(1, stockageId);
                    stmtEnfant.setInt(2, reservoir.getCapacite());
                    stmtEnfant.setInt(3, reservoir.getQuantite());
                    stmtEnfant.setLong(4, reservoir.getDernierRemplissage());
                    stmtEnfant.executeUpdate();
                    
                    reservoir.setId(stockageId);
                    conn.commit();
                    return reservoir;
                }
                
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la sauvegarde du réservoir d'eau: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Finds a ReservoirEau by its ID.
     * @param id The ID of the ReservoirEau.
     * @return The ReservoirEau, or null if not found.
     */
    public ReservoirEau findById(int id) {
        String sql = "SELECT * FROM ReservoirEau WHERE id = ?";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapToReservoirEau(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la recherche du réservoir d'eau: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Finds a ReservoirEau by its ferme ID.
     * @param fermeId The ID of the ferme.
     * @return The ReservoirEau, or null if not found.
     */
    public ReservoirEau findByFermeId(int fermeId) {
        String sql = "SELECT * FROM ReservoirEau WHERE ferme_id = ?";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, fermeId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapToReservoirEau(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la recherche du réservoir d'eau par ferme: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Updates a ReservoirEau in the database.
     * @param reservoir The ReservoirEau to update.
     * @return true if the update was successful, false otherwise.
     */
    public boolean update(ReservoirEau reservoir) {
        String sql = "UPDATE ReservoirEau SET capacite = ?, quantite = ?, dernier_remplissage = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, reservoir.getCapacite());
            stmt.setInt(2, reservoir.getQuantite());
            stmt.setLong(3, reservoir.getDernierRemplissage());
            stmt.setInt(4, reservoir.getId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la mise à jour du réservoir d'eau: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Updates the quantity of water in a ReservoirEau.
     * @param id The ID of the ReservoirEau.
     * @param nouvelleQuantite The new quantity of water.
     * @return true if the update was successful, false otherwise.
     */
    public boolean updateQuantite(int id, int nouvelleQuantite) {
        String sql = "UPDATE ReservoirEau SET quantite = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, nouvelleQuantite);
            stmt.setInt(2, id);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la mise à jour de la quantité d'eau: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Updates the last refill time of a ReservoirEau.
     * @param id The ID of the ReservoirEau.
     * @param temps The new last refill time in milliseconds.
     * @return true if the update was successful, false otherwise.
     */
    public boolean updateDernierRemplissage(int id, long temps) {
        String sql = "UPDATE ReservoirEau SET dernier_remplissage = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, temps);
            stmt.setInt(2, id);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la mise à jour du dernier remplissage: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Deletes a ReservoirEau by its ID.
     * @param id The ID of the ReservoirEau to delete.
     * @return true if the deletion was successful, false otherwise.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM ReservoirEau WHERE id = ?";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la suppression du réservoir d'eau: " + e.getMessage());
        }
        
        return false;
    }
    
    private ReservoirEau mapToReservoirEau(ResultSet rs) throws SQLException {
        ReservoirEau reservoir = new ReservoirEau();
        reservoir.setId(rs.getInt("id"));
        reservoir.setFermeId(rs.getInt("ferme_id"));
        reservoir.setCapacite(rs.getInt("capacite"));
        reservoir.setQuantite(rs.getInt("quantite"));
        reservoir.setDernierRemplissage(rs.getLong("dernier_remplissage"));
        
        return reservoir;
    }
}