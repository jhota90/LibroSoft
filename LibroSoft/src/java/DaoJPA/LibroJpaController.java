/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DaoJPA;

import DaoJPA.exceptions.NonexistentEntityException;
import DaoJPA.exceptions.PreexistingEntityException;
import Entidades.Libro;
import java.io.Serializable;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entidades.Idioma;
import Entidades.AreaDeConocimiento;
import Entidades.Editorial;
import Entidades.PalabrasClave;
import java.util.ArrayList;
import java.util.List;
import Entidades.Autor;

/**
 *
 * @author JHONATHAN
 */
public class LibroJpaController implements Serializable {

    public LibroJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Libro libro) throws PreexistingEntityException, Exception {
        if (libro.getPalabrasClaveList() == null) {
            libro.setPalabrasClaveList(new ArrayList<PalabrasClave>());
        }
        if (libro.getAutorList() == null) {
            libro.setAutorList(new ArrayList<Autor>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Idioma idioma = libro.getIdioma();
            if (idioma != null) {
                idioma = em.getReference(idioma.getClass(), idioma.getCodigo());
                libro.setIdioma(idioma);
            }
            AreaDeConocimiento areaConocimiento = libro.getAreaConocimiento();
            if (areaConocimiento != null) {
                areaConocimiento = em.getReference(areaConocimiento.getClass(), areaConocimiento.getCodigo());
                libro.setAreaConocimiento(areaConocimiento);
            }
            Editorial editorial = libro.getEditorial();
            if (editorial != null) {
                editorial = em.getReference(editorial.getClass(), editorial.getCodigo());
                libro.setEditorial(editorial);
            }
            List<PalabrasClave> attachedPalabrasClaveList = new ArrayList<PalabrasClave>();
            for (PalabrasClave palabrasClaveListPalabrasClaveToAttach : libro.getPalabrasClaveList()) {
                palabrasClaveListPalabrasClaveToAttach = em.getReference(palabrasClaveListPalabrasClaveToAttach.getClass(), palabrasClaveListPalabrasClaveToAttach.getPalabra());
                attachedPalabrasClaveList.add(palabrasClaveListPalabrasClaveToAttach);
            }
            libro.setPalabrasClaveList(attachedPalabrasClaveList);
            List<Autor> attachedAutorList = new ArrayList<Autor>();
            for (Autor autorListAutorToAttach : libro.getAutorList()) {
                autorListAutorToAttach = em.getReference(autorListAutorToAttach.getClass(), autorListAutorToAttach.getCodigo());
                attachedAutorList.add(autorListAutorToAttach);
            }
            libro.setAutorList(attachedAutorList);
            em.persist(libro);
            if (idioma != null) {
                idioma.getLibroList().add(libro);
                idioma = em.merge(idioma);
            }
            if (areaConocimiento != null) {
                areaConocimiento.getLibroList().add(libro);
                areaConocimiento = em.merge(areaConocimiento);
            }
            if (editorial != null) {
                editorial.getLibroList().add(libro);
                editorial = em.merge(editorial);
            }
            for (PalabrasClave palabrasClaveListPalabrasClave : libro.getPalabrasClaveList()) {
                palabrasClaveListPalabrasClave.getLibroList().add(libro);
                palabrasClaveListPalabrasClave = em.merge(palabrasClaveListPalabrasClave);
            }
            for (Autor autorListAutor : libro.getAutorList()) {
                autorListAutor.getLibroList().add(libro);
                autorListAutor = em.merge(autorListAutor);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findLibro(libro.getIsbn()) != null) {
                throw new PreexistingEntityException("Libro " + libro + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Libro libro) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Libro persistentLibro = em.find(Libro.class, libro.getIsbn());
            Idioma idiomaOld = persistentLibro.getIdioma();
            Idioma idiomaNew = libro.getIdioma();
            AreaDeConocimiento areaConocimientoOld = persistentLibro.getAreaConocimiento();
            AreaDeConocimiento areaConocimientoNew = libro.getAreaConocimiento();
            Editorial editorialOld = persistentLibro.getEditorial();
            Editorial editorialNew = libro.getEditorial();
            List<PalabrasClave> palabrasClaveListOld = persistentLibro.getPalabrasClaveList();
            List<PalabrasClave> palabrasClaveListNew = libro.getPalabrasClaveList();
            List<Autor> autorListOld = persistentLibro.getAutorList();
            List<Autor> autorListNew = libro.getAutorList();
            if (idiomaNew != null) {
                idiomaNew = em.getReference(idiomaNew.getClass(), idiomaNew.getCodigo());
                libro.setIdioma(idiomaNew);
            }
            if (areaConocimientoNew != null) {
                areaConocimientoNew = em.getReference(areaConocimientoNew.getClass(), areaConocimientoNew.getCodigo());
                libro.setAreaConocimiento(areaConocimientoNew);
            }
            if (editorialNew != null) {
                editorialNew = em.getReference(editorialNew.getClass(), editorialNew.getCodigo());
                libro.setEditorial(editorialNew);
            }
            List<PalabrasClave> attachedPalabrasClaveListNew = new ArrayList<PalabrasClave>();
            for (PalabrasClave palabrasClaveListNewPalabrasClaveToAttach : palabrasClaveListNew) {
                palabrasClaveListNewPalabrasClaveToAttach = em.getReference(palabrasClaveListNewPalabrasClaveToAttach.getClass(), palabrasClaveListNewPalabrasClaveToAttach.getPalabra());
                attachedPalabrasClaveListNew.add(palabrasClaveListNewPalabrasClaveToAttach);
            }
            palabrasClaveListNew = attachedPalabrasClaveListNew;
            libro.setPalabrasClaveList(palabrasClaveListNew);
            List<Autor> attachedAutorListNew = new ArrayList<Autor>();
            for (Autor autorListNewAutorToAttach : autorListNew) {
                autorListNewAutorToAttach = em.getReference(autorListNewAutorToAttach.getClass(), autorListNewAutorToAttach.getCodigo());
                attachedAutorListNew.add(autorListNewAutorToAttach);
            }
            autorListNew = attachedAutorListNew;
            libro.setAutorList(autorListNew);
            libro = em.merge(libro);
            if (idiomaOld != null && !idiomaOld.equals(idiomaNew)) {
                idiomaOld.getLibroList().remove(libro);
                idiomaOld = em.merge(idiomaOld);
            }
            if (idiomaNew != null && !idiomaNew.equals(idiomaOld)) {
                idiomaNew.getLibroList().add(libro);
                idiomaNew = em.merge(idiomaNew);
            }
            if (areaConocimientoOld != null && !areaConocimientoOld.equals(areaConocimientoNew)) {
                areaConocimientoOld.getLibroList().remove(libro);
                areaConocimientoOld = em.merge(areaConocimientoOld);
            }
            if (areaConocimientoNew != null && !areaConocimientoNew.equals(areaConocimientoOld)) {
                areaConocimientoNew.getLibroList().add(libro);
                areaConocimientoNew = em.merge(areaConocimientoNew);
            }
            if (editorialOld != null && !editorialOld.equals(editorialNew)) {
                editorialOld.getLibroList().remove(libro);
                editorialOld = em.merge(editorialOld);
            }
            if (editorialNew != null && !editorialNew.equals(editorialOld)) {
                editorialNew.getLibroList().add(libro);
                editorialNew = em.merge(editorialNew);
            }
            for (PalabrasClave palabrasClaveListOldPalabrasClave : palabrasClaveListOld) {
                if (!palabrasClaveListNew.contains(palabrasClaveListOldPalabrasClave)) {
                    palabrasClaveListOldPalabrasClave.getLibroList().remove(libro);
                    palabrasClaveListOldPalabrasClave = em.merge(palabrasClaveListOldPalabrasClave);
                }
            }
            for (PalabrasClave palabrasClaveListNewPalabrasClave : palabrasClaveListNew) {
                if (!palabrasClaveListOld.contains(palabrasClaveListNewPalabrasClave)) {
                    palabrasClaveListNewPalabrasClave.getLibroList().add(libro);
                    palabrasClaveListNewPalabrasClave = em.merge(palabrasClaveListNewPalabrasClave);
                }
            }
            for (Autor autorListOldAutor : autorListOld) {
                if (!autorListNew.contains(autorListOldAutor)) {
                    autorListOldAutor.getLibroList().remove(libro);
                    autorListOldAutor = em.merge(autorListOldAutor);
                }
            }
            for (Autor autorListNewAutor : autorListNew) {
                if (!autorListOld.contains(autorListNewAutor)) {
                    autorListNewAutor.getLibroList().add(libro);
                    autorListNewAutor = em.merge(autorListNewAutor);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = libro.getIsbn();
                if (findLibro(id) == null) {
                    throw new NonexistentEntityException("The libro with id " + id + " no longer exists.");
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
            Libro libro;
            try {
                libro = em.getReference(Libro.class, id);
                libro.getIsbn();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The libro with id " + id + " no longer exists.", enfe);
            }
            Idioma idioma = libro.getIdioma();
            if (idioma != null) {
                idioma.getLibroList().remove(libro);
                idioma = em.merge(idioma);
            }
            AreaDeConocimiento areaConocimiento = libro.getAreaConocimiento();
            if (areaConocimiento != null) {
                areaConocimiento.getLibroList().remove(libro);
                areaConocimiento = em.merge(areaConocimiento);
            }
            Editorial editorial = libro.getEditorial();
            if (editorial != null) {
                editorial.getLibroList().remove(libro);
                editorial = em.merge(editorial);
            }
            List<PalabrasClave> palabrasClaveList = libro.getPalabrasClaveList();
            for (PalabrasClave palabrasClaveListPalabrasClave : palabrasClaveList) {
                palabrasClaveListPalabrasClave.getLibroList().remove(libro);
                palabrasClaveListPalabrasClave = em.merge(palabrasClaveListPalabrasClave);
            }
            List<Autor> autorList = libro.getAutorList();
            for (Autor autorListAutor : autorList) {
                autorListAutor.getLibroList().remove(libro);
                autorListAutor = em.merge(autorListAutor);
            }
            em.remove(libro);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Libro> findLibroEntities() {
        return findLibroEntities(true, -1, -1);
    }

    public List<Libro> findLibroEntities(int maxResults, int firstResult) {
        return findLibroEntities(false, maxResults, firstResult);
    }

    private List<Libro> findLibroEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Libro.class));
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

    public Libro findLibro(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Libro.class, id);
        } finally {
            em.close();
        }
    }

    public int getLibroCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Libro> rt = cq.from(Libro.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
