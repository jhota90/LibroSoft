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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author JHONATHAN
 */
@Entity
@Table(name = "usuario")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Usuario.findAll", query = "SELECT u FROM Usuario u"),
    @NamedQuery(name = "Usuario.findByIdUsuario", query = "SELECT u FROM Usuario u WHERE u.idUsuario = :idUsuario"),
    @NamedQuery(name = "Usuario.findByClave", query = "SELECT u FROM Usuario u WHERE u.clave = :clave")})
public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id_usuario")
    private Integer idUsuario;
    @Basic(optional = false)
    @Column(name = "clave")
    private String clave;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "usuario")
    private Profesor profesor;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idUsuario")
    private List<SolicitudDeLibro> solicitudDeLibroList;
    @JoinColumn(name = "perfil", referencedColumnName = "codigo")
    @ManyToOne(optional = false)
    private Perfil perfil;
    @OneToMany(mappedBy = "idUsuario")
    private List<Descarga> descargaList;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "usuario")
    private Estudiante estudiante;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "usuario")
    private Empleado empleado;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idUsuario")
    private List<Devolucion> devolucionList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idUsuario")
    private List<Multa> multaList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idUsuario")
    private List<OrdenDePrestamo> ordenDePrestamoList;

    public Usuario() {
    }

    public Usuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Usuario(Integer idUsuario, String clave) {
        this.idUsuario = idUsuario;
        this.clave = clave;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public Profesor getProfesor() {
        return profesor;
    }

    public void setProfesor(Profesor profesor) {
        this.profesor = profesor;
    }

    @XmlTransient
    public List<SolicitudDeLibro> getSolicitudDeLibroList() {
        return solicitudDeLibroList;
    }

    public void setSolicitudDeLibroList(List<SolicitudDeLibro> solicitudDeLibroList) {
        this.solicitudDeLibroList = solicitudDeLibroList;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }

    @XmlTransient
    public List<Descarga> getDescargaList() {
        return descargaList;
    }

    public void setDescargaList(List<Descarga> descargaList) {
        this.descargaList = descargaList;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
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

    @XmlTransient
    public List<OrdenDePrestamo> getOrdenDePrestamoList() {
        return ordenDePrestamoList;
    }

    public void setOrdenDePrestamoList(List<OrdenDePrestamo> ordenDePrestamoList) {
        this.ordenDePrestamoList = ordenDePrestamoList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idUsuario != null ? idUsuario.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Usuario)) {
            return false;
        }
        Usuario other = (Usuario) object;
        if ((this.idUsuario == null && other.idUsuario != null) || (this.idUsuario != null && !this.idUsuario.equals(other.idUsuario))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidades.Usuario[ idUsuario=" + idUsuario + " ]";
    }
    
}
