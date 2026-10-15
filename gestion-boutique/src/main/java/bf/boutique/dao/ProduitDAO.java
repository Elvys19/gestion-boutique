
package bf.boutique.dao;

import bf.boutique.model.Produit;
import bf.boutique.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProduitDAO {

    // Enregistrer un nouveau produit
    public boolean ajouter(Produit produit) {
        String sql = """
                INSERT INTO produits
                    (nom, prix_achat, prix_vente, stock,
                     stock_minimum, categorie_id)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connexion = DatabaseConnection.getConnection();
                PreparedStatement ps = connexion.prepareStatement(sql)) {

            ps.setString(1, produit.getNom());
            ps.setDouble(2, produit.getPrixAchat());
            ps.setDouble(3, produit.getPrixVente());
            ps.setInt(4, produit.getStock());
            ps.setInt(5, produit.getStockMinimum());

            if (produit.getCategorieId() == null) {
                ps.setNull(6, java.sql.Types.INTEGER);
            } else {
                ps.setInt(6, produit.getCategorieId());
            }

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout : " + e.getMessage());
            return false;
        }
    }

    // Rechercher des produits par leur nom
    public List<Produit> rechercherParNom(String nomRecherche) {
        List<Produit> produits = new ArrayList<>();

        String sql = """
                SELECT id, nom, prix_achat, prix_vente,
                       stock, stock_minimum, categorie_id
                FROM produits
                WHERE nom ILIKE ?
                ORDER BY nom
                """;

        try (Connection connexion = DatabaseConnection.getConnection();
                PreparedStatement ps = connexion.prepareStatement(sql)) {

            ps.setString(1, "%" + nomRecherche + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Produit produit = new Produit();

                    produit.setId(rs.getInt("id"));
                    produit.setNom(rs.getString("nom"));
                    produit.setPrixAchat(rs.getDouble("prix_achat"));
                    produit.setPrixVente(rs.getDouble("prix_vente"));
                    produit.setStock(rs.getInt("stock"));
                    produit.setStockMinimum(rs.getInt("stock_minimum"));

                    int categorieId = rs.getInt("categorie_id");
                    if (rs.wasNull()) {
                        produit.setCategorieId(null);
                    } else {
                        produit.setCategorieId(categorieId);
                    }

                    produits.add(produit);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche : " + e.getMessage());
        }

        return produits;
    }
}
