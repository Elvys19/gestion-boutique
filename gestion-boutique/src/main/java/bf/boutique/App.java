
package bf.boutique;

import bf.boutique.view.ConnexionFrame;
import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ConnexionFrame().setVisible(true));
    }
}
