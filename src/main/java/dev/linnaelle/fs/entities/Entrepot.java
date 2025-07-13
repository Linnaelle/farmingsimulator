package dev.linnaelle.fs.entities;

public class Entrepot extends Stockage {

    public Entrepot() {
        super();
    }

    public Entrepot(int fermeId) {
        super(fermeId, 50000);
    }

    /**
     * Vérifie si l'article peut être stocké dans l'entrepôt.
     * @param article Le nom de l'article à stocker.
     * @return true si l'article peut être stocké, false sinon.
     */
    @Override
    public boolean peutStocker(String article) {
        // TODO: Vérifier via CatalogueDAO si l'article est un produit fini
        // Pour l'instant, on accepte les articles qui contiennent certains mots-clés
        String[] produitsFinis = {"gateau", "chips", "vin", "vetements", "wagons", "jouets"};
        
        for (String produitFini : produitsFinis) {
            if (article.toLowerCase().contains(produitFini)) {
                return true;
            }
        }
        
        return false;
    }

    @Override
    public String toString() {
        return "Entrepot{" +
                "id=" + id +
                ", fermeId=" + fermeId +
                ", capaciteMax=" + capaciteMax +
                ", capaciteLibre=" + capaciteLibre() +
                '}';
    }
}