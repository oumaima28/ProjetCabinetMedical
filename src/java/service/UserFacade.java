/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.User;
import controler.util.HashageUtil;
import controller.util.JsfUtil;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author moi
 */
@Stateless
public class UserFacade extends AbstractFacade<User> {

    @PersistenceContext(unitName = "GestionCabinetMedicalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public User find(Object id) {
        try {
            User user = (User) em.createQuery("select u from User u where u.login = '" + id + "'").getSingleResult();
            if (user != null) {
                return user;
            }
        } catch (Exception e) {
            JsfUtil.addErrorMessage("Login incorrect");
        }
        return null;
    }
    
    public UserFacade() {
        super(User.class);
    }
    
    public int changePassword(String login, String oldPassword, String newPassword, String newPasswordConfirmation) {
        User loadedeUser = find(login);

        if (!newPasswordConfirmation.equals(newPassword)) {
            return -1; // newPass != confPass
        } else if (!loadedeUser.getPassword().equals(HashageUtil.sha256(oldPassword))) {
            return -2; // oldPassOfLoadUser != OldPass Typeed
        } else if (oldPassword.equals(newPassword)) {
            return -3; // oldPass == new Pass
        }
        loadedeUser.setPassword(HashageUtil.sha256(newPassword));
        edit(loadedeUser);
        return 1;
    }
    
    public void createUser(User user)
    {
        User loadUser = find(user.getLogin());
        
        if ( loadUser == null )
        { 
            loadUser = user;
            String pass = getGeneratePass();
            loadUser.setPassword(pass);
            if ( user.getType() > 0)
            create(loadUser);
            JsfUtil.addSuccessMessage("User Created");
        }
        else
            JsfUtil.addErrorMessage("This Login is already Exist");
    }
    
    public void deleteUser(User user)
    {
        User loadUser = find(user.getLogin());
        
        if ( loadUser != null )
        {
            remove(loadUser);
            JsfUtil.addSuccessMessage("User deleted");
        }
        else
            JsfUtil.addErrorMessage("This Login is already Exist");
            
    }
    
    
    
    public int findUserLog (String id )
    {
        List<User> userS = em.createQuery("SELECT u FROM User u WHERE u.login ='"+id+"'").getResultList();
        if (userS == null )
        {
            return 1; // this login does not exist 
        }
        else
            return -1; // this login does exist 
    }
    
    /*Edit Data*/
    public void editData(User userOld , User userNew )
    {
        int res = findUserLog(userNew.getLogin()); // if this login exist
        User loadUser = find(userOld.getLogin()); // if the old user exist
        if ( loadUser != null ) // if old user exist 
        {
            // then we change its data by data of New User
            if ( res > 0 )
            {
                loadUser.setLogin(userNew.getLogin());
                loadUser.setPassword(userNew.getPassword());
                loadUser.setBlocked(userNew.getBlocked());
                edit(loadUser);
            }
        }
    }
    
    /*Inscription*/
    
    public int signIn(User user)
    {
       User loadUser = find(user.getLogin());
       if ( loadUser != null )
       {
           JsfUtil.addErrorMessage("User already Exist");
           return -1; // this user is already exist
       }
       else 
       {
           JsfUtil.addSuccessMessage("User is created");
           create(user); // user is created 
           return 1;
       }
    }
    
    
    /* Connecte User*/
    public int seConnnecter(User user) {
        if (user == null || user.getLogin() == null) {
            JsfUtil.addErrorMessage("Veuilliez saisir votre login");
            return -5; // please type login
        } else {
            User loadedUser = find(user.getLogin());
            if (loadedUser == null) {
                return -4; // makaynch had user
            } else if (!loadedUser.getPassword().equals(HashageUtil.sha256(user.getPassword()))) {
                if (loadedUser.getNbrCnx() < 3) {
                    System.out.println("hana loadedUser.getNbrCnx() < 3 ::: " + loadedUser.getNbrCnx());
                    JsfUtil.addErrorMessage("Incorrect Password ! Try Again ! You have "+(3-loadedUser.getNbrCnx())+" left ");
                    loadedUser.setNbrCnx(loadedUser.getNbrCnx() + 1);
                } else if (loadedUser.getNbrCnx() >= 3) {
                    System.out.println("hana loadedUser.getNbrCnx() >= 3::: " + loadedUser.getNbrCnx());
                    loadedUser.setBlocked(1);
                    // edit(loadedUser);
                }
                JsfUtil.addErrorMessage("Wrong Password");
                return -3; // Wrong Password
            } else if (loadedUser.getBlocked() == 1) {
                JsfUtil.addErrorMessage("this user is blocked");
                return -2; // this user is blocked
            } else {
                loadedUser.setNbrCnx(0);
//                edit(loadedUser);
                user = clone(loadedUser);
                user.setMdpChanged(loadedUser.isMdpChanged());
                user.setPassword(null);
                edit(user);
               // SessionUtil.attachUserToCommune(user);
                return 1; // this user is exist
            }
            
        }
     }
    
    public User clone(User user) {
        User clone = new User();
        clone.setLogin(user.getLogin());
        clone.setBlocked(user.getBlocked());
        clone.setMdpChanged(user.isMdpChanged());
        clone.setNbrCnx(user.getNbrCnx());
        clone.setPassword(user.getPassword());
        return clone;
    }
    /* Connecte User*/
    
    /*Search Methode*/
    public List<User> searchUser(User user)
    {
        String req="SELECT u FROM User u WHERE 1=1 ";
        
        if (!user.getLogin().equals(""))
        {
            req +=" AND u.login ='"+user.getLogin()+"'";
        }
        if (!user.getPassword().equals(""))
        {
            req +=" AND u.password ='"+user.getPassword()+"'";
        }
        if ( user.getType() > 0 )
        {
            req += " AND u.type ='"+user.getType()+"'";
        }
        if ( user.getBlocked() != 0 )
        {
            req += " AND u.blocked ='"+user.getBlocked()+"'";
        }
        
        return em.createQuery(req).getResultList();
        
    }
    /*Search Methode*/
}
