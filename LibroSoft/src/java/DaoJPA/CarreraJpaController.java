/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DaoJPA;

import DaoJPA.exceptions.IllegalOrphanException;
import DaoJPA.exceptions.NonexistentEntityException;
import DaoJPA.exceptions.PreexistingEntityException;
import Entidades.Carrera;
import java.io.Serializable;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entidades.Estudiante;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JHONATHAN
 */
public class CarreraJpaController implements Serializable {

    public CarreraJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Carrera carrera) throws PreexistingEntityException, Exception {
        if (carrera.getEstudianteList() == null) {
            carrera.setEstudianteList(new ArrayList<Estudiante>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Estudiante> attachedEstudianteList = new ArrayList<Estudiante>();
            for (Estudiante estudianteListEstudianteToAttach : carrera.getEstudianteList()) {
                estudianteListEstudianteToAttach = em.getReference(estudianteListEstudianteToAttach.getClass(), estudianteListEstudianteToAttach.getIdEstudiante());
                attachedEstudianteList.add(estudianteListEstudianteToAttach);
            }
            carrera.setEstudianteList(attachedEstudianteList);
            em.persist(carrera);
            for (Estudiante estudianteListEstudiante : carrera.getEstudianteList()) {
                Carrera oldCarreraOfEstudianteListEstudiante = estudianteListEstudiante.getCarrera();
                estudianteListEstudiante.setCarrera(carrera);
                estudianteListEstudiante = em.merge(estudianteListEstudiante);
                if (oldCarreraOfEstudianteListEstudiante != null) {
                    oldCarreraOfEstudianteListEstudiante.getEstudianteList().remove(estudianteListEstudiante);
                    oldCarreraOfEstudianteListEstudiante = em.merge(oldCarreraOfEstudianteListEstudiante);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findCarrera(carrera.getCodigo()) != null) {
                throw new PreexistingEntityException("Carrera " + carrera + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Carrera carrera) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Carrera persistentCarrera = em.find(Carrera.class, carrera.getCodigo());
            List<Estudiante> estudianteListOld = persistentCarrera.getEstudianteList();
            List<Estudiante> estudianteListNew = carrera.getEstudianteList();
            List<String> illegalOrphanMessages = null;
            for (Estudiante estudianteListOldEstudiante : estudianteListOld) {
                if (!estudianteListNew.contains(estudianteListOldEstudiante)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Estudiante " + estudianteListOldEstudiante + " since its carrera field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Estudiante> attachedEstudianteListNew = new ArrayList<Estudiante>();
            for (Estudiante estudianteListNewEstudianteToAttach : estudianteListNew) {
                estudianteListNewEstudianteToAttach = em.getReference(estudianteListNewEstudianteToAttach.getClass(), estudianteListNewEstudianteToAttach.getIdEstudiante());
                attachedEstudianteListNew.add(estudianteListNewEstudianteToAttach);
            }
            estudianteListNew = attachedEstudianteListNew;
            carrera.setEstudianteList(estudianteListNew);
            carrera = em.merge(carrera);
            for (Estudiante estudianteListNewEstudiante : estudianteListNew) {
                if (!estudianteListOld.contains(estudianteListNewEstudiante)) {
                    Carrera oldCarreraOfEstudianteListNewEstudiante = estudianteListNewEstudiante.getCarrera();
                    estudianteListNewEstudiante.setCarrera(carrera);
                    estudianteListNewEstudiante = em.merge(estudianteListNewEstudiante);
                    if (oldCarreraOfEstudianteListNewEstudiante != null && !oldCarreraOfEstudianteListNewEstudiante.equals(carrera)) {
                        oldCarreraOfEstudianteListNewEstudiante.getEstudianteList().remove(estudianteListNewEstudiante);
                        oldCarreraOfEstudianteListNewEstudiante = em.merge(oldCarreraOfEstudianteListNewEstudiante);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = carrera.getCodigo();
                if (findCarrera(id) == null) {
                    throw new NonexistentEntityException("The carrera with id " + id + " no longer exists.");
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
            Carrera carrera;
            try {
                carrera = em.getReference(Carrera.class, id);
                carrera.getCodigo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The carrera with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Estudiante> estudianteListOrphanCheck = carrera.getEstudianteList();
            for (Estudiante estudianteListOrphanCheckEstudiante : estudianteListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Carrera (" + carrera + ") cannot be destroyed since the Estudiante " + estudianteListOrphanCheckEstudiante + " in its estudianteList field has a non-nullable carrera field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(carrera);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Carrera> findCarreraEntities() {
        return findCarreraEntities(true, -1, -1);
    }

    public List<Carrera> findCarreraEntities(int maxResults, int firstResult) {
        return findCarreraEntities(false, maxResults, firstResult);
    }

    private List<Carrera> findCarreraEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Carrera.class));
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

    public Carrera findCarrera(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Carrera.class, id);
        } finally {
            em.close();
        }
    }

    public int getCarreraCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Carrera> rt = cq.from(Carrera.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
