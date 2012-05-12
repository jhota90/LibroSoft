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
@Table(name = "libro")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Libro.findAll", query = "SELECT l FROM Libro l"),
    @NamedQuery(name = "Libro.findByIsbn", query = "SELECT l FROM Libro l WHERE l.isbn = :isbn"),
    @NamedQuery(name = "Libro.findByTitulo", query = "SELECT l FROM Libro l WHERE l.titulo = :titulo"),
    @NamedQuery(name = "Libro.findByAnoPublicacion", query = "SELECT l FROM Libro l WHERE l.anoPublicacion = :anoPublicacion"),
    @NamedQuery(name = "Libro.findByImagen", query = "SELECT l FROM Libro l WHERE l.imagen = :imagen")})
public class Libro implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "isbn")
    private String isbn;
    @Basic(optional = false)
    @Column(name = "titulo")
    private String titulo;
    @Basic(optional = false)
    @Column(name = "ano_publicacion")
    private String anoPublicacion;
    @Basic(optional = false)
    @Column(name = "imagen")
    private boolean imagen;
    @JoinTable(name = "palabras_x_libro", joinColumns = {
        @JoinColumn(name = "isbn", referencedColumnName = "isbn")}, inverseJoinColumns = {
        @JoinColumn(name = "palabra", referencedColumnName = "palabra")})
    @ManyToMany
    private List<PalabrasClave> palabrasClaveList;
    @ManyToMany(mappedBy = "libroList")
    private List<Autor> autorList;
    @JoinColumn(name = "idioma", referencedColumnName = "codigo")
    @ManyToOne(optional = false)
    private Idioma idioma;
    @JoinColumn(name = "area_conocimiento", referencedColumnName = "codigo")
    @ManyToOne(optional = false)
    private AreaDeConocimiento areaConocimiento;
    @JoinColumn(name = "editorial", referencedColumnName = "codigo")
    @ManyToOne(optional = false)
    private Editorial editorial;

    public Libro() {
    }

    public Libro(String isbn) {
        this.isbn = isbn;
    }

    public Libro(String isbn, String titulo, String anoPublicacion, boolean imagen) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.anoPublicacion = anoPublicacion;
        this.imagen = imagen;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAnoPublicacion() {
        return anoPublicacion;
    }

    public void setAnoPublicacion(String anoPublicacion) {
        this.anoPublicacion = anoPublicacion;
    }

    public boolean getImagen() {
        return imagen;
    }

    public void setImagen(boolean imagen) {
        this.imagen = imagen;
    }

    @XmlTransient
    public List<PalabrasClave> getPalabrasClaveList() {
        return palabrasClaveList;
    }

    public void setPalabrasClaveList(List<PalabrasClave> palabrasClaveList) {
        this.palabrasClaveList = palabrasClaveList;
    }

    @XmlTransient
    public List<Autor> getAutorList() {
        return autorList;
    }

    public void setAutorList(List<Autor> autorList) {
        this.autorList = autorList;
    }

    public Idioma getIdioma() {
        return idioma;
    }

    public void setIdioma(Idioma idioma) {
        this.idioma = idioma;
    }

    public AreaDeConocimiento getAreaConocimiento() {
        return areaConocimiento;
    }

    public void setAreaConocimiento(AreaDeConocimiento areaConocimiento) {
        this.areaConocimiento = areaConocimiento;
    }

    public Editorial getEditorial() {
        return editorial;
    }

    public void setEditorial(Editorial editorial) {
        this.editorial = editorial;
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
        if (!(object instanceof Libro)) {
            return false;
        }
        Libro other = (Libro) object;
        if ((this.isbn == null && other.isbn != null) || (this.isbn != null && !this.isbn.equals(other.isbn))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidades.Libro[ isbn=" + isbn + " ]";
    }
    
}
