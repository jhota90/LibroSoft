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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
@Table(name = "descarga")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Descarga.findAll", query = "SELECT d FROM Descarga d"),
    @NamedQuery(name = "Descarga.findByCodigo", query = "SELECT d FROM Descarga d WHERE d.codigo = :codigo"),
    @NamedQuery(name = "Descarga.findByFecha", query = "SELECT d FROM Descarga d WHERE d.fecha = :fecha"),
    @NamedQuery(name = "Descarga.findByIp", query = "SELECT d FROM Descarga d WHERE d.ip = :ip")})
public class Descarga implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "codigo")
    private Integer codigo;
    @Basic(optional = false)
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Column(name = "ip")
    private String ip;
    @JoinColumn(name = "isbn", referencedColumnName = "isbn")
    @ManyToOne
    private LibroDigital isbn;
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario")
    @ManyToOne
    private Usuario idUsuario;

    public Descarga() {
    }

    public Descarga(Integer codigo) {
        this.codigo = codigo;
    }

    public Descarga(Integer codigo, Date fecha) {
        this.codigo = codigo;
        this.fecha = fecha;
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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public LibroDigital getIsbn() {
        return isbn;
    }

    public void setIsbn(LibroDigital isbn) {
        this.isbn = isbn;
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
        if (!(object instanceof Descarga)) {
            return false;
        }
        Descarga other = (Descarga) object;
        if ((this.codigo == null && other.codigo != null) || (this.codigo != null && !this.codigo.equals(other.codigo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidades.Descarga[ codigo=" + codigo + " ]";
    }
    
}
