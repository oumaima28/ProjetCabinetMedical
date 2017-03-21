/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import bean.Configuration;
import bean.Medecin;
import bean.Patient;
import bean.User;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import javax.ejb.EJB;
import service.ConfigurationFacade;
import service.MedecinFacade;
import service.PatientFacade;
import service.UserFacade;

/**
 *
 * @author moi
 */
@Named(value = "inscriptionController")
@SessionScoped
public class InscriptionController implements Serializable {

    private String nom;
    private String prenom;
    private String typeUser;
    private String email;
    private User user;

    private Medecin selectedMedecin;
    private Configuration configuration;
    private Patient selectedPatient;

    @EJB
    private MedecinFacade medecinFacade;
    @EJB
    private PatientFacade patientFacade;
    @EJB
    private UserFacade userFacade;
    @EJB
    private ConfigurationFacade configurationFacade;

    public String goToCreateObjectView() {
        if (typeUser.equals("Medecin")) {
            selectedMedecin = new Medecin();
            selectedMedecin.setNom(nom);
            selectedMedecin.setPrenom(prenom);
            return "/inscription/CreateMedecin.xhtml";
        } else if (typeUser.equals("Patient")) {
            selectedPatient = new Patient();
            selectedPatient.setNom(nom);
            selectedPatient.setPrenom(prenom);
            return "/inscription/CreatePatient.xhtml";
        }
        return null;
    }

    public String goToCreateConfiguration() {
        return "/inscription/CreateMedecinConfiguration.xhtml";
    }

    public void sinscrire() {
        if (typeUser.equals("Medecin")) {
            configurationFacade.createForInscription(configuration);
            selectedMedecin.setEmail(email);
            selectedMedecin.setConfiguration(configuration);
            medecinFacade.create(selectedMedecin);
            user.setMedecin(selectedMedecin);
            user.setType(1);
        } else if (typeUser.equals("Patient")) {
            selectedPatient.setEmail(email);
            patientFacade.create(selectedPatient);
            user.setPatient(selectedPatient);
            user.setType(3);
        }
        String pass = userFacade.getGeneratePass();
        user.setPassword(pass);
        userFacade.create(user);
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

    public Medecin getSelectedMedecin() {
        if (selectedMedecin == null) {
            selectedMedecin = new Medecin();
        }
        return selectedMedecin;
    }

    public void setSelectedMedecin(Medecin selectedMedecin) {
        this.selectedMedecin = selectedMedecin;
    }

    public Patient getSelectedPatient() {
        if (selectedPatient == null) {
            selectedPatient = new Patient();
        }
        return selectedPatient;
    }

    public void setSelectedPatient(Patient selectedPatient) {
        this.selectedPatient = selectedPatient;
    }

    public String getTypeUser() {
        return typeUser;
    }

    public void setTypeUser(String typeUser) {
        this.typeUser = typeUser;
    }

    public User getUser() {
        if (user == null) {
            user = new User();
        }
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Configuration getConfiguration() {
        if (configuration == null) {
            configuration = new Configuration();
        }
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * Creates a new instance of InscriptionController
     */
    public InscriptionController() {
    }

}
