package dev.linnaelle.fs.entities;

public class Champ {
    private int id;
    private int fermeId;
    private String name;
    private int numero;
    private String typeCulture;
    private long tempsAction;
    private EtatChamp etat;
    private double prixAchat;
    private FermeAnimale fermeAnimale;

    public Champ() {
        this.etat = EtatChamp.STANDBY;
        this.tempsAction = 0;
        this.prixAchat = 0.0;
    }

    public Champ(int fermeId, int numero) {
        this();
        this.fermeId = fermeId;
        this.numero = numero;
        this.name = "Champ " + numero;
    }

    /**
     * Vérifie si le champ peut être récolté.
     * @param tempsCourant Le temps courant en millisecondes.
     * @return true si le champ peut être récolté, false sinon.
     */
    public boolean peutRecolter(long tempsCourant) {
        if (etat != EtatChamp.READY) {
            return false;
        }
        
        // Vérifier si assez de temps s'est écoulé (2 minutes = 120 000 ms)
        return tempsCourant >= (tempsAction + 120000);
    }

    /**
     * Laboure le champ.
     * @param tempsCourant Le temps courant en millisecondes.
     * @return true si le labourage a réussi, false sinon.
     */
    public boolean labourer(long tempsCourant) {
        if (etat != EtatChamp.STANDBY) {
            return false;
        }
        
        // Action prend 30 secondes
        this.tempsAction = tempsCourant + 30000;
        this.etat = EtatChamp.LABOURE;
        return true;
    }

    /**
     * Sème une culture dans le champ.
     * @param culture Le type de culture à semer.
     * @param tempsCourant Le temps courant en millisecondes.
     * @return true si la semence a réussi, false sinon.
     */
    public boolean semer(String culture, long tempsCourant) {
        if (etat != EtatChamp.LABOURE) {
            return false;
        }
        
        this.typeCulture = culture;
        this.tempsAction = tempsCourant + 30000;
        this.etat = EtatChamp.SEME;
        return true;
    }

    /**
     * Fertilise le champ.
     * @param tempsCourant Le temps courant en millisecondes.
     * @return true si la fertilisation a réussi, false sinon.
     */
    public boolean fertiliser(long tempsCourant) {
        if (etat != EtatChamp.SEME) {
            return false;
        }
        
        this.tempsAction = tempsCourant + 30000;
        this.etat = EtatChamp.FERTILISE;
        return true;
    }

    /**
     * Récolte le champ si possible.
     * @param tempsCourant Le temps courant en millisecondes.
     * @return Le rendement de la récolte, ou 0 si le champ ne peut pas être récolté.
     */
    public int recolter(long tempsCourant) {
        if (!peutRecolter(tempsCourant)) {
            return 0;
        }
        
        // TODO: Calculer le rendement via CatalogueDAO
        // Rendement de base + bonus fertilisation (50%)
        int rendementBase = 1000; // Par défaut, à récupérer du catalogue
        int bonus = (etat == EtatChamp.FERTILISE) ? (int)(rendementBase * 0.5) : 0;
        
        // Remettre le champ en standby
        this.etat = EtatChamp.STANDBY;
        this.typeCulture = null;
        this.tempsAction = tempsCourant;
        
        return rendementBase + bonus;
    }

    // Getters et Setters
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getTypeCulture() {
        return typeCulture;
    }

    public void setTypeCulture(String typeCulture) {
        this.typeCulture = typeCulture;
    }

    public long getTempsAction() {
        return tempsAction;
    }

    public void setTempsAction(long tempsAction) {
        this.tempsAction = tempsAction;
    }

    public EtatChamp getEtat() {
        return etat;
    }

    public void setEtat(EtatChamp etat) {
        this.etat = etat;
    }

    public double getPrixAchat() {
        return prixAchat;
    }

    public void setPrixAchat(double prixAchat) {
        this.prixAchat = prixAchat;
    }

    public FermeAnimale getFermeAnimale() {
        return fermeAnimale;
    }

    public void setFermeAnimale(FermeAnimale fermeAnimale) {
        this.fermeAnimale = fermeAnimale;
    }

    @Override
    public String toString() {
        return "Champ{" +
                "numero=" + numero +
                ", etat=" + etat +
                ", typeCulture='" + typeCulture + '\'' +
                '}';
    }
}