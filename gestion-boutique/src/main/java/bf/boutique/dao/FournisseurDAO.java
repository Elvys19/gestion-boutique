
package bf.boutique.dao;

import bf.boutique.model.Fournisseur;
import bf.boutique.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FournisseurDAO {

    public boolean ajouter(Fournisseur fournisseur) {
        String sql = """
                INSERT INTO fournisseurs (nom, telephone, adresse)
                VALUES (?, ?, ?)
                """;

        try (Connection connexion = DatabaseConnection.getConnection();
                PreparedStatement ps = connexion.prepareStatement(sql)) {

            ps.setString(1, fournisseur.getNom());
            ps.setString(2, fournisseur.getTelephone());
            ps.setString(3, fournisseur.getAdresse());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println(
                    "Erreur lors de l'ajout du fournisseur : "
                            + e.getMessage());
            return false;
        }
    }

    public List<Fournisseur> obtenirTous() {
        List<Fournisseur> fournisseurs = new ArrayList<>();

        String sql = """
                SELECT id, nom, telephone, adresse
                FROM fournisseurs
                ORDER BY nom
                """;

        try (Connection connexion = DatabaseConnection.getConnection();
                PreparedStatement ps = connexion.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Fournisseur fournisseur = new Fournisseur(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("telephone"),
                        rs.getString("adresse"));

                fournisseurs.add(fournisseur);
            }

        } catch (SQLException e) {
            System.err.println(
                    "Erreur lors du chargement des fournisseurs : "
                            + e.getMessage());
        }

        return fournisseurs;
    }
}
