/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DaoJPA;

import DaoJPA.exceptions.IllegalOrphanException;
import DaoJPA.exceptions.NonexistentEntityException;
import DaoJPA.exceptions.PreexistingEntityException;
import Entidades.Idioma;
import java.io.Serializable;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entidades.Libro;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JHONATHAN
 */
public class IdiomaJpaController implements Serializable {

    public IdiomaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Idioma idioma) throws PreexistingEntityException, Exception {
        if (idioma.getLibroList() == null) {
            idioma.setLibroList(new ArrayList<Libro>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Libro> attachedLibroList = new ArrayList<Libro>();
            for (Libro libroListLibroToAttach : idioma.getLibroList()) {
                libroListLibroToAttach = em.getReference(libroListLibroToAttach.getClass(), libroListLibroToAttach.getIsbn());
                attachedLibroList.add(libroListLibroToAttach);
            }
            idioma.setLibroList(attachedLibroList);
            em.persist(idioma);
            for (Libro libroListLibro : idioma.getLibroList()) {
                Idioma oldIdiomaOfLibroListLibro = libroListLibro.getIdioma();
                libroListLibro.setIdioma(idioma);
                libroListLibro = em.merge(libroListLibro);
                if (oldIdiomaOfLibroListLibro != null) {
                    oldIdiomaOfLibroListLibro.getLibroList().remove(libroListLibro);
                    oldIdiomaOfLibroListLibro = em.merge(oldIdiomaOfLibroListLibro);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findIdioma(idioma.getCodigo()) != null) {
                throw new PreexistingEntityException("Idioma " + idioma + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Idioma idioma) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Idioma persistentIdioma = em.find(Idioma.class, idioma.getCodigo());
            List<Libro> libroListOld = persistentIdioma.getLibroList();
            List<Libro> libroListNew = idioma.getLibroList();
            List<String> illegalOrphanMessages = null;
            for (Libro libroListOldLibro : libroListOld) {
                if (!libroListNew.contains(libroListOldLibro)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Libro " + libroListOldLibro + " since its idioma field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Libro> attachedLibroListNew = new ArrayList<Libro>();
            for (Libro libroListNewLibroToAttach : libroListNew) {
                libroListNewLibroToAttach = em.getReference(libroListNewLibroToAttach.getClass(), libroListNewLibroToAttach.getIsbn());
                attachedLibroListNew.add(libroListNewLibroToAttach);
            }
            libroListNew = attachedLibroListNew;
            idioma.setLibroList(libroListNew);
            idioma = em.merge(idioma);
            for (Libro libroListNewLibro : libroListNew) {
                if (!libroListOld.contains(libroListNewLibro)) {
                    Idioma oldIdiomaOfLibroListNewLibro = libroListNewLibro.getIdioma();
                    libroListNewLibro.setIdioma(idioma);
                    libroListNewLibro = em.merge(libroListNewLibro);
                    if (oldIdiomaOfLibroListNewLibro != null && !oldIdiomaOfLibroListNewLibro.equals(idioma)) {
                        oldIdiomaOfLibroListNewLibro.getLibroList().remove(libroListNewLibro);
                        oldIdiomaOfLibroListNewLibro = em.merge(oldIdiomaOfLibroListNewLibro);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = idioma.getCodigo();
                if (findIdioma(id) == null) {
                    throw new NonexistentEntityException("The idioma with id " + id + " no longer exists.");
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
            Idioma idioma;
            try {
                idioma = em.getReference(Idioma.class, id);
                idioma.getCodigo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The idioma with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Libro> libroListOrphanCheck = idioma.getLibroList();
            for (Libro libroListOrphanCheckLibro : libroListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Idioma (" + idioma + ") cannot be destroyed since the Libro " + libroListOrphanCheckLibro + " in its libroList field has a non-nullable idioma field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(idioma);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Idioma> findIdiomaEntities() {
        return findIdiomaEntities(true, -1, -1);
    }

    public List<Idioma> findIdiomaEntities(int maxResults, int firstResult) {
        return findIdiomaEntities(false, maxResults, firstResult);
    }

    private List<Idioma> findIdiomaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Idioma.class));
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

    public Idioma findIdioma(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Idioma.class, id);
        } finally {
            em.close();
        }
    }

    public int getIdiomaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Idioma> rt = cq.from(Idioma.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
