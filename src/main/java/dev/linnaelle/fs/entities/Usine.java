package dev.linnaelle.fs.entities;

public class Usine extends StructureProduction {

    public Usine() {
        super();
    }

    public Usine(int fermeId, String type, double prixAchat) {
        super(fermeId, type, prixAchat);
    }

    /**
     * Traite les ressources dans l'usine.
     * @param stock Le stock principal à utiliser.
     * @param entrepot L'entrepôt où stocker les produits.
     * @return true si le traitement a réussi, false sinon.
     */
    public boolean traiter(StockPrincipal stock, Entrepot entrepot) {
        if (enPause || !active) {
            return false;
        }

        // TODO: Implémenter la logique de traitement selon le type d'usine
        
        return true;
    }

    /**
     * Produit un article spécifique selon le type d'usine.
     * @param tempsCourant Le temps courant pour la production.
     * @param reservoir Le réservoir d'eau à utiliser pour la production.
     * @param stock Le stockage où stocker le produit fini.
     */
    @Override
    public void produire(long tempsCourant, ReservoirEau reservoir, Stockage stock) {
        if (!active || enPause) {
            return;
        }
        
        // TODO: Implémenter selon le type d'usine
        // - Moulin à huile: tournesol -> huile
        // - Boulangerie: sucre + farine -> gâteau
        // etc.
    }

    @Override
    public String toString() {
        return "Usine{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", active=" + active +
                ", enPause=" + enPause +
                ", fermeId=" + fermeId +
                '}';
    }
}