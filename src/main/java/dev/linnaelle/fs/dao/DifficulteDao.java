package dev.linnaelle.fs.dao;

import dev.linnaelle.fs.utils.DatabaseManager;
import dev.linnaelle.fs.entities.Difficulte;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DifficulteDao {
    /**
     * Finds all Difficulte entities in the database.
     * @return A list of all Difficulte entities.
     */
    public List<Difficulte> findAll() {
        List<Difficulte> difficultes = new ArrayList<>();
        String sql = "SELECT * FROM Difficulte ORDER BY goldDepart ASC";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                difficultes.add(mapToDifficulte(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la recherche des difficultés: " + e.getMessage());
        }
        
        return difficultes;
    }
    
    /**
     * Finds a Difficulte entity by its name.
     * @param nom The name of the difficulty.
     * @return The Difficulte entity, or null if not found.
     */
    public Difficulte findByNom(String nom) {
        String sql = "SELECT * FROM Difficulte WHERE nom = ?";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nom);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapToDifficulte(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la recherche de la difficulté: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Checks if a Difficulte entity exists by its name.
     * @param nom The name of the difficulty.
     * @return True if the difficulty exists, false otherwise.
     */
    public boolean exists(String nom) {
        String sql = "SELECT COUNT(*) FROM Difficulte WHERE nom = ?";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nom);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la vérification d'existence de la difficulté: " + e.getMessage());
        }
        
        return false;
    }
    
    private Difficulte mapToDifficulte(ResultSet rs) throws SQLException {
        Difficulte difficulte = new Difficulte();
        difficulte.setNom(rs.getString("nom"));
        difficulte.setGoldDepart(rs.getDouble("goldDepart"));
        difficulte.setMultiplicateurAchat(rs.getDouble("multiplicateurAchat"));
        difficulte.setMultiplicateurVente(rs.getDouble("multiplicateurVente"));

        return difficulte;
    }
}