/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DaoJPA;

import DaoJPA.exceptions.NonexistentEntityException;
import DaoJPA.exceptions.PreexistingEntityException;
import Entidades.OrdenDeEntrega;
import java.io.Serializable;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entidades.OrdenDePrestamo;
import Entidades.Ejemplar;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JHONATHAN
 */
public class OrdenDeEntregaJpaController implements Serializable {

    public OrdenDeEntregaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(OrdenDeEntrega ordenDeEntrega) throws PreexistingEntityException, Exception {
        if (ordenDeEntrega.getEjemplarList() == null) {
            ordenDeEntrega.setEjemplarList(new ArrayList<Ejemplar>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            OrdenDePrestamo codigoPrestamo = ordenDeEntrega.getCodigoPrestamo();
            if (codigoPrestamo != null) {
                codigoPrestamo = em.getReference(codigoPrestamo.getClass(), codigoPrestamo.getCodigo());
                ordenDeEntrega.setCodigoPrestamo(codigoPrestamo);
            }
            List<Ejemplar> attachedEjemplarList = new ArrayList<Ejemplar>();
            for (Ejemplar ejemplarListEjemplarToAttach : ordenDeEntrega.getEjemplarList()) {
                ejemplarListEjemplarToAttach = em.getReference(ejemplarListEjemplarToAttach.getClass(), ejemplarListEjemplarToAttach.getCodigo());
                attachedEjemplarList.add(ejemplarListEjemplarToAttach);
            }
            ordenDeEntrega.setEjemplarList(attachedEjemplarList);
            em.persist(ordenDeEntrega);
            if (codigoPrestamo != null) {
                codigoPrestamo.getOrdenDeEntregaList().add(ordenDeEntrega);
                codigoPrestamo = em.merge(codigoPrestamo);
            }
            for (Ejemplar ejemplarListEjemplar : ordenDeEntrega.getEjemplarList()) {
                ejemplarListEjemplar.getOrdenDeEntregaList().add(ordenDeEntrega);
                ejemplarListEjemplar = em.merge(ejemplarListEjemplar);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findOrdenDeEntrega(ordenDeEntrega.getCodigo()) != null) {
                throw new PreexistingEntityException("OrdenDeEntrega " + ordenDeEntrega + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(OrdenDeEntrega ordenDeEntrega) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            OrdenDeEntrega persistentOrdenDeEntrega = em.find(OrdenDeEntrega.class, ordenDeEntrega.getCodigo());
            OrdenDePrestamo codigoPrestamoOld = persistentOrdenDeEntrega.getCodigoPrestamo();
            OrdenDePrestamo codigoPrestamoNew = ordenDeEntrega.getCodigoPrestamo();
            List<Ejemplar> ejemplarListOld = persistentOrdenDeEntrega.getEjemplarList();
            List<Ejemplar> ejemplarListNew = ordenDeEntrega.getEjemplarList();
            if (codigoPrestamoNew != null) {
                codigoPrestamoNew = em.getReference(codigoPrestamoNew.getClass(), codigoPrestamoNew.getCodigo());
                ordenDeEntrega.setCodigoPrestamo(codigoPrestamoNew);
            }
            List<Ejemplar> attachedEjemplarListNew = new ArrayList<Ejemplar>();
            for (Ejemplar ejemplarListNewEjemplarToAttach : ejemplarListNew) {
                ejemplarListNewEjemplarToAttach = em.getReference(ejemplarListNewEjemplarToAttach.getClass(), ejemplarListNewEjemplarToAttach.getCodigo());
                attachedEjemplarListNew.add(ejemplarListNewEjemplarToAttach);
            }
            ejemplarListNew = attachedEjemplarListNew;
            ordenDeEntrega.setEjemplarList(ejemplarListNew);
            ordenDeEntrega = em.merge(ordenDeEntrega);
            if (codigoPrestamoOld != null && !codigoPrestamoOld.equals(codigoPrestamoNew)) {
                codigoPrestamoOld.getOrdenDeEntregaList().remove(ordenDeEntrega);
                codigoPrestamoOld = em.merge(codigoPrestamoOld);
            }
            if (codigoPrestamoNew != null && !codigoPrestamoNew.equals(codigoPrestamoOld)) {
                codigoPrestamoNew.getOrdenDeEntregaList().add(ordenDeEntrega);
                codigoPrestamoNew = em.merge(codigoPrestamoNew);
            }
            for (Ejemplar ejemplarListOldEjemplar : ejemplarListOld) {
                if (!ejemplarListNew.contains(ejemplarListOldEjemplar)) {
                    ejemplarListOldEjemplar.getOrdenDeEntregaList().remove(ordenDeEntrega);
                    ejemplarListOldEjemplar = em.merge(ejemplarListOldEjemplar);
                }
            }
            for (Ejemplar ejemplarListNewEjemplar : ejemplarListNew) {
                if (!ejemplarListOld.contains(ejemplarListNewEjemplar)) {
                    ejemplarListNewEjemplar.getOrdenDeEntregaList().add(ordenDeEntrega);
                    ejemplarListNewEjemplar = em.merge(ejemplarListNewEjemplar);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = ordenDeEntrega.getCodigo();
                if (findOrdenDeEntrega(id) == null) {
                    throw new NonexistentEntityException("The ordenDeEntrega with id " + id + " no longer exists.");
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
            OrdenDeEntrega ordenDeEntrega;
            try {
                ordenDeEntrega = em.getReference(OrdenDeEntrega.class, id);
                ordenDeEntrega.getCodigo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The ordenDeEntrega with id " + id + " no longer exists.", enfe);
            }
            OrdenDePrestamo codigoPrestamo = ordenDeEntrega.getCodigoPrestamo();
            if (codigoPrestamo != null) {
                codigoPrestamo.getOrdenDeEntregaList().remove(ordenDeEntrega);
                codigoPrestamo = em.merge(codigoPrestamo);
            }
            List<Ejemplar> ejemplarList = ordenDeEntrega.getEjemplarList();
            for (Ejemplar ejemplarListEjemplar : ejemplarList) {
                ejemplarListEjemplar.getOrdenDeEntregaList().remove(ordenDeEntrega);
                ejemplarListEjemplar = em.merge(ejemplarListEjemplar);
            }
            em.remove(ordenDeEntrega);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<OrdenDeEntrega> findOrdenDeEntregaEntities() {
        return findOrdenDeEntregaEntities(true, -1, -1);
    }

    public List<OrdenDeEntrega> findOrdenDeEntregaEntities(int maxResults, int firstResult) {
        return findOrdenDeEntregaEntities(false, maxResults, firstResult);
    }

    private List<OrdenDeEntrega> findOrdenDeEntregaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(OrdenDeEntrega.class));
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

    public OrdenDeEntrega findOrdenDeEntrega(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(OrdenDeEntrega.class, id);
        } finally {
            em.close();
        }
    }

    public int getOrdenDeEntregaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<OrdenDeEntrega> rt = cq.from(OrdenDeEntrega.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
