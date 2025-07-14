package dev.linnaelle.fs.entities;

import dev.linnaelle.fs.services.CatalogueService;

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
        ArticleInfo info = CatalogueService.getInstance().getArticleInfo(article);

        if (info != null) {
            return true;
        } else {
            System.err.println("Article inconnu: " + article);
            return false;
        }
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