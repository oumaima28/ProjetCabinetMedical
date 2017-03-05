/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.UserMedecin;
import controler.util.HashageUtil;
import controller.util.JsfUtil;
import controller.util.SessionUtil;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author moi
 */
@Stateless
public class UserMedecinFacade extends AbstractFacade<UserMedecin> {

    @PersistenceContext(unitName = "GestionCabinetMedicalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UserMedecinFacade() {
        super(UserMedecin.class);
    }
    
    
    @Override
    public UserMedecin find(Object id) {
        try {
            UserMedecin user = (UserMedecin) em.createQuery("select u from UserMedecin u where u.login = '" + id + "'").getSingleResult();
            if (user != null) {
                return user;
            }
        } catch (Exception e) {
            JsfUtil.addErrorMessage("Login incorrect");
        }
        return null;
    }
    
    public int changePassword(String login, String oldPassword, String newPassword, String newPasswordConfirmation) {
        UserMedecin loadedeUser = find(login);

        if (!newPasswordConfirmation.equals(newPassword)) {
            return -1;
        } else if (!loadedeUser.getPassword().equals(HashageUtil.sha256(oldPassword))) {
            return -2;
        } else if (oldPassword.equals(newPassword)) {
            return -3;
        }
        loadedeUser.setPassword(HashageUtil.sha256(newPassword));
        edit(loadedeUser);
        return 1;
    }
    
    public void changeData(UserMedecin user) {
        UserMedecin loadedUser = find(user.getLogin());
        cloneData(user, loadedUser);
        edit(loadedUser);
    }
    
    public void cloneData(UserMedecin userSource, UserMedecin userDestination) {
        userDestination.getMedecin().setNom(userSource.getMedecin().getNom());
        userDestination.getMedecin().setPrenom(userSource.getMedecin().getPrenom());
        userDestination.getMedecin().setTel(userSource.getMedecin().getTel());
        userDestination.getMedecin().setEmail(userSource.getMedecin().getEmail());
    }
    
    public int seConnnecter(UserMedecin user) {
        if (user == null || user.getLogin() == null) {
            JsfUtil.addErrorMessage("Veuilliez saisir votre login");
            return -5;
        } else {
            UserMedecin loadedUser = find(user.getLogin());
            if (loadedUser == null) {
                return -4;
            } else if (!loadedUser.getPassword().equals(HashageUtil.sha256(user.getPassword()))) {
                if (loadedUser.getNbrCnx() < 3) {
                    loadedUser.setNbrCnx(loadedUser.getNbrCnx() + 1);
                } else if (loadedUser.getNbrCnx() >= 3) {
                    loadedUser.setBlocked(1);
                }
                JsfUtil.addErrorMessage("Mot de passe incorrect");
                return -3;
            } else if (loadedUser.getBlocked() == 1) {
                JsfUtil.addErrorMessage("Cet utilisateur est bloqué");
                return -2;
            } else {
                loadedUser.setNbrCnx(0);
                user = clone(loadedUser);
                user.setMdpChanged(loadedUser.isMdpChanged());
                user.setPassword(null);
//                SessionUtil.attachUserToCommune(user);
//                historiqueConnexionFacade.createConnexion(loadedUser);
                return 1;
            }
        }
    }
    
    public UserMedecin clone(UserMedecin user) {
        UserMedecin clone = new UserMedecin();
        clone.setLogin(user.getLogin());
        clone.setBlocked(user.getBlocked());
      /*  clone.setCommune(user.getCommune());
        clone.setCreationActivite(user.isCreationActivite());
        clone.setCreationUser(user.isCreationUser());*/
        clone.getMedecin().setEmail(user.getMedecin().getEmail());
        clone.setMdpChanged(user.isMdpChanged());
        clone.setNbrCnx(user.getNbrCnx());
        clone.getMedecin().setNom(user.getMedecin().getNom());
        clone.setPassword(user.getPassword());
        clone.getMedecin().setPrenom(user.getMedecin().getPrenom());
        clone.getMedecin().setTel(user.getMedecin().getTel());
        /*clone.setAdmin(user.isAdmin());*/
        return clone;
    }
    
  /*   @Override
    public void create(UserMedecin user) {
//        user.setCommune(SessionUtil.getCurrentCommune());
        super.create(user);
        SessionUtil.getCurrentCommune().getUsers().add(user);

    }*/
    
    
}
