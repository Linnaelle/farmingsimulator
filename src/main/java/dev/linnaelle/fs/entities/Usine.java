package dev.linnaelle.fs.entities;

import java.util.Map;
import java.util.HashMap;
import dev.linnaelle.fs.services.CatalogueService;
import dev.linnaelle.fs.dao.CatalogueDao;
import dev.linnaelle.fs.dao.StockageDao;

public class Usine extends StructureProduction {

    public Usine() {
        super();
    }

    public Usine(int fermeId, String type, double prixAchat) {
        super(fermeId, type, prixAchat);
    }

    /**
     * Traite les ressources dans l'usine.
     * @param stock Le stock principal à utiliser.
     * @param entrepot L'entrepôt où stocker les produits.
     * @return true si le traitement a réussi, false sinon.
     */
    public boolean traiter(StockPrincipal stock, Entrepot entrepot) {
        if (enPause || !active) {
            return false;
        }

        UsineInfo info = CatalogueService.getInstance().getUsineInfo(this.type);
        
        if (info == null) {
            return false; 
        }

        Map<String, Integer> intrantsRequis = CatalogueService.getInstance().getUsineIntrants(this.type);
        for (Map.Entry<String, Integer> intrant : intrantsRequis.entrySet()) {
            String ressource = intrant.getKey();
            int quantite = intrant.getValue();

            if (stock.getQuantite(ressource) < quantite) {
                pauseAutomatique();
                return false; 
            }
        }

        String articleProduit = info.getArticleProduit();
        int quantiteBase = intrantsRequis.values().stream()
                                        .mapToInt(Integer::intValue)
                                        .sum();
        int quantiteProduite = (int) (quantiteBase * info.getMultiplicateur());

        if (!entrepot.peutStocker(articleProduit) || entrepot.capaciteLibre() < quantiteProduite) {
            pauseAutomatique();
            return false;
        }

        for (Map.Entry<String, Integer> intrant : intrantsRequis.entrySet()) {
            stock.retirer(intrant.getKey(), intrant.getValue());
        }

        entrepot.ajouter(articleProduit, quantiteProduite);
        System.out.println("Production de " + quantiteProduite + " " + articleProduit + " dans l'usine " + this.type);
        this.active = true;
        this.enPause = false;
        
        return true;
    }

    /**
     * Produit un article spécifique selon le type d'usine.
     * @param tempsCourant Le temps courant pour la production.
     * @param reservoir Le réservoir d'eau à utiliser pour la production.
     * @param stock Le stockage où stocker le produit fini.
     */
    @Override
    public void produire(long tempsCourant, ReservoirEau reservoir, Stockage stock) {
        if (!active || enPause) { return; }
        
        if (!(stock instanceof StockPrincipal)) { return; }
        
        StockPrincipal stockPrincipal = (StockPrincipal) stock;
        
        StockageDao stockageDao = new StockageDao();
        Entrepot entrepot = stockageDao.findEntrepotByFermeId(this.fermeId);
        
        if (entrepot == null) {
            pauseAutomatique();
            return;
        }
        
        long intervalleTraitement = 120000L * (100 / Math.max(tauxTraitement, 1));
        
        long cycleId = tempsCourant / intervalleTraitement;
        long offsetUsine = this.getId() % 10;
        
        if ((cycleId + offsetUsine) % Math.max(1, intervalleTraitement / 10000) == 0) {
            CatalogueDao catalogueDao = new CatalogueDao();
            UsineInfo info = catalogueDao.getUsineInfo(this.type);
            
            if (info == null) {
                System.err.println("[ERREUR] Informations introuvables pour l'usine: " + this.type);
                pauseAutomatique();
                return;
            }
            
            Map<String, Integer> intrantsRequis = parseIntrants(info.getIntrantsRequis());
            
            if (intrantsRequis.isEmpty()) {
                System.err.println("[ERREUR] Aucun intrant défini pour: " + this.type);
                pauseAutomatique();
                return;
            }
            
            for (Map.Entry<String, Integer> intrant : intrantsRequis.entrySet()) {
                String article = intrant.getKey();
                int quantiteRequise = intrant.getValue();
                
                if (stockPrincipal.getQuantite(article) < quantiteRequise) {
                    System.out.println("[PAUSE] " + this.type + " - Pas assez de " + article + 
                                    " (requis: " + quantiteRequise + "L, disponible: " + 
                                    stockPrincipal.getQuantite(article) + "L)");
                    pauseAutomatique();
                    return;
                }
            }
            
            String articleProduit = info.getArticleProduit();
            int quantiteBase = intrantsRequis.values().stream().findFirst().orElse(0);
            int quantiteProduite = (int) (quantiteBase * info.getMultiplicateur());
            
            if (!entrepot.peutStocker(articleProduit)) {
                System.err.println("[ERREUR] L'entrepôt ne peut pas stocker: " + articleProduit);
                pauseAutomatique();
                return;
            }
            
            if (entrepot.capaciteLibre() < quantiteProduite) {
                System.out.println("[PAUSE] " + this.type + " - Entrepôt plein (requis: " + 
                                quantiteProduite + "L, libre: " + entrepot.capaciteLibre() + "L)");
                pauseAutomatique();
                return;
            }
            
            boolean consommationReussie = true;
            for (Map.Entry<String, Integer> intrant : intrantsRequis.entrySet()) {
                String article = intrant.getKey();
                int quantite = intrant.getValue();
                
                if (!stockPrincipal.retirer(article, quantite)) {
                    System.err.println("[ERREUR] Impossible de retirer " + quantite + "L de " + article);
                    consommationReussie = false;
                    break;
                }
            }
            
            if (consommationReussie) {
                if (entrepot.ajouter(articleProduit, quantiteProduite)) {
                    System.out.println("[PRODUCTION] " + this.type + " a produit " + 
                                    quantiteProduite + "L de " + articleProduit);
                    try {
                        stockageDao.updateArticles(stockPrincipal.getId(), stockPrincipal.getArticles());
                        stockageDao.updateArticles(entrepot.getId(), entrepot.getArticles());
                    } catch (Exception e) {
                        System.err.println("[ERREUR] Sauvegarde impossible: " + e.getMessage());
                    }
                } else {
                    System.err.println("[ERREUR] Impossible d'ajouter " + quantiteProduite + 
                                    "L de " + articleProduit + " à l'entrepôt");
                }
            }
        }
    }

    /**
     * Parse les intrants requis pour l'usine à partir d'une chaîne de caractères.
     * @param intrantsRequis
     * @return
     */
    private Map<String, Integer> parseIntrants(String intrantsRequis) {
        Map<String, Integer> intrants = new HashMap<>();
        
        if (intrantsRequis != null && !intrantsRequis.trim().isEmpty()) {
            String[] pairs = intrantsRequis.split(",");
            for (String pair : pairs) {
                String[] parts = pair.split(":");
                if (parts.length == 2) {
                    try {
                        String article = parts[0].trim();
                        int quantite = Integer.parseInt(parts[1].trim());
                        intrants.put(article, quantite);
                    } catch (NumberFormatException e) {
                        System.err.println("[ERROR] Format intrants invalide: " + pair);
                    }
                }
            }
        }
        
        return intrants;
    }

    @Override
    public String toString() {
        return "Usine{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", active=" + active +
                ", enPause=" + enPause +
                ", fermeId=" + fermeId +
                '}';
    }
}