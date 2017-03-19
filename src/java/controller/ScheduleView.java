package controller;

import bean.MargeBloquee;
import bean.MargeItem;
import bean.MargeNonBloquee;
import bean.Medecin;
import bean.RendezVous;
import controller.util.JsfUtil;
import controller.util.Session;
import java.io.Serializable;
import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import jdk.nashorn.internal.objects.annotations.Constructor;
import org.primefaces.context.RequestContext;

import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import service.MargeItemFacade;
import service.MargeNonBloqueeFacade;
import service.PatientFacade;
import service.RendezVousFacade;

@ManagedBean
@ViewScoped
public class ScheduleView implements Serializable {

    private ScheduleModel eventModel;

    private ScheduleModel lazyEventModel;

    private DefaultScheduleEvent event = new DefaultScheduleEvent();

    @EJB
    private RendezVousFacade rendezVousFacade;
    @EJB
    private MargeItemFacade margeItemFacade;
    @EJB
    private PatientFacade patientFacade;
    @EJB
    private MargeNonBloqueeFacade margeNonBloqueeFacade;
    private Medecin medecin;
    private Medecin selectedMedecin;

    private RendezVous selected;
    private int nbrEvent;

    @PostConstruct
    public void init() {
        eventModel = new DefaultScheduleModel();
//        Session.clear();
//        medecin=(Medecin) Session.getAttribut("selectedMedecinForAgenda");

    }

    public void initAllAgendaEvents() {
        eventModel.clear();
        initAgenda();
        initEventListFromMargeItems();
        initMargeNonBloquee();
    }

//    public String setCombo(){
//        Session.createAtrribute(selectedMedecin, "selectedMedecinForAgenda");
//        return "/agenda/schedule.xhtml";
//    }
    public void addRendezVous() {
        int res = rendezVousFacade.save(selected);
        if (res > 0) {
            Long var = selected.getDateRdv().getTime() + selected.getMedecin().getConfiguration().getPas() * 60 * 1000;
            Date dateFin = new Date();
            dateFin.setTime(var);
            DefaultScheduleEvent rdvEvent = new DefaultScheduleEvent(selected.getPatient().getCin(), selected.getDateRdv(), dateFin);
            //rdvEvent.setId(""+selected.getId());
            eventModel.addEvent(rdvEvent);

        }
    }

    public void initAgenda() {
        for (RendezVous rendezVous : rendezVousFacade.findByMedecin(medecin)) {
            Long var = rendezVous.getDateRdv().getTime() + rendezVous.getMedecin().getConfiguration().getPas() * 60 * 1000;
            Date dateFin = new Date();
            dateFin.setTime(var);
            DefaultScheduleEvent eventRdv = new DefaultScheduleEvent(rendezVous.getPatient().getCin(), rendezVous.getDateRdv(), dateFin);
            eventModel.addEvent(eventRdv);
            eventRdv.setStyleClass("colorRdv");
        }
    }

    public void addEventListFromMargeItem(MargeItem margeItem) {
        List<DefaultScheduleEvent> margeEvents = margeItemFacade.createEventListFromMargeItem(margeItem);
        for (DefaultScheduleEvent margeEvent : margeEvents) {
            eventModel.addEvent(margeEvent);
            margeEvent.setStyleClass("colorMargeBloquee");
        }
    }

    public void initEventListFromMargeItems() {
        for (MargeItem margeItem : margeItemFacade.findByMedecin(medecin)) {
            addEventListFromMargeItem(margeItem);
        }
    }

    public void initMargeNonBloquee() {
        for (MargeNonBloquee margeNonBloquee : margeNonBloqueeFacade.findByMedecin(medecin)) {
            DefaultScheduleEvent margeNonBloqueeEvent = new DefaultScheduleEvent(margeNonBloquee.getNom(), margeNonBloquee.getDateDebut(), margeNonBloquee.getDateFin());
            margeNonBloqueeEvent.setAllDay(true);
            eventModel.addEvent(margeNonBloqueeEvent);
            margeNonBloqueeEvent.setStyleClass("colorMargeNonBloquee");
        }
    }

    public void modifierRdv() {
        RendezVous rdv = new RendezVous();
        rdv.setPatient(patientFacade.find(event.getTitle()));
        rdv.setDateRdv(event.getStartDate());
        rdv.setMedecin(selected.getMedecin());
        event.setTitle(selected.getPatient().getCin());
        event.setStartDate(selected.getDateRdv());
        event.setEndDate(rendezVousFacade.calculDateFin(rdv));
        rendezVousFacade.modifier(rdv, selected);
    }

    public void decideIfMargeEvent(ScheduleEvent scheduleEvent) {
        if (scheduleEvent != null) {
            if (scheduleEvent.getTitle().equals("Marge Bloquée")) {
                RequestContext context = RequestContext.getCurrentInstance();
                context.execute("PF('eventDialog').hide();");
            }
        }
    }

