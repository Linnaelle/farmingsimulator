package dev.linnaelle.fs;

import dev.linnaelle.fs.utils.DatabaseManager;
import dev.linnaelle.fs.service.CatalogueInitializer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestDatabase {
    
    public static void main(String[] args) {
        try {
            System.out.println("=== TEST DE LA BASE DE DONNEES ===\n");
            
            System.out.println("Initialisation de la base de donnees...");
            DatabaseManager.getInstance().initializeDatabase();
            CatalogueInitializer.initializeCatalogueData();
            
            System.out.println("\nVerification des donnees inserees :\n");
            
            testCultureInfo();
            testAnimalInfo();
            testUsineInfo();
            testEquipementInfo();
            testArticleInfo();
            
            System.out.println("\n[SUCCESS] Tous les tests sont reussis !");
            
        } catch (Exception e) {
            System.err.println("[ERROR] Erreur lors du test: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseManager.closeInstance();
        }
    }
    
    private static void testCultureInfo() {
        try (Connection conn = DatabaseManager.get();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM CultureInfo")) {
            
            if (rs.next()) {
                int count = rs.getInt("count");
                System.out.println("[CULTURES] " + count + " cultures trouvees");
                
                ResultSet examples = stmt.executeQuery("SELECT nom, prix_achat, rendement FROM CultureInfo LIMIT 3");
                while (examples.next()) {
                    System.out.println("   - " + examples.getString("nom") + 
                                     " (Prix: " + examples.getDouble("prix_achat") + 
                                     " gold, Rendement: " + examples.getInt("rendement") + "L/ha)");
                }
                examples.close();
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Erreur test CultureInfo: " + e.getMessage());
        }
    }
    
    private static void testAnimalInfo() {
        try (Connection conn = DatabaseManager.get();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM AnimalInfo")) {
            
            if (rs.next()) {
                int count = rs.getInt("count");
                System.out.println("[ANIMAUX] " + count + " animaux trouves");
                
                ResultSet examples = stmt.executeQuery("SELECT nom, prix_achat, articles_produits FROM AnimalInfo LIMIT 3");
                while (examples.next()) {
                    System.out.println("   - " + examples.getString("nom") + 
                                     " (Prix: " + examples.getDouble("prix_achat") + 
                                     " gold, Produit: " + examples.getString("articles_produits") + ")");
                }
                examples.close();
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Erreur test AnimalInfo: " + e.getMessage());
        }
    }
    
    private static void testUsineInfo() {
        try (Connection conn = DatabaseManager.get();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM UsineInfo")) {
            
            if (rs.next()) {
                int count = rs.getInt("count");
                System.out.println("[USINES] " + count + " usines trouvees");
                
                ResultSet examples = stmt.executeQuery("SELECT nom, prix_achat, multiplicateur FROM UsineInfo LIMIT 3");
                while (examples.next()) {
                    System.out.println("   - " + examples.getString("nom") + 
                                     " (Prix: " + examples.getDouble("prix_achat") + 
                                     " gold, x" + examples.getDouble("multiplicateur") + ")");
                }
                examples.close();
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Erreur test UsineInfo: " + e.getMessage());
        }
    }
    
    private static void testEquipementInfo() {
        try (Connection conn = DatabaseManager.get();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM EquipementInfo")) {
            
            if (rs.next()) {
                int count = rs.getInt("count");
                System.out.println("[EQUIPEMENTS] " + count + " equipements trouves");
                
                ResultSet examples = stmt.executeQuery("SELECT nom, prix_achat, prix_vente FROM EquipementInfo LIMIT 3");
                while (examples.next()) {
                    System.out.println("   - " + examples.getString("nom") + 
                                     " (Achat: " + examples.getDouble("prix_achat") + 
                                     " gold, Vente: " + examples.getDouble("prix_vente") + " gold)");
                }
                examples.close();
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Erreur test EquipementInfo: " + e.getMessage());
        }
    }
    
    private static void testArticleInfo() {
        try (Connection conn = DatabaseManager.get();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM ArticleInfo")) {
            
            if (rs.next()) {
                int count = rs.getInt("count");
                System.out.println("[ARTICLES] " + count + " articles trouves");
                
                ResultSet examples = stmt.executeQuery("SELECT nom, categorie, prix_vente FROM ArticleInfo LIMIT 5");
                while (examples.next()) {
                    System.out.println("   - " + examples.getString("nom") + 
                                     " (" + examples.getString("categorie") + 
                                     ", " + examples.getDouble("prix_vente") + " gold/L)");
                }
                examples.close();
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Erreur test ArticleInfo: " + e.getMessage());
        }
    }
}