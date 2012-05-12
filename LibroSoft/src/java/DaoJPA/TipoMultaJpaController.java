/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DaoJPA;

import DaoJPA.exceptions.IllegalOrphanException;
import DaoJPA.exceptions.NonexistentEntityException;
import DaoJPA.exceptions.PreexistingEntityException;
import Entidades.TipoMulta;
import java.io.Serializable;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entidades.Multa;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JHONATHAN
 */
public class TipoMultaJpaController implements Serializable {

    public TipoMultaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(TipoMulta tipoMulta) throws PreexistingEntityException, Exception {
        if (tipoMulta.getMultaList() == null) {
            tipoMulta.setMultaList(new ArrayList<Multa>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Multa> attachedMultaList = new ArrayList<Multa>();
            for (Multa multaListMultaToAttach : tipoMulta.getMultaList()) {
                multaListMultaToAttach = em.getReference(multaListMultaToAttach.getClass(), multaListMultaToAttach.getCodigo());
                attachedMultaList.add(multaListMultaToAttach);
            }
            tipoMulta.setMultaList(attachedMultaList);
            em.persist(tipoMulta);
            for (Multa multaListMulta : tipoMulta.getMultaList()) {
                TipoMulta oldTipoMultaOfMultaListMulta = multaListMulta.getTipoMulta();
                multaListMulta.setTipoMulta(tipoMulta);
                multaListMulta = em.merge(multaListMulta);
                if (oldTipoMultaOfMultaListMulta != null) {
                    oldTipoMultaOfMultaListMulta.getMultaList().remove(multaListMulta);
                    oldTipoMultaOfMultaListMulta = em.merge(oldTipoMultaOfMultaListMulta);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findTipoMulta(tipoMulta.getCodigo()) != null) {
                throw new PreexistingEntityException("TipoMulta " + tipoMulta + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(TipoMulta tipoMulta) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TipoMulta persistentTipoMulta = em.find(TipoMulta.class, tipoMulta.getCodigo());
            List<Multa> multaListOld = persistentTipoMulta.getMultaList();
            List<Multa> multaListNew = tipoMulta.getMultaList();
            List<String> illegalOrphanMessages = null;
            for (Multa multaListOldMulta : multaListOld) {
                if (!multaListNew.contains(multaListOldMulta)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Multa " + multaListOldMulta + " since its tipoMulta field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Multa> attachedMultaListNew = new ArrayList<Multa>();
            for (Multa multaListNewMultaToAttach : multaListNew) {
                multaListNewMultaToAttach = em.getReference(multaListNewMultaToAttach.getClass(), multaListNewMultaToAttach.getCodigo());
                attachedMultaListNew.add(multaListNewMultaToAttach);
            }
            multaListNew = attachedMultaListNew;
            tipoMulta.setMultaList(multaListNew);
            tipoMulta = em.merge(tipoMulta);
            for (Multa multaListNewMulta : multaListNew) {
                if (!multaListOld.contains(multaListNewMulta)) {
                    TipoMulta oldTipoMultaOfMultaListNewMulta = multaListNewMulta.getTipoMulta();
                    multaListNewMulta.setTipoMulta(tipoMulta);
                    multaListNewMulta = em.merge(multaListNewMulta);
                    if (oldTipoMultaOfMultaListNewMulta != null && !oldTipoMultaOfMultaListNewMulta.equals(tipoMulta)) {
                        oldTipoMultaOfMultaListNewMulta.getMultaList().remove(multaListNewMulta);
                        oldTipoMultaOfMultaListNewMulta = em.merge(oldTipoMultaOfMultaListNewMulta);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = tipoMulta.getCodigo();
                if (findTipoMulta(id) == null) {
                    throw new NonexistentEntityException("The tipoMulta with id " + id + " no longer exists.");
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
            TipoMulta tipoMulta;
            try {
                tipoMulta = em.getReference(TipoMulta.class, id);
                tipoMulta.getCodigo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The tipoMulta with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Multa> multaListOrphanCheck = tipoMulta.getMultaList();
            for (Multa multaListOrphanCheckMulta : multaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This TipoMulta (" + tipoMulta + ") cannot be destroyed since the Multa " + multaListOrphanCheckMulta + " in its multaList field has a non-nullable tipoMulta field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(tipoMulta);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<TipoMulta> findTipoMultaEntities() {
        return findTipoMultaEntities(true, -1, -1);
    }

    public List<TipoMulta> findTipoMultaEntities(int maxResults, int firstResult) {
        return findTipoMultaEntities(false, maxResults, firstResult);
    }

    private List<TipoMulta> findTipoMultaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(TipoMulta.class));
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

    public TipoMulta findTipoMulta(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(TipoMulta.class, id);
        } finally {
            em.close();
        }
    }

    public int getTipoMultaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<TipoMulta> rt = cq.from(TipoMulta.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
