/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DaoJPA;

import DaoJPA.exceptions.IllegalOrphanException;
import DaoJPA.exceptions.NonexistentEntityException;
import DaoJPA.exceptions.PreexistingEntityException;
import Entidades.Estudiante;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entidades.Carrera;
import Entidades.Usuario;
import java.util.ArrayList;

/**
 *
 * @author JHONATHAN
 */
public class EstudianteJpaController implements Serializable {

    public EstudianteJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Estudiante estudiante) throws IllegalOrphanException, PreexistingEntityException, Exception {
        List<String> illegalOrphanMessages = null;
        Usuario usuarioOrphanCheck = estudiante.getUsuario();
        if (usuarioOrphanCheck != null) {
            Estudiante oldEstudianteOfUsuario = usuarioOrphanCheck.getEstudiante();
            if (oldEstudianteOfUsuario != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The Usuario " + usuarioOrphanCheck + " already has an item of type Estudiante whose usuario column cannot be null. Please make another selection for the usuario field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Carrera carrera = estudiante.getCarrera();
            if (carrera != null) {
                carrera = em.getReference(carrera.getClass(), carrera.getCodigo());
                estudiante.setCarrera(carrera);
            }
            Usuario usuario = estudiante.getUsuario();
            if (usuario != null) {
                usuario = em.getReference(usuario.getClass(), usuario.getIdUsuario());
                estudiante.setUsuario(usuario);
            }
            em.persist(estudiante);
            if (carrera != null) {
                carrera.getEstudianteList().add(estudiante);
                carrera = em.merge(carrera);
            }
            if (usuario != null) {
                usuario.setEstudiante(estudiante);
                usuario = em.merge(usuario);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findEstudiante(estudiante.getIdEstudiante()) != null) {
                throw new PreexistingEntityException("Estudiante " + estudiante + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Estudiante estudiante) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Estudiante persistentEstudiante = em.find(Estudiante.class, estudiante.getIdEstudiante());
            Carrera carreraOld = persistentEstudiante.getCarrera();
            Carrera carreraNew = estudiante.getCarrera();
            Usuario usuarioOld = persistentEstudiante.getUsuario();
            Usuario usuarioNew = estudiante.getUsuario();
            List<String> illegalOrphanMessages = null;
            if (usuarioNew != null && !usuarioNew.equals(usuarioOld)) {
                Estudiante oldEstudianteOfUsuario = usuarioNew.getEstudiante();
                if (oldEstudianteOfUsuario != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The Usuario " + usuarioNew + " already has an item of type Estudiante whose usuario column cannot be null. Please make another selection for the usuario field.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (carreraNew != null) {
                carreraNew = em.getReference(carreraNew.getClass(), carreraNew.getCodigo());
                estudiante.setCarrera(carreraNew);
            }
            if (usuarioNew != null) {
                usuarioNew = em.getReference(usuarioNew.getClass(), usuarioNew.getIdUsuario());
                estudiante.setUsuario(usuarioNew);
            }
            estudiante = em.merge(estudiante);
            if (carreraOld != null && !carreraOld.equals(carreraNew)) {
                carreraOld.getEstudianteList().remove(estudiante);
                carreraOld = em.merge(carreraOld);
            }
            if (carreraNew != null && !carreraNew.equals(carreraOld)) {
                carreraNew.getEstudianteList().add(estudiante);
                carreraNew = em.merge(carreraNew);
            }
            if (usuarioOld != null && !usuarioOld.equals(usuarioNew)) {
                usuarioOld.setEstudiante(null);
                usuarioOld = em.merge(usuarioOld);
            }
            if (usuarioNew != null && !usuarioNew.equals(usuarioOld)) {
                usuarioNew.setEstudiante(estudiante);
                usuarioNew = em.merge(usuarioNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = estudiante.getIdEstudiante();
                if (findEstudiante(id) == null) {
                    throw new NonexistentEntityException("The estudiante with id " + id + " no longer exists.");
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
            Estudiante estudiante;
            try {
                estudiante = em.getReference(Estudiante.class, id);
                estudiante.getIdEstudiante();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The estudiante with id " + id + " no longer exists.", enfe);
            }
            Carrera carrera = estudiante.getCarrera();
            if (carrera != null) {
                carrera.getEstudianteList().remove(estudiante);
                carrera = em.merge(carrera);
            }
            Usuario usuario = estudiante.getUsuario();
            if (usuario != null) {
                usuario.setEstudiante(null);
                usuario = em.merge(usuario);
            }
            em.remove(estudiante);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Estudiante> findEstudianteEntities() {
        return findEstudianteEntities(true, -1, -1);
    }

    public List<Estudiante> findEstudianteEntities(int maxResults, int firstResult) {
        return findEstudianteEntities(false, maxResults, firstResult);
    }

    private List<Estudiante> findEstudianteEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Estudiante.class));
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

    public Estudiante findEstudiante(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Estudiante.class, id);
        } finally {
            em.close();
        }
    }

    public int getEstudianteCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Estudiante> rt = cq.from(Estudiante.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
