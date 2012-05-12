/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Entidades;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author JHONATHAN
 */
@Entity
@Table(name = "subarea_de_conocimiento")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SubareaDeConocimiento.findAll", query = "SELECT s FROM SubareaDeConocimiento s"),
    @NamedQuery(name = "SubareaDeConocimiento.findByCodigo", query = "SELECT s FROM SubareaDeConocimiento s WHERE s.codigo = :codigo")})
public class SubareaDeConocimiento implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "codigo")
    private Integer codigo;
    @JoinColumn(name = "codigo_padre", referencedColumnName = "codigo")
    @ManyToOne(optional = false)
    private AreaDeConocimiento codigoPadre;
    @JoinColumn(name = "codigo", referencedColumnName = "codigo", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private AreaDeConocimiento areaDeConocimiento;

    public SubareaDeConocimiento() {
    }

    public SubareaDeConocimiento(Integer codigo) {
        this.codigo = codigo;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public AreaDeConocimiento getCodigoPadre() {
        return codigoPadre;
    }

    public void setCodigoPadre(AreaDeConocimiento codigoPadre) {
        this.codigoPadre = codigoPadre;
    }

    public AreaDeConocimiento getAreaDeConocimiento() {
        return areaDeConocimiento;
    }

    public void setAreaDeConocimiento(AreaDeConocimiento areaDeConocimiento) {
        this.areaDeConocimiento = areaDeConocimiento;
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
        if (!(object instanceof SubareaDeConocimiento)) {
            return false;
        }
        SubareaDeConocimiento other = (SubareaDeConocimiento) object;
        if ((this.codigo == null && other.codigo != null) || (this.codigo != null && !this.codigo.equals(other.codigo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidades.SubareaDeConocimiento[ codigo=" + codigo + " ]";
    }
    
}
