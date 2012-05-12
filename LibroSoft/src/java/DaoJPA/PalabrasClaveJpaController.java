/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DaoJPA;

import DaoJPA.exceptions.NonexistentEntityException;
import DaoJPA.exceptions.PreexistingEntityException;
import Entidades.PalabrasClave;
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
public class PalabrasClaveJpaController implements Serializable {

    public PalabrasClaveJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(PalabrasClave palabrasClave) throws PreexistingEntityException, Exception {
        if (palabrasClave.getLibroList() == null) {
            palabrasClave.setLibroList(new ArrayList<Libro>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Libro> attachedLibroList = new ArrayList<Libro>();
            for (Libro libroListLibroToAttach : palabrasClave.getLibroList()) {
                libroListLibroToAttach = em.getReference(libroListLibroToAttach.getClass(), libroListLibroToAttach.getIsbn());
                attachedLibroList.add(libroListLibroToAttach);
            }
            palabrasClave.setLibroList(attachedLibroList);
            em.persist(palabrasClave);
            for (Libro libroListLibro : palabrasClave.getLibroList()) {
                libroListLibro.getPalabrasClaveList().add(palabrasClave);
                libroListLibro = em.merge(libroListLibro);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findPalabrasClave(palabrasClave.getPalabra()) != null) {
                throw new PreexistingEntityException("PalabrasClave " + palabrasClave + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(PalabrasClave palabrasClave) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            PalabrasClave persistentPalabrasClave = em.find(PalabrasClave.class, palabrasClave.getPalabra());
            List<Libro> libroListOld = persistentPalabrasClave.getLibroList();
            List<Libro> libroListNew = palabrasClave.getLibroList();
            List<Libro> attachedLibroListNew = new ArrayList<Libro>();
            for (Libro libroListNewLibroToAttach : libroListNew) {
                libroListNewLibroToAttach = em.getReference(libroListNewLibroToAttach.getClass(), libroListNewLibroToAttach.getIsbn());
                attachedLibroListNew.add(libroListNewLibroToAttach);
            }
            libroListNew = attachedLibroListNew;
            palabrasClave.setLibroList(libroListNew);
            palabrasClave = em.merge(palabrasClave);
            for (Libro libroListOldLibro : libroListOld) {
                if (!libroListNew.contains(libroListOldLibro)) {
                    libroListOldLibro.getPalabrasClaveList().remove(palabrasClave);
                    libroListOldLibro = em.merge(libroListOldLibro);
                }
            }
            for (Libro libroListNewLibro : libroListNew) {
                if (!libroListOld.contains(libroListNewLibro)) {
                    libroListNewLibro.getPalabrasClaveList().add(palabrasClave);
                    libroListNewLibro = em.merge(libroListNewLibro);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = palabrasClave.getPalabra();
                if (findPalabrasClave(id) == null) {
                    throw new NonexistentEntityException("The palabrasClave with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            PalabrasClave palabrasClave;
            try {
                palabrasClave = em.getReference(PalabrasClave.class, id);
                palabrasClave.getPalabra();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The palabrasClave with id " + id + " no longer exists.", enfe);
            }
            List<Libro> libroList = palabrasClave.getLibroList();
            for (Libro libroListLibro : libroList) {
                libroListLibro.getPalabrasClaveList().remove(palabrasClave);
                libroListLibro = em.merge(libroListLibro);
            }
            em.remove(palabrasClave);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<PalabrasClave> findPalabrasClaveEntities() {
        return findPalabrasClaveEntities(true, -1, -1);
    }

    public List<PalabrasClave> findPalabrasClaveEntities(int maxResults, int firstResult) {
        return findPalabrasClaveEntities(false, maxResults, firstResult);
    }

    private List<PalabrasClave> findPalabrasClaveEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(PalabrasClave.class));
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

    public PalabrasClave findPalabrasClave(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(PalabrasClave.class, id);
        } finally {
            em.close();
        }
    }

    public int getPalabrasClaveCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<PalabrasClave> rt = cq.from(PalabrasClave.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
