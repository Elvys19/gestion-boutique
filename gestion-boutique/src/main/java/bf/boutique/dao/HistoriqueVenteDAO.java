
package bf.boutique.dao;

import bf.boutique.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HistoriqueVenteDAO {

    public List<Object[]> obtenirHistorique() {
        List<Object[]> ventes = new ArrayList<>();

        String sql = """
                SELECT id, date_vente, client_nom, total
                FROM ventes
                ORDER BY date_vente DESC
                """;

        try (Connection connexion = DatabaseConnection.getConnection();
                PreparedStatement ps = connexion.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Object[] vente = {
                        rs.getInt("id"),
                        rs.getTimestamp("date_vente"),
                        rs.getString("client_nom"),
                        rs.getBigDecimal("total")
                };

                ventes.add(vente);

                System.out.println(
                        "Vente récupérée : ID=" + vente[0]
                                + ", date=" + vente[1]
                                + ", client=" + vente[2]
                                + ", total=" + vente[3]);
            }

            System.out.println(
                    "Nombre total de ventes récupérées : " + ventes.size());

        } catch (SQLException e) {
            System.err.println("Erreur SQL : " + e.getMessage());
            e.printStackTrace();
        }

        return ventes;
    }

}
