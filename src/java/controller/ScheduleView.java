package controller;

import bean.RendezVous;
import controller.util.JsfUtil;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import service.RendezVousFacade;

@ManagedBean
@ViewScoped
public class ScheduleView implements Serializable {

    private ScheduleModel eventModel;

    private ScheduleModel lazyEventModel;

    private DefaultScheduleEvent event = new DefaultScheduleEvent();
    @EJB
    private RendezVousFacade rendezVousFacade;

    private RendezVous selected;

    @PostConstruct
    public void init() {
        eventModel = new DefaultScheduleModel();
        initAgenda();
    }

    public void addRendezVous() {
        //rendezVousFacade.create(selected);
        int res = rendezVousFacade.save(selected);
        System.out.println(selected.getDateRdv());
      
        if (res < 0) {
            FacesMessage message=new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Rendez Vous Non Disponible");
            addMessage(message);
        } else {
            System.out.println(selected.getDateRdv());
            Long var = selected.getDateRdv().getTime() + selected.getMedecin().getConfiguration().getPas() * 60 * 1000;
            System.out.println(selected.getDateRdv());
            
            Date dateFin = new Date();
            dateFin.setTime(var);
            System.out.println(selected.getDateRdv());
            System.out.println(dateFin);
            eventModel.addEvent(new DefaultScheduleEvent(selected.getPatient().getNom(), selected.getDateRdv(), dateFin));
        }
    }

    public void initAgenda() {
        for (RendezVous rendezVous : rendezVousFacade.findAll()) {
            Long var = rendezVous.getDateRdv().getTime() + rendezVous.getMedecin().getConfiguration().getPas() * 60 * 1000;
            Date dateFin = rendezVous.getDateRdv();
            dateFin.setTime(var);
            eventModel.addEvent(new DefaultScheduleEvent(rendezVous.getPatient().getNom(), rendezVous.getDateRdv(), dateFin));
        }
    }

//    private Date calculDateDebutRdv(RendezVous rendezVous){
//        Calendar t=Calendar.getInstance();
//        Date date=rendezVous.getDateRdv();
//        Date heure=rendezVous.getHeure();
//        t.set(date.getYear()+1900, date.getMonth(), date.getDate(), heure.getHours(), heure.getMinutes(), heure.getSeconds());
//        return t.getTime();
//    }
//    
//    private Date calculDateFinRdv(RendezVous rendezVous){
//        Calendar t=Calendar.getInstance();
//        Date dateDeb=calculDateDebutRdv(rendezVous);
//        Date DateFin=dateDeb.
//    }
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

    private Date previousDay8Pm() {
        Calendar t = (Calendar) today().clone();
        t.set(Calendar.AM_PM, Calendar.PM);
        t.set(Calendar.DATE, t.get(Calendar.DATE) - 1);
        t.set(Calendar.HOUR, 8);

        return t.getTime();
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
        t.set(Calendar.DATE, t.DAY_OF_MONTH+1);
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

    private Date nextDay9Am() {
        Calendar t = (Calendar) today().clone();
        t.set(Calendar.AM_PM, Calendar.AM);
        t.set(Calendar.DATE, t.get(Calendar.DATE) + 1);
        t.set(Calendar.HOUR, 9);

        return t.getTime();
    }

    private Date nextDay11Am() {
        Calendar t = (Calendar) today().clone();
        t.set(Calendar.AM_PM, Calendar.AM);
        t.set(Calendar.DATE, t.get(Calendar.DATE) + 1);
        t.set(Calendar.HOUR, 11);

        return t.getTime();
    }

    private Date fourDaysLater3pm() {
        Calendar t = (Calendar) today().clone();
        t.set(Calendar.AM_PM, Calendar.PM);
        t.set(Calendar.DATE, t.get(Calendar.DATE) + 4);
        t.set(Calendar.HOUR, 3);

        return t.getTime();
    }

    private Date fourDaysLater6pm() {
        Calendar t = (Calendar) today().clone();
        t.set(Calendar.AM_PM, Calendar.PM);
        t.set(Calendar.DATE, t.get(Calendar.DATE) + 4);
        t.set(Calendar.HOUR, 6);

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
        event = (DefaultScheduleEvent) (ScheduleEvent) selectEvent.getObject();
        selected.setDateRdv((Date) selectEvent.getObject());
    }

    public void onDateSelect(SelectEvent selectEvent) {
        event = new DefaultScheduleEvent("", (Date) selectEvent.getObject(), (Date) selectEvent.getObject());
        selected.setDateRdv((Date) selectEvent.getObject());
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

}
