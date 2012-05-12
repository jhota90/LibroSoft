/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DaoJPA;

import DaoJPA.exceptions.IllegalOrphanException;
import DaoJPA.exceptions.NonexistentEntityException;
import DaoJPA.exceptions.PreexistingEntityException;
import Entidades.Profesor;
import java.io.Serializable;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entidades.Dependencia;
import Entidades.Usuario;
import Entidades.AreaDeConocimiento;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JHONATHAN
 */
public class ProfesorJpaController implements Serializable {

    public ProfesorJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Profesor profesor) throws IllegalOrphanException, PreexistingEntityException, Exception {
        if (profesor.getAreaDeConocimientoList() == null) {
            profesor.setAreaDeConocimientoList(new ArrayList<AreaDeConocimiento>());
        }
        List<String> illegalOrphanMessages = null;
        Usuario usuarioOrphanCheck = profesor.getUsuario();
        if (usuarioOrphanCheck != null) {
            Profesor oldProfesorOfUsuario = usuarioOrphanCheck.getProfesor();
            if (oldProfesorOfUsuario != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The Usuario " + usuarioOrphanCheck + " already has an item of type Profesor whose usuario column cannot be null. Please make another selection for the usuario field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Dependencia dependencia = profesor.getDependencia();
            if (dependencia != null) {
                dependencia = em.getReference(dependencia.getClass(), dependencia.getCodigo());
                profesor.setDependencia(dependencia);
            }
            Usuario usuario = profesor.getUsuario();
            if (usuario != null) {
                usuario = em.getReference(usuario.getClass(), usuario.getIdUsuario());
                profesor.setUsuario(usuario);
            }
            List<AreaDeConocimiento> attachedAreaDeConocimientoList = new ArrayList<AreaDeConocimiento>();
            for (AreaDeConocimiento areaDeConocimientoListAreaDeConocimientoToAttach : profesor.getAreaDeConocimientoList()) {
                areaDeConocimientoListAreaDeConocimientoToAttach = em.getReference(areaDeConocimientoListAreaDeConocimientoToAttach.getClass(), areaDeConocimientoListAreaDeConocimientoToAttach.getCodigo());
                attachedAreaDeConocimientoList.add(areaDeConocimientoListAreaDeConocimientoToAttach);
            }
            profesor.setAreaDeConocimientoList(attachedAreaDeConocimientoList);
            em.persist(profesor);
            if (dependencia != null) {
                dependencia.getProfesorList().add(profesor);
                dependencia = em.merge(dependencia);
            }
            if (usuario != null) {
                usuario.setProfesor(profesor);
                usuario = em.merge(usuario);
            }
            for (AreaDeConocimiento areaDeConocimientoListAreaDeConocimiento : profesor.getAreaDeConocimientoList()) {
                areaDeConocimientoListAreaDeConocimiento.getProfesorList().add(profesor);
                areaDeConocimientoListAreaDeConocimiento = em.merge(areaDeConocimientoListAreaDeConocimiento);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findProfesor(profesor.getIdProfesor()) != null) {
                throw new PreexistingEntityException("Profesor " + profesor + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Profesor profesor) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Profesor persistentProfesor = em.find(Profesor.class, profesor.getIdProfesor());
            Dependencia dependenciaOld = persistentProfesor.getDependencia();
            Dependencia dependenciaNew = profesor.getDependencia();
            Usuario usuarioOld = persistentProfesor.getUsuario();
            Usuario usuarioNew = profesor.getUsuario();
            List<AreaDeConocimiento> areaDeConocimientoListOld = persistentProfesor.getAreaDeConocimientoList();
            List<AreaDeConocimiento> areaDeConocimientoListNew = profesor.getAreaDeConocimientoList();
            List<String> illegalOrphanMessages = null;
            if (usuarioNew != null && !usuarioNew.equals(usuarioOld)) {
                Profesor oldProfesorOfUsuario = usuarioNew.getProfesor();
                if (oldProfesorOfUsuario != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The Usuario " + usuarioNew + " already has an item of type Profesor whose usuario column cannot be null. Please make another selection for the usuario field.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (dependenciaNew != null) {
                dependenciaNew = em.getReference(dependenciaNew.getClass(), dependenciaNew.getCodigo());
                profesor.setDependencia(dependenciaNew);
            }
            if (usuarioNew != null) {
                usuarioNew = em.getReference(usuarioNew.getClass(), usuarioNew.getIdUsuario());
                profesor.setUsuario(usuarioNew);
            }
            List<AreaDeConocimiento> attachedAreaDeConocimientoListNew = new ArrayList<AreaDeConocimiento>();
            for (AreaDeConocimiento areaDeConocimientoListNewAreaDeConocimientoToAttach : areaDeConocimientoListNew) {
                areaDeConocimientoListNewAreaDeConocimientoToAttach = em.getReference(areaDeConocimientoListNewAreaDeConocimientoToAttach.getClass(), areaDeConocimientoListNewAreaDeConocimientoToAttach.getCodigo());
                attachedAreaDeConocimientoListNew.add(areaDeConocimientoListNewAreaDeConocimientoToAttach);
            }
            areaDeConocimientoListNew = attachedAreaDeConocimientoListNew;
            profesor.setAreaDeConocimientoList(areaDeConocimientoListNew);
            profesor = em.merge(profesor);
            if (dependenciaOld != null && !dependenciaOld.equals(dependenciaNew)) {
                dependenciaOld.getProfesorList().remove(profesor);
                dependenciaOld = em.merge(dependenciaOld);
            }
            if (dependenciaNew != null && !dependenciaNew.equals(dependenciaOld)) {
                dependenciaNew.getProfesorList().add(profesor);
                dependenciaNew = em.merge(dependenciaNew);
            }
            if (usuarioOld != null && !usuarioOld.equals(usuarioNew)) {
                usuarioOld.setProfesor(null);
                usuarioOld = em.merge(usuarioOld);
            }
            if (usuarioNew != null && !usuarioNew.equals(usuarioOld)) {
                usuarioNew.setProfesor(profesor);
                usuarioNew = em.merge(usuarioNew);
            }
            for (AreaDeConocimiento areaDeConocimientoListOldAreaDeConocimiento : areaDeConocimientoListOld) {
                if (!areaDeConocimientoListNew.contains(areaDeConocimientoListOldAreaDeConocimiento)) {
                    areaDeConocimientoListOldAreaDeConocimiento.getProfesorList().remove(profesor);
                    areaDeConocimientoListOldAreaDeConocimiento = em.merge(areaDeConocimientoListOldAreaDeConocimiento);
                }
            }
            for (AreaDeConocimiento areaDeConocimientoListNewAreaDeConocimiento : areaDeConocimientoListNew) {
                if (!areaDeConocimientoListOld.contains(areaDeConocimientoListNewAreaDeConocimiento)) {
                    areaDeConocimientoListNewAreaDeConocimiento.getProfesorList().add(profesor);
                    areaDeConocimientoListNewAreaDeConocimiento = em.merge(areaDeConocimientoListNewAreaDeConocimiento);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = profesor.getIdProfesor();
                if (findProfesor(id) == null) {
                    throw new NonexistentEntityException("The profesor with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Profesor profesor;
            try {
                profesor = em.getReference(Profesor.class, id);
                profesor.getIdProfesor();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The profesor with id " + id + " no longer exists.", enfe);
            }
            Dependencia dependencia = profesor.getDependencia();
            if (dependencia != null) {
                dependencia.getProfesorList().remove(profesor);
                dependencia = em.merge(dependencia);
            }
            Usuario usuario = profesor.getUsuario();
            if (usuario != null) {
                usuario.setProfesor(null);
                usuario = em.merge(usuario);
            }
            List<AreaDeConocimiento> areaDeConocimientoList = profesor.getAreaDeConocimientoList();
            for (AreaDeConocimiento areaDeConocimientoListAreaDeConocimiento : areaDeConocimientoList) {
                areaDeConocimientoListAreaDeConocimiento.getProfesorList().remove(profesor);
                areaDeConocimientoListAreaDeConocimiento = em.merge(areaDeConocimientoListAreaDeConocimiento);
            }
            em.remove(profesor);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Profesor> findProfesorEntities() {
        return findProfesorEntities(true, -1, -1);
    }

    public List<Profesor> findProfesorEntities(int maxResults, int firstResult) {
        return findProfesorEntities(false, maxResults, firstResult);
    }

    private List<Profesor> findProfesorEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Profesor.class));
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

    public Profesor findProfesor(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Profesor.class, id);
        } finally {
            em.close();
        }
    }

    public int getProfesorCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Profesor> rt = cq.from(Profesor.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
