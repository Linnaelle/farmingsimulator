package dev.linnaelle.fs.dao;

import dev.linnaelle.fs.utils.DatabaseManager;
import dev.linnaelle.fs.entities.*;

import java.sql.*;
import java.util.*;

public class StructureProductionDao {
    /**
     * Saves an Usine to the database.
     * @param usine The Usine to save.
     * @return The saved Usine with its ID set, or null if the save failed.
     */
    public Usine saveUsine(Usine usine) {
        String sqlStructure = "INSERT INTO StructureProduction (ferme_id, type, active, prix_achat, en_pause, taux_traitement) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlUsine = "INSERT INTO Usine (structure_id) VALUES (?)";
        String sqlGetLastId = "SELECT last_insert_rowid()";
        
        try (Connection conn = DatabaseManager.get()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement stmtStructure = conn.prepareStatement(sqlStructure);
                 PreparedStatement stmtUsine = conn.prepareStatement(sqlUsine);
                 PreparedStatement stmtLastId = conn.prepareStatement(sqlGetLastId)) {
                
                // Insérer dans StructureProduction
                stmtStructure.setInt(1, usine.getFermeId());
                stmtStructure.setString(2, usine.getType());
                stmtStructure.setBoolean(3, usine.isActive());
                stmtStructure.setDouble(4, usine.getPrixAchat());
                stmtStructure.setBoolean(5, usine.isEnPause());
                stmtStructure.setInt(6, usine.getTauxTraitement());
                stmtStructure.executeUpdate();
                
                ResultSet rs = stmtLastId.executeQuery();
                if (rs.next()) {
                    int structureId = rs.getInt(1);
                    usine.setId(structureId);
                    
                    // Insérer dans Usine
                    stmtUsine.setInt(1, structureId);
                    stmtUsine.executeUpdate();
                    
                    conn.commit();
                    return usine;
                }
                
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la sauvegarde de l'Usine: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Saves a Serre to the database.
     * @param serre The Serre to save.
     * @return The saved Serre with its ID set, or null if the save failed.
     */
    public Serre saveSerre(Serre serre) {
        String sqlStructure = "INSERT INTO StructureProduction (ferme_id, type, active, prix_achat, en_pause, taux_traitement) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlSerre = "INSERT INTO Serre (structure_id, dernier_recolte) VALUES (?, ?)";
        String sqlGetLastId = "SELECT last_insert_rowid()";
        
        try (Connection conn = DatabaseManager.get()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement stmtStructure = conn.prepareStatement(sqlStructure, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement stmtSerre = conn.prepareStatement(sqlSerre);
                 PreparedStatement stmtLastId = conn.prepareStatement(sqlGetLastId)) {
                
                stmtStructure.setInt(1, serre.getFermeId());
                stmtStructure.setString(2, serre.getType());
                stmtStructure.setBoolean(3, serre.isActive());
                stmtStructure.setDouble(4, serre.getPrixAchat());
                stmtStructure.setBoolean(5, serre.isEnPause());
                stmtStructure.setInt(6, serre.getTauxTraitement());
                stmtStructure.executeUpdate();
                
                ResultSet rs = stmtLastId.executeQuery();
                if (rs.next()) {
                    int structureId = rs.getInt(1);
                    serre.setId(structureId);
                    
                    stmtSerre.setInt(1, structureId);
                    stmtSerre.setLong(2, serre.getDernierRecolte());
                    stmtSerre.executeUpdate();
                    
                    conn.commit();
                    return serre;
                }
                
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la sauvegarde de la Serre: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Finds an Usine by its ID.
     * @param id The ID of the Usine.
     * @return The Usine, or null if not found.
     */
    public List<Usine> findUsinesByFermeId(int fermeId) {
        List<Usine> usines = new ArrayList<>();
        String sql = """
            SELECT sp.*, u.structure_id 
            FROM StructureProduction sp 
            JOIN Usine u ON sp.id = u.structure_id 
            WHERE sp.ferme_id = ?
            """;
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, fermeId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Usine usine = new Usine();
                mapToStructureProduction(rs, usine);
                usines.add(usine);
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la recherche des Usines: " + e.getMessage());
        }
        
        return usines;
    }
    
    /**
     * Finds a Serre by its ID.
     * @param id The ID of the Serre.
     * @return The Serre, or null if not found.
     */
    public List<Serre> findSerresByFermeId(int fermeId) {
        List<Serre> serres = new ArrayList<>();
        String sql = """
            SELECT sp.*, s.dernier_recolte 
            FROM StructureProduction sp 
            JOIN Serre s ON sp.id = s.structure_id 
            WHERE sp.ferme_id = ?
            """;
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, fermeId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Serre serre = new Serre();
                mapToStructureProduction(rs, serre);
                serre.setDernierRecolte(rs.getLong("dernier_recolte"));
                serres.add(serre);
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la recherche des Serres: " + e.getMessage());
        }
        
        return serres;
    }
    
    /**
     * Finds all StructureProduction records by ferme ID.
     * @param fermeId The ID of the ferme.
     * @return A list of StructureProduction, including Usines and Serres.
     */
    public List<StructureProduction> findAllByFermeId(int fermeId) {
        List<StructureProduction> structures = new ArrayList<>();
        
        // Ajouter toutes les usines
        structures.addAll(findUsinesByFermeId(fermeId));
        
        // Ajouter toutes les serres
        structures.addAll(findSerresByFermeId(fermeId));
        
        return structures;
    }
    
    /**
     * Updates an Usine in the database.
     * @param usine The Usine to update.
     * @return true if the update was successful, false otherwise.
     */
    public boolean updateUsine(Usine usine) {
        String sql = "UPDATE StructureProduction SET type = ?, active = ?, prix_achat = ?, en_pause = ?, taux_traitement = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, usine.getType());
            stmt.setBoolean(2, usine.isActive());
            stmt.setDouble(3, usine.getPrixAchat());
            stmt.setBoolean(4, usine.isEnPause());
            stmt.setInt(5, usine.getTauxTraitement());
            stmt.setInt(6, usine.getId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la mise à jour de l'Usine: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Updates a Serre in the database.
     * @param serre The Serre to update.
     * @return true if the update was successful, false otherwise.
     */
    public boolean updateSerre(Serre serre) {
        String sqlStructure = "UPDATE StructureProduction SET type = ?, active = ?, prix_achat = ?, en_pause = ?, taux_traitement = ? WHERE id = ?";
        String sqlSerre = "UPDATE Serre SET dernier_recolte = ? WHERE structure_id = ?";
        
        try (Connection conn = DatabaseManager.get()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement stmtStructure = conn.prepareStatement(sqlStructure);
                 PreparedStatement stmtSerre = conn.prepareStatement(sqlSerre)) {
                
                // Mettre à jour StructureProduction
                stmtStructure.setString(1, serre.getType());
                stmtStructure.setBoolean(2, serre.isActive());
                stmtStructure.setDouble(3, serre.getPrixAchat());
                stmtStructure.setBoolean(4, serre.isEnPause());
                stmtStructure.setInt(5, serre.getTauxTraitement());
                stmtStructure.setInt(6, serre.getId());
                stmtStructure.executeUpdate();
                
                // Mettre à jour Serre
                stmtSerre.setLong(1, serre.getDernierRecolte());
                stmtSerre.setInt(2, serre.getId());
                stmtSerre.executeUpdate();
                
                conn.commit();
                return true;
                
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la mise à jour de la Serre: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Deletes a StructureProduction by its ID.
     * @param structureId The ID of the StructureProduction to delete.
     * @return true if the deletion was successful, false otherwise.
     */
    public boolean delete(int structureId) {
        String sql = "DELETE FROM StructureProduction WHERE id = ?";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, structureId);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la suppression de la structure: " + e.getMessage());
        }
        
        return false;
    }
    
    private void mapToStructureProduction(ResultSet rs, StructureProduction structure) throws SQLException {
        structure.setId(rs.getInt("id"));
        structure.setFermeId(rs.getInt("ferme_id"));
        structure.setType(rs.getString("type"));
        structure.setActive(rs.getBoolean("active"));
        structure.setPrixAchat(rs.getDouble("prix_achat"));
        structure.setEnPause(rs.getBoolean("en_pause"));
        structure.setTauxTraitement(rs.getInt("taux_traitement"));
    }
}