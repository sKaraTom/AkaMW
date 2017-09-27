package fr.santa.akachan.middleware.dao;

import java.util.List;
import java.util.Objects;

import javax.ejb.Stateless;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.santa.akachan.middleware.objetmetier.citation.Citation;
import fr.santa.akachan.middleware.objetmetier.citation.CitationExistanteException;
import fr.santa.akachan.middleware.objetmetier.citation.CitationInexistanteException;

@Stateless
@Transactional
public class CitationDao {

	@PersistenceContext
	private EntityManager em;
	
	private static final Logger LOGGER =
			LoggerFactory.getLogger(CitationDao.class);
	
	
	/** Ajouter une citation à la table.
	 * @param citation
	 * @throws CitationExistanteException si la citation à ajouter existe déjà.
	 */
	public void ajouterCitation(final Citation citation) throws CitationExistanteException {
		
		try {
		em.persist(citation);
		}
		catch(EntityExistsException e) {
			throw new CitationExistanteException();
		}
	}
	
	/**
	 * obtenir une citation par son identifiant
	 * 
	 * @param id l'id (Integer) de la citation
	 * @return Citation la citation trouvée
	 * @throws CitationInexistanteException si aucune citation existante à cet id.
	 */
	public Citation obtenirCitation(final Integer id) throws CitationInexistanteException {
		
		Citation citation = em.find(Citation.class, id);
		
		if(Objects.isNull(citation)) {
			throw new CitationInexistanteException("l'id ne correspond pas à une citation existante.");
		}
		
		return citation;
	}
	
	public Integer obtenirIdMax() throws DaoException {
		
		final String requeteJPQL = "Citation.obtenirIdMax";
		final Query requete = em.createNamedQuery(requeteJPQL);
		
		Integer total;
		
		try {
			total = (Integer) requete.getSingleResult();
		}
		catch(Exception e) {
			throw new DaoException("echec à l'obtention de l'id maximum depuis la bdd :" + e.getClass() + " - " + e.getMessage());
		}

		return total;
	}
	
	/** 
	 * obtenir le nombre total de citations dans la table.
	 * 
	 * @return Integer nombre de tuples de la table.
	 * @throws DaoException si erreur lors de la communication avec la bdd.
	 */
	public Long obtenirNombreTotalCitations() throws DaoException {
		
		final String requeteJPQL = "Citation.obtenirNbreCitations";
		final Query requete = em.createNamedQuery(requeteJPQL);
		
		Long total;
		try {
			total = (Long) requete.getSingleResult();
		}
		catch(Exception e) {
			throw new DaoException("echec à l'obtention du nombre total de citations depuis la bdd :" + e.getClass() + " - " + e.getMessage());
		}

		return total;
	}
	
	/** 
	 * obtenir toutes les citations de la table.
	 * 
	 * @return List<Citations> liste de toutes les citations
	 * @throws DaoException si erreur lors de la communication avec la bdd.
	 */
	public List<Citation> obtenirCitations() throws DaoException {
		
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		
		// préparer une requete Critéria
		final CriteriaQuery<Citation> requeteCriteria = cb.createQuery(Citation.class);
		
		// "root" de sélection
		requeteCriteria.from(Citation.class);

		final TypedQuery<Citation> requete = em.createQuery(requeteCriteria);	
		
		List<Citation> listeCitations;
		
		try {
			listeCitations = requete.getResultList(); 
		}
		catch(Exception e) {
			throw new DaoException("echec à obtenir la liste de citations depuis la bdd :" + e.getClass() + " - " + e.getMessage());
		}
		
		return listeCitations;
		
	}
	
	/**
	 * supprimer une citation
	 * 
	 * @param id
	 * @throws CitationInexistanteException si aucune citation trouvée
	 * @throws DaoException si l'id en paramètre n'est pas valide.
	 */
	public void supprimerCitation(final Integer id) throws CitationInexistanteException, DaoException {
		
		try {
		final Citation citationASupprimer = em.getReference(Citation.class, id);
		em.remove(citationASupprimer);
		}
		catch(EntityNotFoundException e) {
			throw new CitationInexistanteException("citation introuvable");
		}
		catch(IllegalArgumentException e) {
			throw new DaoException("l'id communiqué n'est pas valide");
		}
	}
	
	
}
