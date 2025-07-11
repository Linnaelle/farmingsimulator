package dev.linnaelle.fs.entities;

public class EquipementInfo {
    private String nom;
    private double prixAchat;
    private double prixVente;
    private String type;

    public EquipementInfo() {}

    public EquipementInfo(String nom, double prixAchat, double prixVente, String type) {
        this.nom = nom;
        this.prixAchat = prixAchat;
        this.prixVente = prixVente;
        this.type = type;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "EquipementInfo{" +
                "nom='" + nom + '\'' +
                ", prixAchat=" + prixAchat +
                ", prixVente=" + prixVente +
                ", type='" + type + '\'' +
                '}';
    }
}