package bf.boutique.view;

import bf.boutique.dao.FournisseurDAO;
import bf.boutique.model.Fournisseur;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class FournisseurFrame extends JFrame {

    private static final Color BLEU_MARINE = new Color(23, 43, 77);
    private static final Color VERT = new Color(25, 135, 84);
    private static final Color FOND = new Color(245, 247, 250);
    private static final Color BLANC = Color.WHITE;
    private static final Color TEXTE = new Color(45, 55, 72);

    private final FournisseurDAO dao = new FournisseurDAO();

    private final JTextField nomField = new JTextField(20);
    private final JTextField telephoneField = new JTextField(20);
    private final JTextField adresseField = new JTextField(20);

    private final DefaultTableModel modele = new DefaultTableModel(
            new String[] { "ID", "Nom", "Téléphone", "Adresse" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable tableau = new JTable(modele);

    public FournisseurFrame() {
        setTitle("Gestion des fournisseurs");
        setSize(1100, 750);
        setMinimumSize(new Dimension(950, 650));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(FOND);

        // En-tête
        JPanel entete = new JPanel(new BorderLayout());
        entete.setBackground(BLEU_MARINE);
        entete.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel titre = new JLabel("GESTION DES FOURNISSEURS");
        titre.setFont(new Font("SansSerif", Font.BOLD, 22));
        titre.setForeground(BLANC);

        JLabel sousTitre = new JLabel(
                "Ajouter et consulter les fournisseurs de la boutique");
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

        // Zone centrale
        JPanel contenu = new JPanel(new BorderLayout(0, 18));
        contenu.setBackground(FOND);
        contenu.setBorder(BorderFactory.createEmptyBorder(20, 25, 15, 25));

        // Formulaire
        JPanel panneauFormulaire = new JPanel(new BorderLayout(0, 15));
        panneauFormulaire.setBackground(BLANC);
        panneauFormulaire.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 230, 237)),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)));

        JLabel titreFormulaire = new JLabel("Informations du fournisseur");
        titreFormulaire.setFont(new Font("SansSerif", Font.BOLD, 16));
        titreFormulaire.setForeground(BLEU_MARINE);

        JPanel champs = new JPanel(new GridLayout(3, 2, 15, 12));
        champs.setBackground(BLANC);

        ajouterChamp(champs, "Nom du fournisseur *", nomField);
        ajouterChamp(champs, "Téléphone", telephoneField);
        ajouterChamp(champs, "Adresse", adresseField);

        JPanel boutonsFormulaire = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        boutonsFormulaire.setBackground(BLANC);

        JButton actualiserButton = creerBouton("Actualiser la liste", BLEU_MARINE);
        JButton ajouterButton = creerBouton("Ajouter le fournisseur", VERT);

        boutonsFormulaire.add(actualiserButton);
        boutonsFormulaire.add(ajouterButton);

        panneauFormulaire.add(titreFormulaire, BorderLayout.NORTH);
        panneauFormulaire.add(champs, BorderLayout.CENTER);
        panneauFormulaire.add(boutonsFormulaire, BorderLayout.SOUTH);

        // Tableau des fournisseurs
        JPanel panneauTableau = new JPanel(new BorderLayout(0, 10));
        panneauTableau.setBackground(FOND);

        JLabel titreTableau = new JLabel("Liste des fournisseurs");
        titreTableau.setFont(new Font("SansSerif", Font.BOLD, 16));
        titreTableau.setForeground(BLEU_MARINE);

        configurerTableau();

        JScrollPane scrollPane = new JScrollPane(tableau);
        scrollPane.setBorder(BorderFactory.createLineBorder(
                new Color(225, 230, 237)));
        scrollPane.getViewport().setBackground(BLANC);

        panneauTableau.add(titreTableau, BorderLayout.NORTH);
        panneauTableau.add(scrollPane, BorderLayout.CENTER);

        contenu.add(panneauFormulaire, BorderLayout.NORTH);
        contenu.add(panneauTableau, BorderLayout.CENTER);

        // Le tableau reçoit davantage d'espace que le formulaire
        contenu.setPreferredSize(new Dimension(1050, 600));
        add(contenu, BorderLayout.CENTER);

        // Pied de page
        JPanel pied = new JPanel(new BorderLayout());
        pied.setBackground(BLANC);
        pied.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(
                        1, 0, 0, 0, new Color(225, 230, 237)),
                BorderFactory.createEmptyBorder(12, 25, 12, 25)));

        JLabel aide = new JLabel("* Le nom du fournisseur est obligatoire.");
        aide.setFont(new Font("SansSerif", Font.PLAIN, 12));
        aide.setForeground(Color.GRAY);

        JButton retourButton = creerBouton("Retour au menu principal", BLEU_MARINE);

        pied.add(aide, BorderLayout.WEST);
        pied.add(retourButton, BorderLayout.EAST);

        add(pied, BorderLayout.SOUTH);

        // Actions
        ajouterButton.addActionListener(e -> ajouterFournisseur());
        actualiserButton.addActionListener(e -> chargerFournisseurs());
        retourButton.addActionListener(e -> dispose());

        getRootPane().setDefaultButton(ajouterButton);

        chargerFournisseurs();
    }

    private void ajouterChamp(
            JPanel panneau, String texte, JTextField champ) {

        JLabel label = new JLabel(texte);
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        label.setForeground(TEXTE);

        champ.setFont(new Font("SansSerif", Font.PLAIN, 14));
        champ.setPreferredSize(new Dimension(180, 32));
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
        tableau.setFont(new Font("SansSerif", Font.PLAIN, 15));
        tableau.setForeground(TEXTE);
        tableau.setBackground(BLANC);
        tableau.setSelectionBackground(new Color(220, 235, 250));
        tableau.setSelectionForeground(BLEU_MARINE);
        tableau.setGridColor(new Color(232, 236, 242));
        tableau.setShowVerticalLines(false);
        tableau.setFillsViewportHeight(true);
        tableau.setAutoCreateRowSorter(true);

        JTableHeader enteteTableau = tableau.getTableHeader();

        enteteTableau.setBackground(BLEU_MARINE);
        enteteTableau.setForeground(BLANC);
        enteteTableau.setFont(new Font("SansSerif", Font.BOLD, 15));
        enteteTableau.setPreferredSize(new Dimension(0, 42));
        enteteTableau.setReorderingAllowed(false);
        // Centrer le contenu de toutes les cellules
DefaultTableCellRenderer centre = new DefaultTableCellRenderer();
centre.setHorizontalAlignment(SwingConstants.CENTER);

for (int i = 0; i < tableau.getColumnCount(); i++) {
    tableau.getColumnModel()
           .getColumn(i)
           .setCellRenderer(centre);
}

// Centrer également les titres des colonnes
DefaultTableCellRenderer centreEntete = new DefaultTableCellRenderer();
centreEntete.setHorizontalAlignment(SwingConstants.CENTER);
centreEntete.setBackground(BLEU_MARINE);
centreEntete.setForeground(BLANC);
centreEntete.setFont(new Font("SansSerif", Font.BOLD, 15));

for (int i = 0; i < tableau.getColumnModel().getColumnCount(); i++) {
    tableau.getColumnModel()
           .getColumn(i)
           .setHeaderRenderer(centreEntete);
}
    }

    private void ajouterFournisseur() {
        String nom = nomField.getText().trim();
        String telephone = telephoneField.getText().trim();
        String adresse = adresseField.getText().trim();

        if (nom.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Le nom du fournisseur est obligatoire.",
                    "Champ obligatoire",
                    JOptionPane.WARNING_MESSAGE);
            nomField.requestFocusInWindow();
            return;
        }

        Fournisseur fournisseur = new Fournisseur(nom, telephone, adresse);

        if (dao.ajouter(fournisseur)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Le fournisseur a été ajouté avec succès.",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);

            nomField.setText("");
            telephoneField.setText("");
            adresseField.setText("");
            nomField.requestFocusInWindow();

            chargerFournisseurs();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Échec de l'ajout du fournisseur. Vérifie le terminal.",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void chargerFournisseurs() {
        modele.setRowCount(0);

        List<Fournisseur> fournisseurs = dao.obtenirTous();

        for (Fournisseur f : fournisseurs) {
            modele.addRow(new Object[] {
                    f.getId(),
                    f.getNom(),
                    f.getTelephone(),
                    f.getAdresse()
            });
        }
    }
}