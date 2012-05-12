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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author JHONATHAN
 */
@Entity
@Table(name = "profesor")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Profesor.findAll", query = "SELECT p FROM Profesor p"),
    @NamedQuery(name = "Profesor.findByIdProfesor", query = "SELECT p FROM Profesor p WHERE p.idProfesor = :idProfesor"),
    @NamedQuery(name = "Profesor.findByNombres", query = "SELECT p FROM Profesor p WHERE p.nombres = :nombres"),
    @NamedQuery(name = "Profesor.findByApellidos", query = "SELECT p FROM Profesor p WHERE p.apellidos = :apellidos"),
    @NamedQuery(name = "Profesor.findByTipoDocumento", query = "SELECT p FROM Profesor p WHERE p.tipoDocumento = :tipoDocumento"),
    @NamedQuery(name = "Profesor.findByGenero", query = "SELECT p FROM Profesor p WHERE p.genero = :genero"),
    @NamedQuery(name = "Profesor.findByDireccion", query = "SELECT p FROM Profesor p WHERE p.direccion = :direccion"),
    @NamedQuery(name = "Profesor.findByTelefono", query = "SELECT p FROM Profesor p WHERE p.telefono = :telefono"),
    @NamedQuery(name = "Profesor.findByCiudad", query = "SELECT p FROM Profesor p WHERE p.ciudad = :ciudad"),
    @NamedQuery(name = "Profesor.findByEmail", query = "SELECT p FROM Profesor p WHERE p.email = :email"),
    @NamedQuery(name = "Profesor.findByTitulo", query = "SELECT p FROM Profesor p WHERE p.titulo = :titulo")})
public class Profesor implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id_profesor")
    private Integer idProfesor;
    @Basic(optional = false)
    @Column(name = "nombres")
    private String nombres;
    @Basic(optional = false)
    @Column(name = "apellidos")
    private String apellidos;
    @Basic(optional = false)
    @Column(name = "tipo_documento")
    private String tipoDocumento;
    @Basic(optional = false)
    @Column(name = "genero")
    private String genero;
    @Basic(optional = false)
    @Column(name = "direccion")
    private String direccion;
    @Basic(optional = false)
    @Column(name = "telefono")
    private String telefono;
    @Basic(optional = false)
    @Column(name = "ciudad")
    private String ciudad;
    @Basic(optional = false)
    @Column(name = "email")
    private String email;
    @Column(name = "titulo")
    private String titulo;
    @JoinTable(name = "area_x_profesor", joinColumns = {
        @JoinColumn(name = "id_profesor", referencedColumnName = "id_profesor")}, inverseJoinColumns = {
        @JoinColumn(name = "codigo_area", referencedColumnName = "codigo")})
    @ManyToMany
    private List<AreaDeConocimiento> areaDeConocimientoList;
    @JoinColumn(name = "dependencia", referencedColumnName = "codigo")
    @ManyToOne(optional = false)
    private Dependencia dependencia;
    @JoinColumn(name = "id_profesor", referencedColumnName = "id_usuario", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private Usuario usuario;

    public Profesor() {
    }

    public Profesor(Integer idProfesor) {
        this.idProfesor = idProfesor;
    }

    public Profesor(Integer idProfesor, String nombres, String apellidos, String tipoDocumento, String genero, String direccion, String telefono, String ciudad, String email) {
        this.idProfesor = idProfesor;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.tipoDocumento = tipoDocumento;
        this.genero = genero;
        this.direccion = direccion;
        this.telefono = telefono;
        this.ciudad = ciudad;
        this.email = email;
    }

    public Integer getIdProfesor() {
        return idProfesor;
    }

    public void setIdProfesor(Integer idProfesor) {
        this.idProfesor = idProfesor;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    @XmlTransient
    public List<AreaDeConocimiento> getAreaDeConocimientoList() {
        return areaDeConocimientoList;
    }

    public void setAreaDeConocimientoList(List<AreaDeConocimiento> areaDeConocimientoList) {
        this.areaDeConocimientoList = areaDeConocimientoList;
    }

    public Dependencia getDependencia() {
        return dependencia;
    }

    public void setDependencia(Dependencia dependencia) {
        this.dependencia = dependencia;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idProfesor != null ? idProfesor.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Profesor)) {
            return false;
        }
        Profesor other = (Profesor) object;
        if ((this.idProfesor == null && other.idProfesor != null) || (this.idProfesor != null && !this.idProfesor.equals(other.idProfesor))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidades.Profesor[ idProfesor=" + idProfesor + " ]";
    }
    
}
