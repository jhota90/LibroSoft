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
@Table(name = "ejemplar")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Ejemplar.findAll", query = "SELECT e FROM Ejemplar e"),
    @NamedQuery(name = "Ejemplar.findByCodigo", query = "SELECT e FROM Ejemplar e WHERE e.codigo = :codigo"),
    @NamedQuery(name = "Ejemplar.findByIsbn", query = "SELECT e FROM Ejemplar e WHERE e.isbn = :isbn"),
    @NamedQuery(name = "Ejemplar.findByFechaAdquisicion", query = "SELECT e FROM Ejemplar e WHERE e.fechaAdquisicion = :fechaAdquisicion"),
    @NamedQuery(name = "Ejemplar.findByValorAdquisicion", query = "SELECT e FROM Ejemplar e WHERE e.valorAdquisicion = :valorAdquisicion"),
    @NamedQuery(name = "Ejemplar.findByEstado", query = "SELECT e FROM Ejemplar e WHERE e.estado = :estado")})
public class Ejemplar implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "codigo")
    private Integer codigo;
    @Basic(optional = false)
    @Column(name = "isbn")
    private String isbn;
    @Basic(optional = false)
    @Column(name = "fecha_adquisicion")
    @Temporal(TemporalType.DATE)
    private Date fechaAdquisicion;
    @Basic(optional = false)
    @Column(name = "valor_adquisicion")
    private int valorAdquisicion;
    @Basic(optional = false)
    @Column(name = "estado")
    private String estado;
    @JoinTable(name = "prestamo", joinColumns = {
        @JoinColumn(name = "codigo_ejemplar", referencedColumnName = "codigo")}, inverseJoinColumns = {
        @JoinColumn(name = "codigo_entrega", referencedColumnName = "codigo")})
    @ManyToMany
    private List<OrdenDeEntrega> ordenDeEntregaList;
    @ManyToMany(mappedBy = "ejemplarList")
    private List<OrdenDePrestamo> ordenDePrestamoList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "codigoEjemplar")
    private List<Devolucion> devolucionList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "codigoEjemplar")
    private List<Multa> multaList;
    @JoinColumn(name = "ubicacion", referencedColumnName = "ubicacion")
    @ManyToOne(optional = false)
    private Ubicacion ubicacion;

    public Ejemplar() {
    }

    public Ejemplar(Integer codigo) {
        this.codigo = codigo;
    }

    public Ejemplar(Integer codigo, String isbn, Date fechaAdquisicion, int valorAdquisicion, String estado) {
        this.codigo = codigo;
        this.isbn = isbn;
        this.fechaAdquisicion = fechaAdquisicion;
        this.valorAdquisicion = valorAdquisicion;
        this.estado = estado;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Date getFechaAdquisicion() {
        return fechaAdquisicion;
    }

    public void setFechaAdquisicion(Date fechaAdquisicion) {
        this.fechaAdquisicion = fechaAdquisicion;
    }

    public int getValorAdquisicion() {
        return valorAdquisicion;
    }

    public void setValorAdquisicion(int valorAdquisicion) {
        this.valorAdquisicion = valorAdquisicion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @XmlTransient
    public List<OrdenDeEntrega> getOrdenDeEntregaList() {
        return ordenDeEntregaList;
    }

    public void setOrdenDeEntregaList(List<OrdenDeEntrega> ordenDeEntregaList) {
        this.ordenDeEntregaList = ordenDeEntregaList;
    }

    @XmlTransient
    public List<OrdenDePrestamo> getOrdenDePrestamoList() {
        return ordenDePrestamoList;
    }

    public void setOrdenDePrestamoList(List<OrdenDePrestamo> ordenDePrestamoList) {
        this.ordenDePrestamoList = ordenDePrestamoList;
    }

    @XmlTransient
    public List<Devolucion> getDevolucionList() {
        return devolucionList;
    }

    public void setDevolucionList(List<Devolucion> devolucionList) {
        this.devolucionList = devolucionList;
    }

    @XmlTransient
    public List<Multa> getMultaList() {
        return multaList;
    }

    public void setMultaList(List<Multa> multaList) {
        this.multaList = multaList;
    }

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(Ubicacion ubicacion) {
        this.ubicacion = ubicacion;
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
        if (!(object instanceof Ejemplar)) {
            return false;
        }
        Ejemplar other = (Ejemplar) object;
        if ((this.codigo == null && other.codigo != null) || (this.codigo != null && !this.codigo.equals(other.codigo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidades.Ejemplar[ codigo=" + codigo + " ]";
    }
    
}
