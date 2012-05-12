/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DaoJPA;

import DaoJPA.exceptions.IllegalOrphanException;
import DaoJPA.exceptions.NonexistentEntityException;
import DaoJPA.exceptions.PreexistingEntityException;
import Entidades.Ubicacion;
import java.io.Serializable;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entidades.Sala;
import Entidades.Pasillo;
import Entidades.Estante;
import Entidades.Cajon;
import Entidades.Ejemplar;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JHONATHAN
 */
public class UbicacionJpaController implements Serializable {

    public UbicacionJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Ubicacion ubicacion) throws PreexistingEntityException, Exception {
        if (ubicacion.getEjemplarList() == null) {
            ubicacion.setEjemplarList(new ArrayList<Ejemplar>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Sala sala = ubicacion.getSala();
            if (sala != null) {
                sala = em.getReference(sala.getClass(), sala.getSala());
                ubicacion.setSala(sala);
            }
            Pasillo pasillo = ubicacion.getPasillo();
            if (pasillo != null) {
                pasillo = em.getReference(pasillo.getClass(), pasillo.getPasillo());
                ubicacion.setPasillo(pasillo);
            }
            Estante estante = ubicacion.getEstante();
            if (estante != null) {
                estante = em.getReference(estante.getClass(), estante.getEstante());
                ubicacion.setEstante(estante);
            }
            Cajon cajon = ubicacion.getCajon();
            if (cajon != null) {
                cajon = em.getReference(cajon.getClass(), cajon.getCajon());
                ubicacion.setCajon(cajon);
            }
            List<Ejemplar> attachedEjemplarList = new ArrayList<Ejemplar>();
            for (Ejemplar ejemplarListEjemplarToAttach : ubicacion.getEjemplarList()) {
                ejemplarListEjemplarToAttach = em.getReference(ejemplarListEjemplarToAttach.getClass(), ejemplarListEjemplarToAttach.getCodigo());
                attachedEjemplarList.add(ejemplarListEjemplarToAttach);
            }
            ubicacion.setEjemplarList(attachedEjemplarList);
            em.persist(ubicacion);
            if (sala != null) {
                sala.getUbicacionList().add(ubicacion);
                sala = em.merge(sala);
            }
            if (pasillo != null) {
                pasillo.getUbicacionList().add(ubicacion);
                pasillo = em.merge(pasillo);
            }
            if (estante != null) {
                estante.getUbicacionList().add(ubicacion);
                estante = em.merge(estante);
            }
            if (cajon != null) {
                cajon.getUbicacionList().add(ubicacion);
                cajon = em.merge(cajon);
            }
            for (Ejemplar ejemplarListEjemplar : ubicacion.getEjemplarList()) {
                Ubicacion oldUbicacionOfEjemplarListEjemplar = ejemplarListEjemplar.getUbicacion();
                ejemplarListEjemplar.setUbicacion(ubicacion);
                ejemplarListEjemplar = em.merge(ejemplarListEjemplar);
                if (oldUbicacionOfEjemplarListEjemplar != null) {
                    oldUbicacionOfEjemplarListEjemplar.getEjemplarList().remove(ejemplarListEjemplar);
                    oldUbicacionOfEjemplarListEjemplar = em.merge(oldUbicacionOfEjemplarListEjemplar);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findUbicacion(ubicacion.getUbicacion()) != null) {
                throw new PreexistingEntityException("Ubicacion " + ubicacion + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Ubicacion ubicacion) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Ubicacion persistentUbicacion = em.find(Ubicacion.class, ubicacion.getUbicacion());
            Sala salaOld = persistentUbicacion.getSala();
            Sala salaNew = ubicacion.getSala();
            Pasillo pasilloOld = persistentUbicacion.getPasillo();
            Pasillo pasilloNew = ubicacion.getPasillo();
            Estante estanteOld = persistentUbicacion.getEstante();
            Estante estanteNew = ubicacion.getEstante();
            Cajon cajonOld = persistentUbicacion.getCajon();
            Cajon cajonNew = ubicacion.getCajon();
            List<Ejemplar> ejemplarListOld = persistentUbicacion.getEjemplarList();
            List<Ejemplar> ejemplarListNew = ubicacion.getEjemplarList();
            List<String> illegalOrphanMessages = null;
            for (Ejemplar ejemplarListOldEjemplar : ejemplarListOld) {
                if (!ejemplarListNew.contains(ejemplarListOldEjemplar)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Ejemplar " + ejemplarListOldEjemplar + " since its ubicacion field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (salaNew != null) {
                salaNew = em.getReference(salaNew.getClass(), salaNew.getSala());
                ubicacion.setSala(salaNew);
            }
            if (pasilloNew != null) {
                pasilloNew = em.getReference(pasilloNew.getClass(), pasilloNew.getPasillo());
                ubicacion.setPasillo(pasilloNew);
            }
            if (estanteNew != null) {
                estanteNew = em.getReference(estanteNew.getClass(), estanteNew.getEstante());
                ubicacion.setEstante(estanteNew);
            }
            if (cajonNew != null) {
                cajonNew = em.getReference(cajonNew.getClass(), cajonNew.getCajon());
                ubicacion.setCajon(cajonNew);
            }
            List<Ejemplar> attachedEjemplarListNew = new ArrayList<Ejemplar>();
            for (Ejemplar ejemplarListNewEjemplarToAttach : ejemplarListNew) {
                ejemplarListNewEjemplarToAttach = em.getReference(ejemplarListNewEjemplarToAttach.getClass(), ejemplarListNewEjemplarToAttach.getCodigo());
                attachedEjemplarListNew.add(ejemplarListNewEjemplarToAttach);
            }
            ejemplarListNew = attachedEjemplarListNew;
            ubicacion.setEjemplarList(ejemplarListNew);
            ubicacion = em.merge(ubicacion);
            if (salaOld != null && !salaOld.equals(salaNew)) {
                salaOld.getUbicacionList().remove(ubicacion);
                salaOld = em.merge(salaOld);
            }
            if (salaNew != null && !salaNew.equals(salaOld)) {
                salaNew.getUbicacionList().add(ubicacion);
                salaNew = em.merge(salaNew);
            }
            if (pasilloOld != null && !pasilloOld.equals(pasilloNew)) {
                pasilloOld.getUbicacionList().remove(ubicacion);
                pasilloOld = em.merge(pasilloOld);
            }
            if (pasilloNew != null && !pasilloNew.equals(pasilloOld)) {
                pasilloNew.getUbicacionList().add(ubicacion);
                pasilloNew = em.merge(pasilloNew);
            }
            if (estanteOld != null && !estanteOld.equals(estanteNew)) {
                estanteOld.getUbicacionList().remove(ubicacion);
                estanteOld = em.merge(estanteOld);
            }
            if (estanteNew != null && !estanteNew.equals(estanteOld)) {
                estanteNew.getUbicacionList().add(ubicacion);
                estanteNew = em.merge(estanteNew);
            }
            if (cajonOld != null && !cajonOld.equals(cajonNew)) {
                cajonOld.getUbicacionList().remove(ubicacion);
                cajonOld = em.merge(cajonOld);
            }
            if (cajonNew != null && !cajonNew.equals(cajonOld)) {
                cajonNew.getUbicacionList().add(ubicacion);
                cajonNew = em.merge(cajonNew);
            }
            for (Ejemplar ejemplarListNewEjemplar : ejemplarListNew) {
                if (!ejemplarListOld.contains(ejemplarListNewEjemplar)) {
                    Ubicacion oldUbicacionOfEjemplarListNewEjemplar = ejemplarListNewEjemplar.getUbicacion();
                    ejemplarListNewEjemplar.setUbicacion(ubicacion);
                    ejemplarListNewEjemplar = em.merge(ejemplarListNewEjemplar);
                    if (oldUbicacionOfEjemplarListNewEjemplar != null && !oldUbicacionOfEjemplarListNewEjemplar.equals(ubicacion)) {
                        oldUbicacionOfEjemplarListNewEjemplar.getEjemplarList().remove(ejemplarListNewEjemplar);
                        oldUbicacionOfEjemplarListNewEjemplar = em.merge(oldUbicacionOfEjemplarListNewEjemplar);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = ubicacion.getUbicacion();
                if (findUbicacion(id) == null) {
                    throw new NonexistentEntityException("The ubicacion with id " + id + " no longer exists.");
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
            Ubicacion ubicacion;
            try {
                ubicacion = em.getReference(Ubicacion.class, id);
                ubicacion.getUbicacion();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The ubicacion with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Ejemplar> ejemplarListOrphanCheck = ubicacion.getEjemplarList();
            for (Ejemplar ejemplarListOrphanCheckEjemplar : ejemplarListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Ubicacion (" + ubicacion + ") cannot be destroyed since the Ejemplar " + ejemplarListOrphanCheckEjemplar + " in its ejemplarList field has a non-nullable ubicacion field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Sala sala = ubicacion.getSala();
            if (sala != null) {
                sala.getUbicacionList().remove(ubicacion);
                sala = em.merge(sala);
            }
            Pasillo pasillo = ubicacion.getPasillo();
            if (pasillo != null) {
                pasillo.getUbicacionList().remove(ubicacion);
                pasillo = em.merge(pasillo);
            }
            Estante estante = ubicacion.getEstante();
            if (estante != null) {
                estante.getUbicacionList().remove(ubicacion);
                estante = em.merge(estante);
            }
            Cajon cajon = ubicacion.getCajon();
            if (cajon != null) {
                cajon.getUbicacionList().remove(ubicacion);
                cajon = em.merge(cajon);
            }
            em.remove(ubicacion);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Ubicacion> findUbicacionEntities() {
        return findUbicacionEntities(true, -1, -1);
    }

    public List<Ubicacion> findUbicacionEntities(int maxResults, int firstResult) {
        return findUbicacionEntities(false, maxResults, firstResult);
    }

    private List<Ubicacion> findUbicacionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Ubicacion.class));
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

    public Ubicacion findUbicacion(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Ubicacion.class, id);
        } finally {
            em.close();
        }
    }

    public int getUbicacionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Ubicacion> rt = cq.from(Ubicacion.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
