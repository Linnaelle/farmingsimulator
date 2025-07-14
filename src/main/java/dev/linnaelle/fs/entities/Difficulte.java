package dev.linnaelle.fs.entities;

public class Difficulte {

    private String nom;
    private double goldDepart;
    private double multiplicateurAchat;
    private double multiplicateurVente;

    public Difficulte() {}

    public Difficulte(String nom, double goldDepart, double multiplicateurAchat, double multiplicateurVente) {
        this.nom = nom;
        this.goldDepart = goldDepart;
        this.multiplicateurAchat = multiplicateurAchat;
        this.multiplicateurVente = multiplicateurVente;
    }

    
    public String getNom() {
        return nom;
    }

    public double getGoldDepart() {
        return goldDepart;
    }

    public double getMultiplicateurAchat() {
        return multiplicateurAchat;
    }

    public double getMultiplicateurVente() {
        return multiplicateurVente;
    }

    
    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setGoldDepart(double goldDepart) {
        this.goldDepart = goldDepart;
    }

    public void setMultiplicateurAchat(double multiplicateurAchat) {
        this.multiplicateurAchat = multiplicateurAchat;
    }

    public void setMultiplicateurVente(double multiplicateurVente) {
        this.multiplicateurVente = multiplicateurVente;
    }

    @Override
    public String toString() {
        return "Difficulte{" +
                "nom='" + nom + '\'' +
                ", goldDepart=" + goldDepart +
                ", multiplicateurPrixAchat=" + multiplicateurAchat +
                ", multiplicateurPrixVente=" + multiplicateurVente +
                '}';
    }
}