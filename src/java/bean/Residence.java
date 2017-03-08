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
public class Residence implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    private String nom;
    @OneToMany(mappedBy = "residence")
    private List<Medecin> medecins;
    @OneToMany(mappedBy = "residence")
    private List<Vacance> vacances;

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public List<Medecin> getMedecins() {
        return medecins;
    }

    public void setMedecins(List<Medecin> medecins) {
        this.medecins = medecins;
    }

    public List<Vacance> getVacances() {
        return vacances;
    }

    public void setVacances(List<Vacance> vacances) {
        this.vacances = vacances;
    }
    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (nom != null ? nom.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Residence)) {
            return false;
        }
        Residence other = (Residence) object;
        if ((this.nom == null && other.nom != null) || (this.nom != null && !this.nom.equals(other.nom))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "bean.Residence[ id=" + nom + " ]";
    }

}
