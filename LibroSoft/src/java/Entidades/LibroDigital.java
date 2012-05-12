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
@Table(name = "libro_digital")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "LibroDigital.findAll", query = "SELECT l FROM LibroDigital l"),
    @NamedQuery(name = "LibroDigital.findByIsbn", query = "SELECT l FROM LibroDigital l WHERE l.isbn = :isbn"),
    @NamedQuery(name = "LibroDigital.findByFormatoArchivo", query = "SELECT l FROM LibroDigital l WHERE l.formatoArchivo = :formatoArchivo"),
    @NamedQuery(name = "LibroDigital.findByTamano", query = "SELECT l FROM LibroDigital l WHERE l.tamano = :tamano"),
    @NamedQuery(name = "LibroDigital.findByUrl", query = "SELECT l FROM LibroDigital l WHERE l.url = :url")})
public class LibroDigital implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "isbn")
    private String isbn;
    @Basic(optional = false)
    @Column(name = "formato_archivo")
    private String formatoArchivo;
    @Basic(optional = false)
    @Column(name = "tamano")
    private int tamano;
    @Basic(optional = false)
    @Column(name = "url")
    private String url;
    @OneToMany(mappedBy = "isbn")
    private List<Descarga> descargaList;

    public LibroDigital() {
    }

    public LibroDigital(String isbn) {
        this.isbn = isbn;
    }

    public LibroDigital(String isbn, String formatoArchivo, int tamano, String url) {
        this.isbn = isbn;
        this.formatoArchivo = formatoArchivo;
        this.tamano = tamano;
        this.url = url;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getFormatoArchivo() {
        return formatoArchivo;
    }

    public void setFormatoArchivo(String formatoArchivo) {
        this.formatoArchivo = formatoArchivo;
    }

    public int getTamano() {
        return tamano;
    }

    public void setTamano(int tamano) {
        this.tamano = tamano;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @XmlTransient
    public List<Descarga> getDescargaList() {
        return descargaList;
    }

    public void setDescargaList(List<Descarga> descargaList) {
        this.descargaList = descargaList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (isbn != null ? isbn.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof LibroDigital)) {
            return false;
        }
        LibroDigital other = (LibroDigital) object;
        if ((this.isbn == null && other.isbn != null) || (this.isbn != null && !this.isbn.equals(other.isbn))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidades.LibroDigital[ isbn=" + isbn + " ]";
    }
    
}
