package dev.linnaelle.fs.entities;

public class ArticleInfo {
    private String nom;
    private String categorie;
    private double prixVente;

    public ArticleInfo() {}

    public ArticleInfo(String nom, String categorie, double prixVente) {
        this.nom = nom;
        this.categorie = categorie;
        this.prixVente = prixVente;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public double getPrixVente() {
        return prixVente;
    }

    public void setPrixVente(double prixVente) {
        this.prixVente = prixVente;
    }

    @Override
    public String toString() {
        return "ArticleInfo{" +
                "nom='" + nom + '\'' +
                ", categorie='" + categorie + '\'' +
                ", prixVente=" + prixVente +
                '}';
    }
}