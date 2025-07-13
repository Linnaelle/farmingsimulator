package dev.linnaelle.fs.entities;

public class ReservoirEau extends Stockage {
    private int capacite;
    private int quantite;
    private long dernierRemplissage;

    public ReservoirEau() {
        super();     
    }

    public ReservoirEau(int fermeId, int capacite) {
        super(fermeId, capacite);
        this.capacite = capacite;
        this.quantite = capacite;
        this.dernierRemplissage = System.currentTimeMillis();
    }

    /**
     * Vérifie si le réservoir peut stocker un article donné.
     * @param article L'article à vérifier.
     * @return true si le réservoir peut stocker l'article, false sinon.
     */
    @Override
    public boolean peutStocker(String article) {
        return "eau".equals(article);
    }

    /**
     * Consomme une quantité d'eau du réservoir.
     * @param quantiteConsommee La quantité d'eau à consommer.
     * @return true si la consommation a réussi, false si la quantité demandée est supérieure à la quantité disponible.
     */
    public boolean consommer(int quantiteConsommee) {
        if (this.quantite >= quantiteConsommee) {
            this.quantite -= quantiteConsommee;
            return true;
        }
        return false;
    }

    /**
     * Remplit le réservoir d'eau à sa capacité maximale si le temps écoulé depuis le dernier remplissage est supérieur à 5 minutes.
     * @param tempsCourant Le temps actuel en millisecondes.
     */
    public void remplir(long tempsCourant) {
        if (tempsCourant - dernierRemplissage >= 300000) {
            this.quantite = this.capacite;
            this.dernierRemplissage = tempsCourant;
        }
    }

    public int getCapacite() {
        return capacite;
    }

    public void setCapacite(int capacite) {
        this.capacite = capacite;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public long getDernierRemplissage() {
        return dernierRemplissage;
    }

    public void setDernierRemplissage(long dernierRemplissage) {
        this.dernierRemplissage = dernierRemplissage;
    }

    @Override
    public String toString() {
        return "ReservoirEau{" +
                "id=" + id +
                ", fermeId=" + fermeId +
                ", capacite=" + capacite +
                ", quantite=" + quantite +
                ", pourcentage=" + (quantite * 100 / capacite) + "%" +
                '}';
    }
}