
package bf.boutique.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

import java.security.MessageDigest;

public class PasswordUtil {

    public static boolean verifier(String motDePasse, String valeurStockee) {
        try {
            String[] parties = valeurStockee.split("\\$");

            if (parties.length != 4 || !parties[0].equals("PBKDF2")) {
                return false;
            }

            int iterations = Integer.parseInt(parties[1]);

            if (iterations < 100000 || iterations > 1000000) {
                return false;
            }

            byte[] sel = Base64.getDecoder().decode(parties[2]);
            byte[] hashAttendu = Base64.getDecoder().decode(parties[3]);

            PBEKeySpec spec = new PBEKeySpec(
                    motDePasse.toCharArray(), sel, iterations,
                    hashAttendu.length * 8);

            byte[] hashCalcule = SecretKeyFactory
                    .getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec)
                    .getEncoded();

            spec.clearPassword();

            return MessageDigest.isEqual(hashAttendu, hashCalcule);

        } catch (Exception e) {
            return false;
        }
    }

    public static String hacher(String motDePasse) {
        try {
            byte[] sel = new byte[16];
            new SecureRandom().nextBytes(sel);

            PBEKeySpec spec = new PBEKeySpec(
                    motDePasse.toCharArray(),
                    sel,
                    210000,
                    256);

            byte[] hash = SecretKeyFactory
                    .getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec)
                    .getEncoded();

            spec.clearPassword();

            return "PBKDF2$210000$"
                    + Base64.getEncoder().encodeToString(sel)
                    + "$"
                    + Base64.getEncoder().encodeToString(hash);

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Impossible de sécuriser le mot de passe.", e);
        }
    }
}
