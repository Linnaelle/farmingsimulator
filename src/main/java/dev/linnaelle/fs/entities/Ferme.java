package dev.linnaelle.fs.entities;

import java.util.List;
import java.util.ArrayList;

public class Ferme {
    private int id;
    private int joueurId;
    private String name;
    private double revenu;
    
    private StockPrincipal stockPrincipal;
    private Entrepot entrepot;
    private ReservoirEau reservoirEau;
    private GestionnaireEquipement equipements;
    private List<Champ> champs;
    private List<StructureProduction> structures;

    public Ferme() {
        this.name = "Ferme";
        this.champs = new ArrayList<>();
        this.structures = new ArrayList<>();
    }

    public Ferme(String name, int joueurId) {
        this();
        this.name = name;
        this.joueurId = joueurId;
        this.revenu = 0.0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getJoueurId() {
        return joueurId;
    }

    public void setJoueurId(int joueurId) {
        this.joueurId = joueurId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRevenu() {
        return revenu;
    }

    public void setRevenu(double revenu) {
        this.revenu = revenu;
    }

    public StockPrincipal getStockPrincipal() {
        return stockPrincipal;
    }

    public void setStockPrincipal(StockPrincipal stockPrincipal) {
        this.stockPrincipal = stockPrincipal;
    }

    public Entrepot getEntrepot() {
        return entrepot;
    }

    public void setEntrepot(Entrepot entrepot) {
        this.entrepot = entrepot;
    }

    public ReservoirEau getReservoirEau() {
        return reservoirEau;
    }

    public void setReservoirEau(ReservoirEau reservoirEau) {
        this.reservoirEau = reservoirEau;
    }

    public GestionnaireEquipement getEquipements() {
        return equipements;
    }

    public void setEquipements(GestionnaireEquipement equipements) {
        this.equipements = equipements;
    }

    public List<Champ> getChamps() {
        return champs;
    }

    public void setChamps(List<Champ> champs) {
        this.champs = champs;
    }

    public List<StructureProduction> getStructures() {
        return structures;
    }

    public void setStructures(List<StructureProduction> structures) {
        this.structures = structures;
    }

    @Override
    public String toString() {
        return "Ferme{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", revenu=" + revenu +
                ", joueurId=" + joueurId +
                '}';
    }
}