package bf.boutique.view;

import bf.boutique.model.Utilisateur;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MenuPrincipalFrame extends JFrame {

    private final Utilisateur utilisateur;

    // Palette de couleurs
    private static final Color BLEU_NUIT = new Color(23, 43, 77);
    private static final Color VERT = new Color(25, 135, 84);
    private static final Color BLANC = Color.WHITE;
    private static final Color FOND = new Color(245, 247, 250);
    private static final Color TEXTE = new Color(45, 55, 72);

    public MenuPrincipalFrame(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;

        setTitle("Gestion de boutique - Menu principal");
        setSize(850, 600);
        setMinimumSize(new Dimension(750, 520));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(FOND);

        // En-tête
        JPanel entete = new JPanel(new BorderLayout());
        entete.setBackground(BLEU_NUIT);
        entete.setBorder(new EmptyBorder(22, 25, 22, 25));

        JLabel titre = new JLabel(
                "GESTION DE BOUTIQUE",
                SwingConstants.CENTER);
        titre.setFont(new Font("SansSerif", Font.BOLD, 25));
        titre.setForeground(BLANC);

        JLabel bienvenue = new JLabel(
                "Bienvenue, " + utilisateur.getPrenom()
                        + " " + utilisateur.getNom(),
                SwingConstants.CENTER);
        bienvenue.setFont(new Font("SansSerif", Font.PLAIN, 15));
        bienvenue.setForeground(new Color(225, 233, 245));

        JPanel textesEntete = new JPanel(new GridLayout(2, 1, 0, 8));
        textesEntete.setOpaque(false);
        textesEntete.add(titre);
        textesEntete.add(bienvenue);

        entete.add(textesEntete, BorderLayout.CENTER);
        add(entete, BorderLayout.NORTH);

        // Zone principale du menu
        JPanel zoneCentrale = new JPanel(new BorderLayout(0, 15));
        zoneCentrale.setBackground(FOND);
        zoneCentrale.setBorder(new EmptyBorder(25, 30, 25, 30));

        JLabel sousTitre = new JLabel("MENU PRINCIPAL");
        sousTitre.setFont(new Font("SansSerif", Font.BOLD, 17));
        sousTitre.setForeground(TEXTE);
        sousTitre.setBorder(new EmptyBorder(0, 0, 5, 0));

        zoneCentrale.add(sousTitre, BorderLayout.NORTH);

        JPanel menu = new JPanel(new GridLayout(4, 2, 15, 15));
        menu.setBackground(FOND);

        ajouterBouton(menu, "Gestion des produits",
                () -> new ProduitFrame().setVisible(true));

        ajouterBouton(menu, "Effectuer une vente",
                () -> new VenteFrame(utilisateur).setVisible(true));

        ajouterBouton(menu, "Gestion des fournisseurs",
                () -> new FournisseurFrame().setVisible(true));

        ajouterBouton(menu, "Enregistrer une livraison",
                () -> new LivraisonFrame(utilisateur).setVisible(true));

        ajouterBouton(menu, "Historique des ventes",
                () -> new HistoriqueVenteFrame(utilisateur).setVisible(true));

        ajouterBouton(menu, "Historique des livraisons",
                () -> new HistoriqueLivraisonFrame(utilisateur).setVisible(true));

        ajouterBouton(menu, "Gestion des utilisateurs",
                () -> new UtilisateurFrame(utilisateur).setVisible(true));

        JButton boutonDeconnexion = creerBouton(
                "Déconnexion", new Color(180, 55, 55), BLANC);
        boutonDeconnexion.addActionListener(e -> deconnecter());
        menu.add(boutonDeconnexion);

        zoneCentrale.add(menu, BorderLayout.CENTER);
        add(zoneCentrale, BorderLayout.CENTER);

        // Pied de page
        JLabel pied = new JLabel(
                "Gestion de boutique | Application de gestion commerciale",
                SwingConstants.CENTER);
        pied.setFont(new Font("SansSerif", Font.PLAIN, 11));
        pied.setForeground(new Color(110, 120, 135));
        pied.setBorder(new EmptyBorder(0, 10, 12, 10));
        add(pied, BorderLayout.SOUTH);
    }

    private void ajouterBouton(
            JPanel panneau, String texte, Runnable action) {

        JButton bouton = creerBouton(texte, BLANC, BLEU_NUIT);
        bouton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(215, 222, 232)),
                new EmptyBorder(12, 10, 12, 10)));

        bouton.addActionListener(e -> action.run());
        panneau.add(bouton);
    }

    private JButton creerBouton(
            String texte, Color fond, Color couleurTexte) {

        JButton bouton = new JButton(texte);
        bouton.setFont(new Font("SansSerif", Font.BOLD, 14));
        bouton.setForeground(couleurTexte);
        bouton.setBackground(fond);
        bouton.setOpaque(true);
        bouton.setFocusPainted(false);
        bouton.setCursor(Cursor.getPredefinedCursor(
                Cursor.HAND_CURSOR));
        bouton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(fond),
                new EmptyBorder(12, 10, 12, 10)));
        bouton.setPreferredSize(new Dimension(200, 55));

        return bouton;
    }

    private void deconnecter() {
        int choix = JOptionPane.showConfirmDialog(
                this,
                "Voulez-vous vraiment vous déconnecter ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (choix == JOptionPane.YES_OPTION) {
            new ConnexionFrame().setVisible(true);
            dispose();
        }
    }
}