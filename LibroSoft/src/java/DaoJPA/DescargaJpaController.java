/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DaoJPA;

import DaoJPA.exceptions.NonexistentEntityException;
import Entidades.Descarga;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entidades.LibroDigital;
import Entidades.Usuario;

/**
 *
 * @author JHONATHAN
 */
public class DescargaJpaController implements Serializable {

    public DescargaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Descarga descarga) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            LibroDigital isbn = descarga.getIsbn();
            if (isbn != null) {
                isbn = em.getReference(isbn.getClass(), isbn.getIsbn());
                descarga.setIsbn(isbn);
            }
            Usuario idUsuario = descarga.getIdUsuario();
            if (idUsuario != null) {
                idUsuario = em.getReference(idUsuario.getClass(), idUsuario.getIdUsuario());
                descarga.setIdUsuario(idUsuario);
            }
            em.persist(descarga);
            if (isbn != null) {
                isbn.getDescargaList().add(descarga);
                isbn = em.merge(isbn);
            }
            if (idUsuario != null) {
                idUsuario.getDescargaList().add(descarga);
                idUsuario = em.merge(idUsuario);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Descarga descarga) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Descarga persistentDescarga = em.find(Descarga.class, descarga.getCodigo());
            LibroDigital isbnOld = persistentDescarga.getIsbn();
            LibroDigital isbnNew = descarga.getIsbn();
            Usuario idUsuarioOld = persistentDescarga.getIdUsuario();
            Usuario idUsuarioNew = descarga.getIdUsuario();
            if (isbnNew != null) {
                isbnNew = em.getReference(isbnNew.getClass(), isbnNew.getIsbn());
                descarga.setIsbn(isbnNew);
            }
            if (idUsuarioNew != null) {
                idUsuarioNew = em.getReference(idUsuarioNew.getClass(), idUsuarioNew.getIdUsuario());
                descarga.setIdUsuario(idUsuarioNew);
            }
            descarga = em.merge(descarga);
            if (isbnOld != null && !isbnOld.equals(isbnNew)) {
                isbnOld.getDescargaList().remove(descarga);
                isbnOld = em.merge(isbnOld);
            }
            if (isbnNew != null && !isbnNew.equals(isbnOld)) {
                isbnNew.getDescargaList().add(descarga);
                isbnNew = em.merge(isbnNew);
            }
            if (idUsuarioOld != null && !idUsuarioOld.equals(idUsuarioNew)) {
                idUsuarioOld.getDescargaList().remove(descarga);
                idUsuarioOld = em.merge(idUsuarioOld);
            }
            if (idUsuarioNew != null && !idUsuarioNew.equals(idUsuarioOld)) {
                idUsuarioNew.getDescargaList().add(descarga);
                idUsuarioNew = em.merge(idUsuarioNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = descarga.getCodigo();
                if (findDescarga(id) == null) {
                    throw new NonexistentEntityException("The descarga with id " + id + " no longer exists.");
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
            Descarga descarga;
            try {
                descarga = em.getReference(Descarga.class, id);
                descarga.getCodigo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The descarga with id " + id + " no longer exists.", enfe);
            }
            LibroDigital isbn = descarga.getIsbn();
            if (isbn != null) {
                isbn.getDescargaList().remove(descarga);
                isbn = em.merge(isbn);
            }
            Usuario idUsuario = descarga.getIdUsuario();
            if (idUsuario != null) {
                idUsuario.getDescargaList().remove(descarga);
                idUsuario = em.merge(idUsuario);
            }
            em.remove(descarga);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Descarga> findDescargaEntities() {
        return findDescargaEntities(true, -1, -1);
    }

    public List<Descarga> findDescargaEntities(int maxResults, int firstResult) {
        return findDescargaEntities(false, maxResults, firstResult);
    }

    private List<Descarga> findDescargaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Descarga.class));
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

    public Descarga findDescarga(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Descarga.class, id);
        } finally {
            em.close();
        }
    }

    public int getDescargaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Descarga> rt = cq.from(Descarga.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
