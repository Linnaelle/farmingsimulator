package dev.linnaelle.fs.entities;

import java.util.Map;
import java.util.HashMap;

public class Animal {
    private int id;
    private int fermeAnimaleId;
    private String type;
    private int stockHerbe;
    private boolean vivant;
    private boolean deficit;
    private long dernierPrelevementEau;
    private long dernierPrelevementHerbe;

    public Animal() {
        this.stockHerbe = 10;
        this.vivant = true;
        this.deficit = false;
        this.dernierPrelevementEau = System.currentTimeMillis();
        this.dernierPrelevementHerbe = System.currentTimeMillis();
    }

    public Animal(int id, String type, int stockHerbe) {
        this();
        this.fermeAnimaleId = id;
        this.type = type;
        this.stockHerbe = stockHerbe;
    }

    /**
     * Met à jour l'état de l'animal en fonction du temps écoulé et des ressources disponibles.
     * @param tempsCourant Le temps actuel en millisecondes.
     * @param reservoir Le réservoir d'eau de la ferme.
     * @return Une map contenant les articles produits par l'animal et leur quantité.
     */
    public Map<String, Integer> mettreAJour(long tempsCourant, ReservoirEau reservoir) {
        Map<String, Integer> production = new HashMap<>();
        
        if (!vivant) {
            return production;
        }

        // TODO: Récupérer les données de consommation via CatalogueDAO
        int consoEau = getConsoEauParSeconde();
        int consoHerbe = getConsoHerbeParSeconde();
        
        if (tempsCourant - dernierPrelevementEau >= 1000) {
            if (reservoir.consommer(consoEau)) {
                dernierPrelevementEau = tempsCourant;
            } else {
                deficit = true;
                return production;
            }
        }

        if (tempsCourant - dernierPrelevementHerbe >= 1000) {
            if (stockHerbe >= consoHerbe) {
                stockHerbe -= consoHerbe;
                dernierPrelevementHerbe = tempsCourant;
                
                if (stockHerbe <= -5) {
                    vivant = false;
                    return production;
                }
                
                deficit = stockHerbe < 0;
            } else {
                stockHerbe -= consoHerbe;
                deficit = true;
            }
        }

        if (!deficit && vivant) {
            production = getProductionParSeconde();
        }

        return production;
    }

    private int getConsoEauParSeconde() {
        // TODO: Récupérer via CatalogueDAO
        switch (type) {
            case "vache": return 3;
            case "mouton": return 2;
            case "poule": return 1;
            default: return 1;
        }
    }

    private int getConsoHerbeParSeconde() {
        // TODO: Récupérer via CatalogueDAO
        switch (type) {
            case "vache": return 3;
            case "mouton": return 2;
            case "poule": return 1;
            default: return 1;
        }
    }

    /**
     * Récupère la production de l'animal par seconde.
     * @return Une map contenant les articles produits et leur quantité.
     */
    private Map<String, Integer> getProductionParSeconde() {
        Map<String, Integer> production = new HashMap<>();
        
        // TODO: Récupérer via CatalogueDAO
        switch (type) {
            case "vache":
                production.put("lait", 20);
                production.put("fumier", 5);
                break;
            case "mouton":
                production.put("laine", 5);
                production.put("fumier", 5);
                break;
            case "poule":
                production.put("oeufs", 1);
                break;
        }
        
        return production;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFermeAnimaleId() {
        return fermeAnimaleId;
    }

    public void setFermeAnimaleId(int fermeAnimaleId) {
        this.fermeAnimaleId = fermeAnimaleId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getStockHerbe() {
        return stockHerbe;
    }

    public void setStockHerbe(int stockHerbe) {
        this.stockHerbe = stockHerbe;
    }

    public boolean isVivant() {
        return vivant;
    }

    public void setVivant(boolean vivant) {
        this.vivant = vivant;
    }

    public boolean isDeficit() {
        return deficit;
    }

    public void setDeficit(boolean deficit) {
        this.deficit = deficit;
    }

    public long getDernierPrelevementEau() {
        return dernierPrelevementEau;
    }

    public void setDernierPrelevementEau(long dernierPrelevementEau) {
        this.dernierPrelevementEau = dernierPrelevementEau;
    }

    public long getDernierPrelevementHerbe() {
        return dernierPrelevementHerbe;
    }

    public void setDernierPrelevementHerbe(long dernierPrelevementHerbe) {
        this.dernierPrelevementHerbe = dernierPrelevementHerbe;
    }

    @Override
    public String toString() {
        return "Animal{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", stockHerbe=" + stockHerbe +
                ", vivant=" + vivant +
                ", deficit=" + deficit +
                '}';
    }
}