/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DaoJPA;

import DaoJPA.exceptions.NonexistentEntityException;
import DaoJPA.exceptions.PreexistingEntityException;
import Entidades.Estante;
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
public class EstanteJpaController implements Serializable {

    public EstanteJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Estante estante) throws PreexistingEntityException, Exception {
        if (estante.getUbicacionList() == null) {
            estante.setUbicacionList(new ArrayList<Ubicacion>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Ubicacion> attachedUbicacionList = new ArrayList<Ubicacion>();
            for (Ubicacion ubicacionListUbicacionToAttach : estante.getUbicacionList()) {
                ubicacionListUbicacionToAttach = em.getReference(ubicacionListUbicacionToAttach.getClass(), ubicacionListUbicacionToAttach.getUbicacion());
                attachedUbicacionList.add(ubicacionListUbicacionToAttach);
            }
            estante.setUbicacionList(attachedUbicacionList);
            em.persist(estante);
            for (Ubicacion ubicacionListUbicacion : estante.getUbicacionList()) {
                Estante oldEstanteOfUbicacionListUbicacion = ubicacionListUbicacion.getEstante();
                ubicacionListUbicacion.setEstante(estante);
                ubicacionListUbicacion = em.merge(ubicacionListUbicacion);
                if (oldEstanteOfUbicacionListUbicacion != null) {
                    oldEstanteOfUbicacionListUbicacion.getUbicacionList().remove(ubicacionListUbicacion);
                    oldEstanteOfUbicacionListUbicacion = em.merge(oldEstanteOfUbicacionListUbicacion);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findEstante(estante.getEstante()) != null) {
                throw new PreexistingEntityException("Estante " + estante + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Estante estante) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Estante persistentEstante = em.find(Estante.class, estante.getEstante());
            List<Ubicacion> ubicacionListOld = persistentEstante.getUbicacionList();
            List<Ubicacion> ubicacionListNew = estante.getUbicacionList();
            List<Ubicacion> attachedUbicacionListNew = new ArrayList<Ubicacion>();
            for (Ubicacion ubicacionListNewUbicacionToAttach : ubicacionListNew) {
                ubicacionListNewUbicacionToAttach = em.getReference(ubicacionListNewUbicacionToAttach.getClass(), ubicacionListNewUbicacionToAttach.getUbicacion());
                attachedUbicacionListNew.add(ubicacionListNewUbicacionToAttach);
            }
            ubicacionListNew = attachedUbicacionListNew;
            estante.setUbicacionList(ubicacionListNew);
            estante = em.merge(estante);
            for (Ubicacion ubicacionListOldUbicacion : ubicacionListOld) {
                if (!ubicacionListNew.contains(ubicacionListOldUbicacion)) {
                    ubicacionListOldUbicacion.setEstante(null);
                    ubicacionListOldUbicacion = em.merge(ubicacionListOldUbicacion);
                }
            }
            for (Ubicacion ubicacionListNewUbicacion : ubicacionListNew) {
                if (!ubicacionListOld.contains(ubicacionListNewUbicacion)) {
                    Estante oldEstanteOfUbicacionListNewUbicacion = ubicacionListNewUbicacion.getEstante();
                    ubicacionListNewUbicacion.setEstante(estante);
                    ubicacionListNewUbicacion = em.merge(ubicacionListNewUbicacion);
                    if (oldEstanteOfUbicacionListNewUbicacion != null && !oldEstanteOfUbicacionListNewUbicacion.equals(estante)) {
                        oldEstanteOfUbicacionListNewUbicacion.getUbicacionList().remove(ubicacionListNewUbicacion);
                        oldEstanteOfUbicacionListNewUbicacion = em.merge(oldEstanteOfUbicacionListNewUbicacion);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = estante.getEstante();
                if (findEstante(id) == null) {
                    throw new NonexistentEntityException("The estante with id " + id + " no longer exists.");
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
            Estante estante;
            try {
                estante = em.getReference(Estante.class, id);
                estante.getEstante();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The estante with id " + id + " no longer exists.", enfe);
            }
            List<Ubicacion> ubicacionList = estante.getUbicacionList();
            for (Ubicacion ubicacionListUbicacion : ubicacionList) {
                ubicacionListUbicacion.setEstante(null);
                ubicacionListUbicacion = em.merge(ubicacionListUbicacion);
            }
            em.remove(estante);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Estante> findEstanteEntities() {
        return findEstanteEntities(true, -1, -1);
    }

    public List<Estante> findEstanteEntities(int maxResults, int firstResult) {
        return findEstanteEntities(false, maxResults, firstResult);
    }

    private List<Estante> findEstanteEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Estante.class));
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

    public Estante findEstante(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Estante.class, id);
        } finally {
            em.close();
        }
    }

    public int getEstanteCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Estante> rt = cq.from(Estante.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
