
package bf.boutique;

import bf.boutique.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;

public class TestConnexion {

    public static void main(String[] args) {
        try (Connection connexion = DatabaseConnection.getConnection()) {
            System.out.println("Connexion à PostgreSQL réussie !");
        } catch (SQLException e) {
            System.out.println("Échec de la connexion : " + e.getMessage());
        }
    }
}
