
package bf.boutique.dao;

import bf.boutique.model.Utilisateur;
import bf.boutique.util.DatabaseConnection;
import bf.boutique.util.PasswordUtil;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthentificationDAO {

    public Utilisateur connecter(String identifiant, String motDePasse) {

        String sql = """
                SELECT id, nom, prenom, matricule, sexe,
                       date_naissance, identifiant, mot_de_passe
                FROM users
                WHERE identifiant = ?
                """;

        try (Connection connexion = DatabaseConnection.getConnection();
                PreparedStatement ps = connexion.prepareStatement(sql)) {

            ps.setString(1, identifiant);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hashEnregistre = rs.getString("mot_de_passe");

                    if (!PasswordUtil.verifier(
                            motDePasse, hashEnregistre)) {
                        return null;
                    }

                    Date date = rs.getDate("date_naissance");

                    return new Utilisateur(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("matricule"),
                            rs.getString("sexe"),
                            date == null ? null : date.toLocalDate(),
                            rs.getString("identifiant"));
                }
            }

        } catch (SQLException e) {
            System.err.println(
                    "Erreur d'authentification : " + e.getMessage());
        }

        return null;
    }
}
