
package bf.boutique.dao;

import bf.boutique.model.Utilisateur;
import bf.boutique.util.DatabaseConnection;
import bf.boutique.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurDAO {

    // Authentifier un utilisateur
    public Utilisateur authentifier(String identifiant, String motDePasse) {
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
                if (!rs.next()) {
                    return null;
                }

                String hashStocke = rs.getString("mot_de_passe");

                if (!PasswordUtil.verifier(motDePasse, hashStocke)) {
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

        } catch (SQLException e) {
            System.err.println(
                    "Erreur d'authentification : " + e.getMessage());
            return null;
        }
    }

    // Ajouter un utilisateur
    public boolean ajouter(Utilisateur u) {
        String sql = """
                INSERT INTO users
                (nom, prenom, matricule, sexe, date_naissance,
                 identifiant, mot_de_passe)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        String motDePasseHache = PasswordUtil.hacher(u.getMotDePasse());

        try (Connection connexion = DatabaseConnection.getConnection();
                PreparedStatement ps = connexion.prepareStatement(sql)) {

            remplirInformations(ps, u);
            ps.setString(7, motDePasseHache);

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println(
                    "Erreur lors de l'ajout de l'utilisateur : "
                            + e.getMessage());
            return false;
        }
    }

    // Modifier les informations d'un utilisateur.
    // Si le mot de passe est vide, l'ancien est conservé.
    public boolean modifier(int id, Utilisateur u) {
        String motDePasse = u.getMotDePasse();

        boolean changerMotDePasse = motDePasse != null && !motDePasse.isBlank();

        String sql;

        if (changerMotDePasse) {
            sql = """
                    UPDATE users
                    SET nom = ?, prenom = ?, matricule = ?, sexe = ?,
                        date_naissance = ?, identifiant = ?, mot_de_passe = ?
                    WHERE id = ?
                    """;
        } else {
            sql = """
                    UPDATE users
                    SET nom = ?, prenom = ?, matricule = ?, sexe = ?,
                        date_naissance = ?, identifiant = ?
                    WHERE id = ?
                    """;
        }

        try (Connection connexion = DatabaseConnection.getConnection();
                PreparedStatement ps = connexion.prepareStatement(sql)) {

            remplirInformations(ps, u);

            if (changerMotDePasse) {
                ps.setString(7, PasswordUtil.hacher(motDePasse));
                ps.setInt(8, id);
            } else {
                ps.setInt(7, id);
            }

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println(
                    "Erreur lors de la modification de l'utilisateur : "
                            + e.getMessage());
            return false;
        }
    }

    // Vérifier si le matricule et l'identifiant sont disponibles.
    // idExclu permet de conserver ceux de l'utilisateur modifié.
    public boolean identifiantsDisponibles(
            String matricule,
            String identifiant,
            int idExclu) {

        String sql = """
                SELECT COUNT(*)
                FROM users
                WHERE (matricule = ? OR identifiant = ?)
                  AND id <> ?
                """;

        try (Connection connexion = DatabaseConnection.getConnection();
                PreparedStatement ps = connexion.prepareStatement(sql)) {

            ps.setString(1, matricule);
            ps.setString(2, identifiant);
            ps.setInt(3, idExclu);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) == 0;
            }

        } catch (SQLException e) {
            System.err.println(
                    "Erreur de vérification des doublons : "
                            + e.getMessage());
            return false;
        }
    }

    // Supprimer un utilisateur à partir de son ID.
    public boolean supprimer(int id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection connexion = DatabaseConnection.getConnection();
                PreparedStatement ps = connexion.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println(
                    "Erreur lors de la suppression de l'utilisateur : "
                            + e.getMessage());
            return false;
        }
    }

    // Obtenir tous les utilisateurs sans leurs mots de passe.
    public List<Utilisateur> obtenirTous() {
        List<Utilisateur> utilisateurs = new ArrayList<>();

        String sql = """
                SELECT id, nom, prenom, matricule, sexe,
                       date_naissance, identifiant
                FROM users
                ORDER BY nom, prenom
                """;

        try (Connection connexion = DatabaseConnection.getConnection();
                PreparedStatement ps = connexion.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Date date = rs.getDate("date_naissance");

                utilisateurs.add(new Utilisateur(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("matricule"),
                        rs.getString("sexe"),
                        date == null ? null : date.toLocalDate(),
                        rs.getString("identifiant")));
            }

        } catch (SQLException e) {
            System.err.println(
                    "Erreur de chargement des utilisateurs : "
                            + e.getMessage());
        }

        return utilisateurs;
    }

    // Méthode interne pour éviter de répéter les mêmes affectations SQL.
    private void remplirInformations(
            PreparedStatement ps,
            Utilisateur u) throws SQLException {

        ps.setString(1, u.getNom());
        ps.setString(2, u.getPrenom());
        ps.setString(3, u.getMatricule());
        ps.setString(4, u.getSexe());

        if (u.getDateNaissance() == null) {
            ps.setNull(5, Types.DATE);
        } else {
            ps.setDate(5, Date.valueOf(u.getDateNaissance()));
        }

        ps.setString(6, u.getIdentifiant());
    }
}
