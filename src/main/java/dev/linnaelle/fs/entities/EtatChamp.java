package dev.linnaelle.fs.entities;

public enum EtatChamp {
    STANDBY("En attente"),
    LABOURE("Labouré"),
    SEME("Semé"),
    FERTILISE("Fertilisé"),
    READY("Prêt à récolter");

    private final String description;

    EtatChamp(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}