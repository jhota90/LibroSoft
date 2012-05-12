/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DaoJPA;

import DaoJPA.exceptions.NonexistentEntityException;
import DaoJPA.exceptions.PreexistingEntityException;
import Entidades.Cajon;
import java.io.Serializable;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entidades.Ubicacion;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JHONATHAN
 */
public class CajonJpaController implements Serializable {

    public CajonJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Cajon cajon) throws PreexistingEntityException, Exception {
        if (cajon.getUbicacionList() == null) {
            cajon.setUbicacionList(new ArrayList<Ubicacion>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Ubicacion> attachedUbicacionList = new ArrayList<Ubicacion>();
            for (Ubicacion ubicacionListUbicacionToAttach : cajon.getUbicacionList()) {
                ubicacionListUbicacionToAttach = em.getReference(ubicacionListUbicacionToAttach.getClass(), ubicacionListUbicacionToAttach.getUbicacion());
                attachedUbicacionList.add(ubicacionListUbicacionToAttach);
            }
            cajon.setUbicacionList(attachedUbicacionList);
            em.persist(cajon);
            for (Ubicacion ubicacionListUbicacion : cajon.getUbicacionList()) {
                Cajon oldCajonOfUbicacionListUbicacion = ubicacionListUbicacion.getCajon();
                ubicacionListUbicacion.setCajon(cajon);
                ubicacionListUbicacion = em.merge(ubicacionListUbicacion);
                if (oldCajonOfUbicacionListUbicacion != null) {
                    oldCajonOfUbicacionListUbicacion.getUbicacionList().remove(ubicacionListUbicacion);
                    oldCajonOfUbicacionListUbicacion = em.merge(oldCajonOfUbicacionListUbicacion);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findCajon(cajon.getCajon()) != null) {
                throw new PreexistingEntityException("Cajon " + cajon + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Cajon cajon) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Cajon persistentCajon = em.find(Cajon.class, cajon.getCajon());
            List<Ubicacion> ubicacionListOld = persistentCajon.getUbicacionList();
            List<Ubicacion> ubicacionListNew = cajon.getUbicacionList();
            List<Ubicacion> attachedUbicacionListNew = new ArrayList<Ubicacion>();
            for (Ubicacion ubicacionListNewUbicacionToAttach : ubicacionListNew) {
                ubicacionListNewUbicacionToAttach = em.getReference(ubicacionListNewUbicacionToAttach.getClass(), ubicacionListNewUbicacionToAttach.getUbicacion());
                attachedUbicacionListNew.add(ubicacionListNewUbicacionToAttach);
            }
            ubicacionListNew = attachedUbicacionListNew;
            cajon.setUbicacionList(ubicacionListNew);
            cajon = em.merge(cajon);
            for (Ubicacion ubicacionListOldUbicacion : ubicacionListOld) {
                if (!ubicacionListNew.contains(ubicacionListOldUbicacion)) {
                    ubicacionListOldUbicacion.setCajon(null);
                    ubicacionListOldUbicacion = em.merge(ubicacionListOldUbicacion);
                }
            }
            for (Ubicacion ubicacionListNewUbicacion : ubicacionListNew) {
                if (!ubicacionListOld.contains(ubicacionListNewUbicacion)) {
                    Cajon oldCajonOfUbicacionListNewUbicacion = ubicacionListNewUbicacion.getCajon();
                    ubicacionListNewUbicacion.setCajon(cajon);
                    ubicacionListNewUbicacion = em.merge(ubicacionListNewUbicacion);
                    if (oldCajonOfUbicacionListNewUbicacion != null && !oldCajonOfUbicacionListNewUbicacion.equals(cajon)) {
                        oldCajonOfUbicacionListNewUbicacion.getUbicacionList().remove(ubicacionListNewUbicacion);
                        oldCajonOfUbicacionListNewUbicacion = em.merge(oldCajonOfUbicacionListNewUbicacion);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = cajon.getCajon();
                if (findCajon(id) == null) {
                    throw new NonexistentEntityException("The cajon with id " + id + " no longer exists.");
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
            Cajon cajon;
            try {
                cajon = em.getReference(Cajon.class, id);
                cajon.getCajon();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The cajon with id " + id + " no longer exists.", enfe);
            }
            List<Ubicacion> ubicacionList = cajon.getUbicacionList();
            for (Ubicacion ubicacionListUbicacion : ubicacionList) {
                ubicacionListUbicacion.setCajon(null);
                ubicacionListUbicacion = em.merge(ubicacionListUbicacion);
            }
            em.remove(cajon);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Cajon> findCajonEntities() {
        return findCajonEntities(true, -1, -1);
    }

    public List<Cajon> findCajonEntities(int maxResults, int firstResult) {
        return findCajonEntities(false, maxResults, firstResult);
    }

    private List<Cajon> findCajonEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Cajon.class));
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

    public Cajon findCajon(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Cajon.class, id);
        } finally {
            em.close();
        }
    }

    public int getCajonCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Cajon> rt = cq.from(Cajon.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