    public void deleteEventFromMargeItem(ScheduleEvent scheduleEvent) {
        eventModel.deleteEvent(scheduleEvent);
    }

    public void deleteRdv() {
        RendezVous rdv = rendezVousFacade.findByAttribut(selected.getMedecin(), selected.getPatient(), selected.getDateRdv());
        if (rdv == null) {
            JsfUtil.addErrorMessage("rdv non trouvee");
        } else {
            rendezVousFacade.remove(rdv);
            eventModel.deleteEvent(event);
        }
    }

    public Date getRandomDate(Date base) {
        Calendar date = Calendar.getInstance();
        date.setTime(base);
        date.add(Calendar.DATE, ((int) (Math.random() * 30)) + 1);    //set random day of month

        return date.getTime();
    }

    public Date getInitialDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), Calendar.FEBRUARY, calendar.get(Calendar.DATE), 0, 0, 0);

        return calendar.getTime();
    }

    public ScheduleModel getEventModel() {
        return eventModel;
    }

    public ScheduleModel getLazyEventModel() {
        return lazyEventModel;
    }

    private Date calculDateDebutRdv(RendezVous rendezVous) {
        Calendar t = Calendar.getInstance();
        Date date = rendezVous.getDateRdv();
        t.set(date.getYear() + 1900, date.getMonth(), date.getDate(), date.getHours(), date.getMinutes(), date.getSeconds());
        return t.getTime();
    }

    private Calendar today() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);

        return calendar;
    }

    private Date previousDay11Pm() {
        Calendar t = (Calendar) today().clone();
        t.set(Calendar.AM_PM, Calendar.PM);
        t.set(Calendar.DATE, t.get(Calendar.DATE) - 1);
        t.set(Calendar.HOUR, 11);

        return t.getTime();
    }

    private Date today1Pm() {
        Calendar t = (Calendar) today().clone();
        t.set(Calendar.AM_PM, Calendar.PM);
        t.set(Calendar.HOUR, 1);

        return t.getTime();
    }

    private Date theDayAfter3Pm() {
        Calendar t = (Calendar) today().clone();
        t.set(Calendar.DATE, t.get(Calendar.DATE) + 2);
        t.set(Calendar.AM_PM, Calendar.PM);
        t.set(Calendar.HOUR, 3);
        t.set(Calendar.DATE, t.DAY_OF_MONTH + 1);
        return t.getTime();
    }

    private Date today6Pm() {
        Calendar t = (Calendar) today().clone();
        t.set(Calendar.AM_PM, Calendar.PM);
        t.set(Calendar.HOUR, 6);

        return t.getTime();
    }

    private Date nextDay8Am() {
        Calendar t = (Calendar) today().clone();
        t.set(Calendar.AM_PM, Calendar.AM);
        t.set(Calendar.DATE, t.get(Calendar.DATE) + 1);
        t.set(Calendar.HOUR, 8);

        return t.getTime();
    }

    public ScheduleEvent getEvent() {
        return event;
    }

    public void setEvent(ScheduleEvent event) {
        this.event = (DefaultScheduleEvent) event;
    }

    public void addEvent(ActionEvent actionEvent) {
        if (event.getId() == null) {
            eventModel.addEvent(event);
        } else {
            eventModel.updateEvent(event);
        }

        event = new DefaultScheduleEvent();
    }

    public void onEventSelect(SelectEvent selectEvent) {
        nbrEvent = 1;
        event = (DefaultScheduleEvent) (ScheduleEvent) selectEvent.getObject();
        if (!event.getTitle().equals("Marge Bloquée")) {
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('eventDialog').show();");
            selected.setDateRdv(event.getStartDate());
            selected.setPatient(patientFacade.find(event.getTitle()));
        }
    }

    public void onDateSelect(SelectEvent selectEvent) {
        nbrEvent = 0;
        event = new DefaultScheduleEvent("", (Date) selectEvent.getObject(), (Date) selectEvent.getObject());
        selected.setDateRdv((Date) selectEvent.getObject());
    }

    public int getNbrEvent() {
        return nbrEvent;
    }

    public void setNbrEvent(int nbrEvent) {
        this.nbrEvent = nbrEvent;
    }

    public Date getSelectedDate(SelectEvent selectEvent) {
        return (Date) selectEvent.getObject();
    }

    public void onEventMove(ScheduleEntryMoveEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event moved", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());
        addMessage(message);

    }

    public void onEventResize(ScheduleEntryResizeEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event resized", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());
        addMessage(message);
    }

    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public RendezVous getSelected() {
        if (selected == null) {
            selected = new RendezVous();
        }
        return selected;
    }

    public void setSelected(RendezVous selected) {
        this.selected = selected;
    }

    public Medecin getMedecin() {
        if (medecin == null) {
            medecin = new Medecin();
        }
        return medecin;
    }

    public void setMedecin(Medecin medecin) {
        this.medecin = medecin;
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

}
