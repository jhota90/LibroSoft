/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DaoJPA;

import DaoJPA.exceptions.NonexistentEntityException;
import DaoJPA.exceptions.PreexistingEntityException;
import Entidades.Devolucion;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entidades.Ejemplar;
import Entidades.Usuario;

/**
 *
 * @author JHONATHAN
 */
public class DevolucionJpaController implements Serializable {

    public DevolucionJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Devolucion devolucion) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Ejemplar codigoEjemplar = devolucion.getCodigoEjemplar();
            if (codigoEjemplar != null) {
                codigoEjemplar = em.getReference(codigoEjemplar.getClass(), codigoEjemplar.getCodigo());
                devolucion.setCodigoEjemplar(codigoEjemplar);
            }
            Usuario idUsuario = devolucion.getIdUsuario();
            if (idUsuario != null) {
                idUsuario = em.getReference(idUsuario.getClass(), idUsuario.getIdUsuario());
                devolucion.setIdUsuario(idUsuario);
            }
            em.persist(devolucion);
            if (codigoEjemplar != null) {
                codigoEjemplar.getDevolucionList().add(devolucion);
                codigoEjemplar = em.merge(codigoEjemplar);
            }
            if (idUsuario != null) {
                idUsuario.getDevolucionList().add(devolucion);
                idUsuario = em.merge(idUsuario);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findDevolucion(devolucion.getCodigo()) != null) {
                throw new PreexistingEntityException("Devolucion " + devolucion + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Devolucion devolucion) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Devolucion persistentDevolucion = em.find(Devolucion.class, devolucion.getCodigo());
            Ejemplar codigoEjemplarOld = persistentDevolucion.getCodigoEjemplar();
            Ejemplar codigoEjemplarNew = devolucion.getCodigoEjemplar();
            Usuario idUsuarioOld = persistentDevolucion.getIdUsuario();
            Usuario idUsuarioNew = devolucion.getIdUsuario();
            if (codigoEjemplarNew != null) {
                codigoEjemplarNew = em.getReference(codigoEjemplarNew.getClass(), codigoEjemplarNew.getCodigo());
                devolucion.setCodigoEjemplar(codigoEjemplarNew);
            }
            if (idUsuarioNew != null) {
                idUsuarioNew = em.getReference(idUsuarioNew.getClass(), idUsuarioNew.getIdUsuario());
                devolucion.setIdUsuario(idUsuarioNew);
            }
            devolucion = em.merge(devolucion);
            if (codigoEjemplarOld != null && !codigoEjemplarOld.equals(codigoEjemplarNew)) {
                codigoEjemplarOld.getDevolucionList().remove(devolucion);
                codigoEjemplarOld = em.merge(codigoEjemplarOld);
            }
            if (codigoEjemplarNew != null && !codigoEjemplarNew.equals(codigoEjemplarOld)) {
                codigoEjemplarNew.getDevolucionList().add(devolucion);
                codigoEjemplarNew = em.merge(codigoEjemplarNew);
            }
            if (idUsuarioOld != null && !idUsuarioOld.equals(idUsuarioNew)) {
                idUsuarioOld.getDevolucionList().remove(devolucion);
                idUsuarioOld = em.merge(idUsuarioOld);
            }
            if (idUsuarioNew != null && !idUsuarioNew.equals(idUsuarioOld)) {
                idUsuarioNew.getDevolucionList().add(devolucion);
                idUsuarioNew = em.merge(idUsuarioNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = devolucion.getCodigo();
                if (findDevolucion(id) == null) {
                    throw new NonexistentEntityException("The devolucion with id " + id + " no longer exists.");
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
            Devolucion devolucion;
            try {
                devolucion = em.getReference(Devolucion.class, id);
                devolucion.getCodigo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The devolucion with id " + id + " no longer exists.", enfe);
            }
            Ejemplar codigoEjemplar = devolucion.getCodigoEjemplar();
            if (codigoEjemplar != null) {
                codigoEjemplar.getDevolucionList().remove(devolucion);
                codigoEjemplar = em.merge(codigoEjemplar);
            }
            Usuario idUsuario = devolucion.getIdUsuario();
            if (idUsuario != null) {
                idUsuario.getDevolucionList().remove(devolucion);
                idUsuario = em.merge(idUsuario);
            }
            em.remove(devolucion);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Devolucion> findDevolucionEntities() {
        return findDevolucionEntities(true, -1, -1);
    }

    public List<Devolucion> findDevolucionEntities(int maxResults, int firstResult) {
        return findDevolucionEntities(false, maxResults, firstResult);
    }

    private List<Devolucion> findDevolucionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Devolucion.class));
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

    public Devolucion findDevolucion(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Devolucion.class, id);
        } finally {
            em.close();
        }
    }

    public int getDevolucionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Devolucion> rt = cq.from(Devolucion.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
