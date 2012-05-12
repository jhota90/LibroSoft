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
@Table(name = "pasillo")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Pasillo.findAll", query = "SELECT p FROM Pasillo p"),
    @NamedQuery(name = "Pasillo.findByPasillo", query = "SELECT p FROM Pasillo p WHERE p.pasillo = :pasillo")})
public class Pasillo implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "pasillo")
    private Integer pasillo;
    @JoinColumn(name = "area_conocimiento", referencedColumnName = "codigo")
    @ManyToOne
    private AreaDeConocimiento areaConocimiento;
    @OneToMany(mappedBy = "pasillo")
    private List<Ubicacion> ubicacionList;

    public Pasillo() {
    }

    public Pasillo(Integer pasillo) {
        this.pasillo = pasillo;
    }

    public Integer getPasillo() {
        return pasillo;
    }

    public void setPasillo(Integer pasillo) {
        this.pasillo = pasillo;
    }

    public AreaDeConocimiento getAreaConocimiento() {
        return areaConocimiento;
    }

    public void setAreaConocimiento(AreaDeConocimiento areaConocimiento) {
        this.areaConocimiento = areaConocimiento;
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
        hash += (pasillo != null ? pasillo.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Pasillo)) {
            return false;
        }
        Pasillo other = (Pasillo) object;
        if ((this.pasillo == null && other.pasillo != null) || (this.pasillo != null && !this.pasillo.equals(other.pasillo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidades.Pasillo[ pasillo=" + pasillo + " ]";
    }
    
}
