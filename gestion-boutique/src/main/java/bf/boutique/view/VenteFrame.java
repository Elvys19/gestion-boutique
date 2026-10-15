package bf.boutique.view;

import bf.boutique.dao.ProduitDAO;
import bf.boutique.dao.VenteDAO;
import bf.boutique.model.Produit;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import bf.boutique.model.Utilisateur;

public class VenteFrame extends JFrame {
    private final Utilisateur utilisateur;
    private static final Color BLEU_NUIT = new Color(23, 43, 77);
    private static final Color VERT = new Color(25, 135, 84);
    private static final Color FOND = new Color(245, 247, 250);
    private static final Color TEXTE = new Color(45, 55, 72);
    private static final Color BLANC = Color.WHITE;

    private final JTextField clientField = new JTextField(15);
    private final JTextField quantiteField = new JTextField("1", 5);

    private final JLabel totalLabel = new JLabel("Total du panier : 0.00 FCFA");

    private final DefaultTableModel produitsModel = new DefaultTableModel(
            new String[] { "ID", "Produit", "Prix de vente", "Stock" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final DefaultTableModel panierModel = new DefaultTableModel(
            new String[] { "ID", "Produit", "Prix unitaire", "Quantité", "Sous-total" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable produitsTable = new JTable(produitsModel);
    private final JTable panierTable = new JTable(panierModel);

    private final ProduitDAO produitDAO = new ProduitDAO();
    private final VenteDAO venteDAO = new VenteDAO();

    // ID du produit -> quantité choisie
    private final Map<Integer, Integer> panier = new LinkedHashMap<>();

    public VenteFrame(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
        setTitle("Enregistrement d'une vente");
        setSize(1000, 700);
        setMinimumSize(new Dimension(850, 600));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        construireInterface();
        chargerProduits();
        mettreAJourTotal();
    }

    private void construireInterface() {
        JPanel principal = new JPanel(new BorderLayout(0, 0));
        principal.setBackground(FOND);

        // En-tête
        JPanel entete = new JPanel(new BorderLayout());
        entete.setBackground(BLEU_NUIT);
        entete.setBorder(new EmptyBorder(18, 22, 18, 22));

        JLabel titre = new JLabel("ENREGISTREMENT D'UNE VENTE");
        titre.setForeground(BLANC);
        titre.setFont(new Font("SansSerif", Font.BOLD, 20));

        JLabel sousTitre = new JLabel("Sélectionne les produits et compose le panier");
        sousTitre.setForeground(new Color(220, 228, 240));
        sousTitre.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JPanel titres = new JPanel(new GridLayout(2, 1, 0, 5));
        titres.setOpaque(false);
        titres.add(titre);
        titres.add(sousTitre);
        entete.add(titres, BorderLayout.CENTER);

        principal.add(entete, BorderLayout.NORTH);

        // Zone centrale
        JPanel contenu = new JPanel(new GridLayout(2, 1, 0, 12));
        contenu.setBackground(FOND);
        contenu.setBorder(new EmptyBorder(15, 20, 12, 20));

        // Informations du client
        JPanel clientPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        clientPanel.setBackground(BLANC);
        clientPanel.add(creerLabel("Nom du client :"));
        clientField.setPreferredSize(new Dimension(220, 30));
        styliserChamp(clientField);
        clientPanel.add(clientField);

        // Tableau des produits
        styliserTable(produitsTable);

        JPanel produitsPanel = new JPanel(new BorderLayout(0, 8));
        produitsPanel.setBackground(BLANC);
        produitsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 226, 235)),
                new EmptyBorder(10, 12, 10, 12)));

        JLabel produitsTitre = creerLabel("PRODUITS DISPONIBLES");
        produitsTitre.setFont(new Font("SansSerif", Font.BOLD, 14));
        produitsPanel.add(produitsTitre, BorderLayout.NORTH);
        produitsPanel.add(new JScrollPane(produitsTable), BorderLayout.CENTER);

        JPanel choixPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        choixPanel.setBackground(BLANC);
        choixPanel.add(creerLabel("Quantité :"));
        styliserChamp(quantiteField);
        choixPanel.add(quantiteField);

        JButton ajouterButton = creerBouton("Ajouter au panier", VERT);
        choixPanel.add(ajouterButton);
        produitsPanel.add(choixPanel, BorderLayout.SOUTH);

        // Tableau du panier
        styliserTable(panierTable);

        JPanel panierPanel = new JPanel(new BorderLayout(0, 8));
        panierPanel.setBackground(BLANC);
        panierPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 226, 235)),
                new EmptyBorder(10, 12, 10, 12)));

        JLabel panierTitre = creerLabel("PANIER DE VENTE");
        panierTitre.setFont(new Font("SansSerif", Font.BOLD, 14));
        panierPanel.add(panierTitre, BorderLayout.NORTH);
        panierPanel.add(new JScrollPane(panierTable), BorderLayout.CENTER);

        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        totalPanel.setBackground(new Color(235, 245, 239));

        totalLabel.setForeground(VERT);
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 17));
        totalPanel.add(totalLabel);

        panierPanel.add(totalPanel, BorderLayout.SOUTH);

        // Ajout des zones au contenu
        JPanel zoneProduits = new JPanel(new BorderLayout(0, 8));
        zoneProduits.setBackground(FOND);
        zoneProduits.add(clientPanel, BorderLayout.NORTH);
        zoneProduits.add(produitsPanel, BorderLayout.CENTER);

        contenu.add(zoneProduits);
        contenu.add(panierPanel);

        principal.add(contenu, BorderLayout.CENTER);

        // Boutons du bas
        JButton retourButton = creerBouton("Retour au menu", BLEU_NUIT);
        JButton supprimerButton = creerBouton("Retirer du panier",
                new Color(190, 65, 65));
        JButton validerButton = creerBouton("Enregistrer la vente", VERT);

        JPanel boutonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        boutonsPanel.setBackground(BLANC);
        boutonsPanel.setBorder(new EmptyBorder(0, 15, 0, 15));
        boutonsPanel.add(retourButton);
        boutonsPanel.add(supprimerButton);
        boutonsPanel.add(validerButton);

        principal.add(boutonsPanel, BorderLayout.SOUTH);
        setContentPane(principal);

        // Actions des boutons
        retourButton.addActionListener(e -> dispose());
        ajouterButton.addActionListener(e -> ajouterAuPanier());
        supprimerButton.addActionListener(e -> retirerDuPanier());
        validerButton.addActionListener(e -> enregistrerVente());
    }

    private JLabel creerLabel(String texte) {
        JLabel label = new JLabel(texte);
        label.setForeground(TEXTE);
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        return label;
    }

    private JButton creerBouton(String texte, Color couleur) {
        JButton bouton = new JButton(texte);
        bouton.setFont(new Font("SansSerif", Font.BOLD, 12));
        bouton.setForeground(BLANC);
        bouton.setBackground(couleur);
        bouton.setFocusPainted(false);
        bouton.setOpaque(true);
        bouton.setBorderPainted(false);
        bouton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bouton.setBorder(new EmptyBorder(10, 14, 10, 14));
        return bouton;
    }

    private void styliserChamp(JTextField champ) {
        champ.setFont(new Font("SansSerif", Font.PLAIN, 13));
        champ.setForeground(TEXTE);
        champ.setBackground(BLANC);
        champ.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(205, 215, 228)),
                new EmptyBorder(5, 7, 5, 7)));
    }

    private void styliserTable(JTable table) {
        table.setRowHeight(28);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.setForeground(TEXTE);
        table.setBackground(BLANC);
        table.setSelectionBackground(new Color(215, 232, 248));
        table.setSelectionForeground(TEXTE);
        table.setGridColor(new Color(230, 234, 240));
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);

        table.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 12));
        table.getTableHeader().setBackground(BLEU_NUIT);
        table.getTableHeader().setForeground(BLANC);
        table.getTableHeader().setPreferredSize(new Dimension(0, 32));

        DefaultTableCellRenderer centrer = new DefaultTableCellRenderer();
        centrer.setHorizontalAlignment(SwingConstants.CENTER);

        if (table.getColumnCount() > 0) {
            table.getColumnModel().getColumn(0).setCellRenderer(centrer);
        }
    }

    private void chargerProduits() {
        produitsModel.setRowCount(0);

        List<Produit> produits = produitDAO.rechercherParNom("");

        for (Produit p : produits) {
            produitsModel.addRow(new Object[] {
                    p.getId(),
                    p.getNom(),
                    p.getPrixVente(),
                    p.getStock()
            });
        }
    }

    private void ajouterAuPanier() {
        int ligneVue = produitsTable.getSelectedRow();

        if (ligneVue < 0) {
            JOptionPane.showMessageDialog(
                    this, "Sélectionne un produit dans la liste.");
            return;
        }

        int ligne = produitsTable.convertRowIndexToModel(ligneVue);

        try {
            int produitId = Integer.parseInt(
                    produitsModel.getValueAt(ligne, 0).toString());
            String nom = produitsModel.getValueAt(ligne, 1).toString();
            BigDecimal prix = new BigDecimal(
                    produitsModel.getValueAt(ligne, 2).toString());
            int stock = Integer.parseInt(
                    produitsModel.getValueAt(ligne, 3).toString());
            int quantite = Integer.parseInt(quantiteField.getText().trim());

            if (quantite <= 0) {
                JOptionPane.showMessageDialog(
                        this, "La quantité doit être supérieure à zéro.");
                return;
            }

            int quantiteDejaChoisie = panier.getOrDefault(produitId, 0);

            if (quantiteDejaChoisie + quantite > stock) {
                JOptionPane.showMessageDialog(
                        this, "Stock insuffisant pour ce produit.");
                return;
            }

            int nouvelleQuantite = quantiteDejaChoisie + quantite;
            panier.put(produitId, nouvelleQuantite);

            actualiserPanier(produitId, nom, prix, nouvelleQuantite);
            quantiteField.setText("1");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this, "Saisis une quantité entière valide.");
        }
    }

    private void actualiserPanier(
            int produitId, String nom, BigDecimal prix, int quantite) {

        for (int i = 0; i < panierModel.getRowCount(); i++) {
            int id = Integer.parseInt(
                    panierModel.getValueAt(i, 0).toString());

            if (id == produitId) {
                panierModel.setValueAt(quantite, i, 3);
                panierModel.setValueAt(
                        prix.multiply(BigDecimal.valueOf(quantite)), i, 4);
                mettreAJourTotal();
                return;
            }
        }

        panierModel.addRow(new Object[] {
                produitId,
                nom,
                prix,
                quantite,
                prix.multiply(BigDecimal.valueOf(quantite))
        });

        mettreAJourTotal();
    }

    private void mettreAJourTotal() {
        BigDecimal total = BigDecimal.ZERO;

        for (int i = 0; i < panierModel.getRowCount(); i++) {
            BigDecimal sousTotal = new BigDecimal(
                    panierModel.getValueAt(i, 4).toString());
            total = total.add(sousTotal);
        }

        totalLabel.setText(
                "Total du panier : "
                        + total.setScale(2, RoundingMode.HALF_UP).toPlainString()
                        + " FCFA");
    }

    private void retirerDuPanier() {
        int ligneVue = panierTable.getSelectedRow();

        if (ligneVue < 0) {
            JOptionPane.showMessageDialog(
                    this, "Sélectionne un article du panier.");
            return;
        }

        int ligne = panierTable.convertRowIndexToModel(ligneVue);

        int produitId = Integer.parseInt(
                panierModel.getValueAt(ligne, 0).toString());

        panier.remove(produitId);
        panierModel.removeRow(ligne);
        mettreAJourTotal();
    }

    private void enregistrerVente() {
        if (panier.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this, "Le panier est vide.");
            return;
        }

        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Confirmer l'enregistrement de cette vente ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        String nomClient = clientField.getText().trim();

        // Pour le moment, aucun utilisateur n'est associé à cette vente.
        boolean succes = venteDAO.enregistrerVente(
                nomClient.isEmpty() ? null : nomClient,
                utilisateur.getId(),
                new LinkedHashMap<>(panier));

        if (succes) {
            JOptionPane.showMessageDialog(
                    this, "Vente enregistrée avec succès !");

            panier.clear();
            panierModel.setRowCount(0);
            clientField.setText("");
            chargerProduits();
            mettreAJourTotal();

        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "La vente a échoué. Vérifie le stock et la base de données.");
            chargerProduits();
        }
    }
}