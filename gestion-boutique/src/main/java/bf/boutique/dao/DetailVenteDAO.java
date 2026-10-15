
package bf.boutique.dao;

import bf.boutique.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DetailVenteDAO {

    public List<Object[]> obtenirDetails(int venteId) {
        List<Object[]> details = new ArrayList<>();

        String sql = """
                SELECT p.nom, lv.quantite, lv.prix_unitaire, lv.sous_total
                FROM ligne_vente lv
                JOIN produits p ON p.id = lv.produit_id
                WHERE lv.vente_id = ?
                ORDER BY lv.id
                """;

        try (Connection connexion = DatabaseConnection.getConnection();
                PreparedStatement ps = connexion.prepareStatement(sql)) {

            ps.setInt(1, venteId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    details.add(new Object[] {
                            rs.getString("nom"),
                            rs.getInt("quantite"),
                            rs.getBigDecimal("prix_unitaire"),
                            rs.getBigDecimal("sous_total")
                    });
                }
            }

        } catch (SQLException e) {
            System.err.println(
                    "Erreur lors du chargement du détail : " + e.getMessage());
        }

        return details;
    }
}
