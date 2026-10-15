
package bf.boutique.dao;

import bf.boutique.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Map;

public class LivraisonDAO {

    /**
     * Enregistre une livraison composée de plusieurs produits.
     * La Map contient : identifiant du produit -> quantité.
     * Le prix d'achat est transmis dans une deuxième Map.
     */
    public boolean enregistrerLivraison(
            int fournisseurId,
            int userId,
            Map<Integer, Integer> produitsLivres,
            Map<Integer, BigDecimal> prixAchats) {

        if (produitsLivres == null || produitsLivres.isEmpty()) {
            System.err.println("La livraison ne contient aucun produit.");
            return false;
        }

        if (prixAchats == null) {
            return false;
        }

        String sqlLivraison = """
                INSERT INTO livraisons (fournisseur_id, user_id)
                VALUES (?, ?)
                RETURNING id
                """;

        String sqlLigne = """
                INSERT INTO ligne_livraison
                    (livraison_id, produit_id, quantite, prix_achat)
                VALUES (?, ?, ?, ?)
                """;

        String sqlStock = """
                UPDATE produits
                SET stock = stock + ?,
                    prix_achat = ?
                WHERE id = ?
                """;

        try (Connection connexion = DatabaseConnection.getConnection()) {

            connexion.setAutoCommit(false);

            try {
                int livraisonId;

                try (PreparedStatement ps = connexion.prepareStatement(sqlLivraison)) {
                    ps.setInt(1, fournisseurId);

                    if (userId > 0) {
                        ps.setInt(2, userId);
                    } else {
                        ps.setNull(2, Types.INTEGER);
                    }

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException(
                                    "Impossible de créer la livraison.");
                        }
                        livraisonId = rs.getInt("id");
                    }
                }

                for (Map.Entry<Integer, Integer> entree : produitsLivres.entrySet()) {

                    int produitId = entree.getKey();
                    int quantite = entree.getValue();
                    BigDecimal prixAchat = prixAchats.get(produitId);

                    if (quantite <= 0 || prixAchat == null
                            || prixAchat.compareTo(BigDecimal.ZERO) < 0) {
                        throw new SQLException(
                                "Quantité ou prix d'achat invalide.");
                    }

                    try (PreparedStatement ps = connexion.prepareStatement(sqlLigne)) {
                        ps.setInt(1, livraisonId);
                        ps.setInt(2, produitId);
                        ps.setInt(3, quantite);
                        ps.setBigDecimal(4, prixAchat);
                        ps.executeUpdate();
                    }

                    try (PreparedStatement ps = connexion.prepareStatement(sqlStock)) {
                        ps.setInt(1, quantite);
                        ps.setBigDecimal(2, prixAchat);
                        ps.setInt(3, produitId);

                        if (ps.executeUpdate() != 1) {
                            throw new SQLException(
                                    "Produit introuvable : " + produitId);
                        }
                    }
                }

                connexion.commit();
                return true;

            } catch (SQLException | RuntimeException e) {
                connexion.rollback();
                System.err.println(
                        "Livraison annulée : " + e.getMessage());
                return false;

            } finally {
                connexion.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.err.println(
                    "Erreur de connexion ou de transaction : "
                            + e.getMessage());
            return false;
        }
    }
}
