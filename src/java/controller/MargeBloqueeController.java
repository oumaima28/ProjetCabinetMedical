package controller;

import bean.MargeBloquee;
import bean.MargeItem;
import controller.util.JsfUtil;
import controller.util.JsfUtil.PersistAction;
import service.MargeBloqueeFacade;

import java.io.Serializable;
import java.util.ArrayList;
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
import service.MargeItemFacade;

@Named("margeBloqueeController")
@SessionScoped
public class MargeBloqueeController implements Serializable {

    @EJB
    private service.MargeBloqueeFacade ejbFacade;
    private List<MargeBloquee> items = null;
    private MargeBloquee selected;

    @EJB
    private MargeItemFacade margeItemFacade;
    private ScheduleView scheduleView;
    
    //MargeItem Taken From View
    private MargeItem selectedMargeItem;
    private List<MargeItem> margeItems = null;

    public void save() {
        selected.setId(ejbFacade.generateId("MargeBloquee", "id"));
        create();
        margeItemFacade.save(selected, margeItems);
    }

    public void addMargeItem() {
        selectedMargeItem.setId(margeItemFacade.generateId("MargeItem", "id"));
        margeItems.add(margeItemFacade.clone(selectedMargeItem));
    }

    public void removeMargeItem() {
        margeItems.remove(selectedMargeItem);
    }

    public List<Integer> countJours() {
        List<Integer> list = new ArrayList<>();
        for (int i = 1; i <= 31; i++) {
            list.add(i);
        }
        return list;
    }

    public MargeBloqueeController() {
    }

    public MargeBloquee getSelected() {
        if (selected == null) {
            selected = new MargeBloquee();
        }
        return selected;
    }

    public void setSelected(MargeBloquee selected) {
        this.selected = selected;
    }

    public List<MargeItem> getMargeItems() {
        if (margeItems == null) {
            margeItems = new ArrayList<>();
        }
        return margeItems;
    }

    public void setMargeItems(List<MargeItem> margeItems) {
        this.margeItems = margeItems;
    }

    public MargeItem getSelectedMargeItem() {
        if (selectedMargeItem == null) {
            selectedMargeItem = new MargeItem();
        }
        return selectedMargeItem;
    }

    public void setSelectedMargeItem(MargeItem selectedMargeItem) {
        this.selectedMargeItem = selectedMargeItem;
    }

    public List<MargeBloquee> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private MargeBloqueeFacade getFacade() {
        return ejbFacade;
    }

    public MargeBloquee prepareCreate() {
        selected = new MargeBloquee();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("MargeBloqueeCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("MargeBloqueeUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("MargeBloqueeDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
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

    public MargeBloquee getMargeBloquee(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<MargeBloquee> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<MargeBloquee> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = MargeBloquee.class)
    public static class MargeBloqueeControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MargeBloqueeController controller = (MargeBloqueeController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "margeBloqueeController");
            return controller.getMargeBloquee(getKey(value));
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
            if (object instanceof MargeBloquee) {
                MargeBloquee o = (MargeBloquee) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), MargeBloquee.class.getName()});
                return null;
            }
        }

    }

}
