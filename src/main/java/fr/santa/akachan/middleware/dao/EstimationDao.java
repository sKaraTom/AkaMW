package fr.santa.akachan.middleware.dao;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.santa.akachan.middleware.objetmetier.client.Client;
import fr.santa.akachan.middleware.objetmetier.client.ClientIntrouvableException;
import fr.santa.akachan.middleware.objetmetier.compte.Compte;
import fr.santa.akachan.middleware.objetmetier.estimation.Estimation;
import fr.santa.akachan.middleware.objetmetier.estimation.EstimationExistanteException;
import fr.santa.akachan.middleware.objetmetier.estimation.EstimationIntrouvableException;
import fr.santa.akachan.middleware.objetmetier.prenominsee.PrenomInsee;


@Stateless
@Transactional
public class EstimationDao {

	private static final Logger LOGGER =
			LoggerFactory.getLogger(EstimationDao.class);
	
	@PersistenceContext
	private EntityManager em;

	
	/** 
	 * obtenir le nombre total d'estimations sans distinction de client, liste akachan ou noire, ou autre.
	 *  
	 * @return Long nombre total des estimations.
	 */
	public Long obtenirNbTotalEstimations() {
	
		final String requeteJPQL = "Estimation.obtenirNbreTotal";
		final Query requete = em.createNamedQuery(requeteJPQL);
		
		Long total = (Long) requete.getSingleResult();
		
		return total;
	}
	
	/**
	 * obtenir le total d'estimation par sexe
	 * 
	 * @param sexe
	 * @return
	 */
	public Long obtenirNbTotalEstimParSexe(String sexe) {
		
		final String requeteJPQL = "Estimation.obtenirNbreTotalParSexe";
		final Query requete = em.createNamedQuery(requeteJPQL);
		requete.setParameter("sex", sexe);
		
		Long total = (Long) requete.getSingleResult();
		
		return total;
	}
	
	/** 
	 * obtenir le top 3 des prénoms estimés par sexe et estimés positivement (akachan = "true")
	 * 
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
	
	
	/** 
	 * obtenir le nombre total d'estimations d'un client (sans distinction de sexe).
	 *  
	 * @param refClient
	 * @return le nombre d'estimations d'un client.
	 * @throws DaoException
	 */
	public Long obtenirNbEstimClient(UUID refClient) throws DaoException {
		
		final String requeteJPQL = "Estimation.obtenirNbEstimClient";
		final Query requete = em.createNamedQuery(requeteJPQL);
		requete.setParameter("refclient", refClient);
		
		Long totalEstimClient = null;
		try {
			totalEstimClient = (Long) requete.getSingleResult();
		}
		catch (NoResultException e) {
			throw new DaoException( " échec à l'obtention du nbre d'estimations clients depuis la bdd : " + e.getClass() + " - " + e.getMessage());
		}
		return totalEstimClient;
		
	}
	
	/**
	 *  obtenir le nombre d'estimations d'un client par sexe du prénom.
	 * 
	 * @param refClient
	 * @param sexe
	 * @return le nombre d'estimations d'un client par sexe.
	 * @throws DaoException si aucun résultat n'est obtenu depuis la bdd.
	 */
	public Long obtenirNbEstimClientParSexe(final UUID refClient,final String sexe) throws DaoException {
		
		final String requeteJPQL = "Estimation.obtenirNbEstimClientParSexe";
		final Query requete = em.createNamedQuery(requeteJPQL);
		requete.setParameter("refclient", refClient);
		requete.setParameter("sex", sexe);
		
		Long totalEstimClient = null;
		
		try {
			totalEstimClient = (Long) requete.getSingleResult();
		}
		
		catch (NoResultException e) {
			throw new DaoException( "échec à l'obtention du nbre d'estim d'un client par sexe depuis la bdd : " + e.getClass() + " - " + e.getMessage());
		}
		
		return totalEstimClient;		
	}
	
	
	/**
	 * obtenir une estimation
	 * 
	 * @param uuidEstimation
	 * @return
	 * @throws EstimationIntrouvableException si aucune estimation trouvée pour cet uuid
	 */
	public Estimation obtenirEstimation(final UUID uuidEstimation) throws EstimationIntrouvableException {
		
		Estimation estimation = null;
		estimation = em.find(Estimation.class, uuidEstimation);
		
		if(Objects.isNull(estimation)) {
			throw new EstimationIntrouvableException("aucune estimation trouvée depuis la bdd.");
		}
		
		return estimation;
	}
	
