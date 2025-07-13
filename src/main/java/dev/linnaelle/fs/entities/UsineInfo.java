package dev.linnaelle.fs.entities;

public class UsineInfo {
    private String nom;
    private double prixAchat;
    private double prixVente;
    private String intrantsRequis;
    private double multiplicateur;
    private String articleProduit;

    public UsineInfo() {}

    public UsineInfo(String nom, double prixAchat, double prixVente, String intrantsRequis, double multiplicateur, String articleProduit) {
        this.nom = nom;
        this.prixAchat = prixAchat;
        this.prixVente = prixVente;
        this.intrantsRequis = intrantsRequis;
        this.multiplicateur = multiplicateur;
        this.articleProduit = articleProduit;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public double getPrixAchat() {
        return prixAchat;
    }

    public void setPrixAchat(double prixAchat) {
        this.prixAchat = prixAchat;
    }

    public double getPrixVente() {
        return prixVente;
    }

    public void setPrixVente(double prixVente) {
        this.prixVente = prixVente;
    }

    public String getIntrantsRequis() {
        return intrantsRequis;
    }

    public void setIntrantsRequis(String intrantsRequis) {
        this.intrantsRequis = intrantsRequis;
    }

    public double getMultiplicateur() {
        return multiplicateur;
    }

    public void setMultiplicateur(double multiplicateur) {
        this.multiplicateur = multiplicateur;
    }

    public String getArticleProduit() {
        return articleProduit;
    }

    public void setArticleProduit(String articleProduit) {
        this.articleProduit = articleProduit;
    }

    @Override
    public String toString() {
        return "UsineInfo{" +
                "nom='" + nom + '\'' +
                ", prixAchat=" + prixAchat +
                ", multiplicateur=" + multiplicateur +
                ", articleProduit='" + articleProduit + '\'' +
                '}';
    }
}