package bf.boutique.view;

import bf.boutique.dao.FournisseurDAO;
import bf.boutique.dao.LivraisonDAO;
import bf.boutique.dao.ProduitDAO;
import bf.boutique.model.Fournisseur;
import bf.boutique.model.Produit;
import bf.boutique.model.Utilisateur;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LivraisonFrame extends JFrame {

    private static final Color BLEU_MARINE = new Color(23, 43, 77);
    private static final Color VERT = new Color(25, 135, 84);
    private static final Color ROUGE = new Color(190, 55, 55);
    private static final Color FOND = new Color(245, 247, 250);
    private static final Color BLANC = Color.WHITE;
    private static final Color TEXTE = new Color(45, 55, 72);

    private final FournisseurDAO fournisseurDAO = new FournisseurDAO();
    private final ProduitDAO produitDAO = new ProduitDAO();
    private final LivraisonDAO livraisonDAO = new LivraisonDAO();
    private final Utilisateur utilisateur;

    private final JComboBox<Fournisseur> fournisseurCombo = new JComboBox<>();

    private final JComboBox<Produit> produitCombo = new JComboBox<>();

    private final JTextField quantiteField = new JTextField("1", 10);
    private final JTextField prixField = new JTextField(10);

    private final DefaultTableModel modele = new DefaultTableModel(
            new String[] {
                    "ID produit", "Produit", "Quantité",
                    "Prix d'achat (FCFA)", "Sous-total (FCFA)"
            }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable tableau = new JTable(modele);

    private final JLabel totalLabel = new JLabel("Total de la livraison : 0 FCFA");

    private final Map<Integer, Integer> quantites = new LinkedHashMap<>();

    private final Map<Integer, BigDecimal> prixAchats = new LinkedHashMap<>();

    public LivraisonFrame(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;

        setTitle("Enregistrement des livraisons");
        setSize(1150, 760);
        setMinimumSize(new Dimension(950, 650));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(FOND);

        configurerListesDeroulantes();

        // En-tête
        JPanel entete = new JPanel(new BorderLayout());
        entete.setBackground(BLEU_MARINE);
        entete.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel titre = new JLabel("ENREGISTREMENT DES LIVRAISONS");
        titre.setFont(new Font("SansSerif", Font.BOLD, 22));
        titre.setForeground(BLANC);

        JLabel sousTitre = new JLabel(
                "Sélectionner un fournisseur, ajouter les produits et mettre à jour le stock");
        sousTitre.setFont(new Font("SansSerif", Font.PLAIN, 13));
        sousTitre.setForeground(new Color(220, 230, 242));

        JPanel textesEntete = new JPanel();
        textesEntete.setOpaque(false);
        textesEntete.setLayout(new BoxLayout(textesEntete, BoxLayout.Y_AXIS));
        textesEntete.add(titre);
        textesEntete.add(Box.createVerticalStrut(6));
        textesEntete.add(sousTitre);

        entete.add(textesEntete, BorderLayout.CENTER);
        add(entete, BorderLayout.NORTH);

        // Contenu central
        JPanel contenu = new JPanel(new BorderLayout(0, 18));
        contenu.setBackground(FOND);
        contenu.setBorder(BorderFactory.createEmptyBorder(20, 25, 18, 25));

        // Formulaire
        JPanel formulaire = new JPanel(new BorderLayout(0, 15));
        formulaire.setBackground(BLANC);
        formulaire.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 230, 237)),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)));

        JLabel titreFormulaire = new JLabel("Informations de la livraison");
        titreFormulaire.setFont(new Font("SansSerif", Font.BOLD, 16));
        titreFormulaire.setForeground(BLEU_MARINE);

        JPanel champs = new JPanel(new GridLayout(2, 4, 15, 12));
        champs.setBackground(BLANC);

        ajouterChamp(champs, "Fournisseur", fournisseurCombo);
        ajouterChamp(champs, "Produit", produitCombo);
        ajouterChamp(champs, "Quantité livrée", quantiteField);
        ajouterChamp(champs, "Prix d'achat unitaire (FCFA)", prixField);

        JPanel boutons = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 8, 0));
        boutons.setBackground(BLANC);

        JButton ajouterButton = creerBouton("Ajouter au panier", VERT);

        JButton supprimerButton = creerBouton("Retirer le produit", ROUGE);

        JButton actualiserButton = creerBouton("Actualiser les listes", BLEU_MARINE);

        boutons.add(ajouterButton);
        boutons.add(supprimerButton);
        boutons.add(actualiserButton);

        formulaire.add(titreFormulaire, BorderLayout.NORTH);
        formulaire.add(champs, BorderLayout.CENTER);
        formulaire.add(boutons, BorderLayout.SOUTH);

        // Tableau du panier
        JPanel panneauPanier = new JPanel(new BorderLayout(0, 10));
        panneauPanier.setBackground(FOND);

        JLabel titrePanier = new JLabel("Produits de la livraison");
        titrePanier.setFont(new Font("SansSerif", Font.BOLD, 16));
        titrePanier.setForeground(BLEU_MARINE);

        configurerTableau();

        JScrollPane scrollPane = new JScrollPane(tableau);
        scrollPane.setBorder(BorderFactory.createLineBorder(
                new Color(225, 230, 237)));
        scrollPane.getViewport().setBackground(BLANC);

        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        totalLabel.setForeground(VERT);
        totalLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        JPanel basPanier = new JPanel(new BorderLayout());
        basPanier.setBackground(FOND);
        basPanier.add(totalLabel, BorderLayout.WEST);

        JButton validerButton = creerBouton("Enregistrer la livraison", VERT);
        basPanier.add(validerButton, BorderLayout.EAST);

        panneauPanier.add(titrePanier, BorderLayout.NORTH);
        panneauPanier.add(scrollPane, BorderLayout.CENTER);
        panneauPanier.add(basPanier, BorderLayout.SOUTH);

        contenu.add(formulaire, BorderLayout.NORTH);
        contenu.add(panneauPanier, BorderLayout.CENTER);

        add(contenu, BorderLayout.CENTER);

        // Pied de page
        JPanel pied = new JPanel(new BorderLayout());
        pied.setBackground(BLANC);
        pied.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(
                        1, 0, 0, 0, new Color(225, 230, 237)),
                BorderFactory.createEmptyBorder(12, 25, 12, 25)));

        JLabel aide = new JLabel(
                "Vérifie les quantités et les prix avant l'enregistrement.");
        aide.setFont(new Font("SansSerif", Font.PLAIN, 12));
        aide.setForeground(Color.GRAY);

        JButton retourButton = creerBouton("Retour au menu principal", BLEU_MARINE);

        pied.add(aide, BorderLayout.WEST);
        pied.add(retourButton, BorderLayout.EAST);

        add(pied, BorderLayout.SOUTH);

        // Actions
        ajouterButton.addActionListener(e -> ajouterAuPanier());
        supprimerButton.addActionListener(e -> supprimerDuPanier());
        actualiserButton.addActionListener(e -> chargerDonnees());
        validerButton.addActionListener(e -> enregistrerLivraison());
        retourButton.addActionListener(e -> dispose());

        produitCombo.addActionListener(e -> afficherPrixProduit());

        getRootPane().setDefaultButton(ajouterButton);

        chargerDonnees();
    }

    private void configurerListesDeroulantes() {
        fournisseurCombo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        produitCombo.setFont(new Font("SansSerif", Font.PLAIN, 14));

        fournisseurCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);

                setText(value instanceof Fournisseur
                        ? ((Fournisseur) value).getNom()
                        : "");

                return this;
            }
        });

        produitCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);

                setText(value instanceof Produit
                        ? ((Produit) value).getNom()
                        : "");

                return this;
            }
        });
    }

    private void ajouterChamp(
            JPanel panneau, String texte, JComponent champ) {

        JLabel label = new JLabel(texte);
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        label.setForeground(TEXTE);

        champ.setFont(new Font("SansSerif", Font.PLAIN, 14));
        champ.setPreferredSize(new Dimension(190, 34));

        champ.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(210, 218, 228)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));

        panneau.add(label);
        panneau.add(champ);
    }

    private JButton creerBouton(String texte, Color couleur) {
        JButton bouton = new JButton(texte);
        bouton.setFont(new Font("SansSerif", Font.BOLD, 12));
        bouton.setForeground(BLANC);
        bouton.setBackground(couleur);
        bouton.setFocusPainted(false);
        bouton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bouton.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        bouton.setOpaque(true);
        bouton.setBorderPainted(false);
        return bouton;
    }

    private void configurerTableau() {
        tableau.setRowHeight(38);
        tableau.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tableau.setForeground(TEXTE);
        tableau.setBackground(BLANC);
        tableau.setSelectionBackground(new Color(220, 235, 250));
        tableau.setSelectionForeground(BLEU_MARINE);
        tableau.setGridColor(new Color(232, 236, 242));
        tableau.setShowVerticalLines(false);
        tableau.setFillsViewportHeight(true);
        tableau.setAutoCreateRowSorter(true);

        JTableHeader entete = tableau.getTableHeader();
        entete.setFont(new Font("SansSerif", Font.BOLD, 14));
        entete.setBackground(BLEU_MARINE);
        entete.setForeground(BLANC);
        entete.setPreferredSize(new Dimension(0, 42));
        entete.setReorderingAllowed(false);

        DefaultTableCellRenderer centre = new DefaultTableCellRenderer();
        centre.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < tableau.getColumnCount(); i++) {
            tableau.getColumnModel()
                    .getColumn(i)
                    .setCellRenderer(centre);
        }

        DefaultTableCellRenderer centreEntete = new DefaultTableCellRenderer();
        centreEntete.setHorizontalAlignment(SwingConstants.CENTER);
        centreEntete.setBackground(BLEU_MARINE);
        centreEntete.setForeground(BLANC);
        centreEntete.setFont(new Font("SansSerif", Font.BOLD, 14));

        for (int i = 0; i < tableau.getColumnModel().getColumnCount(); i++) {
            tableau.getColumnModel()
                    .getColumn(i)
                    .setHeaderRenderer(centreEntete);
        }
    }

    private void chargerDonnees() {
        fournisseurCombo.removeAllItems();
        produitCombo.removeAllItems();

        List<Fournisseur> fournisseurs = fournisseurDAO.obtenirTous();
        for (Fournisseur f : fournisseurs) {
            fournisseurCombo.addItem(f);
        }

        List<Produit> produits = produitDAO.rechercherParNom("");
        for (Produit p : produits) {
            produitCombo.addItem(p);
        }

        afficherPrixProduit();
    }

    private void afficherPrixProduit() {
        Produit produit = (Produit) produitCombo.getSelectedItem();

        if (produit != null) {
            prixField.setText(String.valueOf(produit.getPrixAchat()));
        } else {
            prixField.setText("");
        }
    }

    private void ajouterAuPanier() {
        Produit produit = (Produit) produitCombo.getSelectedItem();

        if (produit == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Aucun produit disponible. Ajoute d'abord un produit.",
                    "Produit indisponible",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int quantite = Integer.parseInt(
                    quantiteField.getText().trim());

            BigDecimal prix = new BigDecimal(
                    prixField.getText().trim());

            if (quantite <= 0 || prix.compareTo(BigDecimal.ZERO) < 0) {
                throw new NumberFormatException();
            }

            int id = produit.getId();

            int nouvelleQuantite = quantites.getOrDefault(id, 0) + quantite;

            quantites.put(id, nouvelleQuantite);
            prixAchats.put(id, prix);

            actualiserPanier();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Saisis une quantité entière positive et un prix valide.",
                    "Valeurs invalides",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void supprimerDuPanier() {
        int ligne = tableau.getSelectedRow();

        if (ligne < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Sélectionne un produit dans le panier.",
                    "Aucune sélection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int ligneModele = tableau.convertRowIndexToModel(ligne);

        int id = Integer.parseInt(
                modele.getValueAt(ligneModele, 0).toString());

        quantites.remove(id);
        prixAchats.remove(id);

        actualiserPanier();
    }

    private void actualiserPanier() {
        modele.setRowCount(0);

        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<Integer, Integer> entree : quantites.entrySet()) {
            int id = entree.getKey();
            int quantite = entree.getValue();
            BigDecimal prix = prixAchats.get(id);

            String nom = "Produit #" + id;

            for (int i = 0; i < produitCombo.getItemCount(); i++) {
                Produit p = produitCombo.getItemAt(i);

                if (p.getId() == id) {
                    nom = p.getNom();
                    break;
                }
            }

            BigDecimal sousTotal = prix.multiply(BigDecimal.valueOf(quantite));

            total = total.add(sousTotal);

            modele.addRow(new Object[] {
                    id, nom, quantite, prix, sousTotal
            });
        }

        totalLabel.setText(
                "Total de la livraison : "
                        + total.toPlainString() + " FCFA");
    }

    private void enregistrerLivraison() {
        Fournisseur fournisseur = (Fournisseur) fournisseurCombo.getSelectedItem();

        if (fournisseur == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Ajoute d'abord un fournisseur.",
                    "Fournisseur manquant",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (quantites.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Ajoute au moins un produit à la livraison.",
                    "Panier vide",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Enregistrer cette livraison et mettre à jour le stock ?",
                "Confirmation de la livraison",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        boolean succes = livraisonDAO.enregistrerLivraison(
                fournisseur.getId(),
                utilisateur.getId(),
                new LinkedHashMap<>(quantites),
                new LinkedHashMap<>(prixAchats));

        if (succes) {
            JOptionPane.showMessageDialog(
                    this,
                    "Livraison enregistrée. Le stock a été mis à jour.",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);

            quantites.clear();
            prixAchats.clear();
            actualiserPanier();
            chargerDonnees();

        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Échec de l'enregistrement. Consulte le terminal.",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}