package dev.linnaelle.fs.dao;

import dev.linnaelle.fs.utils.DatabaseManager;
import dev.linnaelle.fs.entities.*;

import java.sql.*;
import java.util.*;

public class SerreDao {
    
    /**
     * Saves a Serre to the database.
     * @param serre The Serre to save.
     * @return The saved Serre with its ID set, or null if the save failed.
     */
    public Serre save(Serre serre) {
        String sqlStructure = "INSERT INTO StructureProduction (ferme_id, type, active, prix_achat, en_pause, taux_traitement) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlSerre = "INSERT INTO Serre (structure_id, dernier_recolte) VALUES (?, ?)";
        String sqlGetLastId = "SELECT last_insert_rowid()";
        
        try (Connection conn = DatabaseManager.get()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement stmtParent = conn.prepareStatement(sqlStructure);
                PreparedStatement stmtEnfant = conn.prepareStatement(sqlSerre);
                PreparedStatement stmtLastId = conn.prepareStatement(sqlGetLastId)) {
                
                stmtParent.setInt(1, serre.getFermeId());
                stmtParent.setString(2, serre.getType());
                stmtParent.setInt(3, serre.isActive() ? 1 : 0);
                stmtParent.setDouble(4, serre.getPrixAchat());
                stmtParent.setInt(5, serre.isEnPause() ? 1 : 0);
                stmtParent.setInt(6, serre.getTauxTraitement());
                stmtParent.executeUpdate();
                
                ResultSet rs = stmtLastId.executeQuery();
                if (rs.next()) {
                    int structureId = rs.getInt(1);
                    
                    stmtEnfant.setInt(1, structureId);
                    stmtEnfant.setLong(2, serre.getDernierRecolte());
                    stmtEnfant.executeUpdate();
                    
                    serre.setId(structureId);
                    conn.commit();
                    return serre;
                }
                
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la sauvegarde de la serre: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Finds a Serre by its ID.
     * @param id The ID of the Serre.
     * @return The Serre, or null if not found.
     */
    public Serre findById(int id) {
        String sql = "SELECT * FROM StructureProduction WHERE id = ? AND type LIKE '%serre%'";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapToSerre(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la recherche de la serre: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Finds all Serres by ferme ID.
     * @param fermeId The ID of the ferme.
     * @return A list of Serres, or an empty list if none found.
     */
    public List<Serre> findByFermeId(int fermeId) {
        List<Serre> serres = new ArrayList<>();
        String sql = "SELECT * FROM StructureProduction WHERE ferme_id = ? AND type LIKE '%serre%'";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, fermeId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                serres.add(mapToSerre(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la recherche des serres: " + e.getMessage());
        }
        
        return serres;
    }
    
    /**
     * Finds all active Serres by ferme ID.
     * @param fermeId The ID of the ferme.
     * @return A list of active Serres, or an empty list if none found.
     */
    public List<Serre> findActiveByFermeId(int fermeId) {
        List<Serre> serres = new ArrayList<>();
        String sql = "SELECT * FROM StructureProduction WHERE ferme_id = ? AND type LIKE '%serre%' AND active = true AND en_pause = false";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, fermeId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                serres.add(mapToSerre(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la recherche des serres actives: " + e.getMessage());
        }
        
        return serres;
    }
    
    /**
     * Updates a Serre in the database.
     * @param serre The Serre to update.
     * @return true if the update was successful, false otherwise.
     */
    public boolean update(Serre serre) {
        String sql = "UPDATE StructureProduction SET type = ?, active = ?, en_pause = ?, taux_traitement = ?, dernier_recolte = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, serre.getType());
            stmt.setBoolean(2, serre.isActive());
            stmt.setBoolean(3, serre.isEnPause());
            stmt.setInt(4, serre.getTauxTraitement());
            stmt.setLong(5, serre.getDernierRecolte());
            stmt.setInt(6, serre.getId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la mise à jour de la serre: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Updates the status of a Serre (active, en_pause).
     * @param id The ID of the Serre.
     * @param active The new active status.
     * @param enPause The new pause status.
     * @return true if the update was successful, false otherwise.
     */
    public boolean updateStatut(int id, boolean active, boolean enPause) {
        String sql = "UPDATE StructureProduction SET active = ?, en_pause = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBoolean(1, active);
            stmt.setBoolean(2, enPause);
            stmt.setInt(3, id);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la mise à jour du statut de la serre: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Updates the last harvest time of a Serre.
     * @param id The ID of the Serre.
     * @param temps The new last harvest time in milliseconds.
     * @return true if the update was successful, false otherwise.
     */
    public boolean updateDernierRecolte(int id, long temps) {
        String sql = "UPDATE StructureProduction SET dernier_recolte = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, temps);
            stmt.setInt(2, id);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la mise à jour du dernier récolte: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Deletes a Serre by its ID.
     * @param id The ID of the Serre to delete.
     * @return true if the deletion was successful, false otherwise.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM StructureProduction WHERE id = ?";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la suppression de la serre: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Finds all Serres ready to harvest based on the current time.
     * @param fermeId The ID of the ferme.
     * @param tempsCourant The current time in milliseconds.
     * @return A list of Serres ready to harvest.
     */
    public List<Serre> findSerresReadyToHarvest(int fermeId, long tempsCourant) {
        List<Serre> serres = new ArrayList<>();
        String sql = "SELECT * FROM StructureProduction WHERE ferme_id = ? AND type LIKE '%serre%' AND active = true AND en_pause = false AND dernier_recolte <= ?";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, fermeId);
            stmt.setLong(2, tempsCourant - 300000); 
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                serres.add(mapToSerre(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la recherche des serres prêtes: " + e.getMessage());
        }
        
        return serres;
    }
    
    private Serre mapToSerre(ResultSet rs) throws SQLException {
        Serre serre = new Serre();
        serre.setId(rs.getInt("id"));
        serre.setFermeId(rs.getInt("ferme_id"));
        serre.setType(rs.getString("type"));
        serre.setActive(rs.getBoolean("active"));
        serre.setPrixAchat(rs.getDouble("prix_achat"));
        serre.setEnPause(rs.getBoolean("en_pause"));
        serre.setTauxTraitement(rs.getInt("taux_traitement"));
        serre.setDernierRecolte(rs.getLong("dernier_recolte"));
        
        return serre;
    }
}