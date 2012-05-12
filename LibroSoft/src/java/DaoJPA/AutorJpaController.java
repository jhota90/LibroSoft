/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DaoJPA;

import DaoJPA.exceptions.NonexistentEntityException;
import DaoJPA.exceptions.PreexistingEntityException;
import Entidades.Autor;
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
public class AutorJpaController implements Serializable {

    public AutorJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Autor autor) throws PreexistingEntityException, Exception {
        if (autor.getLibroList() == null) {
            autor.setLibroList(new ArrayList<Libro>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Nacionalidad codigoNacionalidad = autor.getCodigoNacionalidad();
            if (codigoNacionalidad != null) {
                codigoNacionalidad = em.getReference(codigoNacionalidad.getClass(), codigoNacionalidad.getCodigo());
                autor.setCodigoNacionalidad(codigoNacionalidad);
            }
            List<Libro> attachedLibroList = new ArrayList<Libro>();
            for (Libro libroListLibroToAttach : autor.getLibroList()) {
                libroListLibroToAttach = em.getReference(libroListLibroToAttach.getClass(), libroListLibroToAttach.getIsbn());
                attachedLibroList.add(libroListLibroToAttach);
            }
            autor.setLibroList(attachedLibroList);
            em.persist(autor);
            if (codigoNacionalidad != null) {
                codigoNacionalidad.getAutorList().add(autor);
                codigoNacionalidad = em.merge(codigoNacionalidad);
            }
            for (Libro libroListLibro : autor.getLibroList()) {
                libroListLibro.getAutorList().add(autor);
                libroListLibro = em.merge(libroListLibro);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findAutor(autor.getCodigo()) != null) {
                throw new PreexistingEntityException("Autor " + autor + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Autor autor) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Autor persistentAutor = em.find(Autor.class, autor.getCodigo());
            Nacionalidad codigoNacionalidadOld = persistentAutor.getCodigoNacionalidad();
            Nacionalidad codigoNacionalidadNew = autor.getCodigoNacionalidad();
            List<Libro> libroListOld = persistentAutor.getLibroList();
            List<Libro> libroListNew = autor.getLibroList();
            if (codigoNacionalidadNew != null) {
                codigoNacionalidadNew = em.getReference(codigoNacionalidadNew.getClass(), codigoNacionalidadNew.getCodigo());
                autor.setCodigoNacionalidad(codigoNacionalidadNew);
            }
            List<Libro> attachedLibroListNew = new ArrayList<Libro>();
            for (Libro libroListNewLibroToAttach : libroListNew) {
                libroListNewLibroToAttach = em.getReference(libroListNewLibroToAttach.getClass(), libroListNewLibroToAttach.getIsbn());
                attachedLibroListNew.add(libroListNewLibroToAttach);
            }
            libroListNew = attachedLibroListNew;
            autor.setLibroList(libroListNew);
            autor = em.merge(autor);
            if (codigoNacionalidadOld != null && !codigoNacionalidadOld.equals(codigoNacionalidadNew)) {
                codigoNacionalidadOld.getAutorList().remove(autor);
                codigoNacionalidadOld = em.merge(codigoNacionalidadOld);
            }
            if (codigoNacionalidadNew != null && !codigoNacionalidadNew.equals(codigoNacionalidadOld)) {
                codigoNacionalidadNew.getAutorList().add(autor);
                codigoNacionalidadNew = em.merge(codigoNacionalidadNew);
            }
            for (Libro libroListOldLibro : libroListOld) {
                if (!libroListNew.contains(libroListOldLibro)) {
                    libroListOldLibro.getAutorList().remove(autor);
                    libroListOldLibro = em.merge(libroListOldLibro);
                }
            }
            for (Libro libroListNewLibro : libroListNew) {
                if (!libroListOld.contains(libroListNewLibro)) {
                    libroListNewLibro.getAutorList().add(autor);
                    libroListNewLibro = em.merge(libroListNewLibro);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = autor.getCodigo();
                if (findAutor(id) == null) {
                    throw new NonexistentEntityException("The autor with id " + id + " no longer exists.");
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
            Autor autor;
            try {
                autor = em.getReference(Autor.class, id);
                autor.getCodigo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The autor with id " + id + " no longer exists.", enfe);
            }
            Nacionalidad codigoNacionalidad = autor.getCodigoNacionalidad();
            if (codigoNacionalidad != null) {
                codigoNacionalidad.getAutorList().remove(autor);
                codigoNacionalidad = em.merge(codigoNacionalidad);
            }
            List<Libro> libroList = autor.getLibroList();
            for (Libro libroListLibro : libroList) {
                libroListLibro.getAutorList().remove(autor);
                libroListLibro = em.merge(libroListLibro);
            }
            em.remove(autor);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Autor> findAutorEntities() {
        return findAutorEntities(true, -1, -1);
    }

    public List<Autor> findAutorEntities(int maxResults, int firstResult) {
        return findAutorEntities(false, maxResults, firstResult);
    }

    private List<Autor> findAutorEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Autor.class));
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

    public Autor findAutor(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Autor.class, id);
        } finally {
            em.close();
        }
    }

    public int getAutorCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Autor> rt = cq.from(Autor.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
