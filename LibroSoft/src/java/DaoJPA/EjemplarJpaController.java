/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DaoJPA;

import DaoJPA.exceptions.IllegalOrphanException;
import DaoJPA.exceptions.NonexistentEntityException;
import DaoJPA.exceptions.PreexistingEntityException;
import Entidades.Ejemplar;
import java.io.Serializable;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entidades.Ubicacion;
import Entidades.OrdenDeEntrega;
import java.util.ArrayList;
import java.util.List;
import Entidades.OrdenDePrestamo;
import Entidades.Devolucion;
import Entidades.Multa;

/**
 *
 * @author JHONATHAN
 */
public class EjemplarJpaController implements Serializable {

    public EjemplarJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Ejemplar ejemplar) throws PreexistingEntityException, Exception {
        if (ejemplar.getOrdenDeEntregaList() == null) {
            ejemplar.setOrdenDeEntregaList(new ArrayList<OrdenDeEntrega>());
        }
        if (ejemplar.getOrdenDePrestamoList() == null) {
            ejemplar.setOrdenDePrestamoList(new ArrayList<OrdenDePrestamo>());
        }
        if (ejemplar.getDevolucionList() == null) {
            ejemplar.setDevolucionList(new ArrayList<Devolucion>());
        }
        if (ejemplar.getMultaList() == null) {
            ejemplar.setMultaList(new ArrayList<Multa>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Ubicacion ubicacion = ejemplar.getUbicacion();
            if (ubicacion != null) {
                ubicacion = em.getReference(ubicacion.getClass(), ubicacion.getUbicacion());
                ejemplar.setUbicacion(ubicacion);
            }
            List<OrdenDeEntrega> attachedOrdenDeEntregaList = new ArrayList<OrdenDeEntrega>();
            for (OrdenDeEntrega ordenDeEntregaListOrdenDeEntregaToAttach : ejemplar.getOrdenDeEntregaList()) {
                ordenDeEntregaListOrdenDeEntregaToAttach = em.getReference(ordenDeEntregaListOrdenDeEntregaToAttach.getClass(), ordenDeEntregaListOrdenDeEntregaToAttach.getCodigo());
                attachedOrdenDeEntregaList.add(ordenDeEntregaListOrdenDeEntregaToAttach);
            }
            ejemplar.setOrdenDeEntregaList(attachedOrdenDeEntregaList);
            List<OrdenDePrestamo> attachedOrdenDePrestamoList = new ArrayList<OrdenDePrestamo>();
            for (OrdenDePrestamo ordenDePrestamoListOrdenDePrestamoToAttach : ejemplar.getOrdenDePrestamoList()) {
                ordenDePrestamoListOrdenDePrestamoToAttach = em.getReference(ordenDePrestamoListOrdenDePrestamoToAttach.getClass(), ordenDePrestamoListOrdenDePrestamoToAttach.getCodigo());
                attachedOrdenDePrestamoList.add(ordenDePrestamoListOrdenDePrestamoToAttach);
            }
            ejemplar.setOrdenDePrestamoList(attachedOrdenDePrestamoList);
            List<Devolucion> attachedDevolucionList = new ArrayList<Devolucion>();
            for (Devolucion devolucionListDevolucionToAttach : ejemplar.getDevolucionList()) {
                devolucionListDevolucionToAttach = em.getReference(devolucionListDevolucionToAttach.getClass(), devolucionListDevolucionToAttach.getCodigo());
                attachedDevolucionList.add(devolucionListDevolucionToAttach);
            }
            ejemplar.setDevolucionList(attachedDevolucionList);
            List<Multa> attachedMultaList = new ArrayList<Multa>();
            for (Multa multaListMultaToAttach : ejemplar.getMultaList()) {
                multaListMultaToAttach = em.getReference(multaListMultaToAttach.getClass(), multaListMultaToAttach.getCodigo());
                attachedMultaList.add(multaListMultaToAttach);
            }
            ejemplar.setMultaList(attachedMultaList);
            em.persist(ejemplar);
            if (ubicacion != null) {
                ubicacion.getEjemplarList().add(ejemplar);
                ubicacion = em.merge(ubicacion);
            }
            for (OrdenDeEntrega ordenDeEntregaListOrdenDeEntrega : ejemplar.getOrdenDeEntregaList()) {
                ordenDeEntregaListOrdenDeEntrega.getEjemplarList().add(ejemplar);
                ordenDeEntregaListOrdenDeEntrega = em.merge(ordenDeEntregaListOrdenDeEntrega);
            }
            for (OrdenDePrestamo ordenDePrestamoListOrdenDePrestamo : ejemplar.getOrdenDePrestamoList()) {
                ordenDePrestamoListOrdenDePrestamo.getEjemplarList().add(ejemplar);
                ordenDePrestamoListOrdenDePrestamo = em.merge(ordenDePrestamoListOrdenDePrestamo);
            }
            for (Devolucion devolucionListDevolucion : ejemplar.getDevolucionList()) {
                Ejemplar oldCodigoEjemplarOfDevolucionListDevolucion = devolucionListDevolucion.getCodigoEjemplar();
                devolucionListDevolucion.setCodigoEjemplar(ejemplar);
                devolucionListDevolucion = em.merge(devolucionListDevolucion);
                if (oldCodigoEjemplarOfDevolucionListDevolucion != null) {
                    oldCodigoEjemplarOfDevolucionListDevolucion.getDevolucionList().remove(devolucionListDevolucion);
                    oldCodigoEjemplarOfDevolucionListDevolucion = em.merge(oldCodigoEjemplarOfDevolucionListDevolucion);
                }
            }
            for (Multa multaListMulta : ejemplar.getMultaList()) {
                Ejemplar oldCodigoEjemplarOfMultaListMulta = multaListMulta.getCodigoEjemplar();
                multaListMulta.setCodigoEjemplar(ejemplar);
                multaListMulta = em.merge(multaListMulta);
                if (oldCodigoEjemplarOfMultaListMulta != null) {
                    oldCodigoEjemplarOfMultaListMulta.getMultaList().remove(multaListMulta);
                    oldCodigoEjemplarOfMultaListMulta = em.merge(oldCodigoEjemplarOfMultaListMulta);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findEjemplar(ejemplar.getCodigo()) != null) {
                throw new PreexistingEntityException("Ejemplar " + ejemplar + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Ejemplar ejemplar) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Ejemplar persistentEjemplar = em.find(Ejemplar.class, ejemplar.getCodigo());
            Ubicacion ubicacionOld = persistentEjemplar.getUbicacion();
            Ubicacion ubicacionNew = ejemplar.getUbicacion();
            List<OrdenDeEntrega> ordenDeEntregaListOld = persistentEjemplar.getOrdenDeEntregaList();
            List<OrdenDeEntrega> ordenDeEntregaListNew = ejemplar.getOrdenDeEntregaList();
            List<OrdenDePrestamo> ordenDePrestamoListOld = persistentEjemplar.getOrdenDePrestamoList();
            List<OrdenDePrestamo> ordenDePrestamoListNew = ejemplar.getOrdenDePrestamoList();
            List<Devolucion> devolucionListOld = persistentEjemplar.getDevolucionList();
            List<Devolucion> devolucionListNew = ejemplar.getDevolucionList();
            List<Multa> multaListOld = persistentEjemplar.getMultaList();
            List<Multa> multaListNew = ejemplar.getMultaList();
            List<String> illegalOrphanMessages = null;
            for (Devolucion devolucionListOldDevolucion : devolucionListOld) {
                if (!devolucionListNew.contains(devolucionListOldDevolucion)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Devolucion " + devolucionListOldDevolucion + " since its codigoEjemplar field is not nullable.");
                }
            }
            for (Multa multaListOldMulta : multaListOld) {
                if (!multaListNew.contains(multaListOldMulta)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Multa " + multaListOldMulta + " since its codigoEjemplar field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (ubicacionNew != null) {
                ubicacionNew = em.getReference(ubicacionNew.getClass(), ubicacionNew.getUbicacion());
                ejemplar.setUbicacion(ubicacionNew);
            }
            List<OrdenDeEntrega> attachedOrdenDeEntregaListNew = new ArrayList<OrdenDeEntrega>();
            for (OrdenDeEntrega ordenDeEntregaListNewOrdenDeEntregaToAttach : ordenDeEntregaListNew) {
                ordenDeEntregaListNewOrdenDeEntregaToAttach = em.getReference(ordenDeEntregaListNewOrdenDeEntregaToAttach.getClass(), ordenDeEntregaListNewOrdenDeEntregaToAttach.getCodigo());
                attachedOrdenDeEntregaListNew.add(ordenDeEntregaListNewOrdenDeEntregaToAttach);
            }
            ordenDeEntregaListNew = attachedOrdenDeEntregaListNew;
            ejemplar.setOrdenDeEntregaList(ordenDeEntregaListNew);
            List<OrdenDePrestamo> attachedOrdenDePrestamoListNew = new ArrayList<OrdenDePrestamo>();
            for (OrdenDePrestamo ordenDePrestamoListNewOrdenDePrestamoToAttach : ordenDePrestamoListNew) {
                ordenDePrestamoListNewOrdenDePrestamoToAttach = em.getReference(ordenDePrestamoListNewOrdenDePrestamoToAttach.getClass(), ordenDePrestamoListNewOrdenDePrestamoToAttach.getCodigo());
                attachedOrdenDePrestamoListNew.add(ordenDePrestamoListNewOrdenDePrestamoToAttach);
            }
            ordenDePrestamoListNew = attachedOrdenDePrestamoListNew;
            ejemplar.setOrdenDePrestamoList(ordenDePrestamoListNew);
            List<Devolucion> attachedDevolucionListNew = new ArrayList<Devolucion>();
            for (Devolucion devolucionListNewDevolucionToAttach : devolucionListNew) {
                devolucionListNewDevolucionToAttach = em.getReference(devolucionListNewDevolucionToAttach.getClass(), devolucionListNewDevolucionToAttach.getCodigo());
                attachedDevolucionListNew.add(devolucionListNewDevolucionToAttach);
            }
            devolucionListNew = attachedDevolucionListNew;
            ejemplar.setDevolucionList(devolucionListNew);
            List<Multa> attachedMultaListNew = new ArrayList<Multa>();
            for (Multa multaListNewMultaToAttach : multaListNew) {
                multaListNewMultaToAttach = em.getReference(multaListNewMultaToAttach.getClass(), multaListNewMultaToAttach.getCodigo());
                attachedMultaListNew.add(multaListNewMultaToAttach);
            }
            multaListNew = attachedMultaListNew;
            ejemplar.setMultaList(multaListNew);
            ejemplar = em.merge(ejemplar);
            if (ubicacionOld != null && !ubicacionOld.equals(ubicacionNew)) {
                ubicacionOld.getEjemplarList().remove(ejemplar);
                ubicacionOld = em.merge(ubicacionOld);
            }
            if (ubicacionNew != null && !ubicacionNew.equals(ubicacionOld)) {
                ubicacionNew.getEjemplarList().add(ejemplar);
                ubicacionNew = em.merge(ubicacionNew);
            }
            for (OrdenDeEntrega ordenDeEntregaListOldOrdenDeEntrega : ordenDeEntregaListOld) {
                if (!ordenDeEntregaListNew.contains(ordenDeEntregaListOldOrdenDeEntrega)) {
                    ordenDeEntregaListOldOrdenDeEntrega.getEjemplarList().remove(ejemplar);
                    ordenDeEntregaListOldOrdenDeEntrega = em.merge(ordenDeEntregaListOldOrdenDeEntrega);
                }
            }
            for (OrdenDeEntrega ordenDeEntregaListNewOrdenDeEntrega : ordenDeEntregaListNew) {
                if (!ordenDeEntregaListOld.contains(ordenDeEntregaListNewOrdenDeEntrega)) {
                    ordenDeEntregaListNewOrdenDeEntrega.getEjemplarList().add(ejemplar);
                    ordenDeEntregaListNewOrdenDeEntrega = em.merge(ordenDeEntregaListNewOrdenDeEntrega);
                }
            }
            for (OrdenDePrestamo ordenDePrestamoListOldOrdenDePrestamo : ordenDePrestamoListOld) {
                if (!ordenDePrestamoListNew.contains(ordenDePrestamoListOldOrdenDePrestamo)) {
                    ordenDePrestamoListOldOrdenDePrestamo.getEjemplarList().remove(ejemplar);
                    ordenDePrestamoListOldOrdenDePrestamo = em.merge(ordenDePrestamoListOldOrdenDePrestamo);
                }
            }
            for (OrdenDePrestamo ordenDePrestamoListNewOrdenDePrestamo : ordenDePrestamoListNew) {
                if (!ordenDePrestamoListOld.contains(ordenDePrestamoListNewOrdenDePrestamo)) {
                    ordenDePrestamoListNewOrdenDePrestamo.getEjemplarList().add(ejemplar);
                    ordenDePrestamoListNewOrdenDePrestamo = em.merge(ordenDePrestamoListNewOrdenDePrestamo);
                }
            }
            for (Devolucion devolucionListNewDevolucion : devolucionListNew) {
                if (!devolucionListOld.contains(devolucionListNewDevolucion)) {
                    Ejemplar oldCodigoEjemplarOfDevolucionListNewDevolucion = devolucionListNewDevolucion.getCodigoEjemplar();
                    devolucionListNewDevolucion.setCodigoEjemplar(ejemplar);
                    devolucionListNewDevolucion = em.merge(devolucionListNewDevolucion);
                    if (oldCodigoEjemplarOfDevolucionListNewDevolucion != null && !oldCodigoEjemplarOfDevolucionListNewDevolucion.equals(ejemplar)) {
                        oldCodigoEjemplarOfDevolucionListNewDevolucion.getDevolucionList().remove(devolucionListNewDevolucion);
                        oldCodigoEjemplarOfDevolucionListNewDevolucion = em.merge(oldCodigoEjemplarOfDevolucionListNewDevolucion);
                    }
                }
            }
            for (Multa multaListNewMulta : multaListNew) {
                if (!multaListOld.contains(multaListNewMulta)) {
                    Ejemplar oldCodigoEjemplarOfMultaListNewMulta = multaListNewMulta.getCodigoEjemplar();
                    multaListNewMulta.setCodigoEjemplar(ejemplar);
                    multaListNewMulta = em.merge(multaListNewMulta);
                    if (oldCodigoEjemplarOfMultaListNewMulta != null && !oldCodigoEjemplarOfMultaListNewMulta.equals(ejemplar)) {
                        oldCodigoEjemplarOfMultaListNewMulta.getMultaList().remove(multaListNewMulta);
                        oldCodigoEjemplarOfMultaListNewMulta = em.merge(oldCodigoEjemplarOfMultaListNewMulta);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = ejemplar.getCodigo();
                if (findEjemplar(id) == null) {
                    throw new NonexistentEntityException("The ejemplar with id " + id + " no longer exists.");
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
            Ejemplar ejemplar;
            try {
                ejemplar = em.getReference(Ejemplar.class, id);
                ejemplar.getCodigo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The ejemplar with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Devolucion> devolucionListOrphanCheck = ejemplar.getDevolucionList();
            for (Devolucion devolucionListOrphanCheckDevolucion : devolucionListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Ejemplar (" + ejemplar + ") cannot be destroyed since the Devolucion " + devolucionListOrphanCheckDevolucion + " in its devolucionList field has a non-nullable codigoEjemplar field.");
            }
            List<Multa> multaListOrphanCheck = ejemplar.getMultaList();
            for (Multa multaListOrphanCheckMulta : multaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Ejemplar (" + ejemplar + ") cannot be destroyed since the Multa " + multaListOrphanCheckMulta + " in its multaList field has a non-nullable codigoEjemplar field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Ubicacion ubicacion = ejemplar.getUbicacion();
            if (ubicacion != null) {
                ubicacion.getEjemplarList().remove(ejemplar);
                ubicacion = em.merge(ubicacion);
            }
            List<OrdenDeEntrega> ordenDeEntregaList = ejemplar.getOrdenDeEntregaList();
            for (OrdenDeEntrega ordenDeEntregaListOrdenDeEntrega : ordenDeEntregaList) {
                ordenDeEntregaListOrdenDeEntrega.getEjemplarList().remove(ejemplar);
                ordenDeEntregaListOrdenDeEntrega = em.merge(ordenDeEntregaListOrdenDeEntrega);
            }
            List<OrdenDePrestamo> ordenDePrestamoList = ejemplar.getOrdenDePrestamoList();
            for (OrdenDePrestamo ordenDePrestamoListOrdenDePrestamo : ordenDePrestamoList) {
                ordenDePrestamoListOrdenDePrestamo.getEjemplarList().remove(ejemplar);
                ordenDePrestamoListOrdenDePrestamo = em.merge(ordenDePrestamoListOrdenDePrestamo);
            }
            em.remove(ejemplar);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Ejemplar> findEjemplarEntities() {
        return findEjemplarEntities(true, -1, -1);
    }

    public List<Ejemplar> findEjemplarEntities(int maxResults, int firstResult) {
        return findEjemplarEntities(false, maxResults, firstResult);
    }

    private List<Ejemplar> findEjemplarEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Ejemplar.class));
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

    public Ejemplar findEjemplar(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Ejemplar.class, id);
        } finally {
            em.close();
        }
    }

    public int getEjemplarCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Ejemplar> rt = cq.from(Ejemplar.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
