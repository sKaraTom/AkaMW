package fr.santa.akachan.middleware.dao;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import fr.santa.akachan.middleware.objetmetier.compte.Compte;
import fr.santa.akachan.middleware.objetmetier.estimation.Estimation;
import fr.santa.akachan.middleware.objetmetier.estimation.EstimationExistanteException;
import fr.santa.akachan.middleware.objetmetier.prenom.PrenomInsee;


@Stateless
public class EstimationDao {

	@PersistenceContext
	private EntityManager em;
	

	// toutes les estimations sans distinction de client.
	public Long obtenirNbTotalEstimations() {
	
		final String requeteJPQL = "Estimation.obtenirNbreTotal";
		final Query requete = em.createNamedQuery(requeteJPQL);
		
		Long total = (Long) requete.getSingleResult();
		
		return total;
	}
	
	// toutes les estimations sans distinction de client par sexe.
	public Long obtenirNbTotalEstimParSexe(String sexe) {
		
		final String requeteJPQL = "Estimation.obtenirNbreTotalParSexe";
		final Query requete = em.createNamedQuery(requeteJPQL);
		requete.setParameter("sex", sexe);
		
		Long total = (Long) requete.getSingleResult();
		
		return total;
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
	
	
	public void creerEstimation(final Estimation estimation) throws EstimationExistanteException {
		
		// TODO : tester si le prénom n'est pas déjà estimé... 
		// la génération d'uuid empêche d'utiliser em.contains(estimation).
		// autre option : faire la vérification côté pattern Proxy.
		em.persist(estimation);

		/*
		else {
			throw new EstimationExistanteException();
		}
		*/
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
	
	
	// TODO : à revoir. problématique : je ne peux pas me baser sur la clef primaire (uuid) pour trouver les estimations
	// mais sur le prénom.
	// A revoir quand proxy
	public boolean contenirEstimation (final Estimation estimation) {
		
		// si on trouve le prénom dans la table, retourner true.
		final String requeteJPQL = "SELECT e.prenom FROM Estimation e WHERE e.refClient=:refclient AND e.prenom=:prenom";
		final Query requete = em.createQuery(requeteJPQL);
		requete.setParameter("refclient", estimation.getRefClient());
		requete.setParameter("prenom", estimation.getPrenom());
		
		// Objects.isNull(estimation retournée de la bdd);
		
		
		try {
		requete.getSingleResult();
		return true;
		
		}
		
		catch(Exception e) {
			return false;
		}
	
	}
	
}
