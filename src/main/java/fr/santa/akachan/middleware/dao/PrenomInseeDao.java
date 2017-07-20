package fr.santa.akachan.middleware.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.santa.akachan.middleware.objetmetier.prenomInsee.PrenomInsee;

@Stateless
@Transactional
public class PrenomInseeDao {

	private static final Logger LOGGER =
			LoggerFactory.getLogger(PrenomInseeDao.class);
	
	@PersistenceContext
	private EntityManager em;
	
	
	/**
	 * Obtenir tous les tuples du même prénom pour accéder à : nombre naissances par année
	 * 
	 * @param label le prénom
	 * @return List<PrenomsInsee>
	 * @throws DaoException si une erreur est survenue à la communication avec la bdd.
	 */
	public List<PrenomInsee> obtenirStatsPrenom(String label, String sexe) throws DaoException {

		List<PrenomInsee> liste = null;
		
		// je demande de selectionner les prenoms selon un label defini
		final String requeteJPQL = "PrenomInsee.obtenirStatsPrenom";

		final TypedQuery<PrenomInsee> requete = em.createNamedQuery(requeteJPQL, PrenomInsee.class);
		requete.setParameter("lab", label);
		requete.setParameter("sex", sexe);

		try {
			liste = requete.getResultList();
		} catch (Exception e) {
			throw new DaoException(e);
		}

		return liste;
	}
	

	
	

	

		
	
	
	
}
