package dev.linnaelle.fs.dao;

import dev.linnaelle.fs.utils.DatabaseManager;
import dev.linnaelle.fs.entities.Equipement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EquipementDao {
    /**
     * Saves a new Equipement entity to the database.
     * @param equipement The Equipement entity to save.
     * @return The saved Equipement entity, or null if the save operation failed.
     */
    public Equipement save(Equipement equipement) {
        String sql = "INSERT INTO Equipement (gestionnaire_id, type, en_utilisation) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, equipement.getGestionnaireId());
            stmt.setString(2, equipement.getType());
            stmt.setBoolean(3, equipement.isEnUtilisation());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    equipement.setId(generatedKeys.getInt(1));
                }
                return equipement;
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la sauvegarde de l'équipement: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Finds an Equipement entity by its ID.
     * @param id The ID of the Equipement entity.
     * @return The Equipement entity, or null if not found.
     */
    public Equipement findById(int id) {
        String sql = "SELECT * FROM Equipement WHERE id = ?";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapToEquipement(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la recherche de l'équipement: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Finds all Equipement entities managed by a specific gestionnaire.
     * @param gestionnaireId The ID of the gestionnaire.
     * @return A list of Equipement entities managed by the gestionnaire.
     */
    public List<Equipement> findByGestionnaireId(int gestionnaireId) {
        List<Equipement> equipements = new ArrayList<>();
        String sql = "SELECT * FROM Equipement WHERE gestionnaire_id = ? ORDER BY type, id";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, gestionnaireId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                equipements.add(mapToEquipement(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la recherche des équipements: " + e.getMessage());
        }
        
        return equipements;
    }
    
    /**
     * Finds all Equipement entities of a specific type managed by a gestionnaire.
     * @param gestionnaireId The ID of the gestionnaire.
     * @param type The type of the Equipement.
     * @return A list of Equipement entities of the specified type.
     */
    public List<Equipement> findByType(int gestionnaireId, String type) {
        List<Equipement> equipements = new ArrayList<>();
        String sql = "SELECT * FROM Equipement WHERE gestionnaire_id = ? AND type = ? ORDER BY id";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, gestionnaireId);
            stmt.setString(2, type);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                equipements.add(mapToEquipement(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la recherche des équipements par type: " + e.getMessage());
        }
        
        return equipements;
    }
    
    /**
     * Finds all Equipement entities of a specific type that are available (not in use) for a gestionnaire.
     * @param gestionnaireId The ID of the gestionnaire.
     * @param type The type of the Equipement.
     * @return A list of available Equipement entities of the specified type.
     */
    public List<Equipement> findDisponiblesByGestionnaireAndType(int gestionnaireId, String type) {
        List<Equipement> equipements = new ArrayList<>();
        String sql = "SELECT * FROM Equipement WHERE gestionnaire_id = ? AND type = ? AND en_utilisation = 0 ORDER BY id";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, gestionnaireId);
            stmt.setString(2, type);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                equipements.add(mapToEquipement(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la recherche des équipements disponibles: " + e.getMessage());
        }
        
        return equipements;
    }
    
    /**
     * Updates an existing Equipement entity in the database.
     * @param equipement The Equipement entity to update.
     * @return True if the update was successful, false otherwise.
     */
    public boolean update(Equipement equipement) {
        String sql = "UPDATE Equipement SET type = ?, en_utilisation = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, equipement.getType());
            stmt.setBoolean(2, equipement.isEnUtilisation());
            stmt.setInt(3, equipement.getId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la mise à jour de l'équipement: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Deletes an Equipement entity from the database by its ID.
     * @param id The ID of the Equipement entity to delete.
     * @return True if the deletion was successful, false otherwise.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM Equipement WHERE id = ?";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la suppression de l'équipement: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Counts the number of Equipement entities managed by a gestionnaire of a specific type.
     * @param gestionnaireId The ID of the gestionnaire.
     * @param type The type of the Equipement.
     * @return The count of Equipement entities.
     */
    public int countByGestionnaireAndType(int gestionnaireId, String type) {
        String sql = "SELECT COUNT(*) FROM Equipement WHERE gestionnaire_id = ? AND type = ?";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, gestionnaireId);
            stmt.setString(2, type);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors du comptage des équipements: " + e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Counts the number of available Equipement entities of a specific type for a gestionnaire.
     * @param gestionnaireId The ID of the gestionnaire.
     * @param type The type of the Equipement.
     * @return The count of available Equipement entities.
     */
    public int countDisponibles(int gestionnaireId, String type) {
        String sql = "SELECT COUNT(*) FROM Equipement WHERE gestionnaire_id = ? AND type = ? AND en_utilisation = 0";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, gestionnaireId);
            stmt.setString(2, type);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors du comptage des équipements disponibles: " + e.getMessage());
        }
        
        return 0;
    }
    
    private Equipement mapToEquipement(ResultSet rs) throws SQLException {
        Equipement equipement = new Equipement();
        equipement.setId(rs.getInt("id"));
        equipement.setGestionnaireId(rs.getInt("gestionnaire_id"));
        equipement.setType(rs.getString("type"));
        equipement.setEnUtilisation(rs.getBoolean("en_utilisation"));
        
        return equipement;
    }
}