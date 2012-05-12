/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DaoJPA;

import DaoJPA.exceptions.NonexistentEntityException;
import DaoJPA.exceptions.PreexistingEntityException;
import Entidades.SubareaDeConocimiento;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entidades.AreaDeConocimiento;

/**
 *
 * @author JHONATHAN
 */
public class SubareaDeConocimientoJpaController implements Serializable {

    public SubareaDeConocimientoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(SubareaDeConocimiento subareaDeConocimiento) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            AreaDeConocimiento codigoPadre = subareaDeConocimiento.getCodigoPadre();
            if (codigoPadre != null) {
                codigoPadre = em.getReference(codigoPadre.getClass(), codigoPadre.getCodigo());
                subareaDeConocimiento.setCodigoPadre(codigoPadre);
            }
            AreaDeConocimiento areaDeConocimiento = subareaDeConocimiento.getAreaDeConocimiento();
            if (areaDeConocimiento != null) {
                areaDeConocimiento = em.getReference(areaDeConocimiento.getClass(), areaDeConocimiento.getCodigo());
                subareaDeConocimiento.setAreaDeConocimiento(areaDeConocimiento);
            }
            em.persist(subareaDeConocimiento);
            if (codigoPadre != null) {
                codigoPadre.getSubareaDeConocimientoList().add(subareaDeConocimiento);
                codigoPadre = em.merge(codigoPadre);
            }
            if (areaDeConocimiento != null) {
                areaDeConocimiento.getSubareaDeConocimientoList().add(subareaDeConocimiento);
                areaDeConocimiento = em.merge(areaDeConocimiento);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findSubareaDeConocimiento(subareaDeConocimiento.getCodigo()) != null) {
                throw new PreexistingEntityException("SubareaDeConocimiento " + subareaDeConocimiento + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(SubareaDeConocimiento subareaDeConocimiento) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            SubareaDeConocimiento persistentSubareaDeConocimiento = em.find(SubareaDeConocimiento.class, subareaDeConocimiento.getCodigo());
            AreaDeConocimiento codigoPadreOld = persistentSubareaDeConocimiento.getCodigoPadre();
            AreaDeConocimiento codigoPadreNew = subareaDeConocimiento.getCodigoPadre();
            AreaDeConocimiento areaDeConocimientoOld = persistentSubareaDeConocimiento.getAreaDeConocimiento();
            AreaDeConocimiento areaDeConocimientoNew = subareaDeConocimiento.getAreaDeConocimiento();
            if (codigoPadreNew != null) {
                codigoPadreNew = em.getReference(codigoPadreNew.getClass(), codigoPadreNew.getCodigo());
                subareaDeConocimiento.setCodigoPadre(codigoPadreNew);
            }
            if (areaDeConocimientoNew != null) {
                areaDeConocimientoNew = em.getReference(areaDeConocimientoNew.getClass(), areaDeConocimientoNew.getCodigo());
                subareaDeConocimiento.setAreaDeConocimiento(areaDeConocimientoNew);
            }
            subareaDeConocimiento = em.merge(subareaDeConocimiento);
            if (codigoPadreOld != null && !codigoPadreOld.equals(codigoPadreNew)) {
                codigoPadreOld.getSubareaDeConocimientoList().remove(subareaDeConocimiento);
                codigoPadreOld = em.merge(codigoPadreOld);
            }
            if (codigoPadreNew != null && !codigoPadreNew.equals(codigoPadreOld)) {
                codigoPadreNew.getSubareaDeConocimientoList().add(subareaDeConocimiento);
                codigoPadreNew = em.merge(codigoPadreNew);
            }
            if (areaDeConocimientoOld != null && !areaDeConocimientoOld.equals(areaDeConocimientoNew)) {
                areaDeConocimientoOld.getSubareaDeConocimientoList().remove(subareaDeConocimiento);
                areaDeConocimientoOld = em.merge(areaDeConocimientoOld);
            }
            if (areaDeConocimientoNew != null && !areaDeConocimientoNew.equals(areaDeConocimientoOld)) {
                areaDeConocimientoNew.getSubareaDeConocimientoList().add(subareaDeConocimiento);
                areaDeConocimientoNew = em.merge(areaDeConocimientoNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = subareaDeConocimiento.getCodigo();
                if (findSubareaDeConocimiento(id) == null) {
                    throw new NonexistentEntityException("The subareaDeConocimiento with id " + id + " no longer exists.");
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
            SubareaDeConocimiento subareaDeConocimiento;
            try {
                subareaDeConocimiento = em.getReference(SubareaDeConocimiento.class, id);
                subareaDeConocimiento.getCodigo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The subareaDeConocimiento with id " + id + " no longer exists.", enfe);
            }
            AreaDeConocimiento codigoPadre = subareaDeConocimiento.getCodigoPadre();
            if (codigoPadre != null) {
                codigoPadre.getSubareaDeConocimientoList().remove(subareaDeConocimiento);
                codigoPadre = em.merge(codigoPadre);
            }
            AreaDeConocimiento areaDeConocimiento = subareaDeConocimiento.getAreaDeConocimiento();
            if (areaDeConocimiento != null) {
                areaDeConocimiento.getSubareaDeConocimientoList().remove(subareaDeConocimiento);
                areaDeConocimiento = em.merge(areaDeConocimiento);
            }
            em.remove(subareaDeConocimiento);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<SubareaDeConocimiento> findSubareaDeConocimientoEntities() {
        return findSubareaDeConocimientoEntities(true, -1, -1);
    }

    public List<SubareaDeConocimiento> findSubareaDeConocimientoEntities(int maxResults, int firstResult) {
        return findSubareaDeConocimientoEntities(false, maxResults, firstResult);
    }

    private List<SubareaDeConocimiento> findSubareaDeConocimientoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(SubareaDeConocimiento.class));
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

    public SubareaDeConocimiento findSubareaDeConocimiento(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(SubareaDeConocimiento.class, id);
        } finally {
            em.close();
        }
    }

    public int getSubareaDeConocimientoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<SubareaDeConocimiento> rt = cq.from(SubareaDeConocimiento.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
