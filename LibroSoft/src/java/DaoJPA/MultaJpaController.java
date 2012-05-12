/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DaoJPA;

import DaoJPA.exceptions.NonexistentEntityException;
import DaoJPA.exceptions.PreexistingEntityException;
import Entidades.Multa;
import java.io.Serializable;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entidades.TipoMulta;
import Entidades.Ejemplar;
import Entidades.Usuario;
import Entidades.PagoMulta;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JHONATHAN
 */
public class MultaJpaController implements Serializable {

    public MultaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Multa multa) throws PreexistingEntityException, Exception {
        if (multa.getPagoMultaList() == null) {
            multa.setPagoMultaList(new ArrayList<PagoMulta>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TipoMulta tipoMulta = multa.getTipoMulta();
            if (tipoMulta != null) {
                tipoMulta = em.getReference(tipoMulta.getClass(), tipoMulta.getCodigo());
                multa.setTipoMulta(tipoMulta);
            }
            Ejemplar codigoEjemplar = multa.getCodigoEjemplar();
            if (codigoEjemplar != null) {
                codigoEjemplar = em.getReference(codigoEjemplar.getClass(), codigoEjemplar.getCodigo());
                multa.setCodigoEjemplar(codigoEjemplar);
            }
            Usuario idUsuario = multa.getIdUsuario();
            if (idUsuario != null) {
                idUsuario = em.getReference(idUsuario.getClass(), idUsuario.getIdUsuario());
                multa.setIdUsuario(idUsuario);
            }
            List<PagoMulta> attachedPagoMultaList = new ArrayList<PagoMulta>();
            for (PagoMulta pagoMultaListPagoMultaToAttach : multa.getPagoMultaList()) {
                pagoMultaListPagoMultaToAttach = em.getReference(pagoMultaListPagoMultaToAttach.getClass(), pagoMultaListPagoMultaToAttach.getCodigo());
                attachedPagoMultaList.add(pagoMultaListPagoMultaToAttach);
            }
            multa.setPagoMultaList(attachedPagoMultaList);
            em.persist(multa);
            if (tipoMulta != null) {
                tipoMulta.getMultaList().add(multa);
                tipoMulta = em.merge(tipoMulta);
            }
            if (codigoEjemplar != null) {
                codigoEjemplar.getMultaList().add(multa);
                codigoEjemplar = em.merge(codigoEjemplar);
            }
            if (idUsuario != null) {
                idUsuario.getMultaList().add(multa);
                idUsuario = em.merge(idUsuario);
            }
            for (PagoMulta pagoMultaListPagoMulta : multa.getPagoMultaList()) {
                Multa oldMultaOfPagoMultaListPagoMulta = pagoMultaListPagoMulta.getMulta();
                pagoMultaListPagoMulta.setMulta(multa);
                pagoMultaListPagoMulta = em.merge(pagoMultaListPagoMulta);
                if (oldMultaOfPagoMultaListPagoMulta != null) {
                    oldMultaOfPagoMultaListPagoMulta.getPagoMultaList().remove(pagoMultaListPagoMulta);
                    oldMultaOfPagoMultaListPagoMulta = em.merge(oldMultaOfPagoMultaListPagoMulta);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findMulta(multa.getCodigo()) != null) {
                throw new PreexistingEntityException("Multa " + multa + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Multa multa) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Multa persistentMulta = em.find(Multa.class, multa.getCodigo());
            TipoMulta tipoMultaOld = persistentMulta.getTipoMulta();
            TipoMulta tipoMultaNew = multa.getTipoMulta();
            Ejemplar codigoEjemplarOld = persistentMulta.getCodigoEjemplar();
            Ejemplar codigoEjemplarNew = multa.getCodigoEjemplar();
            Usuario idUsuarioOld = persistentMulta.getIdUsuario();
            Usuario idUsuarioNew = multa.getIdUsuario();
            List<PagoMulta> pagoMultaListOld = persistentMulta.getPagoMultaList();
            List<PagoMulta> pagoMultaListNew = multa.getPagoMultaList();
            if (tipoMultaNew != null) {
                tipoMultaNew = em.getReference(tipoMultaNew.getClass(), tipoMultaNew.getCodigo());
                multa.setTipoMulta(tipoMultaNew);
            }
            if (codigoEjemplarNew != null) {
                codigoEjemplarNew = em.getReference(codigoEjemplarNew.getClass(), codigoEjemplarNew.getCodigo());
                multa.setCodigoEjemplar(codigoEjemplarNew);
            }
            if (idUsuarioNew != null) {
                idUsuarioNew = em.getReference(idUsuarioNew.getClass(), idUsuarioNew.getIdUsuario());
                multa.setIdUsuario(idUsuarioNew);
            }
            List<PagoMulta> attachedPagoMultaListNew = new ArrayList<PagoMulta>();
            for (PagoMulta pagoMultaListNewPagoMultaToAttach : pagoMultaListNew) {
                pagoMultaListNewPagoMultaToAttach = em.getReference(pagoMultaListNewPagoMultaToAttach.getClass(), pagoMultaListNewPagoMultaToAttach.getCodigo());
                attachedPagoMultaListNew.add(pagoMultaListNewPagoMultaToAttach);
            }
            pagoMultaListNew = attachedPagoMultaListNew;
            multa.setPagoMultaList(pagoMultaListNew);
            multa = em.merge(multa);
            if (tipoMultaOld != null && !tipoMultaOld.equals(tipoMultaNew)) {
                tipoMultaOld.getMultaList().remove(multa);
                tipoMultaOld = em.merge(tipoMultaOld);
            }
            if (tipoMultaNew != null && !tipoMultaNew.equals(tipoMultaOld)) {
                tipoMultaNew.getMultaList().add(multa);
                tipoMultaNew = em.merge(tipoMultaNew);
            }
            if (codigoEjemplarOld != null && !codigoEjemplarOld.equals(codigoEjemplarNew)) {
                codigoEjemplarOld.getMultaList().remove(multa);
                codigoEjemplarOld = em.merge(codigoEjemplarOld);
            }
            if (codigoEjemplarNew != null && !codigoEjemplarNew.equals(codigoEjemplarOld)) {
                codigoEjemplarNew.getMultaList().add(multa);
                codigoEjemplarNew = em.merge(codigoEjemplarNew);
            }
            if (idUsuarioOld != null && !idUsuarioOld.equals(idUsuarioNew)) {
                idUsuarioOld.getMultaList().remove(multa);
                idUsuarioOld = em.merge(idUsuarioOld);
            }
            if (idUsuarioNew != null && !idUsuarioNew.equals(idUsuarioOld)) {
                idUsuarioNew.getMultaList().add(multa);
                idUsuarioNew = em.merge(idUsuarioNew);
            }
            for (PagoMulta pagoMultaListOldPagoMulta : pagoMultaListOld) {
                if (!pagoMultaListNew.contains(pagoMultaListOldPagoMulta)) {
                    pagoMultaListOldPagoMulta.setMulta(null);
                    pagoMultaListOldPagoMulta = em.merge(pagoMultaListOldPagoMulta);
                }
            }
            for (PagoMulta pagoMultaListNewPagoMulta : pagoMultaListNew) {
                if (!pagoMultaListOld.contains(pagoMultaListNewPagoMulta)) {
                    Multa oldMultaOfPagoMultaListNewPagoMulta = pagoMultaListNewPagoMulta.getMulta();
                    pagoMultaListNewPagoMulta.setMulta(multa);
                    pagoMultaListNewPagoMulta = em.merge(pagoMultaListNewPagoMulta);
                    if (oldMultaOfPagoMultaListNewPagoMulta != null && !oldMultaOfPagoMultaListNewPagoMulta.equals(multa)) {
                        oldMultaOfPagoMultaListNewPagoMulta.getPagoMultaList().remove(pagoMultaListNewPagoMulta);
                        oldMultaOfPagoMultaListNewPagoMulta = em.merge(oldMultaOfPagoMultaListNewPagoMulta);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = multa.getCodigo();
                if (findMulta(id) == null) {
                    throw new NonexistentEntityException("The multa with id " + id + " no longer exists.");
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
            Multa multa;
            try {
                multa = em.getReference(Multa.class, id);
                multa.getCodigo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The multa with id " + id + " no longer exists.", enfe);
            }
            TipoMulta tipoMulta = multa.getTipoMulta();
            if (tipoMulta != null) {
                tipoMulta.getMultaList().remove(multa);
                tipoMulta = em.merge(tipoMulta);
            }
            Ejemplar codigoEjemplar = multa.getCodigoEjemplar();
            if (codigoEjemplar != null) {
                codigoEjemplar.getMultaList().remove(multa);
                codigoEjemplar = em.merge(codigoEjemplar);
            }
            Usuario idUsuario = multa.getIdUsuario();
            if (idUsuario != null) {
                idUsuario.getMultaList().remove(multa);
                idUsuario = em.merge(idUsuario);
            }
            List<PagoMulta> pagoMultaList = multa.getPagoMultaList();
            for (PagoMulta pagoMultaListPagoMulta : pagoMultaList) {
                pagoMultaListPagoMulta.setMulta(null);
                pagoMultaListPagoMulta = em.merge(pagoMultaListPagoMulta);
            }
            em.remove(multa);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Multa> findMultaEntities() {
        return findMultaEntities(true, -1, -1);
    }

    public List<Multa> findMultaEntities(int maxResults, int firstResult) {
        return findMultaEntities(false, maxResults, firstResult);
    }

    private List<Multa> findMultaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Multa.class));
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

    public Multa findMulta(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Multa.class, id);
        } finally {
            em.close();
        }
    }

    public int getMultaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Multa> rt = cq.from(Multa.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
