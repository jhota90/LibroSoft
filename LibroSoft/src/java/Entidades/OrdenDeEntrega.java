/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Entidades;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
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
@Table(name = "orden_de_entrega")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "OrdenDeEntrega.findAll", query = "SELECT o FROM OrdenDeEntrega o"),
    @NamedQuery(name = "OrdenDeEntrega.findByCodigo", query = "SELECT o FROM OrdenDeEntrega o WHERE o.codigo = :codigo"),
    @NamedQuery(name = "OrdenDeEntrega.findByIdEmpleado", query = "SELECT o FROM OrdenDeEntrega o WHERE o.idEmpleado = :idEmpleado"),
    @NamedQuery(name = "OrdenDeEntrega.findByFechaEntrega", query = "SELECT o FROM OrdenDeEntrega o WHERE o.fechaEntrega = :fechaEntrega"),
    @NamedQuery(name = "OrdenDeEntrega.findByFechaDevolucion", query = "SELECT o FROM OrdenDeEntrega o WHERE o.fechaDevolucion = :fechaDevolucion")})
public class OrdenDeEntrega implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "codigo")
    private Integer codigo;
    @Basic(optional = false)
    @Column(name = "id_empleado")
    private int idEmpleado;
    @Basic(optional = false)
    @Column(name = "fecha_Entrega")
    @Temporal(TemporalType.DATE)
    private Date fechaEntrega;
    @Basic(optional = false)
    @Column(name = "fecha_devolucion")
    @Temporal(TemporalType.DATE)
    private Date fechaDevolucion;
    @ManyToMany(mappedBy = "ordenDeEntregaList")
    private List<Ejemplar> ejemplarList;
    @JoinColumn(name = "codigo_prestamo", referencedColumnName = "codigo")
    @ManyToOne(optional = false)
    private OrdenDePrestamo codigoPrestamo;

    public OrdenDeEntrega() {
    }

    public OrdenDeEntrega(Integer codigo) {
        this.codigo = codigo;
    }

    public OrdenDeEntrega(Integer codigo, int idEmpleado, Date fechaEntrega, Date fechaDevolucion) {
        this.codigo = codigo;
        this.idEmpleado = idEmpleado;
        this.fechaEntrega = fechaEntrega;
        this.fechaDevolucion = fechaDevolucion;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public Date getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(Date fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public Date getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(Date fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    @XmlTransient
    public List<Ejemplar> getEjemplarList() {
        return ejemplarList;
    }

    public void setEjemplarList(List<Ejemplar> ejemplarList) {
        this.ejemplarList = ejemplarList;
    }

    public OrdenDePrestamo getCodigoPrestamo() {
        return codigoPrestamo;
    }

    public void setCodigoPrestamo(OrdenDePrestamo codigoPrestamo) {
        this.codigoPrestamo = codigoPrestamo;
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
        if (!(object instanceof OrdenDeEntrega)) {
            return false;
        }
        OrdenDeEntrega other = (OrdenDeEntrega) object;
        if ((this.codigo == null && other.codigo != null) || (this.codigo != null && !this.codigo.equals(other.codigo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidades.OrdenDeEntrega[ codigo=" + codigo + " ]";
    }
    
}
