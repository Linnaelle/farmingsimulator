package dev.linnaelle.fs.dao;

import dev.linnaelle.fs.utils.DatabaseManager;
import dev.linnaelle.fs.entities.GestionnaireEquipement;
import dev.linnaelle.fs.entities.Equipement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestionnaireEquipementDao {
    
    private EquipementDao equipementDAO;
    
    public GestionnaireEquipementDao() {
        this.equipementDAO = new EquipementDao();
    }
    
    /**
     * Saves a GestionnaireEquipement to the database.
     * @param gestionnaire The GestionnaireEquipement to save.
     * @return The saved GestionnaireEquipement with its ID set, or null if the save failed.
     */
    public GestionnaireEquipement save(GestionnaireEquipement gestionnaire) {
        String sql = "INSERT INTO GestionnaireEquipement (ferme_id) VALUES (?)";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, gestionnaire.getFermeId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (Statement lastIdStmt = conn.createStatement();
                    ResultSet generatedKeys = lastIdStmt.executeQuery("SELECT last_insert_rowid()")) {

                    if (generatedKeys.next()) {
                        gestionnaire.setId(generatedKeys.getInt(1));
                    }
                }
                return gestionnaire;
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la sauvegarde du gestionnaire: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Finds a GestionnaireEquipement by its ID.
     * @param id The ID of the GestionnaireEquipement.
     * @return The GestionnaireEquipement, or null if not found.
     */
    public GestionnaireEquipement findById(int id) {
        String sql = "SELECT * FROM GestionnaireEquipement WHERE id = ?";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                GestionnaireEquipement gestionnaire = mapToGestionnaire(rs);
                loadEquipements(gestionnaire);
                return gestionnaire;
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la recherche du gestionnaire: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Finds a GestionnaireEquipement by the associated ferme ID.
     * @param fermeId The ID of the ferme.
     * @return The GestionnaireEquipement, or null if not found.
     */
    public GestionnaireEquipement findByFermeId(int fermeId) {
        String sql = "SELECT * FROM GestionnaireEquipement WHERE ferme_id = ?";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, fermeId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                GestionnaireEquipement gestionnaire = mapToGestionnaire(rs);
                loadEquipements(gestionnaire);
                return gestionnaire;
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la recherche du gestionnaire par ferme: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Retrieves all GestionnaireEquipement records.
     * @return A list of GestionnaireEquipement.
     */
    public List<GestionnaireEquipement> findAll() {
        String sql = "SELECT * FROM GestionnaireEquipement";
        List<GestionnaireEquipement> gestionnaires = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                GestionnaireEquipement gestionnaire = mapToGestionnaire(rs);
                loadEquipements(gestionnaire);
                gestionnaires.add(gestionnaire);
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la récupération des gestionnaires: " + e.getMessage());
        }
        
        return gestionnaires;
    }

    /**
     * Deletes a GestionnaireEquipement by its ID.
     * @param id The ID of the GestionnaireEquipement to delete.
     * @return true if the deletion was successful, false otherwise.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM GestionnaireEquipement WHERE id = ?";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la suppression du gestionnaire: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Adds an Equipement to a GestionnaireEquipement.
     * @param gestionnaireId The ID of the GestionnaireEquipement.
     * @param equipement The Equipement to add.
     * @return true if the addition was successful, false otherwise.
     */
    public boolean ajouterEquipement(int gestionnaireId, Equipement equipement) {
        equipement.setGestionnaireId(gestionnaireId);
        return equipementDAO.save(equipement) != null;
    }
    
    /**
     * Removes an Equipement from a GestionnaireEquipement.
     * @param equipementId The ID of the Equipement to remove.
     * @return true if the removal was successful, false otherwise.
     */
    public boolean supprimerEquipement(int equipementId) {
        return equipementDAO.delete(equipementId);
    }
    
    /**
     * Retrieves all Equipements for a GestionnaireEquipement.
     * @param gestionnaireId The ID of the GestionnaireEquipement.
     * @return A list of Equipements.
     */
    public List<Equipement> getEquipements(int gestionnaireId) {
        return equipementDAO.findByGestionnaireId(gestionnaireId);
    }
    
    /**
     * Counts the number of Equipements of a specific type for a GestionnaireEquipement.
     * @param gestionnaireId The ID of the GestionnaireEquipement.
     * @param type The type of Equipement to count.
     * @return The count of Equipements of the specified type.
     */
    public int countEquipementsByType(int gestionnaireId, String type) {
        return equipementDAO.countByGestionnaireAndType(gestionnaireId, type);
    }
    
    /**
     * Loads Equipements for a GestionnaireEquipement and organizes them by type.
     * @param gestionnaire The GestionnaireEquipement to load Equipements for.
     */
    private void loadEquipements(GestionnaireEquipement gestionnaire) {
        List<Equipement> equipements = equipementDAO.findByGestionnaireId(gestionnaire.getId());
        
        Map<String, List<Equipement>> inventaire = new HashMap<>();
        for (Equipement equipement : equipements) {
            inventaire.computeIfAbsent(equipement.getType(), k -> new ArrayList<>()).add(equipement);
        }
        
        gestionnaire.setInventaire(inventaire);
    }
    
    private GestionnaireEquipement mapToGestionnaire(ResultSet rs) throws SQLException {
        GestionnaireEquipement gestionnaire = new GestionnaireEquipement();
        gestionnaire.setId(rs.getInt("id"));
        gestionnaire.setFermeId(rs.getInt("ferme_id"));
        
        return gestionnaire;
    }
}