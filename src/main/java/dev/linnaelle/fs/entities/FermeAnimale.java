package dev.linnaelle.fs.entities;

import java.util.List;
import java.util.ArrayList;

public class FermeAnimale {
    private int id;
    private int champId;
    private String typeAnimal;
    private int capaciteMax;
    private List<Animal> animaux;

    public FermeAnimale() {
        this.animaux = new ArrayList<>();
    }

    public FermeAnimale(int champId, String typeAnimal, int capaciteMax) {
        this();
        this.champId = champId;
        this.typeAnimal = typeAnimal;
        this.capaciteMax = capaciteMax;
    }

    /**
     * Ajoute un animal à la ferme.
     * @param animal L'animal à ajouter.
     * @return true si l'animal a été ajouté, false sinon.
     */
    public boolean addAnimal(Animal animal) {
        if (animaux.size() >= capaciteMax) {
            return false; // Capacité maximale atteinte
        }
        
        if (!typeAnimal.equals(animal.getType())) {
            // TODO: Supprimer si l'on souhaite mélanger les types d'animaux
            return false;
        }
        
        animaux.add(animal);
        return true;
    }

    /**
     * Supprime un animal de la ferme.
     * @param animal L'animal à supprimer.
     * @return true si l'animal a été supprimé, false sinon.
     */
    public boolean removeAnimal(Animal animal) {
        return animaux.remove(animal);
    }

    /**
     * Met à jour l'état des animaux dans la ferme.
     * @param tempsCourant Le temps courant en millisecondes.
     */
    public void mettreAJour(long tempsCourant) {
        for (Animal animal : new ArrayList<>(animaux)) {
            if (!animal.isVivant()) {
                animaux.remove(animal);
            }
        }
    }

    public int getNombreAnimaux() {
        return animaux.size();
    }

    public int getId() {
        return id;
    }

    public int getChampId() {
        return champId;
    }

    public void setChampId(int champId) {
        this.champId = champId;
    }

    public String getTypeAnimal() {
        return typeAnimal;
    }

    public void setTypeAnimal(String typeAnimal) {
        this.typeAnimal = typeAnimal;
    }

    public int getCapaciteMax() {
        return capaciteMax;
    }

    public void setCapaciteMax(int capaciteMax) {
        this.capaciteMax = capaciteMax;
    }

    public List<Animal> getAnimaux() {
        return animaux;
    }

    public void setAnimaux(List<Animal> animaux) {
        this.animaux = animaux;
    }

    @Override
    public String toString() {
        return "FermeAnimale{" +
                "id=" + id +
                ", typeAnimal='" + typeAnimal + '\'' +
                ", nbAnimaux=" + capaciteMax +
                '}';
    }
}