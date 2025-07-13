package dev.linnaelle.fs.entities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestionnaireEquipement {
    private int id;
    private int fermeId;
    private Map<String, List<Equipement>> inventaire;

    public GestionnaireEquipement() {
        this.inventaire = new HashMap<>();
    }

    public GestionnaireEquipement(int fermeId) {
        this();
        this.fermeId = fermeId;
    }

    /**
     * Vérifie si un équipement du type spécifié est disponible.
     * @param type Le type d'équipement à vérifier.
     * @return true si un équipement est disponible, false sinon.
     */
    public boolean estDisponible(String type) {
        List<Equipement> equipements = inventaire.get(type);
        if (equipements == null || equipements.isEmpty()) {
            return false;
        }
        
        return equipements.stream()
                .anyMatch(equip -> equip.estLibre(System.currentTimeMillis()));
    }

    /**
     * Réserve un équipement du type spécifié pour une durée donnée.
     * @param type Le type d'équipement à réserver.
     * @param duree La durée de réservation en millisecondes.
     * @return L'équipement réservé, ou null si aucun équipement n'est disponible.
     */
    public Equipement reserver(String type, long duree) {
        List<Equipement> equipements = inventaire.get(type);
        if (equipements == null) {
            return null;
        }
        
        long tempsCourant = System.currentTimeMillis();
        for (Equipement equipement : equipements) {
            if (equipement.estLibre(tempsCourant)) {
                equipement.utiliser(duree, tempsCourant);
                return equipement;
            }
        }
        
        return null;
    }

    /**
     * Libère l'équipement.
     * @param equipement L'équipement à libérer.
     */
    public void liberer(Equipement equipement) {
        equipement.setEnUtilisation(false);
        equipement.setFinUtilisation(0);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFermeId() {
        return fermeId;
    }

    public void setFermeId(int fermeId) {
        this.fermeId = fermeId;
    }

    public Map<String, List<Equipement>> getInventaire() {
        return inventaire;
    }

    public void setInventaire(Map<String, List<Equipement>> inventaire) {
        this.inventaire = inventaire;
    }

    @Override
    public String toString() {
        return "GestionnaireEquipement{" +
                "id=" + id +
                ", fermeId=" + fermeId +
                ", nbEquipements=" + (inventaire != null ? inventaire.size() : 0) +
                '}';
    }
}