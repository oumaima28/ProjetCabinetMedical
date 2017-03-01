/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Medecin;
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

        List<RendezVous> rendezVousesMed = findByMedecin(rendezVous.getMedecin());
        for (RendezVous rendezVousMedecin : rendezVousesMed) {
            if (rendezVousMedecin.getDateRdv().equals(rendezVous.getDateRdv())) {
                return -1;
            }
        }
        edit(rendezVous);
        return 1;
    }

    public List<RendezVous> findByMedecin(Medecin medecin) {
        return em.createQuery("SELECT rdv FROM RendezVous rdv WHERE rdv.medecin.id ='" + medecin.getId() + "'").getResultList();
    }

    public int mofifier(RendezVous rendezVous, RendezVous modifiedRendezVous) {
        Date dateActuelle = new Date();
        if (modifiedRendezVous.getDateRdv().getTime() > dateActuelle.getTime()) {
            RendezVous loadedRendezVous = find(rendezVous.getId());
            loadedRendezVous.setDateRdv(modifiedRendezVous.getDateRdv());
            loadedRendezVous.setMedecin(modifiedRendezVous.getMedecin());
            loadedRendezVous.setPatient(modifiedRendezVous.getPatient());
            edit(loadedRendezVous);
            return 1;
        } else {
            return -1;
        }
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RendezVousFacade() {
        super(RendezVous.class);
    }

}
