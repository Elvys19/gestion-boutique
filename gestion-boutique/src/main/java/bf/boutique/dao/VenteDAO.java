
package bf.boutique.dao;

import bf.boutique.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.math.BigDecimal;
import java.util.Map;

public class VenteDAO {

    public boolean enregistrerVente(
            String nomClient,
            int userId,
            Map<Integer, Integer> produitsVendus) {

        if (produitsVendus == null || produitsVendus.isEmpty()) {
            System.out.println("La vente ne contient aucun produit.");
            return false;
        }

        String sqlVente = """
            INSERT INTO ventes (client_nom, user_id, total)
            VALUES (?, ?, 0)
            """;

        String sqlProduit = """
            SELECT prix_vente, stock
            FROM produits
            WHERE id = ?
            FOR UPDATE
            """;

        String sqlLigne = """
            INSERT INTO ligne_vente
                (vente_id, produit_id, quantite, prix_unitaire, sous_total)
            VALUES (?, ?, ?, ?, ?)
            """;

        String sqlStock = """
            UPDATE produits
            SET stock = stock - ?
            WHERE id = ? AND stock >= ?
            """;

        try (Connection connexion = DatabaseConnection.getConnection()) {
            connexion.setAutoCommit(false);

            try {
                int venteId;

                try (PreparedStatement ps = connexion.prepareStatement(
                        sqlVente, Statement.RETURN_GENERATED_KEYS)) {

                    ps.setString(1, nomClient);

                    if (userId <= 0) {
                        ps.setNull(2, java.sql.Types.INTEGER);
                    } else {
                        ps.setInt(2, userId);
                    }

                    ps.executeUpdate();

                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (!rs.next()) {
                            throw new SQLException(
                                    "Impossible de récupérer l'identifiant de la vente."
                            );
                        }
                        venteId = rs.getInt(1);
                    }
                }

                BigDecimal total = BigDecimal.ZERO;

                for (Map.Entry<Integer, Integer> article
                        : produitsVendus.entrySet()) {

                    int produitId = article.getKey();
                    int quantite = article.getValue();

                    if (quantite <= 0) {
                        throw new SQLException("Quantité invalide.");
                    }

                    BigDecimal prixUnitaire;
                    int stockDisponible;

                    try (PreparedStatement ps =
                            connexion.prepareStatement(sqlProduit)) {

                        ps.setInt(1, produitId);

                        try (ResultSet rs = ps.executeQuery()) {
                            if (!rs.next()) {
                                throw new SQLException(
                                        "Produit introuvable : " + produitId
                                );
                            }

                            prixUnitaire = rs.getBigDecimal("prix_vente");
                            stockDisponible = rs.getInt("stock");
                        }
                    }

                    if (stockDisponible < quantite) {
                        throw new SQLException(
                                "Stock insuffisant pour le produit " + produitId
                        );
                    }

                    BigDecimal sousTotal = prixUnitaire.multiply(
                            BigDecimal.valueOf(quantite)
                    );

                    try (PreparedStatement ps =
                            connexion.prepareStatement(sqlLigne)) {

                        ps.setInt(1, venteId);
                        ps.setInt(2, produitId);
                        ps.setInt(3, quantite);
                        ps.setBigDecimal(4, prixUnitaire);
                        ps.setBigDecimal(5, sousTotal);
                        ps.executeUpdate();
                    }

                    try (PreparedStatement ps =
                            connexion.prepareStatement(sqlStock)) {

                        ps.setInt(1, quantite);
                        ps.setInt(2, produitId);
                        ps.setInt(3, quantite);

                        if (ps.executeUpdate() != 1) {
                            throw new SQLException(
                                    "La mise à jour du stock a échoué."
                            );
                        }
                    }

                    total = total.add(sousTotal);
                }

                try (PreparedStatement ps = connexion.prepareStatement(
                        "UPDATE ventes SET total = ? WHERE id = ?")) {

                    ps.setBigDecimal(1, total);
                    ps.setInt(2, venteId);
                    ps.executeUpdate();
                }

                connexion.commit();
                System.out.println("Vente enregistrée. Total : " + total);
                return true;

            } catch (SQLException e) {
                connexion.rollback();
                System.err.println(
                        "Vente annulée : " + e.getMessage()
                );
                return false;

            } finally {
                connexion.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.err.println(
                    "Erreur de connexion : " + e.getMessage()
            );
            return false;
        }
    }
}
