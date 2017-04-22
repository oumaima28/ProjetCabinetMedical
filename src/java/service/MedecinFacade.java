/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Configuration;
import bean.Medecin;
import bean.Residence;
import bean.Specialite;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author moi
 */
@Stateless
public class MedecinFacade extends AbstractFacade<Medecin> {

    public List<Medecin> findMedecinByResidence(Residence residence) {
        return em.createQuery("SELECT med FROM Medecin med WHERE med.residence.nom = '" + residence.getNom() + "'").getResultList();
    }

     public List<Medecin> search(String nom, String prenom, String ville, Residence residence, Specialite specialite, Configuration configuration) {
        System.out.println("ici c est le service ");
        String req = "SELECT m FROM Medecin m WHERE 1=1 ";
        if (!nom.equals("")) {
            req += " AND m.nom ='" + nom + "'";
        }
        if (!prenom.equals("")) {
            req += " AND m.prenom ='" + prenom + "'";
        }
        if (!ville.equals("")) {
            req += " AND m.ville ='" + ville + "'";
        }
        if (residence != null) {
            req += " AND m.residence.nom ='" + residence.getNom() + "'";
        }
        if (specialite != null) {
            req += " AND m.specialite.id='" + specialite.getId() + "'";
        }
        if (configuration != null) {
            req += " AND m.configuration.id ='" + configuration.getId() + "'";
        }
        return em.createQuery(req).getResultList();

    }

    @PersistenceContext(unitName = "GestionCabinetMedicalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MedecinFacade() {
        super(Medecin.class);
    }

}
