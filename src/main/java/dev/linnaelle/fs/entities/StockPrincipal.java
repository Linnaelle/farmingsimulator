package dev.linnaelle.fs.entities;

import java.util.HashMap;

public class StockPrincipal extends Stockage {

    public StockPrincipal() {
        super();
        if (this.articles == null) {
            this.articles = new HashMap<>(); 
        }
    }

    public StockPrincipal(int fermeId) {
        super(fermeId, 100000);
        if (this.articles == null) {
            this.articles = new HashMap<>(); 
        }
    }

    @Override
    public boolean peutStocker(String article) {
        return true;
    }

    @Override
    public String toString() {
        return "StockPrincipal{" +
                "id=" + id +
                ", fermeId=" + fermeId +
                ", capaciteMax=" + capaciteMax +
                ", capaciteLibre=" + capaciteLibre() +
                '}';
    }
}