package bf.boutique.view;

import bf.boutique.dao.ProduitDAO;
import bf.boutique.model.Produit;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

public class ProduitFrame extends JFrame {

    private final JTextField nomField = new JTextField(15);
    private final JTextField prixAchatField = new JTextField(10);
    private final JTextField prixVenteField = new JTextField(10);
    private final JTextField stockField = new JTextField(5);
    private final JTextField stockMinimumField = new JTextField("5", 5);
    private final JTextField rechercheField = new JTextField(15);

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[] { "ID", "Nom", "Prix achat", "Prix vente", "Stock" },
            0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(tableModel);
    private final ProduitDAO produitDAO = new ProduitDAO();

    private static final Color BLEU_NUIT = new Color(23, 43, 77);
    private static final Color VERT = new Color(25, 135, 84);
    private static final Color FOND = new Color(245, 247, 250);
    private static final Color TEXTE = new Color(45, 55, 72);
    private static final Color BORDURE = new Color(215, 222, 232);

    public ProduitFrame() {
        setTitle("Gestion des produits");
        setSize(900, 620);
        setMinimumSize(new Dimension(780, 540));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        construireInterface();
        chargerProduits("");
    }

    private void construireInterface() {
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(FOND);

        // En-tête
        JPanel entete = new JPanel(new BorderLayout());
        entete.setBackground(BLEU_NUIT);
        entete.setBorder(new EmptyBorder(18, 24, 18, 24));

        JLabel titre = new JLabel("GESTION DES PRODUITS");
        titre.setFont(new Font("SansSerif", Font.BOLD, 22));
        titre.setForeground(Color.WHITE);

        JLabel sousTitre = new JLabel(
                "Ajouter, rechercher et consulter les produits");
        sousTitre.setFont(new Font("SansSerif", Font.PLAIN, 13));
        sousTitre.setForeground(new Color(225, 233, 245));

        JPanel titres = new JPanel(new GridLayout(2, 1, 0, 5));
        titres.setOpaque(false);
        titres.add(titre);
        titres.add(sousTitre);

        entete.add(titres, BorderLayout.CENTER);
        add(entete, BorderLayout.NORTH);

        // Zone centrale
        JPanel contenu = new JPanel(new BorderLayout(0, 14));
        contenu.setBackground(FOND);
        contenu.setBorder(new EmptyBorder(18, 22, 12, 22));

        // Formulaire d'ajout
        JPanel formulaire = new JPanel(new GridBagLayout());
        formulaire.setBackground(Color.WHITE);
        formulaire.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDURE),
                new EmptyBorder(14, 16, 14, 16)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 7, 6, 7);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JLabel titreFormulaire = new JLabel("NOUVEAU PRODUIT");
        titreFormulaire.setFont(new Font("SansSerif", Font.BOLD, 15));
        titreFormulaire.setForeground(BLEU_NUIT);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        formulaire.add(titreFormulaire, gbc);
        gbc.gridwidth = 1;

        ajouterChamp(formulaire, gbc, "Nom du produit", nomField, 0, 1);
        ajouterChamp(formulaire, gbc, "Prix d'achat", prixAchatField, 2, 1);
        ajouterChamp(formulaire, gbc, "Prix de vente", prixVenteField, 0, 3);
        ajouterChamp(formulaire, gbc, "Stock initial", stockField, 2, 3);
        ajouterChamp(formulaire, gbc, "Stock minimum", stockMinimumField, 0, 5);

        JButton ajouterButton = creerBouton(
                "Ajouter le produit", VERT, Color.WHITE);

        gbc.gridx = 2;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formulaire.add(ajouterButton, gbc);

        // Recherche
        JPanel recherchePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 9, 8));
        recherchePanel.setBackground(Color.WHITE);
        recherchePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDURE),
                new EmptyBorder(3, 8, 3, 8)));

        JLabel rechercheLabel = new JLabel("Rechercher un produit :");
        rechercheLabel.setForeground(TEXTE);
        rechercheLabel.setFont(new Font("SansSerif", Font.BOLD, 12));

        styliserChamp(rechercheField);
        recherchePanel.add(rechercheLabel);
        recherchePanel.add(rechercheField);

        JButton rechercherButton = creerBouton(
                "Rechercher", BLEU_NUIT, Color.WHITE);
        JButton afficherToutButton = creerBouton(
                "Afficher tout", Color.WHITE, BLEU_NUIT);

        recherchePanel.add(rechercherButton);
        recherchePanel.add(afficherToutButton);

        JPanel zoneSuperieure = new JPanel(new BorderLayout(0, 12));
        zoneSuperieure.setBackground(FOND);
        zoneSuperieure.add(formulaire, BorderLayout.CENTER);
        zoneSuperieure.add(recherchePanel, BorderLayout.SOUTH);

        contenu.add(zoneSuperieure, BorderLayout.NORTH);

        // Tableau des produits
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setRowHeight(30);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(230, 234, 240));
        table.setSelectionBackground(new Color(220, 238, 229));
        table.setSelectionForeground(TEXTE);
        table.setFillsViewportHeight(true);

        table.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 13));
        table.getTableHeader().setBackground(BLEU_NUIT);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(0, 34));
        table.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer centre = new DefaultTableCellRenderer();
        centre.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centre);
        table.getColumnModel().getColumn(4).setCellRenderer(centre);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDURE));
        scrollPane.getViewport().setBackground(Color.WHITE);

        contenu.add(scrollPane, BorderLayout.CENTER);
        add(contenu, BorderLayout.CENTER);

        // Pied de page
        JPanel pied = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pied.setBackground(FOND);
        pied.setBorder(new EmptyBorder(0, 22, 16, 22));

        JButton retourButton = creerBouton(
                "Retour au menu principal", BLEU_NUIT, Color.WHITE);
        pied.add(retourButton);
        add(pied, BorderLayout.SOUTH);

        // Actions existantes
        ajouterButton.addActionListener(e -> ajouterProduit());

        rechercherButton.addActionListener(
                e -> chargerProduits(rechercheField.getText().trim()));

        rechercheField.addActionListener(
                e -> chargerProduits(rechercheField.getText().trim()));

        afficherToutButton.addActionListener(e -> {
            rechercheField.setText("");
            chargerProduits("");
        });

        retourButton.addActionListener(e -> dispose());
    }

    private void ajouterChamp(
            JPanel panneau,
            GridBagConstraints gbc,
            String texte,
            JTextField champ,
            int colonne,
            int ligne) {

        JLabel label = new JLabel(texte);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setForeground(TEXTE);

        styliserChamp(champ);

        gbc.gridx = colonne;
        gbc.gridy = ligne;
        gbc.gridwidth = 1;
        panneau.add(label, gbc);

        gbc.gridx = colonne + 1;
        panneau.add(champ, gbc);
    }

    private void styliserChamp(JTextField champ) {
        champ.setFont(new Font("SansSerif", Font.PLAIN, 13));
        champ.setPreferredSize(new Dimension(150, 32));
        champ.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDURE),
                new EmptyBorder(5, 8, 5, 8)));
    }

    private JButton creerBouton(
            String texte, Color fond, Color couleurTexte) {

        JButton bouton = new JButton(texte);
        bouton.setFont(new Font("SansSerif", Font.BOLD, 12));
        bouton.setForeground(couleurTexte);
        bouton.setBackground(fond);
        bouton.setOpaque(true);
        bouton.setFocusPainted(false);
        bouton.setCursor(Cursor.getPredefinedCursor(
                Cursor.HAND_CURSOR));
        bouton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(fond.equals(Color.WHITE)
                        ? BORDURE
                        : fond),
                new EmptyBorder(8, 12, 8, 12)));

        return bouton;
    }

    private void ajouterProduit() {
        try {
            String nom = nomField.getText().trim();

            if (nom.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this, "Saisis le nom du produit.");
                return;
            }

            double prixAchat = Double.parseDouble(
                    prixAchatField.getText().trim().replace(',', '.'));
            double prixVente = Double.parseDouble(
                    prixVenteField.getText().trim().replace(',', '.'));
            int stock = Integer.parseInt(stockField.getText().trim());
            int stockMinimum = Integer.parseInt(
                    stockMinimumField.getText().trim());

            if (prixAchat < 0 || prixVente < 0
                    || stock < 0 || stockMinimum < 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "Les prix et les stocks ne doivent pas être négatifs.");
                return;
            }

            Produit produit = new Produit(
                    nom, prixAchat, prixVente, stock, stockMinimum, null);

            if (produitDAO.ajouter(produit)) {
                JOptionPane.showMessageDialog(
                        this, "Produit ajouté avec succès !");

                nomField.setText("");
                prixAchatField.setText("");
                prixVenteField.setText("");
                stockField.setText("");
                stockMinimumField.setText("5");

                chargerProduits("");
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Impossible d'ajouter le produit. Vérifie la base de données.");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Vérifie les prix et les stocks : ils doivent être numériques.");
        }
    }

    private void chargerProduits(String recherche) {
        List<Produit> produits = produitDAO.rechercherParNom(recherche);
        tableModel.setRowCount(0);

        for (Produit produit : produits) {
            tableModel.addRow(new Object[] {
                    produit.getId(),
                    produit.getNom(),
                    produit.getPrixAchat(),
                    produit.getPrixVente(),
                    produit.getStock()
            });
        }
    }
}