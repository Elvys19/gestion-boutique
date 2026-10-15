package bf.boutique.view;

import bf.boutique.dao.AuthentificationDAO;
import bf.boutique.model.Utilisateur;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Arrays;

public class ConnexionFrame extends JFrame {

    private final JTextField identifiantField = new JTextField(20);
    private final JPasswordField motDePasseField = new JPasswordField(20);

    private final AuthentificationDAO dao = new AuthentificationDAO();

    private static final Color BLEU_NUIT = new Color(23, 43, 77);
    private static final Color VERT = new Color(25, 135, 84);
    private static final Color FOND = new Color(245, 247, 250);
    private static final Color TEXTE = new Color(45, 55, 72);

    public ConnexionFrame() {
        setTitle("Connexion - Gestion de boutique");
        setSize(480, 380);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        getContentPane().setBackground(FOND);
        setLayout(new BorderLayout());

        // En-tête bleu nuit
        JPanel entete = new JPanel(new GridLayout(2, 1, 0, 8));
        entete.setBackground(BLEU_NUIT);
        entete.setBorder(new EmptyBorder(22, 20, 22, 20));

        JLabel titre = new JLabel(
                "GESTION DE BOUTIQUE",
                SwingConstants.CENTER);
        titre.setFont(new Font("SansSerif", Font.BOLD, 23));
        titre.setForeground(Color.WHITE);

        JLabel sousTitre = new JLabel(
                "Connectez-vous à votre compte",
                SwingConstants.CENTER);
        sousTitre.setFont(new Font("SansSerif", Font.PLAIN, 14));
        sousTitre.setForeground(new Color(225, 233, 245));

        entete.add(titre);
        entete.add(sousTitre);
        add(entete, BorderLayout.NORTH);

        // Formulaire
        JPanel formulaire = new JPanel(new GridBagLayout());
        formulaire.setBackground(Color.WHITE);
        formulaire.setBorder(new EmptyBorder(22, 30, 22, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 5, 7, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JLabel identifiantLabel = new JLabel("Identifiant");
        JLabel motDePasseLabel = new JLabel("Mot de passe");

        identifiantLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        motDePasseLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        identifiantLabel.setForeground(TEXTE);
        motDePasseLabel.setForeground(TEXTE);

        styliserChamp(identifiantField);
        styliserChamp(motDePasseField);

        gbc.gridx = 0;
        gbc.gridy = 0;
        formulaire.add(identifiantLabel, gbc);

        gbc.gridy = 1;
        formulaire.add(identifiantField, gbc);

        gbc.gridy = 2;
        formulaire.add(motDePasseLabel, gbc);

        gbc.gridy = 3;
        formulaire.add(motDePasseField, gbc);

        // Boutons
        JButton connexionButton = new JButton("Se connecter");
        JButton quitterButton = new JButton("Quitter");

        styliserBouton(connexionButton, VERT, Color.WHITE);
        styliserBouton(quitterButton, Color.WHITE, BLEU_NUIT);

        JPanel boutons = new JPanel(new GridLayout(1, 2, 12, 0));
        boutons.setBackground(Color.WHITE);
        boutons.add(connexionButton);
        boutons.add(quitterButton);

        gbc.gridy = 4;
        gbc.insets = new Insets(18, 5, 4, 5);
        formulaire.add(boutons, gbc);

        add(formulaire, BorderLayout.CENTER);

        connexionButton.addActionListener(e -> seConnecter());
        motDePasseField.addActionListener(e -> seConnecter());
        quitterButton.addActionListener(e -> {
            int choix = JOptionPane.showConfirmDialog(
                    this,
                    "Voulez-vous vraiment quitter l'application ?",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION);

            if (choix == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
    }

    private void styliserChamp(JTextField champ) {
        champ.setFont(new Font("SansSerif", Font.PLAIN, 14));
        champ.setPreferredSize(new Dimension(300, 36));
        champ.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(210, 218, 230)),
                new EmptyBorder(5, 9, 5, 9)));
    }

    private void styliserBouton(
            JButton bouton, Color fond, Color texte) {

        bouton.setFont(new Font("SansSerif", Font.BOLD, 13));
        bouton.setForeground(texte);
        bouton.setBackground(fond);
        bouton.setOpaque(true);
        bouton.setFocusPainted(false);
        bouton.setCursor(Cursor.getPredefinedCursor(
                Cursor.HAND_CURSOR));
        bouton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(fond),
                new EmptyBorder(9, 12, 9, 12)));
    }

    private void seConnecter() {
        String identifiant = identifiantField.getText().trim();
        char[] caracteres = motDePasseField.getPassword();

        try {
            if (identifiant.isEmpty() || caracteres.length == 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "Saisis ton identifiant et ton mot de passe.",
                        "Champs obligatoires",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String motDePasse = new String(caracteres);
            Utilisateur utilisateur = dao.connecter(
                    identifiant, motDePasse);

            if (utilisateur == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Identifiant ou mot de passe incorrect.",
                        "Erreur de connexion",
                        JOptionPane.ERROR_MESSAGE);
                motDePasseField.setText("");
                motDePasseField.requestFocusInWindow();
                return;
            }

            new MenuPrincipalFrame(utilisateur).setVisible(true);
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Une erreur est survenue lors de la connexion.\n"
                            + "Vérifie la connexion à la base de données.",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();

        } finally {
            Arrays.fill(caracteres, '\0');
        }
    }
}