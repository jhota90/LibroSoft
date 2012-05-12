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
import javax.persistence.ManyToMany;
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
@Table(name = "palabras_clave")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PalabrasClave.findAll", query = "SELECT p FROM PalabrasClave p"),
    @NamedQuery(name = "PalabrasClave.findByPalabra", query = "SELECT p FROM PalabrasClave p WHERE p.palabra = :palabra"),
    @NamedQuery(name = "PalabrasClave.findByDescripcion", query = "SELECT p FROM PalabrasClave p WHERE p.descripcion = :descripcion")})
public class PalabrasClave implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "palabra")
    private String palabra;
    @Basic(optional = false)
    @Column(name = "descripcion")
    private String descripcion;
    @ManyToMany(mappedBy = "palabrasClaveList")
    private List<Libro> libroList;

    public PalabrasClave() {
    }

    public PalabrasClave(String palabra) {
        this.palabra = palabra;
    }

    public PalabrasClave(String palabra, String descripcion) {
        this.palabra = palabra;
        this.descripcion = descripcion;
    }

    public String getPalabra() {
        return palabra;
    }

    public void setPalabra(String palabra) {
        this.palabra = palabra;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @XmlTransient
    public List<Libro> getLibroList() {
        return libroList;
    }

    public void setLibroList(List<Libro> libroList) {
        this.libroList = libroList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (palabra != null ? palabra.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PalabrasClave)) {
            return false;
        }
        PalabrasClave other = (PalabrasClave) object;
        if ((this.palabra == null && other.palabra != null) || (this.palabra != null && !this.palabra.equals(other.palabra))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidades.PalabrasClave[ palabra=" + palabra + " ]";
    }
    
}
