package controller;

import bean.MargeBloquee;
import bean.MargeItem;
import controller.util.JsfUtil;
import controller.util.JsfUtil.PersistAction;
import controller.util.Session;
import service.MargeItemFacade;

import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("margeItemController")
@SessionScoped
public class MargeItemController implements Serializable {

    @EJB
    private service.MargeItemFacade ejbFacade;
    private List<MargeItem> items = null;
    private MargeItem selected;

    //Recherche MargeItem
    private Date heureDebutMin;
    private Date heureDebutMax;
    private Date heureFinMin;
    private Date heureFinMax;
    private int jourMin;
    private int jourMax;
    private int anneeMin;
    private int anneeMax;
    private int moisMin;
    private int moisMax;
    // private List<MargeItem> margeItems =null;

    //init items from margeBloquee details
    public void initItems() {
        if (Session.getAttribut("selectedMargeBloqueeForDetails") != null) {
            items = ejbFacade.findByMargeBloquee((MargeBloquee) Session.getAttribut("selectedMargeBloqueeForDetails"));
        } else {
            items = getFacade().findAll();
        }
    }

    //rechercher margeItem
    public void rechercherMargeItem() {
        items = ejbFacade.rechercher(heureDebutMin, heureDebutMax, heureFinMin, heureFinMax, jourMin, jourMax, moisMin, moisMax, anneeMin, anneeMax);
    }

    //modifier a margeItem from rechercher view
    public void modifierMargeItem() {
        ejbFacade.modifier(selected);
        items = null;
    }

    public void editPopUp(MargeItem margeItem){
        selected=margeItem;
    }
    
    //deleting a margeItem form datalist and db in rechercher view
    public void deleteMargeItem(MargeItem margeItem) {
        //System.out.println(selected);
        ejbFacade.delete(margeItem);
        items = ejbFacade.findByMargeBloquee((MargeBloquee) Session.getAttribut("selectedMargeBloqueeForDetails"));
    }

    public MargeItemController() {
    }

    public MargeItem getSelected() {
        if (selected == null) {
            selected = new MargeItem();
        }
        return selected;
    }

    public void setSelected(MargeItem selected) {
        this.selected = selected;
    }

    public MargeItemFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(MargeItemFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

    public Date getHeureDebutMin() {
        return heureDebutMin;
    }

    public void setHeureDebutMin(Date heureDebutMin) {
        this.heureDebutMin = heureDebutMin;
    }

    public Date getHeureDebutMax() {
        return heureDebutMax;
    }

    public void setHeureDebutMax(Date heureDebutMax) {
        this.heureDebutMax = heureDebutMax;
    }

    public Date getHeureFinMin() {
        return heureFinMin;
    }

    public void setHeureFinMin(Date heureFinMin) {
        this.heureFinMin = heureFinMin;
    }

    public Date getHeureFinMax() {
        return heureFinMax;
    }

    public void setHeureFinMax(Date heureFinMax) {
        this.heureFinMax = heureFinMax;
    }

    public int getJourMin() {
        return jourMin;
    }

    public void setJourMin(int jourMin) {
        this.jourMin = jourMin;
    }

    public int getJourMax() {
        return jourMax;
    }

    public void setJourMax(int jourMax) {
        this.jourMax = jourMax;
    }

    public int getAnneeMin() {
        return anneeMin;
    }

    public void setAnneeMin(int anneeMin) {
        this.anneeMin = anneeMin;
    }

    public int getAnneeMax() {
        return anneeMax;
    }

    public void setAnneeMax(int anneeMax) {
        this.anneeMax = anneeMax;
    }

    public int getMoisMin() {
        return moisMin;
    }

    public void setMoisMin(int moisMin) {
        this.moisMin = moisMin;
    }

    public int getMoisMax() {
        return moisMax;
    }

    public void setMoisMax(int moisMax) {
        this.moisMax = moisMax;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private MargeItemFacade getFacade() {
        return ejbFacade;
    }

    public MargeItem prepareCreate() {
        selected = new MargeItem();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("MargeItemCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("MargeItemUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("MargeItemDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<MargeItem> getItems() {
        if (Session.getAttribut("selectedMargeBloqueeForDetails") != null) {
            items = ejbFacade.findByMargeBloquee((MargeBloquee) Session.getAttribut("selectedMargeBloqueeForDetails"));
            Session.clear();

        } else {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
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

    public MargeItem getMargeItem(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<MargeItem> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<MargeItem> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = MargeItem.class)
    public static class MargeItemControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MargeItemController controller = (MargeItemController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "margeItemController");
            return controller.getMargeItem(getKey(value));
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
            if (object instanceof MargeItem) {
                MargeItem o = (MargeItem) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), MargeItem.class.getName()});
                return null;
            }
        }

    }

}
