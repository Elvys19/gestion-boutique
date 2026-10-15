
package bf.boutique.model;

import java.time.LocalDate;

public class Utilisateur {

    private int id;
    private String nom;
    private String prenom;
    private String matricule;
    private String sexe;
    private LocalDate dateNaissance;
    private String identifiant;
    private String motDePasse;

    public Utilisateur(String nom, String prenom,
            String matricule, String sexe,
            LocalDate dateNaissance,
            String identifiant, String motDePasse) {
        this.nom = nom;
        this.prenom = prenom;
        this.matricule = matricule;
        this.sexe = sexe;
        this.dateNaissance = dateNaissance;
        this.identifiant = identifiant;
        this.motDePasse = motDePasse;
    }

    public Utilisateur(int id, String nom, String prenom,
            String matricule, String sexe,
            LocalDate dateNaissance, String identifiant) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.matricule = matricule;
        this.sexe = sexe;
        this.dateNaissance = dateNaissance;
        this.identifiant = identifiant;
    }

    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getMatricule() {
        return matricule;
    }

    public String getSexe() {
        return sexe;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public String getIdentifiant() {
        return identifiant;
    }

    public String getMotDePasse() {
        return motDePasse;
    }
}
