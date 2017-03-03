/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import java.util.Date;
import bean.MargeBloquee;
import bean.MargeItem;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author moi
 */
@Stateless
public class MargeBloqueeFacade extends AbstractFacade<MargeBloquee> {

    private MargeItemFacade margeItemFacade;

    public void createMargeAndMargeItem(MargeBloquee margeBloquee, List<MargeItem> margeItems) {
        if (margeBloquee.getDateDebut().getTime() >= System.currentTimeMillis()) {
//            margeBloquee.setDateDebut(dateDeb);
//            margeBloquee.setDateFin(dateFin);
            create(margeBloquee);
            for (MargeItem margeItem : margeItems) {
                margeItem.setMargeBloquee(margeBloquee);
                margeItemFacade.create(margeItem);
            }
        }
    }

    @PersistenceContext(unitName = "GestionCabinetMedicalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MargeBloqueeFacade() {
        super(MargeBloquee.class);
    }

}
