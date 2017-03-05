/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.UserSecretaire;
import controler.util.HashageUtil;
import controller.util.JsfUtil;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author moi
 */
@Stateless
public class UserSecretaireFacade extends AbstractFacade<UserSecretaire> {

    @PersistenceContext(unitName = "GestionCabinetMedicalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UserSecretaireFacade() {
        super(UserSecretaire.class);
    }
    
    @Override
    public UserSecretaire find(Object id) {
        try {
            UserSecretaire user = (UserSecretaire) em.createQuery("select u from UserSecretaire u where u.login = '" + id + "'").getSingleResult();
            if (user != null) {
                return user;
            }
        } catch (Exception e) {
            JsfUtil.addErrorMessage("Login incorrect");
        }
        return null;
    }
    
       public int changePassword(String login, String oldPassword, String newPassword, String newPasswordConfirmation) {
        UserSecretaire loadedeUser = find(login);

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
    
    public void changeData(UserSecretaire user) {
        UserSecretaire loadedUser = find(user.getLogin());
        cloneData(user, loadedUser);
        edit(loadedUser);
    }
    
    public void cloneData(UserSecretaire userSource, UserSecretaire userDestination) {
        userDestination.getSecretaire().setNom(userSource.getSecretaire().getNom());
        userDestination.getSecretaire().setPrenom(userSource.getSecretaire().getPrenom());
        userDestination.getSecretaire().setTel(userSource.getSecretaire().getTel());
        userDestination.getSecretaire().setEmail(userSource.getSecretaire().getEmail());
    }
    
    public int seConnnecter(UserSecretaire user) {
        if (user == null || user.getLogin() == null) {
            JsfUtil.addErrorMessage("Veuilliez saisir votre login");
            return -5;
        } else {
            UserSecretaire loadedUser = find(user.getLogin());
            if (loadedUser == null) {
                return -4;
            } else if (!loadedUser.getPassword().equals(HashageUtil.sha256(user.getPassword()))) {
                if (loadedUser.getNbrCnx() < 3) {
                    System.out.println("hana loadedUser.getNbrCnx() < 3 ::: " + loadedUser.getNbrCnx());
                    loadedUser.setNbrCnx(loadedUser.getNbrCnx() + 1);
                } else if (loadedUser.getNbrCnx() >= 3) {
                    System.out.println("hana loadedUser.getNbrCnx() >= 3::: " + loadedUser.getNbrCnx());
                    loadedUser.setBlocked(1);
                    // edit(loadedUser);
                }
                JsfUtil.addErrorMessage("Mot de passe incorrect");
                return -3;
            } else if (loadedUser.getBlocked() == 1) {
                JsfUtil.addErrorMessage("Cet utilisateur est bloqué");
                return -2;
            } else {
                loadedUser.setNbrCnx(0);
                //edit(loadedUser);
                user = clone(loadedUser);
//                user.setCommune(communeFacade.findByUser(user));
                user.setMdpChanged(loadedUser.isMdpChanged());
                user.setPassword(null);
//                SessionUtil.attachUserToCommune(user);
//                historiqueConnexionFacade.createConnexion(loadedUser);
                return 1;
            }
        }
    }
    
    public UserSecretaire clone(UserSecretaire user) {
        UserSecretaire clone = new UserSecretaire();
        clone.setLogin(user.getLogin());
        clone.setBlocked(user.getBlocked());
      /*  clone.setCommune(user.getCommune());
        clone.setCreationActivite(user.isCreationActivite());
        clone.setCreationUser(user.isCreationUser());*/
        clone.getSecretaire().setEmail(user.getSecretaire().getEmail());
        clone.setMdpChanged(user.isMdpChanged());
        clone.setNbrCnx(user.getNbrCnx());
        clone.getSecretaire().setNom(user.getSecretaire().getNom());
        clone.setPassword(user.getPassword());
        clone.getSecretaire().setPrenom(user.getSecretaire().getPrenom());
        clone.getSecretaire().setTel(user.getSecretaire().getTel());
        /*clone.setAdmin(user.isAdmin());*/
        return clone;
    }
    
}
