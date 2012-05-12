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
@Table(name = "pago_multa")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PagoMulta.findAll", query = "SELECT p FROM PagoMulta p"),
    @NamedQuery(name = "PagoMulta.findByCodigo", query = "SELECT p FROM PagoMulta p WHERE p.codigo = :codigo"),
    @NamedQuery(name = "PagoMulta.findByFecha", query = "SELECT p FROM PagoMulta p WHERE p.fecha = :fecha")})
public class PagoMulta implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "codigo")
    private Integer codigo;
    @Basic(optional = false)
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @JoinColumn(name = "multa", referencedColumnName = "codigo")
    @ManyToOne
    private Multa multa;

    public PagoMulta() {
    }

    public PagoMulta(Integer codigo) {
        this.codigo = codigo;
    }

    public PagoMulta(Integer codigo, Date fecha) {
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

    public Multa getMulta() {
        return multa;
    }

    public void setMulta(Multa multa) {
        this.multa = multa;
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
        if (!(object instanceof PagoMulta)) {
            return false;
        }
        PagoMulta other = (PagoMulta) object;
        if ((this.codigo == null && other.codigo != null) || (this.codigo != null && !this.codigo.equals(other.codigo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidades.PagoMulta[ codigo=" + codigo + " ]";
    }
    
}
