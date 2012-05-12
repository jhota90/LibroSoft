/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DaoJPA;

import DaoJPA.exceptions.IllegalOrphanException;
import DaoJPA.exceptions.NonexistentEntityException;
import DaoJPA.exceptions.PreexistingEntityException;
import Entidades.Nacionalidad;
import java.io.Serializable;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entidades.Editorial;
import java.util.ArrayList;
import java.util.List;
import Entidades.Autor;

/**
 *
 * @author JHONATHAN
 */
public class NacionalidadJpaController implements Serializable {

    public NacionalidadJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Nacionalidad nacionalidad) throws PreexistingEntityException, Exception {
        if (nacionalidad.getEditorialList() == null) {
            nacionalidad.setEditorialList(new ArrayList<Editorial>());
        }
        if (nacionalidad.getAutorList() == null) {
            nacionalidad.setAutorList(new ArrayList<Autor>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Editorial> attachedEditorialList = new ArrayList<Editorial>();
            for (Editorial editorialListEditorialToAttach : nacionalidad.getEditorialList()) {
                editorialListEditorialToAttach = em.getReference(editorialListEditorialToAttach.getClass(), editorialListEditorialToAttach.getCodigo());
                attachedEditorialList.add(editorialListEditorialToAttach);
            }
            nacionalidad.setEditorialList(attachedEditorialList);
            List<Autor> attachedAutorList = new ArrayList<Autor>();
            for (Autor autorListAutorToAttach : nacionalidad.getAutorList()) {
                autorListAutorToAttach = em.getReference(autorListAutorToAttach.getClass(), autorListAutorToAttach.getCodigo());
                attachedAutorList.add(autorListAutorToAttach);
            }
            nacionalidad.setAutorList(attachedAutorList);
            em.persist(nacionalidad);
            for (Editorial editorialListEditorial : nacionalidad.getEditorialList()) {
                Nacionalidad oldPaisOrigenOfEditorialListEditorial = editorialListEditorial.getPaisOrigen();
                editorialListEditorial.setPaisOrigen(nacionalidad);
                editorialListEditorial = em.merge(editorialListEditorial);
                if (oldPaisOrigenOfEditorialListEditorial != null) {
                    oldPaisOrigenOfEditorialListEditorial.getEditorialList().remove(editorialListEditorial);
                    oldPaisOrigenOfEditorialListEditorial = em.merge(oldPaisOrigenOfEditorialListEditorial);
                }
            }
            for (Autor autorListAutor : nacionalidad.getAutorList()) {
                Nacionalidad oldCodigoNacionalidadOfAutorListAutor = autorListAutor.getCodigoNacionalidad();
                autorListAutor.setCodigoNacionalidad(nacionalidad);
                autorListAutor = em.merge(autorListAutor);
                if (oldCodigoNacionalidadOfAutorListAutor != null) {
                    oldCodigoNacionalidadOfAutorListAutor.getAutorList().remove(autorListAutor);
                    oldCodigoNacionalidadOfAutorListAutor = em.merge(oldCodigoNacionalidadOfAutorListAutor);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findNacionalidad(nacionalidad.getCodigo()) != null) {
                throw new PreexistingEntityException("Nacionalidad " + nacionalidad + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Nacionalidad nacionalidad) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Nacionalidad persistentNacionalidad = em.find(Nacionalidad.class, nacionalidad.getCodigo());
            List<Editorial> editorialListOld = persistentNacionalidad.getEditorialList();
            List<Editorial> editorialListNew = nacionalidad.getEditorialList();
            List<Autor> autorListOld = persistentNacionalidad.getAutorList();
            List<Autor> autorListNew = nacionalidad.getAutorList();
            List<String> illegalOrphanMessages = null;
            for (Editorial editorialListOldEditorial : editorialListOld) {
                if (!editorialListNew.contains(editorialListOldEditorial)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Editorial " + editorialListOldEditorial + " since its paisOrigen field is not nullable.");
                }
            }
            for (Autor autorListOldAutor : autorListOld) {
                if (!autorListNew.contains(autorListOldAutor)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Autor " + autorListOldAutor + " since its codigoNacionalidad field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Editorial> attachedEditorialListNew = new ArrayList<Editorial>();
            for (Editorial editorialListNewEditorialToAttach : editorialListNew) {
                editorialListNewEditorialToAttach = em.getReference(editorialListNewEditorialToAttach.getClass(), editorialListNewEditorialToAttach.getCodigo());
                attachedEditorialListNew.add(editorialListNewEditorialToAttach);
            }
            editorialListNew = attachedEditorialListNew;
            nacionalidad.setEditorialList(editorialListNew);
            List<Autor> attachedAutorListNew = new ArrayList<Autor>();
            for (Autor autorListNewAutorToAttach : autorListNew) {
                autorListNewAutorToAttach = em.getReference(autorListNewAutorToAttach.getClass(), autorListNewAutorToAttach.getCodigo());
                attachedAutorListNew.add(autorListNewAutorToAttach);
            }
            autorListNew = attachedAutorListNew;
            nacionalidad.setAutorList(autorListNew);
            nacionalidad = em.merge(nacionalidad);
            for (Editorial editorialListNewEditorial : editorialListNew) {
                if (!editorialListOld.contains(editorialListNewEditorial)) {
                    Nacionalidad oldPaisOrigenOfEditorialListNewEditorial = editorialListNewEditorial.getPaisOrigen();
                    editorialListNewEditorial.setPaisOrigen(nacionalidad);
                    editorialListNewEditorial = em.merge(editorialListNewEditorial);
                    if (oldPaisOrigenOfEditorialListNewEditorial != null && !oldPaisOrigenOfEditorialListNewEditorial.equals(nacionalidad)) {
                        oldPaisOrigenOfEditorialListNewEditorial.getEditorialList().remove(editorialListNewEditorial);
                        oldPaisOrigenOfEditorialListNewEditorial = em.merge(oldPaisOrigenOfEditorialListNewEditorial);
                    }
                }
            }
            for (Autor autorListNewAutor : autorListNew) {
                if (!autorListOld.contains(autorListNewAutor)) {
                    Nacionalidad oldCodigoNacionalidadOfAutorListNewAutor = autorListNewAutor.getCodigoNacionalidad();
                    autorListNewAutor.setCodigoNacionalidad(nacionalidad);
                    autorListNewAutor = em.merge(autorListNewAutor);
                    if (oldCodigoNacionalidadOfAutorListNewAutor != null && !oldCodigoNacionalidadOfAutorListNewAutor.equals(nacionalidad)) {
                        oldCodigoNacionalidadOfAutorListNewAutor.getAutorList().remove(autorListNewAutor);
                        oldCodigoNacionalidadOfAutorListNewAutor = em.merge(oldCodigoNacionalidadOfAutorListNewAutor);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = nacionalidad.getCodigo();
                if (findNacionalidad(id) == null) {
                    throw new NonexistentEntityException("The nacionalidad with id " + id + " no longer exists.");
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
            Nacionalidad nacionalidad;
            try {
                nacionalidad = em.getReference(Nacionalidad.class, id);
                nacionalidad.getCodigo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The nacionalidad with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Editorial> editorialListOrphanCheck = nacionalidad.getEditorialList();
            for (Editorial editorialListOrphanCheckEditorial : editorialListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Nacionalidad (" + nacionalidad + ") cannot be destroyed since the Editorial " + editorialListOrphanCheckEditorial + " in its editorialList field has a non-nullable paisOrigen field.");
            }
            List<Autor> autorListOrphanCheck = nacionalidad.getAutorList();
            for (Autor autorListOrphanCheckAutor : autorListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Nacionalidad (" + nacionalidad + ") cannot be destroyed since the Autor " + autorListOrphanCheckAutor + " in its autorList field has a non-nullable codigoNacionalidad field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(nacionalidad);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Nacionalidad> findNacionalidadEntities() {
        return findNacionalidadEntities(true, -1, -1);
    }

    public List<Nacionalidad> findNacionalidadEntities(int maxResults, int firstResult) {
        return findNacionalidadEntities(false, maxResults, firstResult);
    }

    private List<Nacionalidad> findNacionalidadEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Nacionalidad.class));
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

    public Nacionalidad findNacionalidad(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Nacionalidad.class, id);
        } finally {
            em.close();
        }
    }

    public int getNacionalidadCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Nacionalidad> rt = cq.from(Nacionalidad.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
