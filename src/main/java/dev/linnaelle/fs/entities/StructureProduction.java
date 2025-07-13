package dev.linnaelle.fs.entities;

public abstract class StructureProduction {
    protected int id;
    protected int fermeId;
    protected String type;
    protected boolean active;
    protected double prixAchat;
    protected boolean enPause;
    protected int tauxTraitement;

    public StructureProduction() {}

    public StructureProduction(int fermeId, String type, double prixAchat) {
        this.fermeId = fermeId;
        this.type = type;
        this.prixAchat = prixAchat;
        this.active = false;
        this.enPause = false;
        this.tauxTraitement = 100;
    }

    /**
     * Démarre la production.
     * @return true si la production a démarré, false sinon.
     */
    public boolean demarrer() {
        this.active = true;
        this.enPause = false;
        return true;
    }

    /**
     * Arrête la production.
     */
    public void arreter() {
        this.active = false;
    }

    /**
     * Met la production en pause.
     * @return true si la pause a été activée, false sinon.
     */
    public boolean pauseAutomatique() {
        this.enPause = true;
        return true;
    }

    /**
     * Consomme un article du stockage.
     * @param stockage Le stockage à partir duquel consommer.
     * @param article L'article à consommer.
     * @param quantite La quantité à consommer.
     * @return true si la consommation a réussi, false sinon.
     */
    public boolean consommer(Stockage stockage, String article, int quantite) {
        return stockage.retirer(article, quantite);
    }

    /**
     * Produit un article.
     * @param tempsCourant Le temps courant pour la production.
     * @param reservoir Le réservoir d'eau à utiliser pour la production.
     * @param stock Le stockage où stocker le produit fini.
     */
    public abstract void produire(long tempsCourant, ReservoirEau reservoir, Stockage stock);

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFermeId() {
        return fermeId;
    }

    public void setFermeId(int fermeId) {
        this.fermeId = fermeId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public double getPrixAchat() {
        return prixAchat;
    }

    public void setPrixAchat(double prixAchat) {
        this.prixAchat = prixAchat;
    }

    public boolean isEnPause() {
        return enPause;
    }

    public void setEnPause(boolean enPause) {
        this.enPause = enPause;
    }

    public int getTauxTraitement() {
        return tauxTraitement;
    }

    public void setTauxTraitement(int tauxTraitement) {
        this.tauxTraitement = tauxTraitement;
    }
}