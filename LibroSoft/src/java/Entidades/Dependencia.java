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
@Table(name = "dependencia")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Dependencia.findAll", query = "SELECT d FROM Dependencia d"),
    @NamedQuery(name = "Dependencia.findByCodigo", query = "SELECT d FROM Dependencia d WHERE d.codigo = :codigo"),
    @NamedQuery(name = "Dependencia.findByNombre", query = "SELECT d FROM Dependencia d WHERE d.nombre = :nombre")})
public class Dependencia implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "codigo")
    private Integer codigo;
    @Basic(optional = false)
    @Column(name = "nombre")
    private String nombre;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dependencia")
    private List<Profesor> profesorList;

    public Dependencia() {
    }

    public Dependencia(Integer codigo) {
        this.codigo = codigo;
    }

    public Dependencia(Integer codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @XmlTransient
    public List<Profesor> getProfesorList() {
        return profesorList;
    }

    public void setProfesorList(List<Profesor> profesorList) {
        this.profesorList = profesorList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (codigo != null ? codigo.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Dependencia)) {
            return false;
        }
        Dependencia other = (Dependencia) object;
        if ((this.codigo == null && other.codigo != null) || (this.codigo != null && !this.codigo.equals(other.codigo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidades.Dependencia[ codigo=" + codigo + " ]";
    }
    
}
