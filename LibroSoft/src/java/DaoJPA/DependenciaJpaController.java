/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DaoJPA;

import DaoJPA.exceptions.IllegalOrphanException;
import DaoJPA.exceptions.NonexistentEntityException;
import DaoJPA.exceptions.PreexistingEntityException;
import Entidades.Dependencia;
import java.io.Serializable;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entidades.Profesor;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JHONATHAN
 */
public class DependenciaJpaController implements Serializable {

    public DependenciaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Dependencia dependencia) throws PreexistingEntityException, Exception {
        if (dependencia.getProfesorList() == null) {
            dependencia.setProfesorList(new ArrayList<Profesor>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Profesor> attachedProfesorList = new ArrayList<Profesor>();
            for (Profesor profesorListProfesorToAttach : dependencia.getProfesorList()) {
                profesorListProfesorToAttach = em.getReference(profesorListProfesorToAttach.getClass(), profesorListProfesorToAttach.getIdProfesor());
                attachedProfesorList.add(profesorListProfesorToAttach);
            }
            dependencia.setProfesorList(attachedProfesorList);
            em.persist(dependencia);
            for (Profesor profesorListProfesor : dependencia.getProfesorList()) {
                Dependencia oldDependenciaOfProfesorListProfesor = profesorListProfesor.getDependencia();
                profesorListProfesor.setDependencia(dependencia);
                profesorListProfesor = em.merge(profesorListProfesor);
                if (oldDependenciaOfProfesorListProfesor != null) {
                    oldDependenciaOfProfesorListProfesor.getProfesorList().remove(profesorListProfesor);
                    oldDependenciaOfProfesorListProfesor = em.merge(oldDependenciaOfProfesorListProfesor);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findDependencia(dependencia.getCodigo()) != null) {
                throw new PreexistingEntityException("Dependencia " + dependencia + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Dependencia dependencia) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Dependencia persistentDependencia = em.find(Dependencia.class, dependencia.getCodigo());
            List<Profesor> profesorListOld = persistentDependencia.getProfesorList();
            List<Profesor> profesorListNew = dependencia.getProfesorList();
            List<String> illegalOrphanMessages = null;
            for (Profesor profesorListOldProfesor : profesorListOld) {
                if (!profesorListNew.contains(profesorListOldProfesor)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Profesor " + profesorListOldProfesor + " since its dependencia field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Profesor> attachedProfesorListNew = new ArrayList<Profesor>();
            for (Profesor profesorListNewProfesorToAttach : profesorListNew) {
                profesorListNewProfesorToAttach = em.getReference(profesorListNewProfesorToAttach.getClass(), profesorListNewProfesorToAttach.getIdProfesor());
                attachedProfesorListNew.add(profesorListNewProfesorToAttach);
            }
            profesorListNew = attachedProfesorListNew;
            dependencia.setProfesorList(profesorListNew);
            dependencia = em.merge(dependencia);
            for (Profesor profesorListNewProfesor : profesorListNew) {
                if (!profesorListOld.contains(profesorListNewProfesor)) {
                    Dependencia oldDependenciaOfProfesorListNewProfesor = profesorListNewProfesor.getDependencia();
                    profesorListNewProfesor.setDependencia(dependencia);
                    profesorListNewProfesor = em.merge(profesorListNewProfesor);
                    if (oldDependenciaOfProfesorListNewProfesor != null && !oldDependenciaOfProfesorListNewProfesor.equals(dependencia)) {
                        oldDependenciaOfProfesorListNewProfesor.getProfesorList().remove(profesorListNewProfesor);
                        oldDependenciaOfProfesorListNewProfesor = em.merge(oldDependenciaOfProfesorListNewProfesor);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = dependencia.getCodigo();
                if (findDependencia(id) == null) {
                    throw new NonexistentEntityException("The dependencia with id " + id + " no longer exists.");
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
            Dependencia dependencia;
            try {
                dependencia = em.getReference(Dependencia.class, id);
                dependencia.getCodigo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The dependencia with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Profesor> profesorListOrphanCheck = dependencia.getProfesorList();
            for (Profesor profesorListOrphanCheckProfesor : profesorListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Dependencia (" + dependencia + ") cannot be destroyed since the Profesor " + profesorListOrphanCheckProfesor + " in its profesorList field has a non-nullable dependencia field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(dependencia);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Dependencia> findDependenciaEntities() {
        return findDependenciaEntities(true, -1, -1);
    }

    public List<Dependencia> findDependenciaEntities(int maxResults, int firstResult) {
        return findDependenciaEntities(false, maxResults, firstResult);
    }

    private List<Dependencia> findDependenciaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Dependencia.class));
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

    public Dependencia findDependencia(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Dependencia.class, id);
        } finally {
            em.close();
        }
    }

    public int getDependenciaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Dependencia> rt = cq.from(Dependencia.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
