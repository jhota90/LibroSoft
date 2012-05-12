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
@Table(name = "cajon")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Cajon.findAll", query = "SELECT c FROM Cajon c"),
    @NamedQuery(name = "Cajon.findByCajon", query = "SELECT c FROM Cajon c WHERE c.cajon = :cajon"),
    @NamedQuery(name = "Cajon.findByEstado", query = "SELECT c FROM Cajon c WHERE c.estado = :estado")})
public class Cajon implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "cajon")
    private Integer cajon;
    @Basic(optional = false)
    @Column(name = "estado")
    private String estado;
    @OneToMany(mappedBy = "cajon")
    private List<Ubicacion> ubicacionList;

    public Cajon() {
    }

    public Cajon(Integer cajon) {
        this.cajon = cajon;
    }

    public Cajon(Integer cajon, String estado) {
        this.cajon = cajon;
        this.estado = estado;
    }

    public Integer getCajon() {
        return cajon;
    }

    public void setCajon(Integer cajon) {
        this.cajon = cajon;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
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
        hash += (cajon != null ? cajon.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Cajon)) {
            return false;
        }
        Cajon other = (Cajon) object;
        if ((this.cajon == null && other.cajon != null) || (this.cajon != null && !this.cajon.equals(other.cajon))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidades.Cajon[ cajon=" + cajon + " ]";
    }
    
}
