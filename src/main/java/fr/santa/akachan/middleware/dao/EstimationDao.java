package fr.santa.akachan.middleware.dao;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.santa.akachan.middleware.objetmetier.compte.Compte;
import fr.santa.akachan.middleware.objetmetier.estimation.Estimation;
import fr.santa.akachan.middleware.objetmetier.estimation.EstimationExistanteException;
import fr.santa.akachan.middleware.objetmetier.prenom.PrenomInsee;


@Stateless
public class EstimationDao {

	@PersistenceContext
	private EntityManager em;
	
	private static final Logger LOGGER =
			LoggerFactory.getLogger(EstimationDao.class);
	
	
	
	// toutes les estimations sans distinction de client.
	public Long obtenirNbTotalEstimations() {
	
		final String requeteJPQL = "Estimation.obtenirNbreTotal";
		final Query requete = em.createNamedQuery(requeteJPQL);
		
		Long total = (Long) requete.getSingleResult();
		
		return total;
	}
	
	// toutes les estimations sans distinction de client, par sexe.
	// TODO : inutilisé pour l'instant.
	public Long obtenirNbTotalEstimParSexe(String sexe) {
		
		final String requeteJPQL = "Estimation.obtenirNbreTotalParSexe";
		final Query requete = em.createNamedQuery(requeteJPQL);
		requete.setParameter("sex", sexe);
		
		Long total = (Long) requete.getSingleResult();
		
		return total;
	}
	
	/**
	 * @param sexe "1" pour garçon "2" pour fille
	 * @return liste des 3 prenoms les plus aimés par sexe.
	 */
	public List<String> obtenirTop3PrenomsEstimes(String sexe) {
		
		final String requeteJPQL = "Estimation.obtenirTopPrenomsEstimes";
		final Query requete = em.createNamedQuery(requeteJPQL);
		requete.setParameter("sex", sexe);
		requete.setMaxResults(3);
		
		List<String> listeTopPrenoms = requete.getResultList();
		
		return listeTopPrenoms;
	}
	
	
	// retourne le nombre d'estimations d'un client.
	public Long obtenirNbEstimClient(UUID refClient) throws DaoException {
		
		final String requeteJPQL = "Estimation.obtenirNbEstimClient";
		final Query requete = em.createNamedQuery(requeteJPQL);
		requete.setParameter("refclient", refClient);
		
		Long totalEstimClient = null;
		try {
			totalEstimClient = (Long) requete.getSingleResult();
		}
		catch (NoResultException n) {
			throw new DaoException();
		}
		return totalEstimClient;
		
	}
	
	// retourne le nombre d'estimations d'un client par sexe.
	public Long obtenirNbEstimClientParSexe(UUID refClient, String sexe) throws DaoException {
		
		final String requeteJPQL = "Estimation.obtenirNbEstimClientParSexe";
		final Query requete = em.createNamedQuery(requeteJPQL);
		requete.setParameter("refclient", refClient);
		requete.setParameter("sex", sexe);
		
		Long totalEstimClient = null;
		try {
		totalEstimClient = (Long) requete.getSingleResult();
		}
		catch (NoResultException n) {
			throw new DaoException();
		}
		
		return totalEstimClient;		
	}
	
	/** obtenir la liste des prénoms déjà estimés par un client (filtre : sexe).
	 * Sert notamment pour la comparaison dans l'outil recherche.
	 * @param sexe
	 * @param refClient
	 * @return List<String> liste de prénoms estimés
	 */
	public List<String> obtenirPrenomsEstimesClientParSexe(final String sexe,final UUID refClient){
		
		final String requeteJPQL = "SELECT e.prenom FROM Estimation e WHERE e.refClient=:refclient AND e.sexe=:sex";
		final Query requete = em.createQuery(requeteJPQL);
		requete.setParameter("refclient", refClient);
		requete.setParameter("sex", sexe);
		
		@SuppressWarnings("unchecked")
		List<String> listePrenomsEstimes = requete.getResultList();
		
		return listePrenomsEstimes;
	}
	
	
	
	
	public void creerEstimation(final Estimation estimation) throws EstimationExistanteException {
		
		Boolean estimationExistante = this.contenirEstimation(estimation);
		
		if(!estimationExistante) {
		em.persist(estimation);
		}
		
		else {
			throw new EstimationExistanteException();
		}
	}
	
	
	public void changerDeListeEstimations(final List<Estimation> estimations, final String akachan) {
		
		for(Estimation estimation:estimations) {
		
			estimation.setAkachan(akachan);	
			em.merge(estimation);
		}
	}

	// sert notamment pour ajouter favori, changer de liste Akachan <--> liste noire.
	public void modifierEstimation(final Estimation estimation) {
		
		em.merge(estimation);
	}
	
		
	public boolean contenirEstimation (final Estimation estimation) {
		
		// si on trouve le prénom dans la table, retourner true.
		final String requeteJPQL = "SELECT e.prenom FROM Estimation e WHERE e.refClient=:refclient AND e.prenom=:prenom";
		final Query requete = em.createQuery(requeteJPQL);
		requete.setParameter("refclient", estimation.getRefClient());
		requete.setParameter("prenom", estimation.getPrenom());
		
		try {
		String resultat = (String) requete.getSingleResult();
		return true;
		}
		
		catch(NoResultException e) {
			return false;
		}
	}
	
	// NE SERT PLUS : refactorisation de l'outil recherche pour éviter requêtages intempestifs dans la bdd.
//	public boolean contenirEstimationPourOutilRecherche (final String prenom, final String sexe, final UUID refClient ) {
//		
//		// si on trouve le prénom dans la table, retourner true.
//		final String requeteJPQL = "SELECT e.prenom FROM Estimation e WHERE e.refClient=:refclient AND e.sexe=:sex AND e.prenom=:prenom";
//		final Query requete = em.createQuery(requeteJPQL);
//		requete.setParameter("refclient", refClient);
//		requete.setParameter("sex", sexe);
//		requete.setParameter("prenom", prenom);
//		
//		try {
//		String resultat = (String) requete.getSingleResult();
//		return true;
//		}
//		
//		catch(NoResultException e) {
//			return false;
//		}
//	
//	}
	
	
}
