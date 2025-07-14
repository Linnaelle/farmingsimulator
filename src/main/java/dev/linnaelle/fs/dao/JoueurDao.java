package dev.linnaelle.fs.dao;

import dev.linnaelle.fs.utils.DatabaseManager;
import dev.linnaelle.fs.entities.Joueur;
import dev.linnaelle.fs.entities.Difficulte;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class JoueurDao {
    /**
     * Saves a Joueur to the database.
     * @param joueur The Joueur to save.
     * @return The saved Joueur with its ID set, or null if the save failed.
     */
    public Joueur save(Joueur joueur) {
        String sql = "INSERT INTO joueur (name, difficulte, temps_jeu) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseManager.get();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, joueur.getName());
            stmt.setString(2, joueur.getDifficulte().getNom());
            stmt.setLong(3, joueur.getTempsJeu());
            
            int result = stmt.executeUpdate();
            
            if (result > 0) {
                try (Statement lastIdStmt = conn.createStatement();
                    ResultSet rs = lastIdStmt.executeQuery("SELECT last_insert_rowid()")) {
                    if (rs.next()) {
                        joueur.setId(rs.getInt(1));
                    }
                }
                return joueur;
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la sauvegarde du joueur: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Finds a Joueur by its ID.
     * @param id The ID of the Joueur.
     * @return The Joueur, or null if not found.
     */
    public Joueur findById(int id) {
        String sql = """
            SELECT j.*, d.goldDepart, d.multiplicateurAchat, d.multiplicateurVente 
            FROM Joueur j 
            JOIN Difficulte d ON j.difficulte = d.nom 
            WHERE j.id = ?
            """;
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapToJoueur(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la recherche du joueur: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Finds a Joueur by its name.
     * @param name The name of the Joueur.
     * @return The Joueur, or null if not found.
     */ 
    public Joueur findByName(String name) {
        String sql = """
            SELECT j.*, d.goldDepart, d.multiplicateurAchat, d.multiplicateurVente 
            FROM Joueur j 
            JOIN Difficulte d ON j.difficulte = d.nom 
            WHERE j.name = ?
            """;
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapToJoueur(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la recherche du joueur par nom: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Retrieves all Joueurs from the database.
     * @return A list of Joueurs.
     */
    public List<Joueur> findAll() {
        List<Joueur> joueurs = new ArrayList<>();
        String sql = """
            SELECT j.*, d.goldDepart, d.multiplicateurAchat, d.multiplicateurVente 
            FROM Joueur j 
            JOIN Difficulte d ON j.difficulte = d.nom 
            ORDER BY j.name
            """;
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                joueurs.add(mapToJoueur(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la récupération des joueurs: " + e.getMessage());
        }
        
        return joueurs;
    }
    
    /**
     * Updates a Joueur in the database.
     * @param joueur The Joueur to update.
     * @return true if the update was successful, false otherwise.
     */
    public boolean update(Joueur joueur) {
        String sql = "UPDATE Joueur SET name = ?, temps_jeu = ?, difficulte = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, joueur.getName());
            stmt.setLong(2, joueur.getTempsJeu());
            stmt.setString(3, joueur.getDifficulte().getNom());
            stmt.setInt(4, joueur.getId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la mise à jour du joueur: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Deletes a Joueur by its ID.
     * @param id The ID of the Joueur to delete.
     * @return true if the deletion was successful, false otherwise.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM Joueur WHERE id = ?";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la suppression du joueur: " + e.getMessage());
        }
        
        return false;
    }
    
    private Joueur mapToJoueur(ResultSet rs) throws SQLException {
        Joueur joueur = new Joueur();
        joueur.setId(rs.getInt("id"));
        joueur.setName(rs.getString("name"));
        joueur.setTempsJeu(rs.getLong("temps_jeu"));
        
        Difficulte difficulte = new Difficulte();
        difficulte.setNom(rs.getString("difficulte"));
        difficulte.setGoldDepart(rs.getDouble("goldDepart"));
        difficulte.setMultiplicateurAchat(rs.getDouble("multiplicateurAchat"));
        difficulte.setMultiplicateurVente(rs.getDouble("multiplicateurVente"));
        joueur.setDifficulte(difficulte);
        
        return joueur;
    }
}