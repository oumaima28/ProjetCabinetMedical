/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.MargeBloquee;
import bean.MargeItem;
import bean.Medecin;
import controller.util.SearchUtil;
import java.util.Date;
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
public class MargeBloqueeFacade extends AbstractFacade<MargeBloquee> {
    
    @EJB
    private MargeItemFacade margeItemFacade;

    @PersistenceContext(unitName = "GestionCabinetMedicalPU")
    private EntityManager em;

    public List<MargeBloquee> rechercher(Medecin medecin, Date dateDebutMin, Date dateDebutMax, Date dateFinMin, Date dateFinMax) {
        String query = "Select m FROM MargeBloquee m WHERE 1=1";
//        if(medecin!=null){
//        query += SearchUtil.addConstraint("m", "medecin.id", "=", medecin.getId());
//        }
        if(medecin!=null){
            query+=" AND m.medecin.id ='"+medecin.getId()+"'";
        }
        if(dateDebutMin!=null){
            query+=" AND m.dateDebut >= '"+SearchUtil.convertToSqlDate(dateDebutMin)+"'";
        }
        if(dateDebutMax!=null){
            query+=" AND m.dateDebut <= '"+SearchUtil.convertToSqlDate(dateDebutMax)+"'";
        }
        if(dateFinMin!=null){
            query+=" AND m.dateFin >= '"+SearchUtil.convertToSqlDate(dateFinMin)+"'";
        }
        if(dateFinMax!=null){
            query+=" AND m.dateFin <= '"+SearchUtil.convertToSqlDate(dateFinMax)+"'";
        }
        //query += SearchUtil.addConstraintMinMaxDate("m", "dateDebut", dateDebutMin, dateDebutMax);
       //query += SearchUtil.addConstraintMinMaxDate("m", "dateFin", dateFinMin, dateFinMax);
        return em.createQuery(query).getResultList();
    }

    public void modifier(MargeBloquee modifiedMargeBloquee) {
        MargeBloquee loadedMargeBloquee = find(find(modifiedMargeBloquee));
        loadedMargeBloquee.setDateDebut(modifiedMargeBloquee.getDateDebut());
        loadedMargeBloquee.setDateFin(modifiedMargeBloquee.getDateFin());
        loadedMargeBloquee.setMedecin(modifiedMargeBloquee.getMedecin());
    }

    public void delete(MargeBloquee margeBloquee) {
        System.out.println("2");
        System.out.println(margeBloquee);
        margeItemFacade.deleteByMargeBloquee(margeBloquee);
        System.out.println("4");
        remove(margeBloquee);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MargeBloqueeFacade() {
        super(MargeBloquee.class);
    }

}