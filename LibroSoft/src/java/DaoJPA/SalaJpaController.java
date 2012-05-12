/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DaoJPA;

import DaoJPA.exceptions.NonexistentEntityException;
import DaoJPA.exceptions.PreexistingEntityException;
import Entidades.Sala;
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
public class SalaJpaController implements Serializable {

    public SalaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Sala sala) throws PreexistingEntityException, Exception {
        if (sala.getUbicacionList() == null) {
            sala.setUbicacionList(new ArrayList<Ubicacion>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Ubicacion> attachedUbicacionList = new ArrayList<Ubicacion>();
            for (Ubicacion ubicacionListUbicacionToAttach : sala.getUbicacionList()) {
                ubicacionListUbicacionToAttach = em.getReference(ubicacionListUbicacionToAttach.getClass(), ubicacionListUbicacionToAttach.getUbicacion());
                attachedUbicacionList.add(ubicacionListUbicacionToAttach);
            }
            sala.setUbicacionList(attachedUbicacionList);
            em.persist(sala);
            for (Ubicacion ubicacionListUbicacion : sala.getUbicacionList()) {
                Sala oldSalaOfUbicacionListUbicacion = ubicacionListUbicacion.getSala();
                ubicacionListUbicacion.setSala(sala);
                ubicacionListUbicacion = em.merge(ubicacionListUbicacion);
                if (oldSalaOfUbicacionListUbicacion != null) {
                    oldSalaOfUbicacionListUbicacion.getUbicacionList().remove(ubicacionListUbicacion);
                    oldSalaOfUbicacionListUbicacion = em.merge(oldSalaOfUbicacionListUbicacion);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findSala(sala.getSala()) != null) {
                throw new PreexistingEntityException("Sala " + sala + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Sala sala) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Sala persistentSala = em.find(Sala.class, sala.getSala());
            List<Ubicacion> ubicacionListOld = persistentSala.getUbicacionList();
            List<Ubicacion> ubicacionListNew = sala.getUbicacionList();
            List<Ubicacion> attachedUbicacionListNew = new ArrayList<Ubicacion>();
            for (Ubicacion ubicacionListNewUbicacionToAttach : ubicacionListNew) {
                ubicacionListNewUbicacionToAttach = em.getReference(ubicacionListNewUbicacionToAttach.getClass(), ubicacionListNewUbicacionToAttach.getUbicacion());
                attachedUbicacionListNew.add(ubicacionListNewUbicacionToAttach);
            }
            ubicacionListNew = attachedUbicacionListNew;
            sala.setUbicacionList(ubicacionListNew);
            sala = em.merge(sala);
            for (Ubicacion ubicacionListOldUbicacion : ubicacionListOld) {
                if (!ubicacionListNew.contains(ubicacionListOldUbicacion)) {
                    ubicacionListOldUbicacion.setSala(null);
                    ubicacionListOldUbicacion = em.merge(ubicacionListOldUbicacion);
                }
            }
            for (Ubicacion ubicacionListNewUbicacion : ubicacionListNew) {
                if (!ubicacionListOld.contains(ubicacionListNewUbicacion)) {
                    Sala oldSalaOfUbicacionListNewUbicacion = ubicacionListNewUbicacion.getSala();
                    ubicacionListNewUbicacion.setSala(sala);
                    ubicacionListNewUbicacion = em.merge(ubicacionListNewUbicacion);
                    if (oldSalaOfUbicacionListNewUbicacion != null && !oldSalaOfUbicacionListNewUbicacion.equals(sala)) {
                        oldSalaOfUbicacionListNewUbicacion.getUbicacionList().remove(ubicacionListNewUbicacion);
                        oldSalaOfUbicacionListNewUbicacion = em.merge(oldSalaOfUbicacionListNewUbicacion);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = sala.getSala();
                if (findSala(id) == null) {
                    throw new NonexistentEntityException("The sala with id " + id + " no longer exists.");
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
            Sala sala;
            try {
                sala = em.getReference(Sala.class, id);
                sala.getSala();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The sala with id " + id + " no longer exists.", enfe);
            }
            List<Ubicacion> ubicacionList = sala.getUbicacionList();
            for (Ubicacion ubicacionListUbicacion : ubicacionList) {
                ubicacionListUbicacion.setSala(null);
                ubicacionListUbicacion = em.merge(ubicacionListUbicacion);
            }
            em.remove(sala);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Sala> findSalaEntities() {
        return findSalaEntities(true, -1, -1);
    }

    public List<Sala> findSalaEntities(int maxResults, int firstResult) {
        return findSalaEntities(false, maxResults, firstResult);
    }

    private List<Sala> findSalaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Sala.class));
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

    public Sala findSala(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Sala.class, id);
        } finally {
            em.close();
        }
    }

    public int getSalaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Sala> rt = cq.from(Sala.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
