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
import javax.persistence.ManyToOne;
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
@Table(name = "multa")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Multa.findAll", query = "SELECT m FROM Multa m"),
    @NamedQuery(name = "Multa.findByCodigo", query = "SELECT m FROM Multa m WHERE m.codigo = :codigo"),
    @NamedQuery(name = "Multa.findByValor", query = "SELECT m FROM Multa m WHERE m.valor = :valor"),
    @NamedQuery(name = "Multa.findByEstado", query = "SELECT m FROM Multa m WHERE m.estado = :estado")})
public class Multa implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "codigo")
    private Integer codigo;
    @Basic(optional = false)
    @Column(name = "valor")
    private int valor;
    @Basic(optional = false)
    @Column(name = "estado")
    private String estado;
    @OneToMany(mappedBy = "multa")
    private List<PagoMulta> pagoMultaList;
    @JoinColumn(name = "tipo_multa", referencedColumnName = "codigo")
    @ManyToOne(optional = false)
    private TipoMulta tipoMulta;
    @JoinColumn(name = "codigo_ejemplar", referencedColumnName = "codigo")
    @ManyToOne(optional = false)
    private Ejemplar codigoEjemplar;
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario")
    @ManyToOne(optional = false)
    private Usuario idUsuario;

    public Multa() {
    }

    public Multa(Integer codigo) {
        this.codigo = codigo;
    }

    public Multa(Integer codigo, int valor, String estado) {
        this.codigo = codigo;
        this.valor = valor;
        this.estado = estado;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @XmlTransient
    public List<PagoMulta> getPagoMultaList() {
        return pagoMultaList;
    }

    public void setPagoMultaList(List<PagoMulta> pagoMultaList) {
        this.pagoMultaList = pagoMultaList;
    }

    public TipoMulta getTipoMulta() {
        return tipoMulta;
    }

    public void setTipoMulta(TipoMulta tipoMulta) {
        this.tipoMulta = tipoMulta;
    }

    public Ejemplar getCodigoEjemplar() {
        return codigoEjemplar;
    }

    public void setCodigoEjemplar(Ejemplar codigoEjemplar) {
        this.codigoEjemplar = codigoEjemplar;
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
        if (!(object instanceof Multa)) {
            return false;
        }
        Multa other = (Multa) object;
        if ((this.codigo == null && other.codigo != null) || (this.codigo != null && !this.codigo.equals(other.codigo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidades.Multa[ codigo=" + codigo + " ]";
    }
    
}
