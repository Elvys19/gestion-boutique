package bf.boutique.view;

import bf.boutique.dao.UtilisateurDAO;
import bf.boutique.model.Utilisateur;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class UtilisateurFrame extends JFrame {

    private static final Color BLEU = new Color(23, 43, 77);
    private static final Color VERT = new Color(25, 135, 84);
    private static final Color ROUGE = new Color(180, 45, 45);
    private static final Color GRIS = new Color(245, 247, 250);
    private static final Color TEXTE = new Color(45, 55, 72);

    private final UtilisateurDAO dao = new UtilisateurDAO();
    private final Utilisateur utilisateurConnecte;

    private final JTextField nomField = new JTextField(15);
    private final JTextField prenomField = new JTextField(15);
    private final JTextField matriculeField = new JTextField(15);
    private final JComboBox<String> sexeCombo = new JComboBox<>(new String[] { "Masculin", "Féminin" });
    private final JTextField dateField = new JTextField(15);
    private final JTextField identifiantField = new JTextField(15);
    private final JPasswordField motDePasseField = new JPasswordField(15);

    private final DefaultTableModel modele = new DefaultTableModel(
            new String[] {
                    "ID", "Nom", "Prénom", "Matricule",
                    "Sexe", "Date de naissance", "Identifiant"
            }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable tableau = new JTable(modele);
    private final JLabel compteur = new JLabel("Utilisateurs : 0");
    private final JLabel titreFormulaire = new JLabel("INFORMATIONS DE L'UTILISATEUR");

    private final JButton ajouterBouton = creerBouton("Ajouter", VERT);
    private final JButton modifierBouton = creerBouton("Modifier la sélection", BLEU);
    private final JButton supprimerBouton = creerBouton("Supprimer la sélection", ROUGE);
    private final JButton actualiserBouton = creerBouton("Actualiser", new Color(80, 95, 115));

    private Integer utilisateurSelectionneId = null;

    public UtilisateurFrame() {
        this(null);
    }

    public UtilisateurFrame(Utilisateur utilisateurConnecte) {
        this.utilisateurConnecte = utilisateurConnecte;

        setTitle("Gestion de boutique - Gestion des utilisateurs");
        setSize(1300, 850);
        setMinimumSize(new Dimension(1100, 750));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(GRIS);

        construireInterface();
        chargerUtilisateurs();
        reinitialiserModeFormulaire();
    }

    private void construireInterface() {
        JPanel entete = new JPanel(new BorderLayout());
        entete.setBackground(BLEU);
        entete.setBorder(
                BorderFactory.createEmptyBorder(22, 28, 22, 28));

        JPanel textes = new JPanel();
        textes.setOpaque(false);
        textes.setLayout(new BoxLayout(textes, BoxLayout.Y_AXIS));

        JLabel titre = new JLabel("GESTION DES UTILISATEURS");
        titre.setFont(new Font("SansSerif", Font.BOLD, 25));
        titre.setForeground(Color.WHITE);

        JLabel sousTitre = new JLabel(
                "Ajoutez, consultez, modifiez et supprimez les comptes.");
        sousTitre.setFont(new Font("SansSerif", Font.PLAIN, 14));
        sousTitre.setForeground(new Color(220, 228, 240));

        textes.add(titre);
        textes.add(Box.createVerticalStrut(7));
        textes.add(sousTitre);
        entete.add(textes, BorderLayout.CENTER);
        add(entete, BorderLayout.NORTH);

        JPanel contenu = new JPanel(new BorderLayout(15, 18));
        contenu.setBackground(GRIS);
        contenu.setBorder(
                BorderFactory.createEmptyBorder(20, 25, 18, 25));

        contenu.add(creerFormulaire(), BorderLayout.NORTH);

        JPanel panneauListe = new JPanel(new BorderLayout(0, 12));
        panneauListe.setOpaque(false);

        JPanel barreInfo = new JPanel(new BorderLayout());
        barreInfo.setOpaque(false);

        compteur.setFont(new Font("SansSerif", Font.BOLD, 15));
        compteur.setForeground(BLEU);
        barreInfo.add(compteur, BorderLayout.WEST);

        JLabel conseil = new JLabel(
                "Sélectionnez une ligne pour modifier ou supprimer un compte.");
        conseil.setFont(new Font("SansSerif", Font.PLAIN, 12));
        conseil.setForeground(TEXTE);
        barreInfo.add(conseil, BorderLayout.EAST);

        panneauListe.add(barreInfo, BorderLayout.NORTH);

        configurerTableau();

        JScrollPane scroll = new JScrollPane(tableau);
        scroll.setBorder(BorderFactory.createLineBorder(
                new Color(220, 225, 232)));
        scroll.getViewport().setBackground(Color.WHITE);
        panneauListe.add(scroll, BorderLayout.CENTER);

        JPanel actionsListe = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 5));
        actionsListe.setOpaque(false);
        actionsListe.add(modifierBouton);
        actionsListe.add(supprimerBouton);
        actionsListe.add(actualiserBouton);
        panneauListe.add(actionsListe, BorderLayout.SOUTH);

        contenu.add(panneauListe, BorderLayout.CENTER);
        add(contenu, BorderLayout.CENTER);

        JPanel bas = new JPanel(new BorderLayout());
        bas.setBackground(Color.WHITE);
        bas.setBorder(
                BorderFactory.createEmptyBorder(15, 25, 15, 25));

        JLabel pied = new JLabel(
                "Boutique - Administration des comptes");
        pied.setForeground(new Color(100, 110, 125));
        pied.setFont(new Font("SansSerif", Font.PLAIN, 12));
        bas.add(pied, BorderLayout.WEST);

        JButton retour = creerBouton("Retour au menu", BLEU);
        retour.addActionListener(e -> retournerAuMenu());

        JPanel zoneBouton = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 0));
        zoneBouton.setOpaque(false);
        zoneBouton.add(retour);
        bas.add(zoneBouton, BorderLayout.EAST);

        add(bas, BorderLayout.SOUTH);
    }

    private JPanel creerFormulaire() {
        JPanel formulaire = new JPanel(new GridBagLayout());
        formulaire.setBackground(Color.WHITE);
        formulaire.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(220, 225, 232)),
                BorderFactory.createEmptyBorder(15, 18, 15, 18)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 8, 7, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        titreFormulaire.setFont(
                new Font("SansSerif", Font.BOLD, 16));
        titreFormulaire.setForeground(BLEU);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        formulaire.add(titreFormulaire, gbc);
        gbc.gridwidth = 1;

        ajouterChamp(formulaire, gbc, "Nom :", nomField, 0, 1);
        ajouterChamp(formulaire, gbc, "Prénom :", prenomField, 2, 1);
        ajouterChamp(formulaire, gbc, "Matricule :", matriculeField, 0, 2);
        ajouterChamp(formulaire, gbc, "Sexe :", sexeCombo, 2, 2);
        ajouterChamp(formulaire, gbc, "Date de naissance :",
                dateField, 0, 3);
        ajouterChamp(formulaire, gbc, "Identifiant :",
                identifiantField, 2, 3);
        ajouterChamp(formulaire, gbc, "Mot de passe :",
                motDePasseField, 0, 4);

        JLabel formatDate = new JLabel("Format : AAAA-MM-JJ");
        formatDate.setForeground(new Color(100, 110, 125));
        formatDate.setFont(
                new Font("SansSerif", Font.ITALIC, 12));

        gbc.gridx = 2;
        gbc.gridy = 4;
        formulaire.add(formatDate, gbc);

        JLabel conseilMotDePasse = new JLabel(
                "En modification, laissez vide pour le conserver.");
        conseilMotDePasse.setForeground(new Color(100, 110, 125));
        conseilMotDePasse.setFont(
                new Font("SansSerif", Font.ITALIC, 11));

        gbc.gridx = 2;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        formulaire.add(conseilMotDePasse, gbc);
        gbc.gridwidth = 1;

        ajouterBouton.addActionListener(e -> ajouterUtilisateur());
        modifierBouton.addActionListener(e -> modifierUtilisateur());
        supprimerBouton.addActionListener(e -> supprimerUtilisateur());
        actualiserBouton.addActionListener(e -> {
            chargerUtilisateurs();
            reinitialiserModeFormulaire();
        });

        JButton annuler = creerBouton(
                "Annuler la sélection", new Color(100, 110, 125));
        annuler.addActionListener(e -> {
            tableau.clearSelection();
            reinitialiserModeFormulaire();
        });

        JPanel actions = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        actions.add(annuler);
        actions.add(ajouterBouton);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 4;
        formulaire.add(actions, gbc);

        return formulaire;
    }

    private void ajouterChamp(
            JPanel panneau,
            GridBagConstraints gbc,
            String texte,
            JComponent champ,
            int colonne,
            int ligne) {

        JLabel label = new JLabel(texte);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(TEXTE);

        gbc.gridx = colonne;
        gbc.gridy = ligne;
        gbc.weightx = 0;
        panneau.add(label, gbc);

        gbc.gridx = colonne + 1;
        gbc.weightx = 1;

        champ.setFont(new Font("SansSerif", Font.PLAIN, 14));
        champ.setPreferredSize(new Dimension(180, 34));
        panneau.add(champ, gbc);
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
        header.setFont(new Font("SansSerif", Font.BOLD, 13));
        header.setBackground(BLEU);
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer centre = new DefaultTableCellRenderer();
        centre.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < tableau.getColumnCount(); i++) {
            tableau.getColumnModel().getColumn(i)
                    .setCellRenderer(centre);
        }

        tableau.getColumnModel().getColumn(0).setPreferredWidth(60);
        tableau.getColumnModel().getColumn(1).setPreferredWidth(140);
        tableau.getColumnModel().getColumn(2).setPreferredWidth(140);
        tableau.getColumnModel().getColumn(3).setPreferredWidth(150);
        tableau.getColumnModel().getColumn(4).setPreferredWidth(100);
        tableau.getColumnModel().getColumn(5).setPreferredWidth(190);
        tableau.getColumnModel().getColumn(6).setPreferredWidth(150);

        tableau.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()
                    && tableau.getSelectedRow() != -1) {
                chargerSelectionDansFormulaire();
            }
        });
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

    private void ajouterUtilisateur() {
        Utilisateur utilisateur = lireFormulaire(true);
        if (utilisateur == null) {
            return;
        }

        if (dao.ajouter(utilisateur)) {
            JOptionPane.showMessageDialog(this,
                    "Utilisateur ajouté avec succès.",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
            chargerUtilisateurs();
            reinitialiserModeFormulaire();
        } else {
            afficherErreurEnregistrement();
        }
    }

    private void chargerSelectionDansFormulaire() {
        int ligneVue = tableau.getSelectedRow();
        if (ligneVue < 0) {
            return;
        }

        int ligne = tableau.convertRowIndexToModel(ligneVue);
        utilisateurSelectionneId = (Integer) modele.getValueAt(ligne, 0);

        nomField.setText(String.valueOf(modele.getValueAt(ligne, 1)));
        prenomField.setText(String.valueOf(modele.getValueAt(ligne, 2)));
        matriculeField.setText(String.valueOf(modele.getValueAt(ligne, 3)));
        sexeCombo.setSelectedItem(String.valueOf(modele.getValueAt(ligne, 4)));
        dateField.setText(String.valueOf(modele.getValueAt(ligne, 5)));
        identifiantField.setText(String.valueOf(modele.getValueAt(ligne, 6)));
        motDePasseField.setText("");

        titreFormulaire.setText(
                "MODIFICATION DE L'UTILISATEUR ID " + utilisateurSelectionneId);
        ajouterBouton.setEnabled(false);
        modifierBouton.setEnabled(true);
        supprimerBouton.setEnabled(true);
    }

    private void modifierUtilisateur() {
        if (utilisateurSelectionneId == null) {
            JOptionPane.showMessageDialog(this,
                    "Sélectionnez d'abord un utilisateur dans le tableau.",
                    "Aucune sélection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Utilisateur utilisateur = lireFormulaire(false);
        if (utilisateur == null) {
            return;
        }

        int confirmation = JOptionPane.showConfirmDialog(this,
                "Confirmer la modification de cet utilisateur ?",
                "Confirmation", JOptionPane.YES_NO_OPTION);

        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        if (dao.modifier(utilisateurSelectionneId, utilisateur)) {
            JOptionPane.showMessageDialog(this,
                    "Utilisateur modifié avec succès.",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
            chargerUtilisateurs();
            reinitialiserModeFormulaire();
        } else {
            afficherErreurEnregistrement();
        }
    }

    private void supprimerUtilisateur() {
        if (utilisateurSelectionneId == null) {
            JOptionPane.showMessageDialog(this,
                    "Sélectionnez d'abord un utilisateur dans le tableau.",
                    "Aucune sélection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (utilisateurConnecte != null
                && utilisateurSelectionneId == utilisateurConnecte.getId()) {
            JOptionPane.showMessageDialog(this,
                    "Vous ne pouvez pas supprimer le compte actuellement connecté.",
                    "Suppression interdite", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmation = JOptionPane.showConfirmDialog(this,
                "Voulez-vous vraiment supprimer cet utilisateur ?\n"
                        + "Cette opération est irréversible.",
                "Confirmer la suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        if (dao.supprimer(utilisateurSelectionneId)) {
            JOptionPane.showMessageDialog(this,
                    "Utilisateur supprimé.",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
            chargerUtilisateurs();
            reinitialiserModeFormulaire();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Impossible de supprimer cet utilisateur.\n"
                            + "Il peut être référencé par des ventes ou livraisons.",
                    "Suppression impossible", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Utilisateur lireFormulaire(boolean motDePasseObligatoire) {
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String matricule = matriculeField.getText().trim();
        String identifiant = identifiantField.getText().trim();
        String motDePasse = new String(motDePasseField.getPassword());

        if (nom.isEmpty() || prenom.isEmpty()
                || matricule.isEmpty() || identifiant.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Remplissez tous les champs obligatoires.",
                    "Champs manquants", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        if (motDePasseObligatoire && motDePasse.isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "Le mot de passe est obligatoire pour un nouvel utilisateur.",
                    "Mot de passe manquant", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        LocalDate dateNaissance;
        try {
            dateNaissance = LocalDate.parse(dateField.getText().trim());

            if (!dateNaissance.isBefore(LocalDate.now())) {
                JOptionPane.showMessageDialog(this,
                        "La date de naissance doit être dans le passé.",
                        "Date invalide", JOptionPane.WARNING_MESSAGE);
                return null;
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                    "Date invalide. Utilisez le format AAAA-MM-JJ,\n"
                            + "par exemple : 2000-05-20.",
                    "Date invalide", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        return new Utilisateur(
                nom, prenom, matricule,
                (String) sexeCombo.getSelectedItem(),
                dateNaissance, identifiant, motDePasse);
    }

    private void afficherErreurEnregistrement() {
        JOptionPane.showMessageDialog(this,
                "Échec de l'enregistrement.\n"
                        + "Vérifiez que le matricule et l'identifiant "
                        + "ne sont pas déjà utilisés.",
                "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    private void reinitialiserModeFormulaire() {
        utilisateurSelectionneId = null;
        nomField.setText("");
        prenomField.setText("");
        matriculeField.setText("");
        dateField.setText("");
        identifiantField.setText("");
        motDePasseField.setText("");
        sexeCombo.setSelectedIndex(0);

        titreFormulaire.setText("INFORMATIONS DU NOUVEL UTILISATEUR");
        ajouterBouton.setEnabled(true);
        modifierBouton.setEnabled(false);
        supprimerBouton.setEnabled(false);
        nomField.requestFocusInWindow();
    }

    private void chargerUtilisateurs() {
        modele.setRowCount(0);

        try {
            List<Utilisateur> utilisateurs = dao.obtenirTous();
            System.out.println("Nombre d'utilisateurs récupérés : " + utilisateurs.size());

            for (Utilisateur u : utilisateurs) {
                modele.addRow(new Object[] {
                        u.getId(),
                        u.getNom(),
                        u.getPrenom(),
                        u.getMatricule(),
                        u.getSexe(),
                        u.getDateNaissance(),
                        u.getIdentifiant()
                });
            }

            compteur.setText("Utilisateurs : " + utilisateurs.size());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Impossible de charger les utilisateurs.\n"
                            + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void retournerAuMenu() {
        if (utilisateurConnecte == null) {
            JOptionPane.showMessageDialog(this,
                    "L'utilisateur connecté n'est pas disponible.\n"
                            + "Ouvrez cette fenêtre depuis le menu principal.");
            return;
        }

        new MenuPrincipalFrame(utilisateurConnecte).setVisible(true);
        dispose();
    }
}