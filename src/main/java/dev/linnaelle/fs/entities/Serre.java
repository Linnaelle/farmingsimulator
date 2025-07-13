package dev.linnaelle.fs.entities;

public class Serre extends StructureProduction {
    private long dernierRecolte;

    public Serre() {
        super();
        this.dernierRecolte = 0;
    }

    public Serre(int fermeId, String type, double prixAchat) {
        super(fermeId, type, prixAchat);
        this.dernierRecolte = System.currentTimeMillis();
    }

    /**
     * La serre produit des fraises toutes les 5 minutes.
     * @param tempsCourant Le temps courant en millisecondes.
     * @param reservoir Le réservoir d'eau à utiliser.
     * @param stock Le stockage où les fraises seront ajoutées.
     * @return void
     */
    @Override
    public void produire(long tempsCourant, ReservoirEau reservoir, Stockage stock) {
        if (!active || enPause) {
            return;
        }

        if (!consommer(reservoir, "eau", 15)) {
            pauseAutomatique();
            return;
        }

        if (tempsCourant - dernierRecolte >= 300000) {
            if (stock.capaciteLibre() >= 1500) {
                stock.ajouter("fraises", 1500);
                dernierRecolte = tempsCourant;
            } else {
                pauseAutomatique();
            }
        }
    }

    public long getDernierRecolte() {
        return dernierRecolte;
    }

    public void setDernierRecolte(long dernierRecolte) {
        this.dernierRecolte = dernierRecolte;
    }

    @Override
    public String toString() {
        return "Serre{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", active=" + active +
                ", dernierRecolte=" + dernierRecolte +
                '}';
    }
}