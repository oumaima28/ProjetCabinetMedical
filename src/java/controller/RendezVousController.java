package controller;

import bean.Medecin;
import bean.Patient;
import bean.RendezVous;
import controller.util.JsfUtil;
import controller.util.JsfUtil.PersistAction;
import service.RendezVousFacade;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("rendezVousController")
@SessionScoped
public class RendezVousController implements Serializable {

    @EJB
    private service.RendezVousFacade ejbFacade;
    private List<RendezVous> items = null;
    private RendezVous selected;
    //Attribut Recherche

    private Date dateMin;
    private Date dateMax;
    private Patient patient;
    private Medecin medecin;

    public void search() {
        items = ejbFacade.search(patient, medecin, dateMin, dateMax);
    }

    public RendezVousController() {
    }

    public Date getDateMin() {
        return dateMin;
    }

    public void setDateMin(Date dateMin) {
        this.dateMin = dateMin;
    }

    public Date getDateMax() {
        return dateMax;
    }

    public void setDateMax(Date dateMax) {
        this.dateMax = dateMax;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Medecin getMedecin() {
        return medecin;
    }

    public void setMedecin(Medecin medecin) {
        this.medecin = medecin;
    }
    
    public void delete(RendezVous rdv){
        ejbFacade.remove(rdv);
    }
    
    public RendezVous getSelected() {
        return selected;
    }

    public void setSelected(RendezVous selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private RendezVousFacade getFacade() {
        return ejbFacade;
    }

    public RendezVous prepareCreate() {
        selected = new RendezVous();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("RendezVousCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("RendezVousUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("RendezVousDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<RendezVous> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction == PersistAction.CREATE) {
                    int res = getFacade().save(selected);
                    if (res < 0) {
                        JsfUtil.addErrorMessage("Impossible d'ajouter un rdv dans cette date");
                    }
                } else if (persistAction == PersistAction.DELETE) {
                    getFacade().remove(selected);
                    JsfUtil.addSuccessMessage(successMessage);
                } else {
                    getFacade().edit(selected);
                    JsfUtil.addSuccessMessage(successMessage);
                }
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public RendezVous getRendezVous(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<RendezVous> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<RendezVous> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = RendezVous.class)
    public static class RendezVousControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            RendezVousController controller = (RendezVousController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "rendezVousController");
            return controller.getRendezVous(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof RendezVous) {
                RendezVous o = (RendezVous) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), RendezVous.class.getName()});
                return null;
            }
        }

    }

}
