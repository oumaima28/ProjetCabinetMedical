package controller;

import bean.User;
import controller.util.JsfUtil;
import controller.util.JsfUtil.PersistAction;
import service.UserFacade;

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

@Named("userController")
@SessionScoped
public class UserController implements Serializable {

    @EJB
    private service.UserFacade ejbFacade;
    private List<User> items = null;
    private User selected;
    private User selectedSign;
    private String confirmPass;
    private String oldPass;

    public UserController() {
    }

    /*Methode Look Down */
    
    /*Save Methode */
    public void save()
    {
        ejbFacade.createUser(selected); // save this Selected User
        selected = new User();
    }
    /*Save Methode */
    
    
    /*Connect Methode*/
    public void isConnecte()
    {
        int res = ejbFacade.seConnnecter(selectedSign);
        
        switch (res) {
        // Seccessful
            case 1:
                JsfUtil.addSuccessMessage("Success");
                break;
        // user blocked
            case -2:
                JsfUtil.addErrorMessage("this user is blocked");
                break;
        // Wrong Password
            case -3:
                JsfUtil.addErrorMessage("Wrong Password");
                break;
        // User doesn't exist
            case -4:
                JsfUtil.addErrorMessage("User does not exist");
                break;
        // You have not type any login
            case -5:
                JsfUtil.addErrorMessage("Please type login");
                break;
            default:
                break;
        }
    }
    /*Connect Methode*/
    
    /*findLogin*/
    public int isLogin()
    {
        return ejbFacade.findUserLog(selected.getLogin());
        
    }
    /*findLogin*/
    
    /*Change Data */
    public void changePass()
    {
        User loadUser = ejbFacade.find(selected.getLogin());
        int res = ejbFacade.changePassword(loadUser.getLogin(), oldPass, selected.getPassword(), confirmPass);
    }
    /*Change Data */
    
    
    /*Search List*/
    public void searchUser(){
        items = ejbFacade.searchUser(selected);
        if ( items == null )
        {
            items = ejbFacade.findAll();
        }
        selected = new User();
    }
    
    public void editePopUp(User user)
    {
        selected = user;
    }
    /*Search List*/
    /* Look UP Methode*/
    
    
    /* Delete Methode */
    public void deleteUser(User user)
    {
        ejbFacade.deleteUser(user);
        items.remove(items.indexOf(user));
    }
    /* Delete Methode */
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    /* Dakchi Li L Fo9 howa li khassk Tswwa9 Lih */
    public User getSelected() {
        if (selected == null )
        {
            selected = new User();
        }
        return selected;
    }

    public void setSelected(User selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private UserFacade getFacade() {
        return ejbFacade;
    }

    public User prepareCreate() {
        selected = new User();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("UserCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("UserUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("UserDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<User> getItems() {
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

    public User getUser(java.lang.String id) {
        return getFacade().find(id);
    }

    public List<User> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<User> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    public String getConfirmPass() {
        if ( confirmPass != null )
        {
            return "";
        }
        return confirmPass;
    }

    public void setConfirmPass(String confirmPass) {
        this.confirmPass = confirmPass;
    }

    public String getOldPass() {
        if ( oldPass == null )
        {
            return "";
        }
        return oldPass;
    }

    public void setOldPass(String oldPass) {
        this.oldPass = oldPass;
    }
    
    

    
    @FacesConverter(forClass = User.class)
    public static class UserControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            UserController controller = (UserController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "userController");
            return controller.getUser(getKey(value));
        }

        java.lang.String getKey(String value) {
            java.lang.String key;
            key = value;
            return key;
        }

        String getStringKey(java.lang.String value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof User) {
                User o = (User) object;
                return getStringKey(o.getLogin());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), User.class.getName()});
                return null;
            }
        }

    }

    public User getSelectedSign() {
        if ( selectedSign == null )
        {
            selectedSign = new User();
        }
        return selectedSign;
    }

    public void setSelectedSign(User selectedSign) {
        this.selectedSign = selectedSign;
    }
    
    

}
