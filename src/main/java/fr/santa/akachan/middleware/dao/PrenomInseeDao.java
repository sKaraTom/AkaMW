package fr.santa.akachan.middleware.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.santa.akachan.middleware.objetmetier.prenomInsee.PrenomInsee;
import fr.santa.akachan.middleware.objetmetier.prenomInsee.PrenomInseeInexistantException;

@Stateless
@Transactional
public class PrenomInseeDao {

	private static final Logger LOGGER =
			LoggerFactory.getLogger(PrenomInseeDao.class);
	
	@PersistenceContext
	private EntityManager em;
	
	/**
	 * obtenir le total de naissances depuis 1900 pour un prénom et un sexe donnés.
	 * 
	 * @param prenom
	 * @param sexe
	 * @return Long total
	 */
	public Long obtenirNombreTotalNaissancesPourUnPrenom(String prenom, String sexe) {
		
		final String requeteJPQL = "PrenomInsee.obtenirTotalNaissancesPourUnPrenom";
		
		final Query requete = em.createNamedQuery(requeteJPQL);
		requete.setParameter("lab", prenom);
		requete.setParameter("sexe", sexe);
		
		Long total;
		
		total = (Long) requete.getSingleResult();
		
		return total;
		
	}
	
	/**
	 * obtenir les années où il y a eu le max de naissances d'un prénom
	 * peut y avoir plusieurs résultats (même nombre max sur plusieurs années)
	 * 
	 * @param prenom
	 * @param sexe
	 * @return liste de prenomInsee contenant l'année et le nombre de naissances.
	 * @throws DaoException
	 */
	public List<PrenomInsee> obtenirAnneesMaxNaissancesPourUnPrenom(String prenom, String sexe) throws DaoException {
		
		final String requeteJPQL = "PrenomInsee.obtenirAnneesMaxNaissancesPourUnPrenom";
		
		final TypedQuery<PrenomInsee> requete = em.createNamedQuery(requeteJPQL, PrenomInsee.class);
		requete.setParameter("lab", prenom);
		requete.setParameter("sexe", sexe);
		
		List<PrenomInsee> listePrenomInsee = null;
		
		try {
			listePrenomInsee = requete.getResultList();
		}
		catch(Exception e) {
			throw new DaoException("échec à l'obtention des stats d'un prénom depuis la bdd : " + e.getClass() + " - " + e.getMessage());
		}

		return listePrenomInsee;
		
	}
	
	
	
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
			throw new DaoException("échec à l'obtention des stats d'un prénom depuis la bdd : " + e.getClass() + " - " + e.getMessage());
		}

		return liste;
	}
	

	
	

	

		
	
	
	
}
