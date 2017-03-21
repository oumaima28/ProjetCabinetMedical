/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 *
 * @author ASUS
 */
@Entity
public class Medecin implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String tel;
    private String adresse;
    private String ville;
    @ManyToOne
    private Residence residence;
    @ManyToOne
    private Specialite specialite;
    @ManyToOne
    private Configuration configuration;
    @OneToMany(mappedBy = "medecin")
    private List<SecretaireMedecin> secretaireMedecins;
    @OneToMany(mappedBy = "medecin")
    private List<RendezVous> rendezVouss;
    @OneToMany(mappedBy = "medecin")
    private List<MargeNonBloquee> margeNonBloquees;
    @OneToMany(mappedBy = "medecin")
    private List<MargeBloquee> margeBloquees;
    @OneToOne(mappedBy = "medecin")
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public Residence getResidence() {
        return residence;
    }

    public void setResidence(Residence residence) {
        this.residence = residence;
    }

    public Specialite getSpecialite() {
        return specialite;
    }

    public void setSpecialite(Specialite specialite) {
        this.specialite = specialite;
    }

    public List<SecretaireMedecin> getSecretaireMedecins() {
        return secretaireMedecins;
    }

    public void setSecretaireMedecins(List<SecretaireMedecin> secretaireMedecins) {
        this.secretaireMedecins = secretaireMedecins;
    }

    public List<RendezVous> getRendezVouss() {
        return rendezVouss;
    }

    public void setRendezVouss(List<RendezVous> rendezVouss) {
        this.rendezVouss = rendezVouss;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public List<MargeNonBloquee> getMargeNonBloquees() {
        return margeNonBloquees;
    }

    public void setMargeNonBloquees(List<MargeNonBloquee> margeNonBloquees) {
        this.margeNonBloquees = margeNonBloquees;
    }

    public List<MargeBloquee> getMargeBloquees() {
        return margeBloquees;
    }

    public void setMargeBloquees(List<MargeBloquee> margeBloquees) {
        this.margeBloquees = margeBloquees;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Medecin)) {
            return false;
        }
        Medecin other = (Medecin) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "bean.Medecin[ id=" + id + " ]";
    }

}
