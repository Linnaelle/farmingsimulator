package dev.linnaelle.fs.entities;

import java.util.List;

public class CultureInfo {
    private String nom;
    private double prixAchat;
    private double prixVente;
    private int rendement;
    private boolean needLabour;
    private String articleProduit;
    private List<String> equipements;

    public CultureInfo() {}

    public CultureInfo(String nom, double prixAchat, double prixVente, int rendement, boolean needLabour, String articleProduit, List<String> equipements) {
        this.nom = nom;
        this.prixAchat = prixAchat;
        this.prixVente = prixVente;
        this.rendement = rendement;
        this.needLabour = needLabour;
        this.articleProduit = articleProduit;
        this.equipements = equipements;
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

    public int getRendement() {
        return rendement;
    }

    public void setRendement(int rendement) {
        this.rendement = rendement;
    }

    public boolean isNeedLabour() {
        return needLabour;
    }

    public void setNeedLabour(boolean needLabour) {
        this.needLabour = needLabour;
    }

    public String getArticleProduit() {
        return articleProduit;
    }

    public void setArticleProduit(String articleProduit) {
        this.articleProduit = articleProduit;
    }

    public List<String> getEquipements() {
        return equipements;
    }

    public void setEquipements(List<String> equipements) {
        this.equipements = equipements;
    }

    @Override
    public String toString() {
        return "CultureInfo{" +
                "nom='" + nom + '\'' +
                ", prixAchat=" + prixAchat +
                ", rendement=" + rendement +
                ", articleProduit='" + articleProduit + '\'' +
                '}';
    }
}