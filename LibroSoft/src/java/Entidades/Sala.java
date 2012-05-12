/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Entidades;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author JHONATHAN
 */
@Entity
@Table(name = "sala")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Sala.findAll", query = "SELECT s FROM Sala s"),
    @NamedQuery(name = "Sala.findBySala", query = "SELECT s FROM Sala s WHERE s.sala = :sala"),
    @NamedQuery(name = "Sala.findByNombre", query = "SELECT s FROM Sala s WHERE s.nombre = :nombre")})
public class Sala implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "sala")
    private Integer sala;
    @Basic(optional = false)
    @Column(name = "nombre")
    private String nombre;
    @OneToMany(mappedBy = "sala")
    private List<Ubicacion> ubicacionList;

    public Sala() {
    }

    public Sala(Integer sala) {
        this.sala = sala;
    }

    public Sala(Integer sala, String nombre) {
        this.sala = sala;
        this.nombre = nombre;
    }

    public Integer getSala() {
        return sala;
    }

    public void setSala(Integer sala) {
        this.sala = sala;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @XmlTransient
    public List<Ubicacion> getUbicacionList() {
        return ubicacionList;
    }

    public void setUbicacionList(List<Ubicacion> ubicacionList) {
        this.ubicacionList = ubicacionList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (sala != null ? sala.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Sala)) {
            return false;
        }
        Sala other = (Sala) object;
        if ((this.sala == null && other.sala != null) || (this.sala != null && !this.sala.equals(other.sala))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidades.Sala[ sala=" + sala + " ]";
    }
    
}
