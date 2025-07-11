package dev.linnaelle.fs.entities;

import java.util.List;

public class AnimalInfo {
    private String nom;
    private double prixAchat;
    private double prixVente;
    private int consoEau;
    private int consoHerbe;
    private int stockHerbe;
    private List<String> articlesProduits;

    public AnimalInfo() {}

    public AnimalInfo(String nom, double prixAchat, double prixVente, int consoEau, 
                     int consoHerbe, int stockHerbe, List<String> articlesProduits) {
        this.nom = nom;
        this.prixAchat = prixAchat;
        this.prixVente = prixVente;
        this.consoEau = consoEau;
        this.consoHerbe = consoHerbe;
        this.stockHerbe = stockHerbe;
        this.articlesProduits = articlesProduits;
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

    public int getConsoEau() {
        return consoEau;
    }

    public void setConsoEau(int consoEau) {
        this.consoEau = consoEau;
    }

    public int getConsoHerbe() {
        return consoHerbe;
    }

    public void setConsoHerbe(int consoHerbe) {
        this.consoHerbe = consoHerbe;
    }

    public int getStockHerbe() {
        return stockHerbe;
    }

    public void setStockHerbe(int stockHerbe) {
        this.stockHerbe = stockHerbe;
    }

    public List<String> getArticlesProduits() {
        return articlesProduits;
    }

    public void setArticlesProduits(List<String> articlesProduits) {
        this.articlesProduits = articlesProduits;
    }

    @Override
    public String toString() {
        return "AnimalInfo{" +
                "nom='" + nom + '\'' +
                ", prixAchat=" + prixAchat +
                ", consoEau=" + consoEau +
                ", articlesProduits=" + articlesProduits +
                '}';
    }
}