	/** 
	 * Obtenir liste des estimations (liste akachan ou noir) d'un client
	 * 
	 * @param refClient, booleenAkachan si prénoms aimés ou pas
	 * @return List<Estimation>
	 */
	public List<Estimation> obtenirListeEstimations(final UUID refClient,String booleenAkachan) {
			
		 final String requeteJPQL = "Estimation.obtenirListeEstimations";
		 
		 final TypedQuery<Estimation> requete = em.createNamedQuery(requeteJPQL,Estimation.class);
			requete.setParameter("refclient", refClient);
			requete.setParameter("akachan", booleenAkachan);
			
		List<Estimation> listeAkachan = requete.getResultList();
		return listeAkachan;
	}
	
	
	/** 
	 * Obtenir la liste des favoris d'un client (favori = 1)
	 * 
	 * @param refClient
	 * @return List<Estimation>
	 */
	public List<Estimation> obtenirListeFavoris(final UUID refClient) {
		
		final String requeteJPQL ="Estimation.obtenirListeFavoris";
		
		final TypedQuery<Estimation> requete = em.createNamedQuery(requeteJPQL,Estimation.class);
		requete.setParameter("refclient", refClient);
		
		List<Estimation> listeFavoris = requete.getResultList();
	 
		return listeFavoris;
	}
	
	
	
	/** créer une estimation.
	 * 
	 * @param estimation
	 * @throws EstimationExistanteException si une estimation est trouvée avec ce prénom, sexe et uuid client.
	 */
	public void creerEstimation(final Estimation estimation) throws EstimationExistanteException {
		
		Boolean estimationExistante = this.contenirEstimation(estimation);
		
		if(!estimationExistante) {
		em.persist(estimation);
		}
		
		else {
			throw new EstimationExistanteException();
		}
	}
	
	/** changer de liste un groupe d'estimations.
	 * 
	 * @param estimations
	 * @param akachan true:passe dans liste Akachan, false:passe dans liste noire.
	 */
	public void changerDeListeEstimations(final List<Estimation> estimations, final String akachan) {
		
		for(Estimation estimation:estimations) {
		
			estimation.setAkachan(akachan);	
			em.merge(estimation);
		}
	}

	/** 
	 * modifier une estimation.
	 * sert notamment pour ajouter favori, changer de liste Akachan <--> liste noire.
	 * 
	 * @param estimation
	 * @throws DaoException si la persistance a échoué.
	 */
	public void modifierEstimation(final Estimation estimation) throws DaoException {
		
		try {
			em.merge(estimation);
		}
		catch(Exception e) {
			throw new DaoException("impossible d'enregistrer la modification dans la base : " + e.getClass() + " - " + e.getMessage());
		}
	}
	
	/**
	 * retirer toutes les estimations d'un client de la bdd
	 * 
	 * @param refClient
	 * @throws DaoException si l'éxécution de la requête échoue.
	 */
	public void supprimerToutesEstimationsClient(final UUID refClient) throws DaoException {
		
		final String requeteJPQL = "Estimation.supprimerToutesEstimationsClient";
		final Query requete = em.createNamedQuery(requeteJPQL);
		requete.setParameter("refclient", refClient);
		
		try {
			requete.executeUpdate();
		}
		catch(Exception e) {
			throw new DaoException("échec à la suppression des estimations : " + e.getClass() + " - " + e.getMessage());
		}
	}
	
	
	/** 
	 * méthode de vérification si une estimation existe déjà : prénom, sexe et uuid client.
	 * cette méthode est nécessaire car on ne peut se baser sur l'uuid de l'estimation comme ref de vérification.
	 * 
	 * @param estimation
	 * @return true si une estimation existe déjà.
	 */
	public boolean contenirEstimation (final Estimation estimation) {
		
		// si on trouve le prénom dans la table lié au client, retourner true.
		final String requeteJPQL = "SELECT e.prenom FROM Estimation e WHERE e.refClient=:refclient AND e.prenom=:prenom AND e.sexe=:sex";
		final Query requete = em.createQuery(requeteJPQL);
		requete.setParameter("refclient", estimation.getRefClient());
		requete.setParameter("prenom", estimation.getPrenom());
		requete.setParameter("sex", estimation.getSexe());
		
		try {
			String resultat = (String) requete.getSingleResult();
			return true;
		}
		
		catch(NoResultException e) {
			return false;
		}
	}
	
}
