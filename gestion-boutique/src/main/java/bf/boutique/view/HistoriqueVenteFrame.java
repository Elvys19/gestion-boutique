
package bf.boutique.view;

import bf.boutique.dao.DetailVenteDAO;
import bf.boutique.dao.HistoriqueVenteDAO;
import bf.boutique.model.Utilisateur;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.print.PrinterException;
import java.math.BigDecimal;
import java.util.List;

public class HistoriqueVenteFrame extends JFrame {

    private static final Color BLEU = new Color(23, 43, 77);
    private static final Color VERT = new Color(25, 135, 84);
    private static final Color GRIS = new Color(245, 247, 250);
    private static final Color TEXTE = new Color(45, 55, 72);

    private final HistoriqueVenteDAO dao = new HistoriqueVenteDAO();
    private final DetailVenteDAO detailDAO = new DetailVenteDAO();
    private final Utilisateur utilisateur;

    private final DefaultTableModel modele = new DefaultTableModel(
            new String[] { "N° vente", "Date", "Client", "Total (FCFA)" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable tableau = new JTable(modele);
    private final JLabel compteur = new JLabel("Nombre de ventes : 0");

    // Constructeur conservé pour rester compatible avec les anciens appels.
    public HistoriqueVenteFrame() {
        this(null);
    }

    public HistoriqueVenteFrame(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;

        setTitle("Gestion de boutique - Historique des ventes");
        setSize(1100, 700);
        setMinimumSize(new Dimension(900, 550));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(GRIS);

        construireInterface();
        chargerVentes();
    }

    private void construireInterface() {
        // En-tête
        JPanel entete = new JPanel(new BorderLayout());
        entete.setBackground(BLEU);
        entete.setBorder(BorderFactory.createEmptyBorder(22, 28, 22, 28));

        JPanel textes = new JPanel();
        textes.setOpaque(false);
        textes.setLayout(new BoxLayout(textes, BoxLayout.Y_AXIS));

        JLabel titre = new JLabel("HISTORIQUE DES VENTES");
        titre.setFont(new Font("SansSerif", Font.BOLD, 25));
        titre.setForeground(Color.WHITE);

        JLabel sousTitre = new JLabel(
                "Consultez les ventes, leurs détails et imprimez les reçus.");
        sousTitre.setFont(new Font("SansSerif", Font.PLAIN, 14));
        sousTitre.setForeground(new Color(220, 228, 240));

        textes.add(titre);
        textes.add(Box.createVerticalStrut(7));
        textes.add(sousTitre);
        entete.add(textes, BorderLayout.CENTER);

        add(entete, BorderLayout.NORTH);

        // Zone centrale
        JPanel contenu = new JPanel(new BorderLayout(12, 12));
        contenu.setBackground(GRIS);
        contenu.setBorder(BorderFactory.createEmptyBorder(22, 25, 18, 25));

        JPanel barreInfo = new JPanel(new BorderLayout());
        barreInfo.setOpaque(false);

        compteur.setFont(new Font("SansSerif", Font.BOLD, 15));
        compteur.setForeground(BLEU);
        barreInfo.add(compteur, BorderLayout.WEST);

        JLabel conseil = new JLabel(
                "Sélectionnez une vente pour consulter son reçu.");
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

        // Boutons du bas
        JPanel bas = new JPanel(new BorderLayout());
        bas.setBackground(Color.WHITE);
        bas.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        JLabel indication = new JLabel("Boutique - Gestion des ventes");
        indication.setForeground(new Color(100, 110, 125));
        indication.setFont(new Font("SansSerif", Font.PLAIN, 12));
        bas.add(indication, BorderLayout.WEST);

        JPanel boutons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        boutons.setOpaque(false);

        JButton retour = creerBouton("Retour au menu", BLEU);
        JButton actualiser = creerBouton("Actualiser", new Color(80, 95, 115));
        JButton detail = creerBouton("Voir le détail", VERT);
        JButton imprimer = creerBouton("Imprimer le reçu", new Color(0, 123, 180));

        retour.addActionListener(e -> retournerAuMenu());
        actualiser.addActionListener(e -> chargerVentes());
        detail.addActionListener(e -> afficherDetail(false));
        imprimer.addActionListener(e -> afficherDetail(true));

        boutons.add(retour);
        boutons.add(actualiser);
        boutons.add(detail);
        boutons.add(imprimer);

        bas.add(boutons, BorderLayout.EAST);
        add(bas, BorderLayout.SOUTH);
    }

    private void configurerTableau() {
        tableau.setRowHeight(36);
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
        header.setPreferredSize(new Dimension(0, 42));
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setBackground(BLEU);
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer centre = new DefaultTableCellRenderer();
        centre.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < tableau.getColumnCount(); i++) {
            tableau.getColumnModel().getColumn(i).setCellRenderer(centre);
        }

        tableau.getColumnModel().getColumn(0).setPreferredWidth(100);
        tableau.getColumnModel().getColumn(1).setPreferredWidth(220);
        tableau.getColumnModel().getColumn(2).setPreferredWidth(300);
        tableau.getColumnModel().getColumn(3).setPreferredWidth(180);
    }

    private JButton creerBouton(String texte, Color couleur) {
        JButton bouton = new JButton(texte);
        bouton.setFont(new Font("SansSerif", Font.BOLD, 13));
        bouton.setForeground(Color.WHITE);
        bouton.setBackground(couleur);
        bouton.setFocusPainted(false);
        bouton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bouton.setBorder(BorderFactory.createEmptyBorder(11, 15, 11, 15));
        return bouton;
    }

    private void chargerVentes() {
        modele.setRowCount(0);

        try {
            List<Object[]> ventes = dao.obtenirHistorique();

            for (Object[] vente : ventes) {
                modele.addRow(vente);
            }

            compteur.setText("Nombre de ventes : " + ventes.size());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Impossible de charger l'historique des ventes.\n"
                            + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void afficherDetail(boolean imprimer) {
        int ligne = tableau.getSelectedRow();

        if (ligne < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Sélectionnez d'abord une vente dans le tableau.",
                    "Aucune vente sélectionnée",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int ligneModele = tableau.convertRowIndexToModel(ligne);

        int venteId = Integer.parseInt(
                modele.getValueAt(ligneModele, 0).toString());

        String date = String.valueOf(
                modele.getValueAt(ligneModele, 1));

        String client = String.valueOf(
                modele.getValueAt(ligneModele, 2));

        BigDecimal total = new BigDecimal(
                modele.getValueAt(ligneModele, 3).toString());

        StringBuilder recu = new StringBuilder();

        recu.append("              BOUTIQUE\n");
        recu.append("             RECU DE VENTE\n");
        recu.append("========================================\n");
        recu.append("Numero de vente : ").append(venteId).append("\n");
        recu.append("Date            : ").append(date).append("\n");
        recu.append("Client          : ").append(client).append("\n");
        recu.append("----------------------------------------\n");
        recu.append("Produit | Quantite | Prix | Sous-total\n");
        recu.append("----------------------------------------\n");

        try {
            List<Object[]> details = detailDAO.obtenirDetails(venteId);

            for (Object[] detail : details) {
                recu.append(detail[0]).append(" | ")
                        .append(detail[1]).append(" | ")
                        .append(detail[2]).append(" | ")
                        .append(detail[3]).append(" FCFA\n");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Impossible de charger les détails de cette vente.\n"
                            + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        recu.append("----------------------------------------\n");
        recu.append("TOTAL : ").append(total).append(" FCFA\n");
        recu.append("========================================\n");
        recu.append("       Merci pour votre achat !\n");

        JTextArea zoneTexte = new JTextArea(recu.toString(), 20, 45);
        zoneTexte.setEditable(false);
        zoneTexte.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        zoneTexte.setMargin(new Insets(12, 12, 12, 12));

        if (!imprimer) {
            JOptionPane.showMessageDialog(
                    this,
                    new JScrollPane(zoneTexte),
                    "Détail de la vente n°" + venteId,
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            boolean impressionLancee = zoneTexte.print();

            if (impressionLancee) {
                JOptionPane.showMessageDialog(
                        this,
                        "Impression terminée.",
                        "Impression",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (PrinterException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Erreur d'impression : " + e.getMessage(),
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
