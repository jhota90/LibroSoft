/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DaoJPA;

import DaoJPA.exceptions.IllegalOrphanException;
import DaoJPA.exceptions.NonexistentEntityException;
import DaoJPA.exceptions.PreexistingEntityException;
import Entidades.AreaDeConocimiento;
import java.io.Serializable;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entidades.SubareaDeConocimiento;
import Entidades.Profesor;
import java.util.ArrayList;
import java.util.List;
import Entidades.Libro;
import Entidades.Pasillo;

/**
 *
 * @author JHONATHAN
 */
public class AreaDeConocimientoJpaController implements Serializable {

    public AreaDeConocimientoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(AreaDeConocimiento areaDeConocimiento) throws PreexistingEntityException, Exception {
        if (areaDeConocimiento.getProfesorList() == null) {
            areaDeConocimiento.setProfesorList(new ArrayList<Profesor>());
        }
        if (areaDeConocimiento.getSubareaDeConocimientoList() == null) {
            areaDeConocimiento.setSubareaDeConocimientoList(new ArrayList<SubareaDeConocimiento>());
        }
        if (areaDeConocimiento.getLibroList() == null) {
            areaDeConocimiento.setLibroList(new ArrayList<Libro>());
        }
        if (areaDeConocimiento.getPasilloList() == null) {
            areaDeConocimiento.setPasilloList(new ArrayList<Pasillo>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            SubareaDeConocimiento subareaDeConocimiento = areaDeConocimiento.getSubareaDeConocimiento();
            if (subareaDeConocimiento != null) {
                subareaDeConocimiento = em.getReference(subareaDeConocimiento.getClass(), subareaDeConocimiento.getCodigo());
                areaDeConocimiento.setSubareaDeConocimiento(subareaDeConocimiento);
            }
            List<Profesor> attachedProfesorList = new ArrayList<Profesor>();
            for (Profesor profesorListProfesorToAttach : areaDeConocimiento.getProfesorList()) {
                profesorListProfesorToAttach = em.getReference(profesorListProfesorToAttach.getClass(), profesorListProfesorToAttach.getIdProfesor());
                attachedProfesorList.add(profesorListProfesorToAttach);
            }
            areaDeConocimiento.setProfesorList(attachedProfesorList);
            List<SubareaDeConocimiento> attachedSubareaDeConocimientoList = new ArrayList<SubareaDeConocimiento>();
            for (SubareaDeConocimiento subareaDeConocimientoListSubareaDeConocimientoToAttach : areaDeConocimiento.getSubareaDeConocimientoList()) {
                subareaDeConocimientoListSubareaDeConocimientoToAttach = em.getReference(subareaDeConocimientoListSubareaDeConocimientoToAttach.getClass(), subareaDeConocimientoListSubareaDeConocimientoToAttach.getCodigo());
                attachedSubareaDeConocimientoList.add(subareaDeConocimientoListSubareaDeConocimientoToAttach);
            }
            areaDeConocimiento.setSubareaDeConocimientoList(attachedSubareaDeConocimientoList);
            List<Libro> attachedLibroList = new ArrayList<Libro>();
            for (Libro libroListLibroToAttach : areaDeConocimiento.getLibroList()) {
                libroListLibroToAttach = em.getReference(libroListLibroToAttach.getClass(), libroListLibroToAttach.getIsbn());
                attachedLibroList.add(libroListLibroToAttach);
            }
            areaDeConocimiento.setLibroList(attachedLibroList);
            List<Pasillo> attachedPasilloList = new ArrayList<Pasillo>();
            for (Pasillo pasilloListPasilloToAttach : areaDeConocimiento.getPasilloList()) {
                pasilloListPasilloToAttach = em.getReference(pasilloListPasilloToAttach.getClass(), pasilloListPasilloToAttach.getPasillo());
                attachedPasilloList.add(pasilloListPasilloToAttach);
            }
            areaDeConocimiento.setPasilloList(attachedPasilloList);
            em.persist(areaDeConocimiento);
            if (subareaDeConocimiento != null) {
                AreaDeConocimiento oldAreaDeConocimientoOfSubareaDeConocimiento = subareaDeConocimiento.getAreaDeConocimiento();
                if (oldAreaDeConocimientoOfSubareaDeConocimiento != null) {
                    oldAreaDeConocimientoOfSubareaDeConocimiento.setSubareaDeConocimiento(null);
                    oldAreaDeConocimientoOfSubareaDeConocimiento = em.merge(oldAreaDeConocimientoOfSubareaDeConocimiento);
                }
                subareaDeConocimiento.setAreaDeConocimiento(areaDeConocimiento);
                subareaDeConocimiento = em.merge(subareaDeConocimiento);
            }
            for (Profesor profesorListProfesor : areaDeConocimiento.getProfesorList()) {
                profesorListProfesor.getAreaDeConocimientoList().add(areaDeConocimiento);
                profesorListProfesor = em.merge(profesorListProfesor);
            }
            for (SubareaDeConocimiento subareaDeConocimientoListSubareaDeConocimiento : areaDeConocimiento.getSubareaDeConocimientoList()) {
                AreaDeConocimiento oldCodigoPadreOfSubareaDeConocimientoListSubareaDeConocimiento = subareaDeConocimientoListSubareaDeConocimiento.getCodigoPadre();
                subareaDeConocimientoListSubareaDeConocimiento.setCodigoPadre(areaDeConocimiento);
                subareaDeConocimientoListSubareaDeConocimiento = em.merge(subareaDeConocimientoListSubareaDeConocimiento);
                if (oldCodigoPadreOfSubareaDeConocimientoListSubareaDeConocimiento != null) {
                    oldCodigoPadreOfSubareaDeConocimientoListSubareaDeConocimiento.getSubareaDeConocimientoList().remove(subareaDeConocimientoListSubareaDeConocimiento);
                    oldCodigoPadreOfSubareaDeConocimientoListSubareaDeConocimiento = em.merge(oldCodigoPadreOfSubareaDeConocimientoListSubareaDeConocimiento);
                }
            }
            for (Libro libroListLibro : areaDeConocimiento.getLibroList()) {
                AreaDeConocimiento oldAreaConocimientoOfLibroListLibro = libroListLibro.getAreaConocimiento();
                libroListLibro.setAreaConocimiento(areaDeConocimiento);
                libroListLibro = em.merge(libroListLibro);
                if (oldAreaConocimientoOfLibroListLibro != null) {
                    oldAreaConocimientoOfLibroListLibro.getLibroList().remove(libroListLibro);
                    oldAreaConocimientoOfLibroListLibro = em.merge(oldAreaConocimientoOfLibroListLibro);
                }
            }
            for (Pasillo pasilloListPasillo : areaDeConocimiento.getPasilloList()) {
                AreaDeConocimiento oldAreaConocimientoOfPasilloListPasillo = pasilloListPasillo.getAreaConocimiento();
                pasilloListPasillo.setAreaConocimiento(areaDeConocimiento);
                pasilloListPasillo = em.merge(pasilloListPasillo);
                if (oldAreaConocimientoOfPasilloListPasillo != null) {
                    oldAreaConocimientoOfPasilloListPasillo.getPasilloList().remove(pasilloListPasillo);
                    oldAreaConocimientoOfPasilloListPasillo = em.merge(oldAreaConocimientoOfPasilloListPasillo);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findAreaDeConocimiento(areaDeConocimiento.getCodigo()) != null) {
                throw new PreexistingEntityException("AreaDeConocimiento " + areaDeConocimiento + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(AreaDeConocimiento areaDeConocimiento) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            AreaDeConocimiento persistentAreaDeConocimiento = em.find(AreaDeConocimiento.class, areaDeConocimiento.getCodigo());
            SubareaDeConocimiento subareaDeConocimientoOld = persistentAreaDeConocimiento.getSubareaDeConocimiento();
            SubareaDeConocimiento subareaDeConocimientoNew = areaDeConocimiento.getSubareaDeConocimiento();
            List<Profesor> profesorListOld = persistentAreaDeConocimiento.getProfesorList();
            List<Profesor> profesorListNew = areaDeConocimiento.getProfesorList();
            List<SubareaDeConocimiento> subareaDeConocimientoListOld = persistentAreaDeConocimiento.getSubareaDeConocimientoList();
            List<SubareaDeConocimiento> subareaDeConocimientoListNew = areaDeConocimiento.getSubareaDeConocimientoList();
            List<Libro> libroListOld = persistentAreaDeConocimiento.getLibroList();
            List<Libro> libroListNew = areaDeConocimiento.getLibroList();
            List<Pasillo> pasilloListOld = persistentAreaDeConocimiento.getPasilloList();
            List<Pasillo> pasilloListNew = areaDeConocimiento.getPasilloList();
            List<String> illegalOrphanMessages = null;
            if (subareaDeConocimientoOld != null && !subareaDeConocimientoOld.equals(subareaDeConocimientoNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain SubareaDeConocimiento " + subareaDeConocimientoOld + " since its areaDeConocimiento field is not nullable.");
            }
            for (SubareaDeConocimiento subareaDeConocimientoListOldSubareaDeConocimiento : subareaDeConocimientoListOld) {
                if (!subareaDeConocimientoListNew.contains(subareaDeConocimientoListOldSubareaDeConocimiento)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain SubareaDeConocimiento " + subareaDeConocimientoListOldSubareaDeConocimiento + " since its codigoPadre field is not nullable.");
                }
            }
            for (Libro libroListOldLibro : libroListOld) {
                if (!libroListNew.contains(libroListOldLibro)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Libro " + libroListOldLibro + " since its areaConocimiento field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (subareaDeConocimientoNew != null) {
                subareaDeConocimientoNew = em.getReference(subareaDeConocimientoNew.getClass(), subareaDeConocimientoNew.getCodigo());
                areaDeConocimiento.setSubareaDeConocimiento(subareaDeConocimientoNew);
            }
            List<Profesor> attachedProfesorListNew = new ArrayList<Profesor>();
            for (Profesor profesorListNewProfesorToAttach : profesorListNew) {
                profesorListNewProfesorToAttach = em.getReference(profesorListNewProfesorToAttach.getClass(), profesorListNewProfesorToAttach.getIdProfesor());
                attachedProfesorListNew.add(profesorListNewProfesorToAttach);
            }
            profesorListNew = attachedProfesorListNew;
            areaDeConocimiento.setProfesorList(profesorListNew);
            List<SubareaDeConocimiento> attachedSubareaDeConocimientoListNew = new ArrayList<SubareaDeConocimiento>();
            for (SubareaDeConocimiento subareaDeConocimientoListNewSubareaDeConocimientoToAttach : subareaDeConocimientoListNew) {
                subareaDeConocimientoListNewSubareaDeConocimientoToAttach = em.getReference(subareaDeConocimientoListNewSubareaDeConocimientoToAttach.getClass(), subareaDeConocimientoListNewSubareaDeConocimientoToAttach.getCodigo());
                attachedSubareaDeConocimientoListNew.add(subareaDeConocimientoListNewSubareaDeConocimientoToAttach);
            }
            subareaDeConocimientoListNew = attachedSubareaDeConocimientoListNew;
            areaDeConocimiento.setSubareaDeConocimientoList(subareaDeConocimientoListNew);
            List<Libro> attachedLibroListNew = new ArrayList<Libro>();
            for (Libro libroListNewLibroToAttach : libroListNew) {
                libroListNewLibroToAttach = em.getReference(libroListNewLibroToAttach.getClass(), libroListNewLibroToAttach.getIsbn());
                attachedLibroListNew.add(libroListNewLibroToAttach);
            }
            libroListNew = attachedLibroListNew;
            areaDeConocimiento.setLibroList(libroListNew);
            List<Pasillo> attachedPasilloListNew = new ArrayList<Pasillo>();
            for (Pasillo pasilloListNewPasilloToAttach : pasilloListNew) {
                pasilloListNewPasilloToAttach = em.getReference(pasilloListNewPasilloToAttach.getClass(), pasilloListNewPasilloToAttach.getPasillo());
                attachedPasilloListNew.add(pasilloListNewPasilloToAttach);
            }
            pasilloListNew = attachedPasilloListNew;
            areaDeConocimiento.setPasilloList(pasilloListNew);
            areaDeConocimiento = em.merge(areaDeConocimiento);
            if (subareaDeConocimientoNew != null && !subareaDeConocimientoNew.equals(subareaDeConocimientoOld)) {
                AreaDeConocimiento oldAreaDeConocimientoOfSubareaDeConocimiento = subareaDeConocimientoNew.getAreaDeConocimiento();
                if (oldAreaDeConocimientoOfSubareaDeConocimiento != null) {
                    oldAreaDeConocimientoOfSubareaDeConocimiento.setSubareaDeConocimiento(null);
                    oldAreaDeConocimientoOfSubareaDeConocimiento = em.merge(oldAreaDeConocimientoOfSubareaDeConocimiento);
                }
                subareaDeConocimientoNew.setAreaDeConocimiento(areaDeConocimiento);
                subareaDeConocimientoNew = em.merge(subareaDeConocimientoNew);
            }
            for (Profesor profesorListOldProfesor : profesorListOld) {
                if (!profesorListNew.contains(profesorListOldProfesor)) {
                    profesorListOldProfesor.getAreaDeConocimientoList().remove(areaDeConocimiento);
                    profesorListOldProfesor = em.merge(profesorListOldProfesor);
                }
            }
            for (Profesor profesorListNewProfesor : profesorListNew) {
                if (!profesorListOld.contains(profesorListNewProfesor)) {
                    profesorListNewProfesor.getAreaDeConocimientoList().add(areaDeConocimiento);
                    profesorListNewProfesor = em.merge(profesorListNewProfesor);
                }
            }
            for (SubareaDeConocimiento subareaDeConocimientoListNewSubareaDeConocimiento : subareaDeConocimientoListNew) {
                if (!subareaDeConocimientoListOld.contains(subareaDeConocimientoListNewSubareaDeConocimiento)) {
                    AreaDeConocimiento oldCodigoPadreOfSubareaDeConocimientoListNewSubareaDeConocimiento = subareaDeConocimientoListNewSubareaDeConocimiento.getCodigoPadre();
                    subareaDeConocimientoListNewSubareaDeConocimiento.setCodigoPadre(areaDeConocimiento);
                    subareaDeConocimientoListNewSubareaDeConocimiento = em.merge(subareaDeConocimientoListNewSubareaDeConocimiento);
                    if (oldCodigoPadreOfSubareaDeConocimientoListNewSubareaDeConocimiento != null && !oldCodigoPadreOfSubareaDeConocimientoListNewSubareaDeConocimiento.equals(areaDeConocimiento)) {
                        oldCodigoPadreOfSubareaDeConocimientoListNewSubareaDeConocimiento.getSubareaDeConocimientoList().remove(subareaDeConocimientoListNewSubareaDeConocimiento);
                        oldCodigoPadreOfSubareaDeConocimientoListNewSubareaDeConocimiento = em.merge(oldCodigoPadreOfSubareaDeConocimientoListNewSubareaDeConocimiento);
                    }
                }
            }
            for (Libro libroListNewLibro : libroListNew) {
                if (!libroListOld.contains(libroListNewLibro)) {
                    AreaDeConocimiento oldAreaConocimientoOfLibroListNewLibro = libroListNewLibro.getAreaConocimiento();
                    libroListNewLibro.setAreaConocimiento(areaDeConocimiento);
                    libroListNewLibro = em.merge(libroListNewLibro);
                    if (oldAreaConocimientoOfLibroListNewLibro != null && !oldAreaConocimientoOfLibroListNewLibro.equals(areaDeConocimiento)) {
                        oldAreaConocimientoOfLibroListNewLibro.getLibroList().remove(libroListNewLibro);
                        oldAreaConocimientoOfLibroListNewLibro = em.merge(oldAreaConocimientoOfLibroListNewLibro);
                    }
                }
            }
            for (Pasillo pasilloListOldPasillo : pasilloListOld) {
                if (!pasilloListNew.contains(pasilloListOldPasillo)) {
                    pasilloListOldPasillo.setAreaConocimiento(null);
                    pasilloListOldPasillo = em.merge(pasilloListOldPasillo);
                }
            }
            for (Pasillo pasilloListNewPasillo : pasilloListNew) {
                if (!pasilloListOld.contains(pasilloListNewPasillo)) {
                    AreaDeConocimiento oldAreaConocimientoOfPasilloListNewPasillo = pasilloListNewPasillo.getAreaConocimiento();
                    pasilloListNewPasillo.setAreaConocimiento(areaDeConocimiento);
                    pasilloListNewPasillo = em.merge(pasilloListNewPasillo);
                    if (oldAreaConocimientoOfPasilloListNewPasillo != null && !oldAreaConocimientoOfPasilloListNewPasillo.equals(areaDeConocimiento)) {
                        oldAreaConocimientoOfPasilloListNewPasillo.getPasilloList().remove(pasilloListNewPasillo);
                        oldAreaConocimientoOfPasilloListNewPasillo = em.merge(oldAreaConocimientoOfPasilloListNewPasillo);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = areaDeConocimiento.getCodigo();
                if (findAreaDeConocimiento(id) == null) {
                    throw new NonexistentEntityException("The areaDeConocimiento with id " + id + " no longer exists.");
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
            AreaDeConocimiento areaDeConocimiento;
            try {
                areaDeConocimiento = em.getReference(AreaDeConocimiento.class, id);
                areaDeConocimiento.getCodigo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The areaDeConocimiento with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            SubareaDeConocimiento subareaDeConocimientoOrphanCheck = areaDeConocimiento.getSubareaDeConocimiento();
            if (subareaDeConocimientoOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This AreaDeConocimiento (" + areaDeConocimiento + ") cannot be destroyed since the SubareaDeConocimiento " + subareaDeConocimientoOrphanCheck + " in its subareaDeConocimiento field has a non-nullable areaDeConocimiento field.");
            }
            List<SubareaDeConocimiento> subareaDeConocimientoListOrphanCheck = areaDeConocimiento.getSubareaDeConocimientoList();
            for (SubareaDeConocimiento subareaDeConocimientoListOrphanCheckSubareaDeConocimiento : subareaDeConocimientoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This AreaDeConocimiento (" + areaDeConocimiento + ") cannot be destroyed since the SubareaDeConocimiento " + subareaDeConocimientoListOrphanCheckSubareaDeConocimiento + " in its subareaDeConocimientoList field has a non-nullable codigoPadre field.");
            }
            List<Libro> libroListOrphanCheck = areaDeConocimiento.getLibroList();
            for (Libro libroListOrphanCheckLibro : libroListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This AreaDeConocimiento (" + areaDeConocimiento + ") cannot be destroyed since the Libro " + libroListOrphanCheckLibro + " in its libroList field has a non-nullable areaConocimiento field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Profesor> profesorList = areaDeConocimiento.getProfesorList();
            for (Profesor profesorListProfesor : profesorList) {
                profesorListProfesor.getAreaDeConocimientoList().remove(areaDeConocimiento);
                profesorListProfesor = em.merge(profesorListProfesor);
            }
            List<Pasillo> pasilloList = areaDeConocimiento.getPasilloList();
            for (Pasillo pasilloListPasillo : pasilloList) {
                pasilloListPasillo.setAreaConocimiento(null);
                pasilloListPasillo = em.merge(pasilloListPasillo);
            }
            em.remove(areaDeConocimiento);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<AreaDeConocimiento> findAreaDeConocimientoEntities() {
        return findAreaDeConocimientoEntities(true, -1, -1);
    }

    public List<AreaDeConocimiento> findAreaDeConocimientoEntities(int maxResults, int firstResult) {
        return findAreaDeConocimientoEntities(false, maxResults, firstResult);
    }

    private List<AreaDeConocimiento> findAreaDeConocimientoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(AreaDeConocimiento.class));
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

    public AreaDeConocimiento findAreaDeConocimiento(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(AreaDeConocimiento.class, id);
        } finally {
            em.close();
        }
    }

    public int getAreaDeConocimientoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<AreaDeConocimiento> rt = cq.from(AreaDeConocimiento.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
