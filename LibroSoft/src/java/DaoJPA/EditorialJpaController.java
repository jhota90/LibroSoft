/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DaoJPA;

import DaoJPA.exceptions.IllegalOrphanException;
import DaoJPA.exceptions.NonexistentEntityException;
import DaoJPA.exceptions.PreexistingEntityException;
import Entidades.Editorial;
import java.io.Serializable;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entidades.Nacionalidad;
import Entidades.Libro;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JHONATHAN
 */
public class EditorialJpaController implements Serializable {

    public EditorialJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Editorial editorial) throws PreexistingEntityException, Exception {
        if (editorial.getLibroList() == null) {
            editorial.setLibroList(new ArrayList<Libro>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Nacionalidad paisOrigen = editorial.getPaisOrigen();
            if (paisOrigen != null) {
                paisOrigen = em.getReference(paisOrigen.getClass(), paisOrigen.getCodigo());
                editorial.setPaisOrigen(paisOrigen);
            }
            List<Libro> attachedLibroList = new ArrayList<Libro>();
            for (Libro libroListLibroToAttach : editorial.getLibroList()) {
                libroListLibroToAttach = em.getReference(libroListLibroToAttach.getClass(), libroListLibroToAttach.getIsbn());
                attachedLibroList.add(libroListLibroToAttach);
            }
            editorial.setLibroList(attachedLibroList);
            em.persist(editorial);
            if (paisOrigen != null) {
                paisOrigen.getEditorialList().add(editorial);
                paisOrigen = em.merge(paisOrigen);
            }
            for (Libro libroListLibro : editorial.getLibroList()) {
                Editorial oldEditorialOfLibroListLibro = libroListLibro.getEditorial();
                libroListLibro.setEditorial(editorial);
                libroListLibro = em.merge(libroListLibro);
                if (oldEditorialOfLibroListLibro != null) {
                    oldEditorialOfLibroListLibro.getLibroList().remove(libroListLibro);
                    oldEditorialOfLibroListLibro = em.merge(oldEditorialOfLibroListLibro);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findEditorial(editorial.getCodigo()) != null) {
                throw new PreexistingEntityException("Editorial " + editorial + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Editorial editorial) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Editorial persistentEditorial = em.find(Editorial.class, editorial.getCodigo());
            Nacionalidad paisOrigenOld = persistentEditorial.getPaisOrigen();
            Nacionalidad paisOrigenNew = editorial.getPaisOrigen();
            List<Libro> libroListOld = persistentEditorial.getLibroList();
            List<Libro> libroListNew = editorial.getLibroList();
            List<String> illegalOrphanMessages = null;
            for (Libro libroListOldLibro : libroListOld) {
                if (!libroListNew.contains(libroListOldLibro)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Libro " + libroListOldLibro + " since its editorial field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (paisOrigenNew != null) {
                paisOrigenNew = em.getReference(paisOrigenNew.getClass(), paisOrigenNew.getCodigo());
                editorial.setPaisOrigen(paisOrigenNew);
            }
            List<Libro> attachedLibroListNew = new ArrayList<Libro>();
            for (Libro libroListNewLibroToAttach : libroListNew) {
                libroListNewLibroToAttach = em.getReference(libroListNewLibroToAttach.getClass(), libroListNewLibroToAttach.getIsbn());
                attachedLibroListNew.add(libroListNewLibroToAttach);
            }
            libroListNew = attachedLibroListNew;
            editorial.setLibroList(libroListNew);
            editorial = em.merge(editorial);
            if (paisOrigenOld != null && !paisOrigenOld.equals(paisOrigenNew)) {
                paisOrigenOld.getEditorialList().remove(editorial);
                paisOrigenOld = em.merge(paisOrigenOld);
            }
            if (paisOrigenNew != null && !paisOrigenNew.equals(paisOrigenOld)) {
                paisOrigenNew.getEditorialList().add(editorial);
                paisOrigenNew = em.merge(paisOrigenNew);
            }
            for (Libro libroListNewLibro : libroListNew) {
                if (!libroListOld.contains(libroListNewLibro)) {
                    Editorial oldEditorialOfLibroListNewLibro = libroListNewLibro.getEditorial();
                    libroListNewLibro.setEditorial(editorial);
                    libroListNewLibro = em.merge(libroListNewLibro);
                    if (oldEditorialOfLibroListNewLibro != null && !oldEditorialOfLibroListNewLibro.equals(editorial)) {
                        oldEditorialOfLibroListNewLibro.getLibroList().remove(libroListNewLibro);
                        oldEditorialOfLibroListNewLibro = em.merge(oldEditorialOfLibroListNewLibro);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = editorial.getCodigo();
                if (findEditorial(id) == null) {
                    throw new NonexistentEntityException("The editorial with id " + id + " no longer exists.");
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
            Editorial editorial;
            try {
                editorial = em.getReference(Editorial.class, id);
                editorial.getCodigo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The editorial with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Libro> libroListOrphanCheck = editorial.getLibroList();
            for (Libro libroListOrphanCheckLibro : libroListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Editorial (" + editorial + ") cannot be destroyed since the Libro " + libroListOrphanCheckLibro + " in its libroList field has a non-nullable editorial field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Nacionalidad paisOrigen = editorial.getPaisOrigen();
            if (paisOrigen != null) {
                paisOrigen.getEditorialList().remove(editorial);
                paisOrigen = em.merge(paisOrigen);
            }
            em.remove(editorial);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Editorial> findEditorialEntities() {
        return findEditorialEntities(true, -1, -1);
    }

    public List<Editorial> findEditorialEntities(int maxResults, int firstResult) {
        return findEditorialEntities(false, maxResults, firstResult);
    }

    private List<Editorial> findEditorialEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Editorial.class));
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

    public Editorial findEditorial(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Editorial.class, id);
        } finally {
            em.close();
        }
    }

    public int getEditorialCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Editorial> rt = cq.from(Editorial.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
