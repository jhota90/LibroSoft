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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author JHONATHAN
 */
@Entity
@Table(name = "autor")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Autor.findAll", query = "SELECT a FROM Autor a"),
    @NamedQuery(name = "Autor.findByCodigo", query = "SELECT a FROM Autor a WHERE a.codigo = :codigo"),
    @NamedQuery(name = "Autor.findByNombres", query = "SELECT a FROM Autor a WHERE a.nombres = :nombres"),
    @NamedQuery(name = "Autor.findByApellidos", query = "SELECT a FROM Autor a WHERE a.apellidos = :apellidos")})
public class Autor implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "codigo")
    private Integer codigo;
    @Basic(optional = false)
    @Column(name = "nombres")
    private String nombres;
    @Basic(optional = false)
    @Column(name = "apellidos")
    private String apellidos;
    @JoinTable(name = "autores_x_libro", joinColumns = {
        @JoinColumn(name = "codigo", referencedColumnName = "codigo")}, inverseJoinColumns = {
        @JoinColumn(name = "isbn", referencedColumnName = "isbn")})
    @ManyToMany
    private List<Libro> libroList;
    @JoinColumn(name = "codigo_nacionalidad", referencedColumnName = "codigo")
    @ManyToOne(optional = false)
    private Nacionalidad codigoNacionalidad;

    public Autor() {
    }

    public Autor(Integer codigo) {
        this.codigo = codigo;
    }

    public Autor(Integer codigo, String nombres, String apellidos) {
        this.codigo = codigo;
        this.nombres = nombres;
        this.apellidos = apellidos;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    @XmlTransient
    public List<Libro> getLibroList() {
        return libroList;
    }

    public void setLibroList(List<Libro> libroList) {
        this.libroList = libroList;
    }

    public Nacionalidad getCodigoNacionalidad() {
        return codigoNacionalidad;
    }

    public void setCodigoNacionalidad(Nacionalidad codigoNacionalidad) {
        this.codigoNacionalidad = codigoNacionalidad;
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
        if (!(object instanceof Autor)) {
            return false;
        }
        Autor other = (Autor) object;
        if ((this.codigo == null && other.codigo != null) || (this.codigo != null && !this.codigo.equals(other.codigo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidades.Autor[ codigo=" + codigo + " ]";
    }
    
}
