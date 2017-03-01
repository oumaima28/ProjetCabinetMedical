/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 *
 * @author ASUS
 */
@Entity
public class Chef  extends Secretaire implements Serializable{

    
    @OneToMany(mappedBy = "chef")
    private List<SecretaireAgence> secretaireAgences;

    public List<SecretaireAgence> getSecretaireAgences() {
        return secretaireAgences;
    }

    public void setSecretaireAgences(List<SecretaireAgence> secretaireAgences) {
        this.secretaireAgences = secretaireAgences;
    }

   
}
