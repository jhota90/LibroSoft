/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DaoJPA;

import DaoJPA.exceptions.NonexistentEntityException;
import DaoJPA.exceptions.PreexistingEntityException;
import Entidades.SolicitudDeLibro;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entidades.Usuario;

/**
 *
 * @author JHONATHAN
 */
public class SolicitudDeLibroJpaController implements Serializable {

    public SolicitudDeLibroJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(SolicitudDeLibro solicitudDeLibro) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario idUsuario = solicitudDeLibro.getIdUsuario();
            if (idUsuario != null) {
                idUsuario = em.getReference(idUsuario.getClass(), idUsuario.getIdUsuario());
                solicitudDeLibro.setIdUsuario(idUsuario);
            }
            em.persist(solicitudDeLibro);
            if (idUsuario != null) {
                idUsuario.getSolicitudDeLibroList().add(solicitudDeLibro);
                idUsuario = em.merge(idUsuario);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findSolicitudDeLibro(solicitudDeLibro.getCodigo()) != null) {
                throw new PreexistingEntityException("SolicitudDeLibro " + solicitudDeLibro + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(SolicitudDeLibro solicitudDeLibro) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            SolicitudDeLibro persistentSolicitudDeLibro = em.find(SolicitudDeLibro.class, solicitudDeLibro.getCodigo());
            Usuario idUsuarioOld = persistentSolicitudDeLibro.getIdUsuario();
            Usuario idUsuarioNew = solicitudDeLibro.getIdUsuario();
            if (idUsuarioNew != null) {
                idUsuarioNew = em.getReference(idUsuarioNew.getClass(), idUsuarioNew.getIdUsuario());
                solicitudDeLibro.setIdUsuario(idUsuarioNew);
            }
            solicitudDeLibro = em.merge(solicitudDeLibro);
            if (idUsuarioOld != null && !idUsuarioOld.equals(idUsuarioNew)) {
                idUsuarioOld.getSolicitudDeLibroList().remove(solicitudDeLibro);
                idUsuarioOld = em.merge(idUsuarioOld);
            }
            if (idUsuarioNew != null && !idUsuarioNew.equals(idUsuarioOld)) {
                idUsuarioNew.getSolicitudDeLibroList().add(solicitudDeLibro);
                idUsuarioNew = em.merge(idUsuarioNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = solicitudDeLibro.getCodigo();
                if (findSolicitudDeLibro(id) == null) {
                    throw new NonexistentEntityException("The solicitudDeLibro with id " + id + " no longer exists.");
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
            SolicitudDeLibro solicitudDeLibro;
            try {
                solicitudDeLibro = em.getReference(SolicitudDeLibro.class, id);
                solicitudDeLibro.getCodigo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The solicitudDeLibro with id " + id + " no longer exists.", enfe);
            }
            Usuario idUsuario = solicitudDeLibro.getIdUsuario();
            if (idUsuario != null) {
                idUsuario.getSolicitudDeLibroList().remove(solicitudDeLibro);
                idUsuario = em.merge(idUsuario);
            }
            em.remove(solicitudDeLibro);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<SolicitudDeLibro> findSolicitudDeLibroEntities() {
        return findSolicitudDeLibroEntities(true, -1, -1);
    }

    public List<SolicitudDeLibro> findSolicitudDeLibroEntities(int maxResults, int firstResult) {
        return findSolicitudDeLibroEntities(false, maxResults, firstResult);
    }

    private List<SolicitudDeLibro> findSolicitudDeLibroEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(SolicitudDeLibro.class));
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

    public SolicitudDeLibro findSolicitudDeLibro(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(SolicitudDeLibro.class, id);
        } finally {
            em.close();
        }
    }

    public int getSolicitudDeLibroCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<SolicitudDeLibro> rt = cq.from(SolicitudDeLibro.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
