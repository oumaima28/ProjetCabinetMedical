/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.User;
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

    public String findType(User user){
       List<String> list= em.createNativeQuery("SELECT user.DTYPE FROM User user WHERE user.login = '"+user.getLogin()+"'").getResultList();
       return list.get(0);
    }
    
    public UserFacade() {
        super(User.class);
    }
    
}
