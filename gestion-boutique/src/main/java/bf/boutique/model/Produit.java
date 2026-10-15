
package bf.boutique.model;

public class Produit {

    private int id;
    private String nom;
    private double prixAchat;
    private double prixVente;
    private int stock;
    private int stockMinimum;
    private Integer categorieId;

    public Produit() {
    }

    public Produit(String nom, double prixAchat,
            double prixVente, int stock,
            int stockMinimum, Integer categorieId) {
        this.nom = nom;
        this.prixAchat = prixAchat;
        this.prixVente = prixVente;
        this.stock = stock;
        this.stockMinimum = stockMinimum;
        this.categorieId = categorieId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getStockMinimum() {
        return stockMinimum;
    }

    public void setStockMinimum(int stockMinimum) {
        this.stockMinimum = stockMinimum;
    }

    public Integer getCategorieId() {
        return categorieId;
    }

    public void setCategorieId(Integer categorieId) {
        this.categorieId = categorieId;
    }
}
