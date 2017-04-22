/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Medecin;
import bean.Patient;
import bean.RendezVous;
import controler.util.HashageUtil;
import controller.util.DateUtil;
import controller.util.SearchUtil;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.ScheduleEvent;

/**
 *
 * @author moi
 */
@Stateless
public class RendezVousFacade extends AbstractFacade<RendezVous> {

    @PersistenceContext(unitName = "GestionCabinetMedicalPU")
    private EntityManager em;

    public int save(RendezVous rendezVous) {
        List<RendezVous> rendezVousesMed = findByMedecin(rendezVous.getMedecin());
        for (RendezVous rendezVousMedecin : rendezVousesMed) {
            if (rendezVousMedecin.getDateRdv().equals(rendezVous.getDateRdv())) {
                return -1;
            }
        }
        create(rendezVous);
        return 1;
    }

    public List<RendezVous> search(Patient patient, Medecin medecin, Date dateMin, Date dateMax) {
        System.out.println("mm" + medecin);
        String req = "SELECT rdv FROM RendezVous rdv WHERE 1=1";

        if (patient != null) {
            req += " AND rdv.patient.cin = '" + patient.getCin() + "'";
        }
        if (medecin != null) {
            req += " AND rdv.medecin.id = '" + medecin.getId() + "'";
        }
        if (dateMin != null && dateMax != null) {
            req += controller.util.DateUtil.addConstraintMinMaxTimestamp("rdv", "dateRdv", dateMin, dateMax);
        }
        return em.createQuery(req).getResultList();
    }

    public List<RendezVous> findByMedecin(Medecin medecin) {
        return em.createQuery("SELECT rdv FROM RendezVous rdv WHERE rdv.medecin.id = '" + medecin.getId() + "'").getResultList();
    }

    public RendezVous findByAttribut(Medecin medecin, Patient patient, Date dateRendv) {
        String req = "SELECT rdv FROM RendezVous rdv WHERE 1=1";
        if (patient != null) {
            req += " AND rdv.patient.cin = '" + patient.getCin() + "'";
        }
        if (medecin != null) {
            req += " AND rdv.medecin.id = '" + medecin.getId() + "'";
        }
        if (dateRendv != null) {
            req += " AND rdv.dateRdv = '" + SearchUtil.convertToSqlTimeStamp(dateRendv) + "'";
        }
        List<RendezVous> rdvs = em.createQuery(req).getResultList();
        if (rdvs.isEmpty()) {
            return null;
        } else {
            return rdvs.get(0);
        }
    }

    public int modifier(RendezVous loadedRendezVous, RendezVous modifiedRendezVous) {
        if (modifiedRendezVous.getDateRdv().getTime() > System.currentTimeMillis()) {
            loadedRendezVous.setDateRdv(modifiedRendezVous.getDateRdv());
            loadedRendezVous.setMedecin(modifiedRendezVous.getMedecin());
            loadedRendezVous.setPatient(modifiedRendezVous.getPatient());
            edit(loadedRendezVous);
            return 1;
        } else {
            return -1;
        }
    }
    
    public void deleteByMedecin(Medecin medecin) {
        if (medecin != null) {
            List<RendezVous> rdvs = findByMedecin(medecin);
            for (RendezVous rdv : rdvs) {
                remove(rdv);
            }
        }
    }

    

    public Date calculDateFin(RendezVous rdv) {
        Long var = rdv.getDateRdv().getTime() + rdv.getMedecin().getConfiguration().getPas() * 60 * 1000;
        Date dateFin = new Date();
        dateFin.setTime(var);
        return dateFin;
    }

    public void eventMove(ScheduleEvent event, int dayDelta, int minuteDelta){
        RendezVous loadedRendezVous = find(Long.parseLong(event.getId()) * 1L);
        System.out.println(loadedRendezVous);
        RendezVous newRdv=new RendezVous();
        newRdv.setDateRdv(loadedRendezVous.getDateRdv());
        newRdv.setId(generateId("RendezVous", "id"));
        newRdv.setMedecin(loadedRendezVous.getMedecin());
        newRdv.setPatient(loadedRendezVous.getPatient());
        remove(loadedRendezVous);
        create(newRdv);
        event.setId(""+newRdv.getId());
        System.out.println("ha id"+event.getId());
    }
    
    public RendezVous findRdvByEventId(String eventId){
        System.out.println("hani");
        List<RendezVous> list=findAll();
        int i=list.size();
        for (RendezVous rdv : list) {
            i--;
            System.out.println(rdv);
            String hashedId = HashageUtil.sha256(""+rdv.getId());
            System.out.println(hashedId);
            System.out.println(rdv);
            if(hashedId.equals(eventId)){
                System.out.println(rdv);
                return rdv;
            }
            if(i==0){
                break;
            }
        }
        return null;
    }
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RendezVousFacade() {
        super(RendezVous.class);
    }
}
