/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Entidades;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
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
@Table(name = "tipo_multa")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TipoMulta.findAll", query = "SELECT t FROM TipoMulta t"),
    @NamedQuery(name = "TipoMulta.findByCodigo", query = "SELECT t FROM TipoMulta t WHERE t.codigo = :codigo"),
    @NamedQuery(name = "TipoMulta.findByDescripcion", query = "SELECT t FROM TipoMulta t WHERE t.descripcion = :descripcion")})
public class TipoMulta implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "codigo")
    private Integer codigo;
    @Basic(optional = false)
    @Column(name = "descripcion")
    private String descripcion;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tipoMulta")
    private List<Multa> multaList;

    public TipoMulta() {
    }

    public TipoMulta(Integer codigo) {
        this.codigo = codigo;
    }

    public TipoMulta(Integer codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @XmlTransient
    public List<Multa> getMultaList() {
        return multaList;
    }

    public void setMultaList(List<Multa> multaList) {
        this.multaList = multaList;
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
        if (!(object instanceof TipoMulta)) {
            return false;
        }
        TipoMulta other = (TipoMulta) object;
        if ((this.codigo == null && other.codigo != null) || (this.codigo != null && !this.codigo.equals(other.codigo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidades.TipoMulta[ codigo=" + codigo + " ]";
    }
    
}
