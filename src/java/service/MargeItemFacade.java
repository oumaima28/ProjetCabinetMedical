/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.MargeBloquee;
import bean.MargeItem;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.primefaces.model.DefaultScheduleEvent;

/**
 *
 * @author moi
 */
@Stateless
public class MargeItemFacade extends AbstractFacade<MargeItem> {

    public void clone(MargeItem margeItemSource, MargeItem margeItemDestination) {
        margeItemDestination.setId(margeItemSource.getId());
        margeItemDestination.setHeureDebut(margeItemSource.getHeureDebut());
        margeItemDestination.setHeureFin(margeItemSource.getHeureFin());
        margeItemDestination.setAnnee(margeItemSource.getAnnee());
        margeItemDestination.setMois(margeItemSource.getMois());
        margeItemDestination.setJour(margeItemSource.getJour());
        margeItemDestination.setMargeBloquee(margeItemSource.getMargeBloquee());
    }

    public MargeItem clone(MargeItem margeItem) {
        MargeItem cloned = new MargeItem();
        clone(margeItem, cloned);
        return cloned;
    }

    public void save(MargeBloquee margeBloquee, List<MargeItem> margeItems) {
        for (MargeItem margeItem : margeItems) {
            margeItem.setMargeBloquee(margeBloquee);
            create(margeItem);
        }
    }

    public List<DefaultScheduleEvent> createEventListFromMargeItem(MargeItem margeItem) {
        List<DefaultScheduleEvent> margeEvents = new ArrayList<>();
        Date deb = new Date();
        Date fin = new Date();
        deb.setTime(margeItem.getMargeBloquee().getDateDebut().getTime() + margeItem.getHeureDebut().getTime());
        fin.setTime(margeItem.getMargeBloquee().getDateFin().getTime() + margeItem.getHeureFin().getTime());
        Date dateDebEvent = deb;
        while (dateDebEvent.getTime() < fin.getTime()) {
            Date dateFinEvent = new Date();
            Date d = new Date();
            d.setTime(dateDebEvent.getTime());
            dateFinEvent.setTime(dateDebEvent.getTime());
            dateFinEvent.setHours(margeItem.getHeureFin().getHours());
            dateFinEvent.setMinutes(margeItem.getHeureFin().getMinutes());
            dateFinEvent.setSeconds(margeItem.getHeureFin().getSeconds());
            DefaultScheduleEvent margeItemEvent = new DefaultScheduleEvent("Marge Bloquée", d, dateFinEvent);
            margeEvents.add(margeItemEvent);
            if (margeItem.getAnnee() != 0) {
                dateDebEvent.setYear(dateDebEvent.getYear() + margeItem.getAnnee());
            } else if (margeItem.getMois() != 0) {
                dateDebEvent.setMonth(dateDebEvent.getMonth() + margeItem.getMois());
            } else if (margeItem.getJour() != 0) {
                dateDebEvent.setTime(dateDebEvent.getTime() + (margeItem.getJour() * 24 * 60 * 60 * 1000));
            }
        }
        return margeEvents;
    }

    public void deleteByMargeBloquee(MargeBloquee margeBloquee) {
//        String req = "DELETE FROM MargeItem m WHERE m.margeBloquee.id ='" + margeBloquee.getId()+ "'";
//        em.getTransaction().begin();
//        em.createQuery(req).executeUpdate();
//        em.getTransaction().commit();
        System.out.println("3");
        System.out.println(findByMargeBloquee(margeBloquee));
        List <MargeItem> list=findByMargeBloquee(margeBloquee);
        int i=list.size();
        for (MargeItem margeItem : findByMargeBloquee(margeBloquee)) {
            i--;
            remove(margeItem);
            if(i==0) break;
        }
    }

    public void modifier(MargeItem modifiedMargeItem) {
        MargeItem loadedMargeItem = find(modifiedMargeItem.getId());
        loadedMargeItem.setAnnee(modifiedMargeItem.getAnnee());
        loadedMargeItem.setJour(modifiedMargeItem.getJour());
        loadedMargeItem.setMois(modifiedMargeItem.getMois());
        loadedMargeItem.setHeureDebut(modifiedMargeItem.getHeureDebut());
        loadedMargeItem.setHeureFin(modifiedMargeItem.getHeureFin());
        loadedMargeItem.setMargeBloquee(modifiedMargeItem.getMargeBloquee());
        edit(loadedMargeItem);
    }

    public List<MargeItem> findByMargeBloquee(MargeBloquee margeBloquee) {
        return em.createQuery("SELECT m FROM MargeItem m WHERE m.margeBloquee.id ='"+margeBloquee.getId()+"'").getResultList();
    }

    @PersistenceContext(unitName = "GestionCabinetMedicalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MargeItemFacade() {
        super(MargeItem.class);
    }

}
