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
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author JHONATHAN
 */
@Entity
@Table(name = "area_de_conocimiento")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AreaDeConocimiento.findAll", query = "SELECT a FROM AreaDeConocimiento a"),
    @NamedQuery(name = "AreaDeConocimiento.findByCodigo", query = "SELECT a FROM AreaDeConocimiento a WHERE a.codigo = :codigo"),
    @NamedQuery(name = "AreaDeConocimiento.findByNombre", query = "SELECT a FROM AreaDeConocimiento a WHERE a.nombre = :nombre"),
    @NamedQuery(name = "AreaDeConocimiento.findByDescripcion", query = "SELECT a FROM AreaDeConocimiento a WHERE a.descripcion = :descripcion")})
public class AreaDeConocimiento implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "codigo")
    private Integer codigo;
    @Basic(optional = false)
    @Column(name = "nombre")
    private String nombre;
    @Basic(optional = false)
    @Column(name = "descripcion")
    private String descripcion;
    @ManyToMany(mappedBy = "areaDeConocimientoList")
    private List<Profesor> profesorList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "codigoPadre")
    private List<SubareaDeConocimiento> subareaDeConocimientoList;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "areaDeConocimiento")
    private SubareaDeConocimiento subareaDeConocimiento;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "areaConocimiento")
    private List<Libro> libroList;
    @OneToMany(mappedBy = "areaConocimiento")
    private List<Pasillo> pasilloList;

    public AreaDeConocimiento() {
    }

    public AreaDeConocimiento(Integer codigo) {
        this.codigo = codigo;
    }

    public AreaDeConocimiento(Integer codigo, String nombre, String descripcion) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @XmlTransient
    public List<Profesor> getProfesorList() {
        return profesorList;
    }

    public void setProfesorList(List<Profesor> profesorList) {
        this.profesorList = profesorList;
    }

    @XmlTransient
    public List<SubareaDeConocimiento> getSubareaDeConocimientoList() {
        return subareaDeConocimientoList;
    }

    public void setSubareaDeConocimientoList(List<SubareaDeConocimiento> subareaDeConocimientoList) {
        this.subareaDeConocimientoList = subareaDeConocimientoList;
    }

    public SubareaDeConocimiento getSubareaDeConocimiento() {
        return subareaDeConocimiento;
    }

    public void setSubareaDeConocimiento(SubareaDeConocimiento subareaDeConocimiento) {
        this.subareaDeConocimiento = subareaDeConocimiento;
    }

    @XmlTransient
    public List<Libro> getLibroList() {
        return libroList;
    }

    public void setLibroList(List<Libro> libroList) {
        this.libroList = libroList;
    }

    @XmlTransient
    public List<Pasillo> getPasilloList() {
        return pasilloList;
    }

    public void setPasilloList(List<Pasillo> pasilloList) {
        this.pasilloList = pasilloList;
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
        if (!(object instanceof AreaDeConocimiento)) {
            return false;
        }
        AreaDeConocimiento other = (AreaDeConocimiento) object;
        if ((this.codigo == null && other.codigo != null) || (this.codigo != null && !this.codigo.equals(other.codigo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidades.AreaDeConocimiento[ codigo=" + codigo + " ]";
    }
    
}
