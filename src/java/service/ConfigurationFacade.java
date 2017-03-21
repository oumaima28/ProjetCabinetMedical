/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Configuration;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author moi
 */
@Stateless
public class ConfigurationFacade extends AbstractFacade<Configuration> {

    @PersistenceContext(unitName = "GestionCabinetMedicalPU")
    private EntityManager em;

    public void createForInscription(Configuration configurationForInscription) {
        int res = 1;
        for (Configuration configuration : findList()) {
            if (configurationForInscription.getHeureDebut().equals(configuration.getHeureDebut())
                    && configurationForInscription.getHeureFin().equals(configuration.getHeureFin())
                    && configurationForInscription.getPas() == configuration.getPas()) {
                res = -1;
            }
        }
        if (res == 1) {
            create(configurationForInscription);
        }
    }

    public List<Configuration> findList() {
        return em.createQuery("SELECT c FROM Configuration c").getResultList();
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConfigurationFacade() {
        super(Configuration.class);
    }

}
