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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @author ASUS
 */
@Entity
public class SecretaireMedecin extends Secretaire implements Serializable{

    
    @ManyToOne
    private Medecin medecin;
    @OneToMany(mappedBy = "secretaireMedecin")
    private List<NoteService> noteServices;

    public Medecin getMedecin() {
        return medecin;
    }

    public void setMedecin(Medecin medecin) {
        this.medecin = medecin;
    }

    public List<NoteService> getNoteServices() {
        return noteServices;
    }

    public void setNoteServices(List<NoteService> noteServices) {
        this.noteServices = noteServices;
    }

    
}
