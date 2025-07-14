package dev.linnaelle.fs.entities;

import java.util.HashMap;
import java.util.Map;

public abstract class Stockage {
    protected int id;
    protected int fermeId;
    protected int capaciteMax;
    protected Map<String, Integer> articles;

    public Stockage() {
        this.articles = new HashMap<>();
    }

    public Stockage(int fermeId, int capaciteMax) {
        this();
        this.fermeId = fermeId;
        this.capaciteMax = capaciteMax;
    }

    public abstract boolean peutStocker(String article);

    /**
     * Ajoute une quantité d'un article au stockage.
     * @param article L'article à ajouter.
     * @param quantite La quantité à ajouter.
     * @return true si l'ajout a réussi, false sinon (par exemple, si la capacité est dépassée).
     */
    public boolean ajouter(String article, int quantite) {
        if (!peutStocker(article)) {
            return false;
        }
        
        int quantiteActuelle = articles.getOrDefault(article, 0);
        int nouvelleQuantite = quantiteActuelle + quantite;
        
        if (nouvelleQuantite <= capaciteMax) {
            articles.put(article, nouvelleQuantite);
            return true;
        }
        
        return false;
    }

    /**
     * Retire une quantité d'un article du stockage.
     * @param article L'article à retirer.
     * @param quantite La quantité à retirer.
     * @return true si le retrait a réussi, false sinon (par exemple, si la quantité est insuffisante).
     */
    public boolean retirer(String article, int quantite) {
        int quantiteActuelle = articles.getOrDefault(article, 0);
        
        if (quantiteActuelle >= quantite) {
            articles.put(article, quantiteActuelle - quantite);
            return true;
        }
        
        return false;
    }

    /**
     * Retourne la quantité d'un article dans le stockage.
     * @param article L'article dont on veut connaître la quantité.
     * @return La quantité de l'article, ou 0 si l'article n'est pas présent.
     */
    public int getQuantite(String article) {
        return articles.getOrDefault(article, 0);
    }

    /**
     * Calcule la capacité libre du stockage.
     * @return La capacité libre en fonction des articles stockés.
     */
    public int capaciteLibre() {
        int totalUtilise = articles.values().stream().mapToInt(Integer::intValue).sum();
        return capaciteMax - totalUtilise;
    }

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

    public int getCapaciteMax() {
        return capaciteMax;
    }

    public void setCapaciteMax(int capaciteMax) {
        this.capaciteMax = capaciteMax;
    }

    public Map<String, Integer> getArticles() {
        return articles;
    }

    public void setArticles(Map<String, Integer> articles) {
        this.articles = articles;
    }
}