package dev.linnaelle.fs.dao;

import dev.linnaelle.fs.utils.DatabaseManager;
import dev.linnaelle.fs.entities.Champ;
import dev.linnaelle.fs.entities.EtatChamp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ChampDao {
    /**
     * Saves a Champ entity to the database.
     * @param champ The Champ entity to save.
     * @return The saved Champ entity, or null if the save operation failed.
     */
    public Champ save(Champ champ) {
        String sql = "INSERT INTO Champ (ferme_id, name, numero, type_culture, temps_action, etat, prix_achat) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, champ.getFermeId());
            stmt.setString(2, champ.getName());
            stmt.setInt(3, champ.getNumero());
            stmt.setString(4, champ.getTypeCulture());
            stmt.setLong(5, champ.getTempsAction());
            stmt.setString(6, champ.getEtat().name());
            stmt.setDouble(7, champ.getPrixAchat());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (Statement lastIdStmt = conn.createStatement();
                    ResultSet rs = lastIdStmt.executeQuery("SELECT last_insert_rowid()")) {
                    if (rs.next()) {
                        champ.setId(rs.getInt(1));
                    }

                    return champ;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la sauvegarde du champ: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Finds a Champ entity by its ID.
     * @param id The ID of the Champ to find.
     * @return The Champ entity if found, or null if not found.
     */
    public Champ findById(int id) {
        String sql = "SELECT * FROM Champ WHERE id = ?";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapToChamp(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la recherche du champ: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Finds all Champ entities associated with a specific ferme ID.
     * @param fermeId The ID of the ferme to find Champs for.
     * @return A list of Champ entities associated with the specified ferme ID.
     */
    public List<Champ> findByFermeId(int fermeId) {
        List<Champ> champs = new ArrayList<>();
        String sql = "SELECT * FROM Champ WHERE ferme_id = ? ORDER BY numero";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, fermeId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                champs.add(mapToChamp(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la recherche des champs: " + e.getMessage());
        }
        
        return champs;
    }
    
    /**
     * Finds all Champ entities with a specific state for a given ferme ID.
     * @param fermeId The ID of the ferme to find Champs for.
     * @param etat The state of the Champs to find.
     * @return A list of Champ entities with the specified state.
     */
    public List<Champ> findByEtat(int fermeId, EtatChamp etat) {
        List<Champ> champs = new ArrayList<>();
        String sql = "SELECT * FROM Champ WHERE ferme_id = ? AND etat = ? ORDER BY numero";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, fermeId);
            stmt.setString(2, etat.name());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                champs.add(mapToChamp(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la recherche des champs par état: " + e.getMessage());
        }
        
        return champs;
    }
    
    /**
     * Updates an existing Champ entity in the database.
     * @param champ The Champ entity to update.
     * @return true if the update was successful, false otherwise.
     */
    public boolean update(Champ champ) {
        String sql = "UPDATE Champ SET name = ?, type_culture = ?, temps_action = ?, etat = ?, prix_achat = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, champ.getName());
            stmt.setString(2, champ.getTypeCulture());
            stmt.setLong(3, champ.getTempsAction());
            stmt.setString(4, champ.getEtat().name());
            stmt.setDouble(5, champ.getPrixAchat());
            stmt.setInt(6, champ.getId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la mise à jour du champ: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Deletes a Champ entity from the database by its ID.
     * @param id The ID of the Champ to delete.
     * @return true if the deletion was successful, false otherwise.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM Champ WHERE id = ?";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la suppression du champ: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Counts the number of Champ entities associated with a specific ferme ID.
     * @param fermeId The ID of the ferme to count Champs for.
     * @return The count of Champ entities associated with the specified ferme ID.
     */
    public int countByFermeId(int fermeId) {
        String sql = "SELECT COUNT(*) FROM Champ WHERE ferme_id = ?";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, fermeId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors du comptage des champs: " + e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Finds all Champ entities that are ready to be harvested for a given ferme ID and current time.
     * @param fermeId The ID of the ferme to find Champs for.
     * @param tempsCourant The current time in milliseconds.
     * @return A list of Champ entities that are ready to be harvested.
     */
    public List<Champ> findChampsPretsARecolter(int fermeId, long tempsCourant) {
        List<Champ> champs = new ArrayList<>();
        String sql = "SELECT * FROM Champ WHERE ferme_id = ? AND etat = ? AND temps_action <= ? ORDER BY numero";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, fermeId);
            stmt.setString(2, EtatChamp.READY.name());
            stmt.setLong(3, tempsCourant - 120000);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                champs.add(mapToChamp(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la recherche des champs prêts: " + e.getMessage());
        }
        
        return champs;
    }
    
    private Champ mapToChamp(ResultSet rs) throws SQLException {
        Champ champ = new Champ();
        champ.setId(rs.getInt("id"));
        champ.setFermeId(rs.getInt("ferme_id"));
        champ.setName(rs.getString("name"));
        champ.setNumero(rs.getInt("numero"));
        champ.setTypeCulture(rs.getString("type_culture"));
        champ.setTempsAction(rs.getLong("temps_action"));
        champ.setEtat(EtatChamp.valueOf(rs.getString("etat")));
        champ.setPrixAchat(rs.getDouble("prix_achat"));
        
        return champ;
    }
}