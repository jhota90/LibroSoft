/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DaoJPA;

import DaoJPA.exceptions.NonexistentEntityException;
import DaoJPA.exceptions.PreexistingEntityException;
import Entidades.PagoMulta;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entidades.Multa;

/**
 *
 * @author JHONATHAN
 */
public class PagoMultaJpaController implements Serializable {

    public PagoMultaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(PagoMulta pagoMulta) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Multa multa = pagoMulta.getMulta();
            if (multa != null) {
                multa = em.getReference(multa.getClass(), multa.getCodigo());
                pagoMulta.setMulta(multa);
            }
            em.persist(pagoMulta);
            if (multa != null) {
                multa.getPagoMultaList().add(pagoMulta);
                multa = em.merge(multa);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findPagoMulta(pagoMulta.getCodigo()) != null) {
                throw new PreexistingEntityException("PagoMulta " + pagoMulta + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(PagoMulta pagoMulta) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            PagoMulta persistentPagoMulta = em.find(PagoMulta.class, pagoMulta.getCodigo());
            Multa multaOld = persistentPagoMulta.getMulta();
            Multa multaNew = pagoMulta.getMulta();
            if (multaNew != null) {
                multaNew = em.getReference(multaNew.getClass(), multaNew.getCodigo());
                pagoMulta.setMulta(multaNew);
            }
            pagoMulta = em.merge(pagoMulta);
            if (multaOld != null && !multaOld.equals(multaNew)) {
                multaOld.getPagoMultaList().remove(pagoMulta);
                multaOld = em.merge(multaOld);
            }
            if (multaNew != null && !multaNew.equals(multaOld)) {
                multaNew.getPagoMultaList().add(pagoMulta);
                multaNew = em.merge(multaNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = pagoMulta.getCodigo();
                if (findPagoMulta(id) == null) {
                    throw new NonexistentEntityException("The pagoMulta with id " + id + " no longer exists.");
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
            PagoMulta pagoMulta;
            try {
                pagoMulta = em.getReference(PagoMulta.class, id);
                pagoMulta.getCodigo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The pagoMulta with id " + id + " no longer exists.", enfe);
            }
            Multa multa = pagoMulta.getMulta();
            if (multa != null) {
                multa.getPagoMultaList().remove(pagoMulta);
                multa = em.merge(multa);
            }
            em.remove(pagoMulta);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<PagoMulta> findPagoMultaEntities() {
        return findPagoMultaEntities(true, -1, -1);
    }

    public List<PagoMulta> findPagoMultaEntities(int maxResults, int firstResult) {
        return findPagoMultaEntities(false, maxResults, firstResult);
    }

    private List<PagoMulta> findPagoMultaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(PagoMulta.class));
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

    public PagoMulta findPagoMulta(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(PagoMulta.class, id);
        } finally {
            em.close();
        }
    }

    public int getPagoMultaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<PagoMulta> rt = cq.from(PagoMulta.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
