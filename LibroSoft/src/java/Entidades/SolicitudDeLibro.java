/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Entidades;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author JHONATHAN
 */
@Entity
@Table(name = "solicitud_de_libro")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SolicitudDeLibro.findAll", query = "SELECT s FROM SolicitudDeLibro s"),
    @NamedQuery(name = "SolicitudDeLibro.findByCodigo", query = "SELECT s FROM SolicitudDeLibro s WHERE s.codigo = :codigo"),
    @NamedQuery(name = "SolicitudDeLibro.findByFecha", query = "SELECT s FROM SolicitudDeLibro s WHERE s.fecha = :fecha"),
    @NamedQuery(name = "SolicitudDeLibro.findByIsbn", query = "SELECT s FROM SolicitudDeLibro s WHERE s.isbn = :isbn"),
    @NamedQuery(name = "SolicitudDeLibro.findByTitulo", query = "SELECT s FROM SolicitudDeLibro s WHERE s.titulo = :titulo"),
    @NamedQuery(name = "SolicitudDeLibro.findByDescripion", query = "SELECT s FROM SolicitudDeLibro s WHERE s.descripion = :descripion")})
public class SolicitudDeLibro implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "codigo")
    private Integer codigo;
    @Basic(optional = false)
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Basic(optional = false)
    @Column(name = "isbn")
    private String isbn;
    @Basic(optional = false)
    @Column(name = "titulo")
    private String titulo;
    @Basic(optional = false)
    @Column(name = "descripion")
    private String descripion;
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario")
    @ManyToOne(optional = false)
    private Usuario idUsuario;

    public SolicitudDeLibro() {
    }

    public SolicitudDeLibro(Integer codigo) {
        this.codigo = codigo;
    }

    public SolicitudDeLibro(Integer codigo, Date fecha, String isbn, String titulo, String descripion) {
        this.codigo = codigo;
        this.fecha = fecha;
        this.isbn = isbn;
        this.titulo = titulo;
        this.descripion = descripion;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
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

    public String getDescripion() {
        return descripion;
    }

    public void setDescripion(String descripion) {
        this.descripion = descripion;
    }

    public Usuario getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Usuario idUsuario) {
        this.idUsuario = idUsuario;
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
        if (!(object instanceof SolicitudDeLibro)) {
            return false;
        }
        SolicitudDeLibro other = (SolicitudDeLibro) object;
        if ((this.codigo == null && other.codigo != null) || (this.codigo != null && !this.codigo.equals(other.codigo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidades.SolicitudDeLibro[ codigo=" + codigo + " ]";
    }
    
}
