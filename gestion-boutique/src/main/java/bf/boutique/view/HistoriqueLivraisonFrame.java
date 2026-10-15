
package bf.boutique.view;

import bf.boutique.dao.HistoriqueLivraisonDAO;
import bf.boutique.model.Utilisateur;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class HistoriqueLivraisonFrame extends JFrame {

    private static final Color BLEU = new Color(23, 43, 77);
    private static final Color VERT = new Color(25, 135, 84);
    private static final Color GRIS = new Color(245, 247, 250);
    private static final Color TEXTE = new Color(45, 55, 72);

    private final HistoriqueLivraisonDAO dao = new HistoriqueLivraisonDAO();

    private final Utilisateur utilisateur;

    private final DefaultTableModel modele = new DefaultTableModel(
            new String[] {
                    "N° livraison",
                    "Date",
                    "Fournisseur",
                    "Produit",
                    "Quantité",
                    "Prix d'achat unitaire (FCFA)"
            }, 0) {

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable tableau = new JTable(modele);
    private final JLabel compteur = new JLabel("Lignes affichées : 0");

    // Conservé pour les anciens appels du projet.
    public HistoriqueLivraisonFrame() {
        this(null);
    }

    public HistoriqueLivraisonFrame(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;

        setTitle("Gestion de boutique - Historique des livraisons");
        setSize(1200, 700);
        setMinimumSize(new Dimension(950, 550));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(GRIS);

        construireInterface();
        chargerLivraisons();
    }

    private void construireInterface() {
        // En-tête
        JPanel entete = new JPanel(new BorderLayout());
        entete.setBackground(BLEU);
        entete.setBorder(
                BorderFactory.createEmptyBorder(22, 28, 22, 28));

        JPanel textes = new JPanel();
        textes.setOpaque(false);
        textes.setLayout(new BoxLayout(textes, BoxLayout.Y_AXIS));

        JLabel titre = new JLabel("HISTORIQUE DES LIVRAISONS");
        titre.setFont(new Font("SansSerif", Font.BOLD, 25));
        titre.setForeground(Color.WHITE);

        JLabel sousTitre = new JLabel(
                "Consultez les entrées de stock et les achats auprès des fournisseurs.");
        sousTitre.setFont(new Font("SansSerif", Font.PLAIN, 14));
        sousTitre.setForeground(new Color(220, 228, 240));

        textes.add(titre);
        textes.add(Box.createVerticalStrut(7));
        textes.add(sousTitre);

        entete.add(textes, BorderLayout.CENTER);
        add(entete, BorderLayout.NORTH);

        // Contenu principal
        JPanel contenu = new JPanel(new BorderLayout(12, 12));
        contenu.setBackground(GRIS);
        contenu.setBorder(
                BorderFactory.createEmptyBorder(22, 25, 18, 25));

        JPanel barreInfo = new JPanel(new BorderLayout());
        barreInfo.setOpaque(false);

        compteur.setFont(new Font("SansSerif", Font.BOLD, 15));
        compteur.setForeground(BLEU);
        barreInfo.add(compteur, BorderLayout.WEST);

        JLabel conseil = new JLabel(
                "Utilisez les en-têtes pour trier les résultats.");
        conseil.setFont(new Font("SansSerif", Font.PLAIN, 13));
        conseil.setForeground(TEXTE);
        barreInfo.add(conseil, BorderLayout.EAST);

        contenu.add(barreInfo, BorderLayout.NORTH);

        // Tableau
        configurerTableau();

        JScrollPane scroll = new JScrollPane(tableau);
        scroll.setBorder(BorderFactory.createLineBorder(
                new Color(220, 225, 232)));
        scroll.getViewport().setBackground(Color.WHITE);

        contenu.add(scroll, BorderLayout.CENTER);
        add(contenu, BorderLayout.CENTER);

        // Pied de page et boutons
        JPanel bas = new JPanel(new BorderLayout());
        bas.setBackground(Color.WHITE);
        bas.setBorder(
                BorderFactory.createEmptyBorder(15, 25, 15, 25));

        JLabel pied = new JLabel(
                "Boutique - Gestion des approvisionnements");
        pied.setForeground(new Color(100, 110, 125));
        pied.setFont(new Font("SansSerif", Font.PLAIN, 12));
        bas.add(pied, BorderLayout.WEST);

        JPanel boutons = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 0));
        boutons.setOpaque(false);

        JButton retour = creerBouton("Retour au menu", BLEU);
        JButton actualiser = creerBouton("Actualiser", VERT);

        retour.addActionListener(e -> retournerAuMenu());
        actualiser.addActionListener(e -> chargerLivraisons());

        boutons.add(retour);
        boutons.add(actualiser);

        bas.add(boutons, BorderLayout.EAST);
        add(bas, BorderLayout.SOUTH);
    }

    private void configurerTableau() {
        tableau.setRowHeight(38);
        tableau.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tableau.setForeground(TEXTE);
        tableau.setBackground(Color.WHITE);
        tableau.setSelectionBackground(new Color(210, 231, 250));
        tableau.setSelectionForeground(BLEU);
        tableau.setGridColor(new Color(230, 234, 240));
        tableau.setShowVerticalLines(false);
        tableau.setShowHorizontalLines(true);
        tableau.setFillsViewportHeight(true);
        tableau.setAutoCreateRowSorter(true);
        tableau.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader header = tableau.getTableHeader();
        header.setPreferredSize(new Dimension(0, 44));
        header.setFont(new Font("SansSerif", Font.BOLD, 13));
        header.setBackground(BLEU);
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer centre = new DefaultTableCellRenderer();
        centre.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < tableau.getColumnCount(); i++) {
            tableau.getColumnModel()
                    .getColumn(i)
                    .setCellRenderer(centre);
        }

        tableau.getColumnModel().getColumn(0).setPreferredWidth(110);
        tableau.getColumnModel().getColumn(1).setPreferredWidth(190);
        tableau.getColumnModel().getColumn(2).setPreferredWidth(200);
        tableau.getColumnModel().getColumn(3).setPreferredWidth(220);
        tableau.getColumnModel().getColumn(4).setPreferredWidth(110);
        tableau.getColumnModel().getColumn(5).setPreferredWidth(250);
    }

    private JButton creerBouton(String texte, Color couleur) {
        JButton bouton = new JButton(texte);
        bouton.setFont(new Font("SansSerif", Font.BOLD, 13));
        bouton.setForeground(Color.WHITE);
        bouton.setBackground(couleur);
        bouton.setFocusPainted(false);
        bouton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bouton.setBorder(
                BorderFactory.createEmptyBorder(11, 15, 11, 15));
        return bouton;
    }

    private void chargerLivraisons() {
        modele.setRowCount(0);

        try {
            List<Object[]> livraisons = dao.obtenirHistorique();

            for (Object[] livraison : livraisons) {
                modele.addRow(livraison);
            }

            compteur.setText(
                    "Lignes affichées : " + livraisons.size());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Impossible de charger l'historique des livraisons.\n"
                            + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void retournerAuMenu() {
        if (utilisateur == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "L'utilisateur connecté n'est pas disponible.\n"
                            + "Ouvrez cette fenêtre depuis le menu principal.");
            return;
        }

        new MenuPrincipalFrame(utilisateur).setVisible(true);
        dispose();
    }
}
