/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DaoJPA;

import DaoJPA.exceptions.NonexistentEntityException;
import DaoJPA.exceptions.PreexistingEntityException;
import Entidades.Pasillo;
import java.io.Serializable;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entidades.AreaDeConocimiento;
import Entidades.Ubicacion;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JHONATHAN
 */
public class PasilloJpaController implements Serializable {

    public PasilloJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Pasillo pasillo) throws PreexistingEntityException, Exception {
        if (pasillo.getUbicacionList() == null) {
            pasillo.setUbicacionList(new ArrayList<Ubicacion>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            AreaDeConocimiento areaConocimiento = pasillo.getAreaConocimiento();
            if (areaConocimiento != null) {
                areaConocimiento = em.getReference(areaConocimiento.getClass(), areaConocimiento.getCodigo());
                pasillo.setAreaConocimiento(areaConocimiento);
            }
            List<Ubicacion> attachedUbicacionList = new ArrayList<Ubicacion>();
            for (Ubicacion ubicacionListUbicacionToAttach : pasillo.getUbicacionList()) {
                ubicacionListUbicacionToAttach = em.getReference(ubicacionListUbicacionToAttach.getClass(), ubicacionListUbicacionToAttach.getUbicacion());
                attachedUbicacionList.add(ubicacionListUbicacionToAttach);
            }
            pasillo.setUbicacionList(attachedUbicacionList);
            em.persist(pasillo);
            if (areaConocimiento != null) {
                areaConocimiento.getPasilloList().add(pasillo);
                areaConocimiento = em.merge(areaConocimiento);
            }
            for (Ubicacion ubicacionListUbicacion : pasillo.getUbicacionList()) {
                Pasillo oldPasilloOfUbicacionListUbicacion = ubicacionListUbicacion.getPasillo();
                ubicacionListUbicacion.setPasillo(pasillo);
                ubicacionListUbicacion = em.merge(ubicacionListUbicacion);
                if (oldPasilloOfUbicacionListUbicacion != null) {
                    oldPasilloOfUbicacionListUbicacion.getUbicacionList().remove(ubicacionListUbicacion);
                    oldPasilloOfUbicacionListUbicacion = em.merge(oldPasilloOfUbicacionListUbicacion);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findPasillo(pasillo.getPasillo()) != null) {
                throw new PreexistingEntityException("Pasillo " + pasillo + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Pasillo pasillo) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Pasillo persistentPasillo = em.find(Pasillo.class, pasillo.getPasillo());
            AreaDeConocimiento areaConocimientoOld = persistentPasillo.getAreaConocimiento();
            AreaDeConocimiento areaConocimientoNew = pasillo.getAreaConocimiento();
            List<Ubicacion> ubicacionListOld = persistentPasillo.getUbicacionList();
            List<Ubicacion> ubicacionListNew = pasillo.getUbicacionList();
            if (areaConocimientoNew != null) {
                areaConocimientoNew = em.getReference(areaConocimientoNew.getClass(), areaConocimientoNew.getCodigo());
                pasillo.setAreaConocimiento(areaConocimientoNew);
            }
            List<Ubicacion> attachedUbicacionListNew = new ArrayList<Ubicacion>();
            for (Ubicacion ubicacionListNewUbicacionToAttach : ubicacionListNew) {
                ubicacionListNewUbicacionToAttach = em.getReference(ubicacionListNewUbicacionToAttach.getClass(), ubicacionListNewUbicacionToAttach.getUbicacion());
                attachedUbicacionListNew.add(ubicacionListNewUbicacionToAttach);
            }
            ubicacionListNew = attachedUbicacionListNew;
            pasillo.setUbicacionList(ubicacionListNew);
            pasillo = em.merge(pasillo);
            if (areaConocimientoOld != null && !areaConocimientoOld.equals(areaConocimientoNew)) {
                areaConocimientoOld.getPasilloList().remove(pasillo);
                areaConocimientoOld = em.merge(areaConocimientoOld);
            }
            if (areaConocimientoNew != null && !areaConocimientoNew.equals(areaConocimientoOld)) {
                areaConocimientoNew.getPasilloList().add(pasillo);
                areaConocimientoNew = em.merge(areaConocimientoNew);
            }
            for (Ubicacion ubicacionListOldUbicacion : ubicacionListOld) {
                if (!ubicacionListNew.contains(ubicacionListOldUbicacion)) {
                    ubicacionListOldUbicacion.setPasillo(null);
                    ubicacionListOldUbicacion = em.merge(ubicacionListOldUbicacion);
                }
            }
            for (Ubicacion ubicacionListNewUbicacion : ubicacionListNew) {
                if (!ubicacionListOld.contains(ubicacionListNewUbicacion)) {
                    Pasillo oldPasilloOfUbicacionListNewUbicacion = ubicacionListNewUbicacion.getPasillo();
                    ubicacionListNewUbicacion.setPasillo(pasillo);
                    ubicacionListNewUbicacion = em.merge(ubicacionListNewUbicacion);
                    if (oldPasilloOfUbicacionListNewUbicacion != null && !oldPasilloOfUbicacionListNewUbicacion.equals(pasillo)) {
                        oldPasilloOfUbicacionListNewUbicacion.getUbicacionList().remove(ubicacionListNewUbicacion);
                        oldPasilloOfUbicacionListNewUbicacion = em.merge(oldPasilloOfUbicacionListNewUbicacion);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = pasillo.getPasillo();
                if (findPasillo(id) == null) {
                    throw new NonexistentEntityException("The pasillo with id " + id + " no longer exists.");
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
            Pasillo pasillo;
            try {
                pasillo = em.getReference(Pasillo.class, id);
                pasillo.getPasillo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The pasillo with id " + id + " no longer exists.", enfe);
            }
            AreaDeConocimiento areaConocimiento = pasillo.getAreaConocimiento();
            if (areaConocimiento != null) {
                areaConocimiento.getPasilloList().remove(pasillo);
                areaConocimiento = em.merge(areaConocimiento);
            }
            List<Ubicacion> ubicacionList = pasillo.getUbicacionList();
            for (Ubicacion ubicacionListUbicacion : ubicacionList) {
                ubicacionListUbicacion.setPasillo(null);
                ubicacionListUbicacion = em.merge(ubicacionListUbicacion);
            }
            em.remove(pasillo);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Pasillo> findPasilloEntities() {
        return findPasilloEntities(true, -1, -1);
    }

    public List<Pasillo> findPasilloEntities(int maxResults, int firstResult) {
        return findPasilloEntities(false, maxResults, firstResult);
    }

    private List<Pasillo> findPasilloEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Pasillo.class));
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

    public Pasillo findPasillo(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Pasillo.class, id);
        } finally {
            em.close();
        }
    }

    public int getPasilloCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Pasillo> rt = cq.from(Pasillo.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
