/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DaoJPA;

import DaoJPA.exceptions.IllegalOrphanException;
import DaoJPA.exceptions.NonexistentEntityException;
import DaoJPA.exceptions.PreexistingEntityException;
import Entidades.Usuario;
import java.io.Serializable;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entidades.Profesor;
import Entidades.Perfil;
import Entidades.Estudiante;
import Entidades.Empleado;
import Entidades.SolicitudDeLibro;
import java.util.ArrayList;
import java.util.List;
import Entidades.Descarga;
import Entidades.Devolucion;
import Entidades.Multa;
import Entidades.OrdenDePrestamo;

/**
 *
 * @author JHONATHAN
 */
public class UsuarioJpaController implements Serializable {

    public UsuarioJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Usuario usuario) throws PreexistingEntityException, Exception {
        if (usuario.getSolicitudDeLibroList() == null) {
            usuario.setSolicitudDeLibroList(new ArrayList<SolicitudDeLibro>());
        }
        if (usuario.getDescargaList() == null) {
            usuario.setDescargaList(new ArrayList<Descarga>());
        }
        if (usuario.getDevolucionList() == null) {
            usuario.setDevolucionList(new ArrayList<Devolucion>());
        }
        if (usuario.getMultaList() == null) {
            usuario.setMultaList(new ArrayList<Multa>());
        }
        if (usuario.getOrdenDePrestamoList() == null) {
            usuario.setOrdenDePrestamoList(new ArrayList<OrdenDePrestamo>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Profesor profesor = usuario.getProfesor();
            if (profesor != null) {
                profesor = em.getReference(profesor.getClass(), profesor.getIdProfesor());
                usuario.setProfesor(profesor);
            }
            Perfil perfil = usuario.getPerfil();
            if (perfil != null) {
                perfil = em.getReference(perfil.getClass(), perfil.getCodigo());
                usuario.setPerfil(perfil);
            }
            Estudiante estudiante = usuario.getEstudiante();
            if (estudiante != null) {
                estudiante = em.getReference(estudiante.getClass(), estudiante.getIdEstudiante());
                usuario.setEstudiante(estudiante);
            }
            Empleado empleado = usuario.getEmpleado();
            if (empleado != null) {
                empleado = em.getReference(empleado.getClass(), empleado.getIdEmpleado());
                usuario.setEmpleado(empleado);
            }
            List<SolicitudDeLibro> attachedSolicitudDeLibroList = new ArrayList<SolicitudDeLibro>();
            for (SolicitudDeLibro solicitudDeLibroListSolicitudDeLibroToAttach : usuario.getSolicitudDeLibroList()) {
                solicitudDeLibroListSolicitudDeLibroToAttach = em.getReference(solicitudDeLibroListSolicitudDeLibroToAttach.getClass(), solicitudDeLibroListSolicitudDeLibroToAttach.getCodigo());
                attachedSolicitudDeLibroList.add(solicitudDeLibroListSolicitudDeLibroToAttach);
            }
            usuario.setSolicitudDeLibroList(attachedSolicitudDeLibroList);
            List<Descarga> attachedDescargaList = new ArrayList<Descarga>();
            for (Descarga descargaListDescargaToAttach : usuario.getDescargaList()) {
                descargaListDescargaToAttach = em.getReference(descargaListDescargaToAttach.getClass(), descargaListDescargaToAttach.getCodigo());
                attachedDescargaList.add(descargaListDescargaToAttach);
            }
            usuario.setDescargaList(attachedDescargaList);
            List<Devolucion> attachedDevolucionList = new ArrayList<Devolucion>();
            for (Devolucion devolucionListDevolucionToAttach : usuario.getDevolucionList()) {
                devolucionListDevolucionToAttach = em.getReference(devolucionListDevolucionToAttach.getClass(), devolucionListDevolucionToAttach.getCodigo());
                attachedDevolucionList.add(devolucionListDevolucionToAttach);
            }
            usuario.setDevolucionList(attachedDevolucionList);
            List<Multa> attachedMultaList = new ArrayList<Multa>();
            for (Multa multaListMultaToAttach : usuario.getMultaList()) {
                multaListMultaToAttach = em.getReference(multaListMultaToAttach.getClass(), multaListMultaToAttach.getCodigo());
                attachedMultaList.add(multaListMultaToAttach);
            }
            usuario.setMultaList(attachedMultaList);
            List<OrdenDePrestamo> attachedOrdenDePrestamoList = new ArrayList<OrdenDePrestamo>();
            for (OrdenDePrestamo ordenDePrestamoListOrdenDePrestamoToAttach : usuario.getOrdenDePrestamoList()) {
                ordenDePrestamoListOrdenDePrestamoToAttach = em.getReference(ordenDePrestamoListOrdenDePrestamoToAttach.getClass(), ordenDePrestamoListOrdenDePrestamoToAttach.getCodigo());
                attachedOrdenDePrestamoList.add(ordenDePrestamoListOrdenDePrestamoToAttach);
            }
            usuario.setOrdenDePrestamoList(attachedOrdenDePrestamoList);
            em.persist(usuario);
            if (profesor != null) {
                Usuario oldUsuarioOfProfesor = profesor.getUsuario();
                if (oldUsuarioOfProfesor != null) {
                    oldUsuarioOfProfesor.setProfesor(null);
                    oldUsuarioOfProfesor = em.merge(oldUsuarioOfProfesor);
                }
                profesor.setUsuario(usuario);
                profesor = em.merge(profesor);
            }
            if (perfil != null) {
                perfil.getUsuarioList().add(usuario);
                perfil = em.merge(perfil);
            }
            if (estudiante != null) {
                Usuario oldUsuarioOfEstudiante = estudiante.getUsuario();
                if (oldUsuarioOfEstudiante != null) {
                    oldUsuarioOfEstudiante.setEstudiante(null);
                    oldUsuarioOfEstudiante = em.merge(oldUsuarioOfEstudiante);
                }
                estudiante.setUsuario(usuario);
                estudiante = em.merge(estudiante);
            }
            if (empleado != null) {
                Usuario oldUsuarioOfEmpleado = empleado.getUsuario();
                if (oldUsuarioOfEmpleado != null) {
                    oldUsuarioOfEmpleado.setEmpleado(null);
                    oldUsuarioOfEmpleado = em.merge(oldUsuarioOfEmpleado);
                }
                empleado.setUsuario(usuario);
                empleado = em.merge(empleado);
            }
            for (SolicitudDeLibro solicitudDeLibroListSolicitudDeLibro : usuario.getSolicitudDeLibroList()) {
                Usuario oldIdUsuarioOfSolicitudDeLibroListSolicitudDeLibro = solicitudDeLibroListSolicitudDeLibro.getIdUsuario();
                solicitudDeLibroListSolicitudDeLibro.setIdUsuario(usuario);
                solicitudDeLibroListSolicitudDeLibro = em.merge(solicitudDeLibroListSolicitudDeLibro);
                if (oldIdUsuarioOfSolicitudDeLibroListSolicitudDeLibro != null) {
                    oldIdUsuarioOfSolicitudDeLibroListSolicitudDeLibro.getSolicitudDeLibroList().remove(solicitudDeLibroListSolicitudDeLibro);
                    oldIdUsuarioOfSolicitudDeLibroListSolicitudDeLibro = em.merge(oldIdUsuarioOfSolicitudDeLibroListSolicitudDeLibro);
                }
            }
            for (Descarga descargaListDescarga : usuario.getDescargaList()) {
                Usuario oldIdUsuarioOfDescargaListDescarga = descargaListDescarga.getIdUsuario();
                descargaListDescarga.setIdUsuario(usuario);
                descargaListDescarga = em.merge(descargaListDescarga);
                if (oldIdUsuarioOfDescargaListDescarga != null) {
                    oldIdUsuarioOfDescargaListDescarga.getDescargaList().remove(descargaListDescarga);
                    oldIdUsuarioOfDescargaListDescarga = em.merge(oldIdUsuarioOfDescargaListDescarga);
                }
            }
            for (Devolucion devolucionListDevolucion : usuario.getDevolucionList()) {
                Usuario oldIdUsuarioOfDevolucionListDevolucion = devolucionListDevolucion.getIdUsuario();
                devolucionListDevolucion.setIdUsuario(usuario);
                devolucionListDevolucion = em.merge(devolucionListDevolucion);
                if (oldIdUsuarioOfDevolucionListDevolucion != null) {
                    oldIdUsuarioOfDevolucionListDevolucion.getDevolucionList().remove(devolucionListDevolucion);
                    oldIdUsuarioOfDevolucionListDevolucion = em.merge(oldIdUsuarioOfDevolucionListDevolucion);
                }
            }
            for (Multa multaListMulta : usuario.getMultaList()) {
                Usuario oldIdUsuarioOfMultaListMulta = multaListMulta.getIdUsuario();
                multaListMulta.setIdUsuario(usuario);
                multaListMulta = em.merge(multaListMulta);
                if (oldIdUsuarioOfMultaListMulta != null) {
                    oldIdUsuarioOfMultaListMulta.getMultaList().remove(multaListMulta);
                    oldIdUsuarioOfMultaListMulta = em.merge(oldIdUsuarioOfMultaListMulta);
                }
            }
            for (OrdenDePrestamo ordenDePrestamoListOrdenDePrestamo : usuario.getOrdenDePrestamoList()) {
                Usuario oldIdUsuarioOfOrdenDePrestamoListOrdenDePrestamo = ordenDePrestamoListOrdenDePrestamo.getIdUsuario();
                ordenDePrestamoListOrdenDePrestamo.setIdUsuario(usuario);
                ordenDePrestamoListOrdenDePrestamo = em.merge(ordenDePrestamoListOrdenDePrestamo);
                if (oldIdUsuarioOfOrdenDePrestamoListOrdenDePrestamo != null) {
                    oldIdUsuarioOfOrdenDePrestamoListOrdenDePrestamo.getOrdenDePrestamoList().remove(ordenDePrestamoListOrdenDePrestamo);
                    oldIdUsuarioOfOrdenDePrestamoListOrdenDePrestamo = em.merge(oldIdUsuarioOfOrdenDePrestamoListOrdenDePrestamo);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findUsuario(usuario.getIdUsuario()) != null) {
                throw new PreexistingEntityException("Usuario " + usuario + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Usuario usuario) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario persistentUsuario = em.find(Usuario.class, usuario.getIdUsuario());
            Profesor profesorOld = persistentUsuario.getProfesor();
            Profesor profesorNew = usuario.getProfesor();
            Perfil perfilOld = persistentUsuario.getPerfil();
            Perfil perfilNew = usuario.getPerfil();
            Estudiante estudianteOld = persistentUsuario.getEstudiante();
            Estudiante estudianteNew = usuario.getEstudiante();
            Empleado empleadoOld = persistentUsuario.getEmpleado();
            Empleado empleadoNew = usuario.getEmpleado();
            List<SolicitudDeLibro> solicitudDeLibroListOld = persistentUsuario.getSolicitudDeLibroList();
            List<SolicitudDeLibro> solicitudDeLibroListNew = usuario.getSolicitudDeLibroList();
            List<Descarga> descargaListOld = persistentUsuario.getDescargaList();
            List<Descarga> descargaListNew = usuario.getDescargaList();
            List<Devolucion> devolucionListOld = persistentUsuario.getDevolucionList();
            List<Devolucion> devolucionListNew = usuario.getDevolucionList();
            List<Multa> multaListOld = persistentUsuario.getMultaList();
            List<Multa> multaListNew = usuario.getMultaList();
            List<OrdenDePrestamo> ordenDePrestamoListOld = persistentUsuario.getOrdenDePrestamoList();
            List<OrdenDePrestamo> ordenDePrestamoListNew = usuario.getOrdenDePrestamoList();
            List<String> illegalOrphanMessages = null;
            if (profesorOld != null && !profesorOld.equals(profesorNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain Profesor " + profesorOld + " since its usuario field is not nullable.");
            }
            if (estudianteOld != null && !estudianteOld.equals(estudianteNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain Estudiante " + estudianteOld + " since its usuario field is not nullable.");
            }
            if (empleadoOld != null && !empleadoOld.equals(empleadoNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain Empleado " + empleadoOld + " since its usuario field is not nullable.");
            }
            for (SolicitudDeLibro solicitudDeLibroListOldSolicitudDeLibro : solicitudDeLibroListOld) {
                if (!solicitudDeLibroListNew.contains(solicitudDeLibroListOldSolicitudDeLibro)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain SolicitudDeLibro " + solicitudDeLibroListOldSolicitudDeLibro + " since its idUsuario field is not nullable.");
                }
            }
            for (Devolucion devolucionListOldDevolucion : devolucionListOld) {
                if (!devolucionListNew.contains(devolucionListOldDevolucion)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Devolucion " + devolucionListOldDevolucion + " since its idUsuario field is not nullable.");
                }
            }
            for (Multa multaListOldMulta : multaListOld) {
                if (!multaListNew.contains(multaListOldMulta)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Multa " + multaListOldMulta + " since its idUsuario field is not nullable.");
                }
            }
            for (OrdenDePrestamo ordenDePrestamoListOldOrdenDePrestamo : ordenDePrestamoListOld) {
                if (!ordenDePrestamoListNew.contains(ordenDePrestamoListOldOrdenDePrestamo)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain OrdenDePrestamo " + ordenDePrestamoListOldOrdenDePrestamo + " since its idUsuario field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (profesorNew != null) {
                profesorNew = em.getReference(profesorNew.getClass(), profesorNew.getIdProfesor());
                usuario.setProfesor(profesorNew);
            }
            if (perfilNew != null) {
                perfilNew = em.getReference(perfilNew.getClass(), perfilNew.getCodigo());
                usuario.setPerfil(perfilNew);
            }
            if (estudianteNew != null) {
                estudianteNew = em.getReference(estudianteNew.getClass(), estudianteNew.getIdEstudiante());
                usuario.setEstudiante(estudianteNew);
            }
            if (empleadoNew != null) {
                empleadoNew = em.getReference(empleadoNew.getClass(), empleadoNew.getIdEmpleado());
                usuario.setEmpleado(empleadoNew);
            }
            List<SolicitudDeLibro> attachedSolicitudDeLibroListNew = new ArrayList<SolicitudDeLibro>();
            for (SolicitudDeLibro solicitudDeLibroListNewSolicitudDeLibroToAttach : solicitudDeLibroListNew) {
                solicitudDeLibroListNewSolicitudDeLibroToAttach = em.getReference(solicitudDeLibroListNewSolicitudDeLibroToAttach.getClass(), solicitudDeLibroListNewSolicitudDeLibroToAttach.getCodigo());
                attachedSolicitudDeLibroListNew.add(solicitudDeLibroListNewSolicitudDeLibroToAttach);
            }
            solicitudDeLibroListNew = attachedSolicitudDeLibroListNew;
            usuario.setSolicitudDeLibroList(solicitudDeLibroListNew);
            List<Descarga> attachedDescargaListNew = new ArrayList<Descarga>();
            for (Descarga descargaListNewDescargaToAttach : descargaListNew) {
                descargaListNewDescargaToAttach = em.getReference(descargaListNewDescargaToAttach.getClass(), descargaListNewDescargaToAttach.getCodigo());
                attachedDescargaListNew.add(descargaListNewDescargaToAttach);
            }
            descargaListNew = attachedDescargaListNew;
            usuario.setDescargaList(descargaListNew);
            List<Devolucion> attachedDevolucionListNew = new ArrayList<Devolucion>();
            for (Devolucion devolucionListNewDevolucionToAttach : devolucionListNew) {
                devolucionListNewDevolucionToAttach = em.getReference(devolucionListNewDevolucionToAttach.getClass(), devolucionListNewDevolucionToAttach.getCodigo());
                attachedDevolucionListNew.add(devolucionListNewDevolucionToAttach);
            }
            devolucionListNew = attachedDevolucionListNew;
            usuario.setDevolucionList(devolucionListNew);
            List<Multa> attachedMultaListNew = new ArrayList<Multa>();
            for (Multa multaListNewMultaToAttach : multaListNew) {
                multaListNewMultaToAttach = em.getReference(multaListNewMultaToAttach.getClass(), multaListNewMultaToAttach.getCodigo());
                attachedMultaListNew.add(multaListNewMultaToAttach);
            }
            multaListNew = attachedMultaListNew;
            usuario.setMultaList(multaListNew);
            List<OrdenDePrestamo> attachedOrdenDePrestamoListNew = new ArrayList<OrdenDePrestamo>();
            for (OrdenDePrestamo ordenDePrestamoListNewOrdenDePrestamoToAttach : ordenDePrestamoListNew) {
                ordenDePrestamoListNewOrdenDePrestamoToAttach = em.getReference(ordenDePrestamoListNewOrdenDePrestamoToAttach.getClass(), ordenDePrestamoListNewOrdenDePrestamoToAttach.getCodigo());
                attachedOrdenDePrestamoListNew.add(ordenDePrestamoListNewOrdenDePrestamoToAttach);
            }
            ordenDePrestamoListNew = attachedOrdenDePrestamoListNew;
            usuario.setOrdenDePrestamoList(ordenDePrestamoListNew);
            usuario = em.merge(usuario);
            if (profesorNew != null && !profesorNew.equals(profesorOld)) {
                Usuario oldUsuarioOfProfesor = profesorNew.getUsuario();
                if (oldUsuarioOfProfesor != null) {
                    oldUsuarioOfProfesor.setProfesor(null);
                    oldUsuarioOfProfesor = em.merge(oldUsuarioOfProfesor);
                }
                profesorNew.setUsuario(usuario);
                profesorNew = em.merge(profesorNew);
            }
            if (perfilOld != null && !perfilOld.equals(perfilNew)) {
                perfilOld.getUsuarioList().remove(usuario);
                perfilOld = em.merge(perfilOld);
            }
            if (perfilNew != null && !perfilNew.equals(perfilOld)) {
                perfilNew.getUsuarioList().add(usuario);
                perfilNew = em.merge(perfilNew);
            }
            if (estudianteNew != null && !estudianteNew.equals(estudianteOld)) {
                Usuario oldUsuarioOfEstudiante = estudianteNew.getUsuario();
                if (oldUsuarioOfEstudiante != null) {
                    oldUsuarioOfEstudiante.setEstudiante(null);
                    oldUsuarioOfEstudiante = em.merge(oldUsuarioOfEstudiante);
                }
                estudianteNew.setUsuario(usuario);
                estudianteNew = em.merge(estudianteNew);
            }
            if (empleadoNew != null && !empleadoNew.equals(empleadoOld)) {
                Usuario oldUsuarioOfEmpleado = empleadoNew.getUsuario();
                if (oldUsuarioOfEmpleado != null) {
                    oldUsuarioOfEmpleado.setEmpleado(null);
                    oldUsuarioOfEmpleado = em.merge(oldUsuarioOfEmpleado);
                }
                empleadoNew.setUsuario(usuario);
                empleadoNew = em.merge(empleadoNew);
            }
            for (SolicitudDeLibro solicitudDeLibroListNewSolicitudDeLibro : solicitudDeLibroListNew) {
                if (!solicitudDeLibroListOld.contains(solicitudDeLibroListNewSolicitudDeLibro)) {
                    Usuario oldIdUsuarioOfSolicitudDeLibroListNewSolicitudDeLibro = solicitudDeLibroListNewSolicitudDeLibro.getIdUsuario();
                    solicitudDeLibroListNewSolicitudDeLibro.setIdUsuario(usuario);
                    solicitudDeLibroListNewSolicitudDeLibro = em.merge(solicitudDeLibroListNewSolicitudDeLibro);
                    if (oldIdUsuarioOfSolicitudDeLibroListNewSolicitudDeLibro != null && !oldIdUsuarioOfSolicitudDeLibroListNewSolicitudDeLibro.equals(usuario)) {
                        oldIdUsuarioOfSolicitudDeLibroListNewSolicitudDeLibro.getSolicitudDeLibroList().remove(solicitudDeLibroListNewSolicitudDeLibro);
                        oldIdUsuarioOfSolicitudDeLibroListNewSolicitudDeLibro = em.merge(oldIdUsuarioOfSolicitudDeLibroListNewSolicitudDeLibro);
                    }
                }
            }
            for (Descarga descargaListOldDescarga : descargaListOld) {
                if (!descargaListNew.contains(descargaListOldDescarga)) {
                    descargaListOldDescarga.setIdUsuario(null);
                    descargaListOldDescarga = em.merge(descargaListOldDescarga);
                }
            }
            for (Descarga descargaListNewDescarga : descargaListNew) {
                if (!descargaListOld.contains(descargaListNewDescarga)) {
                    Usuario oldIdUsuarioOfDescargaListNewDescarga = descargaListNewDescarga.getIdUsuario();
                    descargaListNewDescarga.setIdUsuario(usuario);
                    descargaListNewDescarga = em.merge(descargaListNewDescarga);
                    if (oldIdUsuarioOfDescargaListNewDescarga != null && !oldIdUsuarioOfDescargaListNewDescarga.equals(usuario)) {
                        oldIdUsuarioOfDescargaListNewDescarga.getDescargaList().remove(descargaListNewDescarga);
                        oldIdUsuarioOfDescargaListNewDescarga = em.merge(oldIdUsuarioOfDescargaListNewDescarga);
                    }
                }
            }
            for (Devolucion devolucionListNewDevolucion : devolucionListNew) {
                if (!devolucionListOld.contains(devolucionListNewDevolucion)) {
                    Usuario oldIdUsuarioOfDevolucionListNewDevolucion = devolucionListNewDevolucion.getIdUsuario();
                    devolucionListNewDevolucion.setIdUsuario(usuario);
                    devolucionListNewDevolucion = em.merge(devolucionListNewDevolucion);
                    if (oldIdUsuarioOfDevolucionListNewDevolucion != null && !oldIdUsuarioOfDevolucionListNewDevolucion.equals(usuario)) {
                        oldIdUsuarioOfDevolucionListNewDevolucion.getDevolucionList().remove(devolucionListNewDevolucion);
                        oldIdUsuarioOfDevolucionListNewDevolucion = em.merge(oldIdUsuarioOfDevolucionListNewDevolucion);
                    }
                }
            }
            for (Multa multaListNewMulta : multaListNew) {
                if (!multaListOld.contains(multaListNewMulta)) {
                    Usuario oldIdUsuarioOfMultaListNewMulta = multaListNewMulta.getIdUsuario();
                    multaListNewMulta.setIdUsuario(usuario);
                    multaListNewMulta = em.merge(multaListNewMulta);
                    if (oldIdUsuarioOfMultaListNewMulta != null && !oldIdUsuarioOfMultaListNewMulta.equals(usuario)) {
                        oldIdUsuarioOfMultaListNewMulta.getMultaList().remove(multaListNewMulta);
                        oldIdUsuarioOfMultaListNewMulta = em.merge(oldIdUsuarioOfMultaListNewMulta);
                    }
                }
            }
            for (OrdenDePrestamo ordenDePrestamoListNewOrdenDePrestamo : ordenDePrestamoListNew) {
                if (!ordenDePrestamoListOld.contains(ordenDePrestamoListNewOrdenDePrestamo)) {
                    Usuario oldIdUsuarioOfOrdenDePrestamoListNewOrdenDePrestamo = ordenDePrestamoListNewOrdenDePrestamo.getIdUsuario();
                    ordenDePrestamoListNewOrdenDePrestamo.setIdUsuario(usuario);
                    ordenDePrestamoListNewOrdenDePrestamo = em.merge(ordenDePrestamoListNewOrdenDePrestamo);
                    if (oldIdUsuarioOfOrdenDePrestamoListNewOrdenDePrestamo != null && !oldIdUsuarioOfOrdenDePrestamoListNewOrdenDePrestamo.equals(usuario)) {
                        oldIdUsuarioOfOrdenDePrestamoListNewOrdenDePrestamo.getOrdenDePrestamoList().remove(ordenDePrestamoListNewOrdenDePrestamo);
                        oldIdUsuarioOfOrdenDePrestamoListNewOrdenDePrestamo = em.merge(oldIdUsuarioOfOrdenDePrestamoListNewOrdenDePrestamo);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = usuario.getIdUsuario();
                if (findUsuario(id) == null) {
                    throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario usuario;
            try {
                usuario = em.getReference(Usuario.class, id);
                usuario.getIdUsuario();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Profesor profesorOrphanCheck = usuario.getProfesor();
            if (profesorOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuario (" + usuario + ") cannot be destroyed since the Profesor " + profesorOrphanCheck + " in its profesor field has a non-nullable usuario field.");
            }
            Estudiante estudianteOrphanCheck = usuario.getEstudiante();
            if (estudianteOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuario (" + usuario + ") cannot be destroyed since the Estudiante " + estudianteOrphanCheck + " in its estudiante field has a non-nullable usuario field.");
            }
            Empleado empleadoOrphanCheck = usuario.getEmpleado();
            if (empleadoOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuario (" + usuario + ") cannot be destroyed since the Empleado " + empleadoOrphanCheck + " in its empleado field has a non-nullable usuario field.");
            }
            List<SolicitudDeLibro> solicitudDeLibroListOrphanCheck = usuario.getSolicitudDeLibroList();
            for (SolicitudDeLibro solicitudDeLibroListOrphanCheckSolicitudDeLibro : solicitudDeLibroListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuario (" + usuario + ") cannot be destroyed since the SolicitudDeLibro " + solicitudDeLibroListOrphanCheckSolicitudDeLibro + " in its solicitudDeLibroList field has a non-nullable idUsuario field.");
            }
            List<Devolucion> devolucionListOrphanCheck = usuario.getDevolucionList();
            for (Devolucion devolucionListOrphanCheckDevolucion : devolucionListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuario (" + usuario + ") cannot be destroyed since the Devolucion " + devolucionListOrphanCheckDevolucion + " in its devolucionList field has a non-nullable idUsuario field.");
            }
            List<Multa> multaListOrphanCheck = usuario.getMultaList();
            for (Multa multaListOrphanCheckMulta : multaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuario (" + usuario + ") cannot be destroyed since the Multa " + multaListOrphanCheckMulta + " in its multaList field has a non-nullable idUsuario field.");
            }
            List<OrdenDePrestamo> ordenDePrestamoListOrphanCheck = usuario.getOrdenDePrestamoList();
            for (OrdenDePrestamo ordenDePrestamoListOrphanCheckOrdenDePrestamo : ordenDePrestamoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuario (" + usuario + ") cannot be destroyed since the OrdenDePrestamo " + ordenDePrestamoListOrphanCheckOrdenDePrestamo + " in its ordenDePrestamoList field has a non-nullable idUsuario field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Perfil perfil = usuario.getPerfil();
            if (perfil != null) {
                perfil.getUsuarioList().remove(usuario);
                perfil = em.merge(perfil);
            }
            List<Descarga> descargaList = usuario.getDescargaList();
            for (Descarga descargaListDescarga : descargaList) {
                descargaListDescarga.setIdUsuario(null);
                descargaListDescarga = em.merge(descargaListDescarga);
            }
            em.remove(usuario);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Usuario> findUsuarioEntities() {
        return findUsuarioEntities(true, -1, -1);
    }

    public List<Usuario> findUsuarioEntities(int maxResults, int firstResult) {
        return findUsuarioEntities(false, maxResults, firstResult);
    }

    private List<Usuario> findUsuarioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Usuario.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Usuario findUsuario(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Usuario.class, id);
        } finally {
            em.close();
        }
    }

    public int getUsuarioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Usuario> rt = cq.from(Usuario.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
