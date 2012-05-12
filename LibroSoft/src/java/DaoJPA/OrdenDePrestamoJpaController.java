/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DaoJPA;

import DaoJPA.exceptions.IllegalOrphanException;
import DaoJPA.exceptions.NonexistentEntityException;
import DaoJPA.exceptions.PreexistingEntityException;
import Entidades.OrdenDePrestamo;
import java.io.Serializable;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entidades.Usuario;
import Entidades.Ejemplar;
import java.util.ArrayList;
import java.util.List;
import Entidades.OrdenDeEntrega;

/**
 *
 * @author JHONATHAN
 */
public class OrdenDePrestamoJpaController implements Serializable {

    public OrdenDePrestamoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(OrdenDePrestamo ordenDePrestamo) throws PreexistingEntityException, Exception {
        if (ordenDePrestamo.getEjemplarList() == null) {
            ordenDePrestamo.setEjemplarList(new ArrayList<Ejemplar>());
        }
        if (ordenDePrestamo.getOrdenDeEntregaList() == null) {
            ordenDePrestamo.setOrdenDeEntregaList(new ArrayList<OrdenDeEntrega>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario idUsuario = ordenDePrestamo.getIdUsuario();
            if (idUsuario != null) {
                idUsuario = em.getReference(idUsuario.getClass(), idUsuario.getIdUsuario());
                ordenDePrestamo.setIdUsuario(idUsuario);
            }
            List<Ejemplar> attachedEjemplarList = new ArrayList<Ejemplar>();
            for (Ejemplar ejemplarListEjemplarToAttach : ordenDePrestamo.getEjemplarList()) {
                ejemplarListEjemplarToAttach = em.getReference(ejemplarListEjemplarToAttach.getClass(), ejemplarListEjemplarToAttach.getCodigo());
                attachedEjemplarList.add(ejemplarListEjemplarToAttach);
            }
            ordenDePrestamo.setEjemplarList(attachedEjemplarList);
            List<OrdenDeEntrega> attachedOrdenDeEntregaList = new ArrayList<OrdenDeEntrega>();
            for (OrdenDeEntrega ordenDeEntregaListOrdenDeEntregaToAttach : ordenDePrestamo.getOrdenDeEntregaList()) {
                ordenDeEntregaListOrdenDeEntregaToAttach = em.getReference(ordenDeEntregaListOrdenDeEntregaToAttach.getClass(), ordenDeEntregaListOrdenDeEntregaToAttach.getCodigo());
                attachedOrdenDeEntregaList.add(ordenDeEntregaListOrdenDeEntregaToAttach);
            }
            ordenDePrestamo.setOrdenDeEntregaList(attachedOrdenDeEntregaList);
            em.persist(ordenDePrestamo);
            if (idUsuario != null) {
                idUsuario.getOrdenDePrestamoList().add(ordenDePrestamo);
                idUsuario = em.merge(idUsuario);
            }
            for (Ejemplar ejemplarListEjemplar : ordenDePrestamo.getEjemplarList()) {
                ejemplarListEjemplar.getOrdenDePrestamoList().add(ordenDePrestamo);
                ejemplarListEjemplar = em.merge(ejemplarListEjemplar);
            }
            for (OrdenDeEntrega ordenDeEntregaListOrdenDeEntrega : ordenDePrestamo.getOrdenDeEntregaList()) {
                OrdenDePrestamo oldCodigoPrestamoOfOrdenDeEntregaListOrdenDeEntrega = ordenDeEntregaListOrdenDeEntrega.getCodigoPrestamo();
                ordenDeEntregaListOrdenDeEntrega.setCodigoPrestamo(ordenDePrestamo);
                ordenDeEntregaListOrdenDeEntrega = em.merge(ordenDeEntregaListOrdenDeEntrega);
                if (oldCodigoPrestamoOfOrdenDeEntregaListOrdenDeEntrega != null) {
                    oldCodigoPrestamoOfOrdenDeEntregaListOrdenDeEntrega.getOrdenDeEntregaList().remove(ordenDeEntregaListOrdenDeEntrega);
                    oldCodigoPrestamoOfOrdenDeEntregaListOrdenDeEntrega = em.merge(oldCodigoPrestamoOfOrdenDeEntregaListOrdenDeEntrega);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findOrdenDePrestamo(ordenDePrestamo.getCodigo()) != null) {
                throw new PreexistingEntityException("OrdenDePrestamo " + ordenDePrestamo + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(OrdenDePrestamo ordenDePrestamo) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            OrdenDePrestamo persistentOrdenDePrestamo = em.find(OrdenDePrestamo.class, ordenDePrestamo.getCodigo());
            Usuario idUsuarioOld = persistentOrdenDePrestamo.getIdUsuario();
            Usuario idUsuarioNew = ordenDePrestamo.getIdUsuario();
            List<Ejemplar> ejemplarListOld = persistentOrdenDePrestamo.getEjemplarList();
            List<Ejemplar> ejemplarListNew = ordenDePrestamo.getEjemplarList();
            List<OrdenDeEntrega> ordenDeEntregaListOld = persistentOrdenDePrestamo.getOrdenDeEntregaList();
            List<OrdenDeEntrega> ordenDeEntregaListNew = ordenDePrestamo.getOrdenDeEntregaList();
            List<String> illegalOrphanMessages = null;
            for (OrdenDeEntrega ordenDeEntregaListOldOrdenDeEntrega : ordenDeEntregaListOld) {
                if (!ordenDeEntregaListNew.contains(ordenDeEntregaListOldOrdenDeEntrega)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain OrdenDeEntrega " + ordenDeEntregaListOldOrdenDeEntrega + " since its codigoPrestamo field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idUsuarioNew != null) {
                idUsuarioNew = em.getReference(idUsuarioNew.getClass(), idUsuarioNew.getIdUsuario());
                ordenDePrestamo.setIdUsuario(idUsuarioNew);
            }
            List<Ejemplar> attachedEjemplarListNew = new ArrayList<Ejemplar>();
            for (Ejemplar ejemplarListNewEjemplarToAttach : ejemplarListNew) {
                ejemplarListNewEjemplarToAttach = em.getReference(ejemplarListNewEjemplarToAttach.getClass(), ejemplarListNewEjemplarToAttach.getCodigo());
                attachedEjemplarListNew.add(ejemplarListNewEjemplarToAttach);
            }
            ejemplarListNew = attachedEjemplarListNew;
            ordenDePrestamo.setEjemplarList(ejemplarListNew);
            List<OrdenDeEntrega> attachedOrdenDeEntregaListNew = new ArrayList<OrdenDeEntrega>();
            for (OrdenDeEntrega ordenDeEntregaListNewOrdenDeEntregaToAttach : ordenDeEntregaListNew) {
                ordenDeEntregaListNewOrdenDeEntregaToAttach = em.getReference(ordenDeEntregaListNewOrdenDeEntregaToAttach.getClass(), ordenDeEntregaListNewOrdenDeEntregaToAttach.getCodigo());
                attachedOrdenDeEntregaListNew.add(ordenDeEntregaListNewOrdenDeEntregaToAttach);
            }
            ordenDeEntregaListNew = attachedOrdenDeEntregaListNew;
            ordenDePrestamo.setOrdenDeEntregaList(ordenDeEntregaListNew);
            ordenDePrestamo = em.merge(ordenDePrestamo);
            if (idUsuarioOld != null && !idUsuarioOld.equals(idUsuarioNew)) {
                idUsuarioOld.getOrdenDePrestamoList().remove(ordenDePrestamo);
                idUsuarioOld = em.merge(idUsuarioOld);
            }
            if (idUsuarioNew != null && !idUsuarioNew.equals(idUsuarioOld)) {
                idUsuarioNew.getOrdenDePrestamoList().add(ordenDePrestamo);
                idUsuarioNew = em.merge(idUsuarioNew);
            }
            for (Ejemplar ejemplarListOldEjemplar : ejemplarListOld) {
                if (!ejemplarListNew.contains(ejemplarListOldEjemplar)) {
                    ejemplarListOldEjemplar.getOrdenDePrestamoList().remove(ordenDePrestamo);
                    ejemplarListOldEjemplar = em.merge(ejemplarListOldEjemplar);
                }
            }
            for (Ejemplar ejemplarListNewEjemplar : ejemplarListNew) {
                if (!ejemplarListOld.contains(ejemplarListNewEjemplar)) {
                    ejemplarListNewEjemplar.getOrdenDePrestamoList().add(ordenDePrestamo);
                    ejemplarListNewEjemplar = em.merge(ejemplarListNewEjemplar);
                }
            }
            for (OrdenDeEntrega ordenDeEntregaListNewOrdenDeEntrega : ordenDeEntregaListNew) {
                if (!ordenDeEntregaListOld.contains(ordenDeEntregaListNewOrdenDeEntrega)) {
                    OrdenDePrestamo oldCodigoPrestamoOfOrdenDeEntregaListNewOrdenDeEntrega = ordenDeEntregaListNewOrdenDeEntrega.getCodigoPrestamo();
                    ordenDeEntregaListNewOrdenDeEntrega.setCodigoPrestamo(ordenDePrestamo);
                    ordenDeEntregaListNewOrdenDeEntrega = em.merge(ordenDeEntregaListNewOrdenDeEntrega);
                    if (oldCodigoPrestamoOfOrdenDeEntregaListNewOrdenDeEntrega != null && !oldCodigoPrestamoOfOrdenDeEntregaListNewOrdenDeEntrega.equals(ordenDePrestamo)) {
                        oldCodigoPrestamoOfOrdenDeEntregaListNewOrdenDeEntrega.getOrdenDeEntregaList().remove(ordenDeEntregaListNewOrdenDeEntrega);
                        oldCodigoPrestamoOfOrdenDeEntregaListNewOrdenDeEntrega = em.merge(oldCodigoPrestamoOfOrdenDeEntregaListNewOrdenDeEntrega);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = ordenDePrestamo.getCodigo();
                if (findOrdenDePrestamo(id) == null) {
                    throw new NonexistentEntityException("The ordenDePrestamo with id " + id + " no longer exists.");
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
            OrdenDePrestamo ordenDePrestamo;
            try {
                ordenDePrestamo = em.getReference(OrdenDePrestamo.class, id);
                ordenDePrestamo.getCodigo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The ordenDePrestamo with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<OrdenDeEntrega> ordenDeEntregaListOrphanCheck = ordenDePrestamo.getOrdenDeEntregaList();
            for (OrdenDeEntrega ordenDeEntregaListOrphanCheckOrdenDeEntrega : ordenDeEntregaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This OrdenDePrestamo (" + ordenDePrestamo + ") cannot be destroyed since the OrdenDeEntrega " + ordenDeEntregaListOrphanCheckOrdenDeEntrega + " in its ordenDeEntregaList field has a non-nullable codigoPrestamo field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Usuario idUsuario = ordenDePrestamo.getIdUsuario();
            if (idUsuario != null) {
                idUsuario.getOrdenDePrestamoList().remove(ordenDePrestamo);
                idUsuario = em.merge(idUsuario);
            }
            List<Ejemplar> ejemplarList = ordenDePrestamo.getEjemplarList();
            for (Ejemplar ejemplarListEjemplar : ejemplarList) {
                ejemplarListEjemplar.getOrdenDePrestamoList().remove(ordenDePrestamo);
                ejemplarListEjemplar = em.merge(ejemplarListEjemplar);
            }
            em.remove(ordenDePrestamo);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<OrdenDePrestamo> findOrdenDePrestamoEntities() {
        return findOrdenDePrestamoEntities(true, -1, -1);
    }

    public List<OrdenDePrestamo> findOrdenDePrestamoEntities(int maxResults, int firstResult) {
        return findOrdenDePrestamoEntities(false, maxResults, firstResult);
    }

    private List<OrdenDePrestamo> findOrdenDePrestamoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(OrdenDePrestamo.class));
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

    public OrdenDePrestamo findOrdenDePrestamo(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(OrdenDePrestamo.class, id);
        } finally {
            em.close();
        }
    }

    public int getOrdenDePrestamoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<OrdenDePrestamo> rt = cq.from(OrdenDePrestamo.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
