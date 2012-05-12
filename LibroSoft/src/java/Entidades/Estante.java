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
@Table(name = "estante")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Estante.findAll", query = "SELECT e FROM Estante e"),
    @NamedQuery(name = "Estante.findByEstante", query = "SELECT e FROM Estante e WHERE e.estante = :estante"),
    @NamedQuery(name = "Estante.findByNumeroCajones", query = "SELECT e FROM Estante e WHERE e.numeroCajones = :numeroCajones")})
public class Estante implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "estante")
    private Integer estante;
    @Basic(optional = false)
    @Column(name = "numero_cajones")
    private int numeroCajones;
    @OneToMany(mappedBy = "estante")
    private List<Ubicacion> ubicacionList;

    public Estante() {
    }

    public Estante(Integer estante) {
        this.estante = estante;
    }

    public Estante(Integer estante, int numeroCajones) {
        this.estante = estante;
        this.numeroCajones = numeroCajones;
    }

    public Integer getEstante() {
        return estante;
    }

    public void setEstante(Integer estante) {
        this.estante = estante;
    }

    public int getNumeroCajones() {
        return numeroCajones;
    }

    public void setNumeroCajones(int numeroCajones) {
        this.numeroCajones = numeroCajones;
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
        hash += (estante != null ? estante.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Estante)) {
            return false;
        }
        Estante other = (Estante) object;
        if ((this.estante == null && other.estante != null) || (this.estante != null && !this.estante.equals(other.estante))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidades.Estante[ estante=" + estante + " ]";
    }
    
}
