package dev.linnaelle.fs.entities;

public class Joueur {
    private int id;
    private String name;
    private long tempsJeu;
    private Difficulte difficulte;

    public Joueur() {
        this.name = "Joueur";
        this.tempsJeu = 0;
        this.difficulte = null;
    }

    public Joueur(String name, Difficulte difficulte) {
        this();
        this.name = name;
        this.difficulte = difficulte;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getTempsJeu() {
        return tempsJeu;
    }

    public Difficulte getDifficulte() {
        return difficulte;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTempsJeu(long tempsJeu) {
        this.tempsJeu = tempsJeu;
    }

    public void setDifficulte(Difficulte difficulte) {
        this.difficulte = difficulte;
    }

    // Méthode utilitaire
    @Override
    public String toString() {
        return "Joueur{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", tempsJeu=" + tempsJeu +
                ", difficulte=" + (difficulte != null ? difficulte.getNom() : "null") +
                '}';
    }
}