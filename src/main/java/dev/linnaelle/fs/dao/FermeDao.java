package dev.linnaelle.fs.dao;

import dev.linnaelle.fs.utils.DatabaseManager;
import dev.linnaelle.fs.entities.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class FermeDao {
    private StockageDao stockageDao;
    private StructureProductionDao structureDao;
    private ChampDao champDao;
    private GestionnaireEquipementDao equipementDao;
    
    public FermeDao() {
        this.stockageDao = new StockageDao();
        this.structureDao = new StructureProductionDao();
        this.champDao = new ChampDao();
        this.equipementDao = new GestionnaireEquipementDao();
    }
    
    /**
     * Saves a new Ferme entity to the database.
     * @param ferme The Ferme entity to save.
     * @return The saved Ferme entity with its generated ID, or null if the save failed.
     */
    public Ferme save(Ferme ferme) {
        String sql = "INSERT INTO Ferme (joueur_id, name, revenu) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, ferme.getJoueurId());
            stmt.setString(2, ferme.getName());
            stmt.setDouble(3, ferme.getRevenu());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (Statement lastIdStmt = conn.createStatement();
                     ResultSet generatedKeys = lastIdStmt.executeQuery("SELECT last_insert_rowid()")) {
                    
                    if (generatedKeys.next()) {
                        ferme.setId(generatedKeys.getInt(1));
                    }
                    createDefaultStockages(ferme);
                    createGestionnaireEquipement(ferme);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la sauvegarde de la ferme: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Finds a Ferme entity by its ID.
     * @param id The ID of the Ferme entity.
     * @return The Ferme entity, or null if not found.
     */
    public Ferme findById(int id) {
        String sql = "SELECT * FROM Ferme WHERE id = ?";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Ferme ferme = mapToFerme(rs);
                loadFermeDetails(ferme);
                return ferme;
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la recherche de la ferme: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Finds a Ferme entity by the ID of the associated Joueur.
     * @param joueurId The ID of the Joueur.
     * @return The Ferme entity, or null if not found.
     */
    public Ferme findByJoueurId(int joueurId) {
        String sql = "SELECT * FROM Ferme WHERE joueur_id = ?";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, joueurId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Ferme ferme = mapToFerme(rs);
                loadFermeDetails(ferme);
                return ferme;
            }
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la recherche de la ferme par joueur: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Updates an existing Ferme entity in the database.
     * @param ferme The Ferme entity to update.
     * @return True if the update was successful, false otherwise.
     */
    public boolean update(Ferme ferme) {
        String sql = "UPDATE Ferme SET name = ?, revenu = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, ferme.getName());
            stmt.setDouble(2, ferme.getRevenu());
            stmt.setInt(3, ferme.getId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la mise à jour de la ferme: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Deletes a Ferme entity from the database by its ID.
     * @param id The ID of the Ferme entity to delete.
     * @return True if the deletion was successful, false otherwise.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM Ferme WHERE id = ?";
        
        try (Connection conn = DatabaseManager.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("[ERROR] Erreur lors de la suppression de la ferme: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Creates default stockages for a new Ferme.
     * @param ferme The Ferme entity for which to create the stockages.
     */
    private void createDefaultStockages(Ferme ferme) {
        StockPrincipal stockPrincipal = new StockPrincipal(ferme.getId());
        stockPrincipal = stockageDao.saveStockPrincipal(stockPrincipal);
        ferme.setStockPrincipal(stockPrincipal);
        
        Entrepot entrepot = new Entrepot(ferme.getId());
        entrepot = stockageDao.saveEntrepot(entrepot);
        ferme.setEntrepot(entrepot);
        
        ReservoirEau reservoir = new ReservoirEau(ferme.getId(), 10000); 
        ReservoirEauDao reservoirDao = new ReservoirEauDao();
        reservoir = reservoirDao.save(reservoir);
        ferme.setReservoirEau(reservoir);
    }
    
    /**
     * Creates a GestionnaireEquipement for the Ferme.
     * @param ferme The Ferme entity for which to create the gestionnaire.
     */
    private void createGestionnaireEquipement(Ferme ferme) {
        GestionnaireEquipement gestionnaire = new GestionnaireEquipement(ferme.getId());
        gestionnaire = equipementDao.save(gestionnaire);
        ferme.setEquipements(gestionnaire);
    }
    
    /**
     * Loads the details of a Ferme entity, including its stockages, structures, and champs.
     * @param ferme The Ferme entity to load details for.
     */
    private void loadFermeDetails(Ferme ferme) {
        ferme.setStockPrincipal(stockageDao.findStockPrincipalByFermeId(ferme.getId()));
        ferme.setEntrepot(stockageDao.findEntrepotByFermeId(ferme.getId()));
        ferme.setReservoirEau(stockageDao.findReservoirEauByFermeId(ferme.getId()));
        ferme.setStructures(structureDao.findAllByFermeId(ferme.getId()));
        ferme.setChamps(champDao.findByFermeId(ferme.getId()));
        ferme.setEquipements(equipementDao.findByFermeId(ferme.getId()));
    }
    
    private Ferme mapToFerme(ResultSet rs) throws SQLException {
        Ferme ferme = new Ferme();
        ferme.setId(rs.getInt("id"));
        ferme.setJoueurId(rs.getInt("joueur_id"));
        ferme.setName(rs.getString("name"));
        ferme.setRevenu(rs.getDouble("revenu"));
        
        return ferme;
    }
}