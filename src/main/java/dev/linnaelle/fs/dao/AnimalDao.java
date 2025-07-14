package dev.linnaelle.fs.dao;

import dev.linnaelle.fs.utils.DatabaseManager;
import dev.linnaelle.fs.entities.Animal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AnimalDao {
    /**
     * Sauvegarde un animal dans la base de données.
     * @param animal L'animal à sauvegarder.
     * @return L'animal sauvegardé avec son ID généré, ou null en cas d'erreur.
     */
    public Animal save(Animal animal) {
        String sql = "INSERT INTO Animal (ferme_animale_id, type, stock_herbe, vivant, deficit) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, animal.getFermeAnimaleId());
            stmt.setString(2, animal.getType());
            stmt.setInt(3, animal.getStockHerbe());
            stmt.setBoolean(4, animal.isVivant());
            stmt.setBoolean(5, animal.isDeficit());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (Statement lastIdStmt = conn.createStatement();
                    ResultSet rs = lastIdStmt.executeQuery("SELECT last_insert_rowid()")) {
                    if (rs.next()) {
                        animal.setId(rs.getInt(1));
                    }
                }
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la sauvegarde de l'animal: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Recherche un animal par son ID.
     * @param id L'ID de l'animal à rechercher.
     * @return L'animal trouvé, ou null si aucun animal n'est trouvé.
     */
    public Animal findById(int id) {
        String sql = "SELECT * FROM Animal WHERE id = ?";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapToAnimal(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la recherche de l'animal: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Recherche tous les animaux d'une ferme animale par son ID.
     * @param fermeAnimaleId L'ID de la ferme animale.
     * @return La liste des animaux trouvés, ou une liste vide si aucun animal n'est trouvé.
     */
    public List<Animal> findByFermeAnimaleId(int fermeAnimaleId) {
        List<Animal> animaux = new ArrayList<>();
        String sql = "SELECT * FROM Animal WHERE ferme_animale_id = ? ORDER BY id";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, fermeAnimaleId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                animaux.add(mapToAnimal(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la recherche des animaux: " + e.getMessage());
        }
        
        return animaux;
    }
    
    /**
     * Recherche les animaux vivants d'une ferme animale par son ID.
     * @param fermeAnimaleId L'ID de la ferme animale.
     * @return La liste des animaux vivants trouvés, ou une liste vide si aucun animal n'est trouvé.
     */
    public List<Animal> findVivantsByFermeAnimaleId(int fermeAnimaleId) {
        List<Animal> animaux = new ArrayList<>();
        String sql = "SELECT * FROM Animal WHERE ferme_animale_id = ? AND vivant = 1 ORDER BY id";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, fermeAnimaleId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                animaux.add(mapToAnimal(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la recherche des animaux vivants: " + e.getMessage());
        }
        
        return animaux;
    }
    
    /**
     * Recherche les animaux en déficit d'une ferme animale par son ID.
     * @param fermeAnimaleId L'ID de la ferme animale.
     * @return La liste des animaux en déficit trouvés, ou une liste vide si aucun animal n'est trouvé.
     */
    public List<Animal> findEnDeficitByFermeAnimaleId(int fermeAnimaleId) {
        List<Animal> animaux = new ArrayList<>();
        String sql = "SELECT * FROM Animal WHERE ferme_animale_id = ? AND deficit = 1 AND vivant = 1 ORDER BY id";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, fermeAnimaleId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                animaux.add(mapToAnimal(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la recherche des animaux en déficit: " + e.getMessage());
        }
        
        return animaux;
    }
    
    /**
     * Met à jour les informations d'un animal dans la base de données.
     * @param animal L'animal avec les informations mises à jour.
     * @return true si la mise à jour a réussi, false sinon.
     */
    public boolean update(Animal animal) {
        String sql = "UPDATE Animal SET stock_herbe = ?, vivant = ?, deficit = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, animal.getStockHerbe());
            stmt.setBoolean(2, animal.isVivant());
            stmt.setBoolean(3, animal.isDeficit());
            stmt.setInt(4, animal.getId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la mise à jour de l'animal: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Supprime un animal de la base de données par son ID.
     * @param id L'ID de l'animal à supprimer.
     * @return true si la suppression a réussi, false sinon.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM Animal WHERE id = ?";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la suppression de l'animal: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Compte le nombre total d'animaux dans une ferme animale par son ID.
     * @param fermeAnimaleId L'ID de la ferme animale.
     * @return Le nombre total d'animaux, ou 0 en cas d'erreur.
     */
    public int countByFermeAnimaleId(int fermeAnimaleId) {
        String sql = "SELECT COUNT(*) FROM Animal WHERE ferme_animale_id = ?";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, fermeAnimaleId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors du comptage des animaux: " + e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Supprime tous les animaux morts d'une ferme animale par son ID.
     * @param fermeAnimaleId L'ID de la ferme animale.
     * @return true si la suppression a réussi, false sinon.
     */
    public boolean supprimerAnimauxMorts(int fermeAnimaleId) {
        String sql = "DELETE FROM Animal WHERE ferme_animale_id = ? AND vivant = 0";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, fermeAnimaleId);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la suppression des animaux morts: " + e.getMessage());
        }
        
        return false;
    }
    
    private Animal mapToAnimal(ResultSet rs) throws SQLException {
        Animal animal = new Animal();
        animal.setId(rs.getInt("id"));
        animal.setFermeAnimaleId(rs.getInt("ferme_animale_id"));
        animal.setType(rs.getString("type"));
        animal.setStockHerbe(rs.getInt("stock_herbe"));
        animal.setVivant(rs.getBoolean("vivant"));
        animal.setDeficit(rs.getBoolean("deficit"));
        
        return animal;
    }
}