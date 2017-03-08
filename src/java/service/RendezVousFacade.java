/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Medecin;
import bean.Patient;
import bean.RendezVous;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author moi
 */
@Stateless
public class RendezVousFacade extends AbstractFacade<RendezVous> {

    @PersistenceContext(unitName = "GestionCabinetMedicalPU")
    private EntityManager em;

    public int save(RendezVous rendezVous) {
        Date dateActuelle = new Date();
        if (rendezVous.getDateRdv().getTime() < dateActuelle.getTime()) {
            return -1;//si la date de rdv est inf a la date actuelle 
        } else {
            List<RendezVous> rendezVousesMed = findByMedecin(rendezVous.getMedecin());
            for (RendezVous rendezVousMedecin : rendezVousesMed) {
                if (rendezVousMedecin.getDateRdv().equals(rendezVous.getDateRdv())) {
                    return -1; //si on a deja un rdv dans la meme date
                }
            }
            create(rendezVous);
            return 1;
        }
    }

    public int modifier(RendezVous rendezVous, RendezVous modifiedRendezVous) {
        Date dateActuelle = new Date();
        if (modifiedRendezVous.getDateRdv().getTime() > dateActuelle.getTime()) {
            RendezVous loadedRendezVous = findByAttribut(rendezVous.getMedecin(), rendezVous.getPatient(), rendezVous.getDateRdv()).get(0);
            loadedRendezVous.setDateRdv(modifiedRendezVous.getDateRdv());
            loadedRendezVous.setPatient(modifiedRendezVous.getPatient());
            edit(loadedRendezVous);
            return 1;
        } else {
            return -1;
        }
    }

    public List<RendezVous> search(Medecin medecin, Patient patient, Date dateMin, Date dateMax) {
        String req = "SELECT rdv FROM RendezVous rdv WHERE 1=1";
        if (medecin != null) {
            req += controller.util.DateUtil.addConstraint("rdv", "medecin.id", "=", medecin.getId());
        }
        if (patient != null) {
            req += controller.util.DateUtil.addConstraint("rdv", "patient.cin", "=", patient.getCin());
        }
        if (dateMin != null && dateMax != null) {
            req += controller.util.DateUtil.addConstraintMinMaxDate("rdv", "dateRdv", dateMin, dateMax);
        }
        return em.createQuery(req).getResultList();
    }

    public Date calculDateFin(RendezVous rdv) {
        Long var = rdv.getDateRdv().getTime() + rdv.getMedecin().getConfiguration().getPas() * 60 * 1000;
        Date dateFin = new Date();
        dateFin.setTime(var);
        return dateFin;
    }

    public List<RendezVous> findByAttribut(Medecin medecin, Patient patient, Date dateRendv) {
        return em.createQuery("SELECT rdv FROM RendezVous rdv WHERE rdv.medecin.id ='" + medecin.getId() + "'AND rdv.patient.cin='" + patient.getCin() + "'" + controller.util.DateUtil.addConstraintDate("rdv", "dateRdv", "=", dateRendv)).getResultList();
    }

    public List<RendezVous> findByMedecin(Medecin medecin) {
        return em.createQuery("SELECT rdv FROM RendezVous rdv WHERE rdv.medecin.id ='" + medecin.getId() + "'").getResultList();
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RendezVousFacade() {
        super(RendezVous.class);
    }
}
