
package bf.boutique.dao;

import bf.boutique.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HistoriqueLivraisonDAO {

    public List<Object[]> obtenirHistorique() {
        List<Object[]> livraisons = new ArrayList<>();

        String sql = """
                SELECT l.id AS livraison_id,
                       l.date_livraison,
                       f.nom AS fournisseur,
                       p.nom AS produit,
                       ll.quantite,
                       ll.prix_achat
                FROM livraisons l
                JOIN fournisseurs f ON f.id = l.fournisseur_id
                JOIN ligne_livraison ll ON ll.livraison_id = l.id
                JOIN produits p ON p.id = ll.produit_id
                ORDER BY l.date_livraison DESC, l.id DESC
                """;

        try (Connection connexion = DatabaseConnection.getConnection();
                PreparedStatement ps = connexion.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                livraisons.add(new Object[] {
                        rs.getInt("livraison_id"),
                        rs.getTimestamp("date_livraison"),
                        rs.getString("fournisseur"),
                        rs.getString("produit"),
                        rs.getInt("quantite"),
                        rs.getBigDecimal("prix_achat")
                });
            }

        } catch (SQLException e) {
            System.err.println(
                    "Erreur lors du chargement de l'historique des livraisons : "
                            + e.getMessage());
            e.printStackTrace();
        }

        return livraisons;
    }
}
