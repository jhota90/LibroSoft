/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Entidades;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author JHONATHAN
 */
@Entity
@Table(name = "orden_de_prestamo")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "OrdenDePrestamo.findAll", query = "SELECT o FROM OrdenDePrestamo o"),
    @NamedQuery(name = "OrdenDePrestamo.findByCodigo", query = "SELECT o FROM OrdenDePrestamo o WHERE o.codigo = :codigo"),
    @NamedQuery(name = "OrdenDePrestamo.findByFecha", query = "SELECT o FROM OrdenDePrestamo o WHERE o.fecha = :fecha")})
public class OrdenDePrestamo implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "codigo")
    private Integer codigo;
    @Basic(optional = false)
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @JoinTable(name = "ordenprestamo_x_ejemplar", joinColumns = {
        @JoinColumn(name = "codigo_orden_prestamo", referencedColumnName = "codigo")}, inverseJoinColumns = {
        @JoinColumn(name = "codigo_ejemplar", referencedColumnName = "codigo")})
    @ManyToMany
    private List<Ejemplar> ejemplarList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "codigoPrestamo")
    private List<OrdenDeEntrega> ordenDeEntregaList;
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario")
    @ManyToOne(optional = false)
    private Usuario idUsuario;

    public OrdenDePrestamo() {
    }

    public OrdenDePrestamo(Integer codigo) {
        this.codigo = codigo;
    }

    public OrdenDePrestamo(Integer codigo, Date fecha) {
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

    @XmlTransient
    public List<Ejemplar> getEjemplarList() {
        return ejemplarList;
    }

    public void setEjemplarList(List<Ejemplar> ejemplarList) {
        this.ejemplarList = ejemplarList;
    }

    @XmlTransient
    public List<OrdenDeEntrega> getOrdenDeEntregaList() {
        return ordenDeEntregaList;
    }

    public void setOrdenDeEntregaList(List<OrdenDeEntrega> ordenDeEntregaList) {
        this.ordenDeEntregaList = ordenDeEntregaList;
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
        if (!(object instanceof OrdenDePrestamo)) {
            return false;
        }
        OrdenDePrestamo other = (OrdenDePrestamo) object;
        if ((this.codigo == null && other.codigo != null) || (this.codigo != null && !this.codigo.equals(other.codigo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidades.OrdenDePrestamo[ codigo=" + codigo + " ]";
    }
    
}
