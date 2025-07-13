package dev.linnaelle.fs.entities;

public class Equipement {
    private int id;
    private int gestionnaireId;
    private String type;
    private boolean enUtilisation;
    private long finUtilisation;

    public Equipement() {}

    public Equipement(int gestionnaireId, String type) {
        this.gestionnaireId = gestionnaireId;
        this.type = type;
        this.enUtilisation = false;
        this.finUtilisation = 0;
    }

    /**
     * Vérifie si l'équipement est libre.
     * @param tempsCourant Le temps courant en millisecondes.
     * @return true si l'équipement est libre, false sinon.
     */
    public boolean estLibre(long tempsCourant) {
        if (!enUtilisation) {
            return true;
        }
        
        if (tempsCourant >= finUtilisation) {
            enUtilisation = false;
            finUtilisation = 0;
            return true;
        }
        
        return false;
    }

    /**
     * Utilise l'équipement pour une durée donnée.
     * @param duree La durée d'utilisation en millisecondes.
     * @param tempsCourant Le temps courant en millisecondes.
     */
    public void utiliser(long duree, long tempsCourant) {
        this.enUtilisation = true;
        this.finUtilisation = tempsCourant + duree;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGestionnaireId() {
        return gestionnaireId;
    }

    public void setGestionnaireId(int gestionnaireId) {
        this.gestionnaireId = gestionnaireId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isEnUtilisation() {
        return enUtilisation;
    }

    public void setEnUtilisation(boolean enUtilisation) {
        this.enUtilisation = enUtilisation;
    }

    public long getFinUtilisation() {
        return finUtilisation;
    }

    public void setFinUtilisation(long finUtilisation) {
        this.finUtilisation = finUtilisation;
    }

    @Override
    public String toString() {
        return "Equipement{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", enUtilisation=" + enUtilisation +
                ", finUtilisation=" + finUtilisation +
                '}';
    }
}