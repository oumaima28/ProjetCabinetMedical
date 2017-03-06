/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;


import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

/**
 *
 * @author moi
 */
@Entity
public class UserSecretaire extends User implements Serializable {

    @OneToOne
    private Secretaire secretaire;

    public Secretaire getSecretaire() {
        return secretaire;
    }

    public void setSecretaire(Secretaire secretaire) {
        this.secretaire = secretaire;
    }

 
    
}
