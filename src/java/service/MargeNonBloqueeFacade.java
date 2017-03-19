/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.MargeNonBloquee;
import bean.Medecin;
import bean.Vacance;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author moi
 */
@Stateless
public class MargeNonBloqueeFacade extends AbstractFacade<MargeNonBloquee> {

    @EJB
    private MedecinFacade medecinFacade;
    @EJB
    private VacanceFacade vacanceFacade;
    
    @PersistenceContext(unitName = "GestionCabinetMedicalPU")
    private EntityManager em;

    
    public List<MargeNonBloquee> findByMedecin(Medecin medecin){
          return em.createQuery("SELECT m FROM MargeNonBloquee m WHERE m.medecin.id = '"+medecin.getId()+'"').getResultList();
      }
      

    public int saveByMedecin(Medecin medecin) {
        if (medecin == null) {
            return -1;
        } else {
            List<Vacance> vacances = vacanceFacade.findByResidence(medecin.getResidence());
            for (Vacance vacance : vacances) {
                createMargeNonBloquee(medecin, vacance);
            }
            return 1;
        }
    }

    public void createMargeNonBloquee(Medecin medecin, Vacance vacance) {
        MargeNonBloquee marge = new MargeNonBloquee();
        marge.setDateDebut(vacance.getDateDebut());
        marge.setDateFin(vacance.getDateFin());
        marge.setNom(vacance.getNom());
        marge.setMedecin(medecin);
        create(marge);
    }

    public int saveByVacance(Vacance vacance) {
        if (vacance == null) {
            return -1;
        } else {
            System.out.println(medecinFacade.findMedecinByResidence(vacance.getResidence()));
            List<Medecin> medecins = medecinFacade.findMedecinByResidence(vacance.getResidence());
            for (Medecin medecin : medecins) {
                createMargeNonBloquee(medecin, vacance);
            }
            return 1;
        }
    }
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MargeNonBloqueeFacade() {
        super(MargeNonBloquee.class);
    }
    
}
