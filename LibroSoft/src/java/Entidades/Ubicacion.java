/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Entidades;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "ubicacion")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Ubicacion.findAll", query = "SELECT u FROM Ubicacion u"),
    @NamedQuery(name = "Ubicacion.findByUbicacion", query = "SELECT u FROM Ubicacion u WHERE u.ubicacion = :ubicacion")})
public class Ubicacion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "ubicacion")
    private Integer ubicacion;
    @JoinColumn(name = "sala", referencedColumnName = "sala")
    @ManyToOne
    private Sala sala;
    @JoinColumn(name = "pasillo", referencedColumnName = "pasillo")
    @ManyToOne
    private Pasillo pasillo;
    @JoinColumn(name = "estante", referencedColumnName = "estante")
    @ManyToOne
    private Estante estante;
    @JoinColumn(name = "cajon", referencedColumnName = "cajon")
    @ManyToOne
    private Cajon cajon;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "ubicacion")
    private List<Ejemplar> ejemplarList;

    public Ubicacion() {
    }

    public Ubicacion(Integer ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Integer getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(Integer ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }

    public Pasillo getPasillo() {
        return pasillo;
    }

    public void setPasillo(Pasillo pasillo) {
        this.pasillo = pasillo;
    }

    public Estante getEstante() {
        return estante;
    }

    public void setEstante(Estante estante) {
        this.estante = estante;
    }

    public Cajon getCajon() {
        return cajon;
    }

    public void setCajon(Cajon cajon) {
        this.cajon = cajon;
    }

    @XmlTransient
    public List<Ejemplar> getEjemplarList() {
        return ejemplarList;
    }

    public void setEjemplarList(List<Ejemplar> ejemplarList) {
        this.ejemplarList = ejemplarList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (ubicacion != null ? ubicacion.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Ubicacion)) {
            return false;
        }
        Ubicacion other = (Ubicacion) object;
        if ((this.ubicacion == null && other.ubicacion != null) || (this.ubicacion != null && !this.ubicacion.equals(other.ubicacion))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidades.Ubicacion[ ubicacion=" + ubicacion + " ]";
    }
    
}
