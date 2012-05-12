/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DaoJPA;

import DaoJPA.exceptions.NonexistentEntityException;
import DaoJPA.exceptions.PreexistingEntityException;
import Entidades.LibroDigital;
import java.io.Serializable;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entidades.Descarga;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JHONATHAN
 */
public class LibroDigitalJpaController implements Serializable {

    public LibroDigitalJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(LibroDigital libroDigital) throws PreexistingEntityException, Exception {
        if (libroDigital.getDescargaList() == null) {
            libroDigital.setDescargaList(new ArrayList<Descarga>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Descarga> attachedDescargaList = new ArrayList<Descarga>();
            for (Descarga descargaListDescargaToAttach : libroDigital.getDescargaList()) {
                descargaListDescargaToAttach = em.getReference(descargaListDescargaToAttach.getClass(), descargaListDescargaToAttach.getCodigo());
                attachedDescargaList.add(descargaListDescargaToAttach);
            }
            libroDigital.setDescargaList(attachedDescargaList);
            em.persist(libroDigital);
            for (Descarga descargaListDescarga : libroDigital.getDescargaList()) {
                LibroDigital oldIsbnOfDescargaListDescarga = descargaListDescarga.getIsbn();
                descargaListDescarga.setIsbn(libroDigital);
                descargaListDescarga = em.merge(descargaListDescarga);
                if (oldIsbnOfDescargaListDescarga != null) {
                    oldIsbnOfDescargaListDescarga.getDescargaList().remove(descargaListDescarga);
                    oldIsbnOfDescargaListDescarga = em.merge(oldIsbnOfDescargaListDescarga);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findLibroDigital(libroDigital.getIsbn()) != null) {
                throw new PreexistingEntityException("LibroDigital " + libroDigital + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(LibroDigital libroDigital) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            LibroDigital persistentLibroDigital = em.find(LibroDigital.class, libroDigital.getIsbn());
            List<Descarga> descargaListOld = persistentLibroDigital.getDescargaList();
            List<Descarga> descargaListNew = libroDigital.getDescargaList();
            List<Descarga> attachedDescargaListNew = new ArrayList<Descarga>();
            for (Descarga descargaListNewDescargaToAttach : descargaListNew) {
                descargaListNewDescargaToAttach = em.getReference(descargaListNewDescargaToAttach.getClass(), descargaListNewDescargaToAttach.getCodigo());
                attachedDescargaListNew.add(descargaListNewDescargaToAttach);
            }
            descargaListNew = attachedDescargaListNew;
            libroDigital.setDescargaList(descargaListNew);
            libroDigital = em.merge(libroDigital);
            for (Descarga descargaListOldDescarga : descargaListOld) {
                if (!descargaListNew.contains(descargaListOldDescarga)) {
                    descargaListOldDescarga.setIsbn(null);
                    descargaListOldDescarga = em.merge(descargaListOldDescarga);
                }
            }
            for (Descarga descargaListNewDescarga : descargaListNew) {
                if (!descargaListOld.contains(descargaListNewDescarga)) {
                    LibroDigital oldIsbnOfDescargaListNewDescarga = descargaListNewDescarga.getIsbn();
                    descargaListNewDescarga.setIsbn(libroDigital);
                    descargaListNewDescarga = em.merge(descargaListNewDescarga);
                    if (oldIsbnOfDescargaListNewDescarga != null && !oldIsbnOfDescargaListNewDescarga.equals(libroDigital)) {
                        oldIsbnOfDescargaListNewDescarga.getDescargaList().remove(descargaListNewDescarga);
                        oldIsbnOfDescargaListNewDescarga = em.merge(oldIsbnOfDescargaListNewDescarga);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = libroDigital.getIsbn();
                if (findLibroDigital(id) == null) {
                    throw new NonexistentEntityException("The libroDigital with id " + id + " no longer exists.");
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
            LibroDigital libroDigital;
            try {
                libroDigital = em.getReference(LibroDigital.class, id);
                libroDigital.getIsbn();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The libroDigital with id " + id + " no longer exists.", enfe);
            }
            List<Descarga> descargaList = libroDigital.getDescargaList();
            for (Descarga descargaListDescarga : descargaList) {
                descargaListDescarga.setIsbn(null);
                descargaListDescarga = em.merge(descargaListDescarga);
            }
            em.remove(libroDigital);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<LibroDigital> findLibroDigitalEntities() {
        return findLibroDigitalEntities(true, -1, -1);
    }

    public List<LibroDigital> findLibroDigitalEntities(int maxResults, int firstResult) {
        return findLibroDigitalEntities(false, maxResults, firstResult);
    }

    private List<LibroDigital> findLibroDigitalEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(LibroDigital.class));
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

    public LibroDigital findLibroDigital(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(LibroDigital.class, id);
        } finally {
            em.close();
        }
    }

    public int getLibroDigitalCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<LibroDigital> rt = cq.from(LibroDigital.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
