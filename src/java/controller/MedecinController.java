package controller;

import bean.Configuration;
import bean.Medecin;
import bean.Residence;
import bean.Specialite;
import controller.util.JsfUtil;
import controller.util.JsfUtil.PersistAction;
import controller.util.Session;
import controller.util.SessionUtil;
import service.MedecinFacade;

import java.io.Serializable;
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
import service.MargeNonBloqueeFacade;
import service.RendezVousFacade;

@Named("medecinController")
@SessionScoped
public class MedecinController implements Serializable {

    @EJB
    private service.MedecinFacade ejbFacade;
    private List<Medecin> items = null;
    private Medecin selected;
    @EJB
    private RendezVousFacade rendezVousFacade;
    //Attribut Recherche : 
    private String nom;
    private String prenom;
    private String ville;
    private Residence residence;
    private Specialite specialite;
    private Configuration configuration;

    public void search() {
        items = ejbFacade.search(nom, prenom, ville, residence, specialite, configuration);
    }

    public void updatePopUp(Medecin medecin) {
        selected =medecin;
    }

    public Medecin medecinEdit() {
        return selected;
    }

    public void edit() {
        ejbFacade.edit(selected);
    }

    public String view(Medecin medecin) {
        SessionUtil.setAttribute("medecinView", medecin);
        // Session.createAtrribute(medecin, "medecinSelected");
        return "/medecin/Profil.xhtml";
    }

    public Medecin medecinView() {
        return (Medecin) SessionUtil.getAttribute("medecinView");
    }

    public String findSecretaire() {
        Medecin medecin = (Medecin) Session.getAttribut("medecinSelected");
        Session.createAtrribute(medecin, "medecinSecretaire");
        return "/secretaireMedecin/secretairesMedecin.xhtml";
    }

    public String findRdv() {
        Medecin medecin = (Medecin) Session.getAttribut("medecinSelected");
        Session.createAtrribute(medecin, "medecinRdv");
        return "/rendezVous/rendezVousMedecin.xhtml";
    }

    public String findMargeBloquee() {
        Medecin medecin = (Medecin) Session.getAttribut("medecinSelected");
        Session.createAtrribute(medecin, "medecinMargeBloquee");
        return "/margeBloquee/MargeBloqueeMedecin.xhtml";
    }

    public String findMargeNonBloquee() {
        Medecin medecin = (Medecin) Session.getAttribut("medecinSelected");
        Session.createAtrribute(medecin, "medecinMargeNonBloquee");
        return "/margeNonBloquee/MargeNonBloqueeMedecin.xhtml";
    }
    
    public String agendaDetail(Medecin medecin){
        Session.clear();
        Session.createAtrribute(medecin, "selectedMedecinForAgenda");
        return "/agenda/schedule.xhtml";
    }

    public void deleteMedecin(Medecin medecin) {
        rendezVousFacade.deleteByMedecin(medecin);
        ejbFacade.remove(medecin);
        items.remove(items.indexOf(medecin));
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public Residence getResidence() {
        return residence;
    }

    public void setResidence(Residence residence) {
        this.residence = residence;
    }

    public Specialite getSpecialite() {
        return specialite;
    }

    public void setSpecialite(Specialite specialite) {
        this.specialite = specialite;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @EJB
    private MargeNonBloqueeFacade margeNonBloqueeFacade;

    public MedecinController() {
    }

    public Medecin getSelected() {
        if (selected == null) {
            selected = new Medecin();
        }
        return selected;
    }

    public void setSelected(Medecin selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private MedecinFacade getFacade() {
        return ejbFacade;
    }

    public Medecin prepareCreate() {
        selected = new Medecin();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("MedecinCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("MedecinUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("MedecinDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Medecin> getItems() {
        if (items == null) {
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
                    margeNonBloqueeFacade.saveByMedecin(selected);
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

    public Medecin getMedecin(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Medecin> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Medecin> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Medecin.class)
    public static class MedecinControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MedecinController controller = (MedecinController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "medecinController");
            return controller.getMedecin(getKey(value));
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
            if (object instanceof Medecin) {
                Medecin o = (Medecin) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Medecin.class.getName()});
                return null;
            }
        }

    }

}
