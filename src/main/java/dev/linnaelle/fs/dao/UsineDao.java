package dev.linnaelle.fs.dao;

import dev.linnaelle.fs.utils.DatabaseManager;
import dev.linnaelle.fs.entities.Usine;

import java.sql.*;
import java.util.*;

public class UsineDao {
    /**
     * Saves an Usine to the database.
     * @param usine The Usine to save.
     * @return The saved Usine with its ID set, or null if the save failed.
     */
    public Usine save(Usine usine) {
        String sqlStructure = "INSERT INTO StructureProduction (ferme_id, type, active, prix_achat, en_pause, taux_traitement) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlUsine = "INSERT INTO Usine (structure_id) VALUES (?)";
        String sqlGetLastId = "SELECT last_insert_rowid()";
        
        try (Connection conn = DatabaseManager.get()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement stmtParent = conn.prepareStatement(sqlStructure);
                PreparedStatement stmtEnfant = conn.prepareStatement(sqlUsine);
                PreparedStatement stmtLastId = conn.prepareStatement(sqlGetLastId)) {
                
                stmtParent.setInt(1, usine.getFermeId());
                stmtParent.setString(2, usine.getType());
                stmtParent.setInt(3, usine.isActive() ? 1 : 0);
                stmtParent.setDouble(4, usine.getPrixAchat());
                stmtParent.setInt(5, usine.isEnPause() ? 1 : 0);
                stmtParent.setInt(6, usine.getTauxTraitement());
                stmtParent.executeUpdate();
                
                ResultSet rs = stmtLastId.executeQuery();
                if (rs.next()) {
                    int structureId = rs.getInt(1);
                    
                    stmtEnfant.setInt(1, structureId);
                    stmtEnfant.executeUpdate();
                    
                    usine.setId(structureId);
                    conn.commit();
                    return usine;
                }
                
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la sauvegarde de l'usine: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Finds an Usine by its ID.
     * @param id The ID of the Usine.
     * @return The Usine, or null if not found.
     */
    public Usine findById(int id) {
        String sql = "SELECT * FROM StructureProduction WHERE id = ? AND type NOT LIKE '%serre%'";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapToUsine(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la recherche de l'usine: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Finds all Usines by ferme ID.
     * @param fermeId The ID of the ferme.
     * @return A list of Usines.
     */
    public List<Usine> findByFermeId(int fermeId) {
        List<Usine> usines = new ArrayList<>();
        String sql = "SELECT * FROM StructureProduction WHERE ferme_id = ? AND type NOT LIKE '%serre%'";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, fermeId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                usines.add(mapToUsine(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la recherche des usines: " + e.getMessage());
        }
        
        return usines;
    }
    
    /**
     * Finds Usines by ferme ID and type.
     * @param fermeId The ID of the ferme.
     * @param type The type of Usine.
     * @return A list of Usines of the specified type.
     */
    public List<Usine> findByType(int fermeId, String type) {
        List<Usine> usines = new ArrayList<>();
        String sql = "SELECT * FROM StructureProduction WHERE ferme_id = ? AND type = ?";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, fermeId);
            stmt.setString(2, type);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                usines.add(mapToUsine(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la recherche des usines par type: " + e.getMessage());
        }
        
        return usines;
    }
    
    /**
     * Finds all active Usines by ferme ID.
     * @param fermeId The ID of the ferme.
     * @return A list of active Usines.
     */
    public List<Usine> findActiveByFermeId(int fermeId) {
        List<Usine> usines = new ArrayList<>();
        String sql = "SELECT * FROM StructureProduction WHERE ferme_id = ? AND type NOT LIKE '%serre%' AND active = true AND en_pause = false";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, fermeId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                usines.add(mapToUsine(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la recherche des usines actives: " + e.getMessage());
        }
        
        return usines;
    }
    
    /**
     * Updates an Usine in the database.
     * @param usine The Usine to update.
     * @return true if the update was successful, false otherwise.
     */
    public boolean update(Usine usine) {
        String sql = "UPDATE StructureProduction SET type = ?, active = ?, en_pause = ?, taux_traitement = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, usine.getType());
            stmt.setBoolean(2, usine.isActive());
            stmt.setBoolean(3, usine.isEnPause());
            stmt.setInt(4, usine.getTauxTraitement());
            stmt.setInt(5, usine.getId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la mise à jour de l'usine: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Updates the status of an Usine (active and en_pause).
     * @param id The ID of the Usine.
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
            System.err.println("[ERROR] Erreur lors de la mise à jour du statut de l'usine: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Updates the processing rate of an Usine.
     * @param id The ID of the Usine.
     * @param nouveauTaux The new processing rate.
     * @return true if the update was successful, false otherwise.
     */
    public boolean updateTauxTraitement(int id, int nouveauTaux) {
        String sql = "UPDATE StructureProduction SET taux_traitement = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, nouveauTaux);
            stmt.setInt(2, id);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la mise à jour du taux de traitement: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Deletes an Usine by its ID.
     * @param id The ID of the Usine to delete.
     * @return true if the deletion was successful, false otherwise.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM StructureProduction WHERE id = ?";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la suppression de l'usine: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Counts the number of Usines of a specific type in a ferme.
     * @param fermeId The ID of the ferme.
     * @param type The type of Usine.
     * @return The count of Usines of the specified type.
     */
    public int countByType(int fermeId, String type) {
        String sql = "SELECT COUNT(*) FROM StructureProduction WHERE ferme_id = ? AND type = ?";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, fermeId);
            stmt.setString(2, type);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors du comptage des usines: " + e.getMessage());
        }
        
        return 0;
    }
    
    private Usine mapToUsine(ResultSet rs) throws SQLException {
        Usine usine = new Usine();
        usine.setId(rs.getInt("id"));
        usine.setFermeId(rs.getInt("ferme_id"));
        usine.setType(rs.getString("type"));
        usine.setActive(rs.getBoolean("active"));
        usine.setPrixAchat(rs.getDouble("prix_achat"));
        usine.setEnPause(rs.getBoolean("en_pause"));
        usine.setTauxTraitement(rs.getInt("taux_traitement"));
        
        return usine;
    }
}