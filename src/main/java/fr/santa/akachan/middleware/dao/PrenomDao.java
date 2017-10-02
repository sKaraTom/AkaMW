package fr.santa.akachan.middleware.dao;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import fr.santa.akachan.middleware.objetmetier.prenom.PrenomIntrouvableException;

@Stateless
@Transactional
public class PrenomDao {
	
	@PersistenceContext
	private EntityManager em;
	
	
	/**
	 * obtenir un prénom aléatoire sans option de tendance.
	 * requête native SQL pour RANDOM()
	 * 
	 * @param sexe String '1' garçon, '2' fille
	 * @param refClient pour s'assurer que le prénom n'a pas déjà été estimé par le client.
	 * @return String un prénom
	 * @throws PrenomIntrouvableException si aucun prénom n'est obtenu.
	 * @throws DaoException 
	 */
	public String obtenirPrenomAleatoire (String sexe, UUID refClient) throws PrenomIntrouvableException, DaoException {
		
		final StringBuilder requeteSQL = new StringBuilder();
				requeteSQL.append("SELECT pre_label FROM t_prenom p");
				requeteSQL.append(" WHERE NOT EXISTS (SELECT est_prenom FROM t_estimation e WHERE e.est_refClient=:refclient AND e.est_sexe=:sex");
				requeteSQL.append(" AND e.est_prenom=p.pre_label)");
				requeteSQL.append(" AND pre_sexe= :sex");
				requeteSQL.append(" ORDER BY RANDOM() LIMIT 1");
		
		Query q = em.createNativeQuery(requeteSQL.toString());
		q.setParameter("sex", sexe);
		q.setParameter("refclient", refClient);
	
		String prenomAleatoire;
		
		try {
			prenomAleatoire = (String)q.getSingleResult();
		}
		catch(NoResultException e) {
			throw new PrenomIntrouvableException("aucun prénom n'a pu être trouvé.");
		}
		catch(Exception e) {
			throw new DaoException("échec à l'obtention d'un prénom aléatoire depuis la bdd : " + e.getClass() + " - " + e.getMessage());
		}
		return prenomAleatoire;
	}
	
	/**
	 * obtenir un prénom aléatoire tendance ou ancien
	 * requête native SQL pour RANDOM()
	 * 
	 * @param sexe
	 * @param refClient
	 * @param choixTendance ("ANCIEN" ou "TENDANCE")
	 * @return String un prénom tendance
	 * @throws DaoException
	 * @throws PrenomIntrouvableException
	 */
	public String obtenirPrenomAleatoireTendanceOuAncien(String sexe, UUID refClient,String choixTendance) throws DaoException, PrenomIntrouvableException {
		
		final StringBuilder requeteSQL = new StringBuilder();
				requeteSQL.append("SELECT pre_label FROM t_prenom p");
				requeteSQL.append(" WHERE NOT EXISTS (SELECT est_prenom FROM t_estimation e WHERE e.est_refClient=:refclient AND e.est_sexe=:sex");
				requeteSQL.append(" AND e.est_prenom=p.pre_label)");
				requeteSQL.append(" AND pre_sexe= :sex");
				requeteSQL.append(" AND pre_tendance= :choixTendance");
				requeteSQL.append(" ORDER BY RANDOM() LIMIT 1");
				
		Query q = em.createNativeQuery(requeteSQL.toString());
		q.setParameter("sex", sexe);
		q.setParameter("refclient", refClient);
		q.setParameter("choixTendance", choixTendance);
		
		String prenomTendance;
		
		try {
			prenomTendance = (String)q.getSingleResult();
		}
		catch(NoResultException e) {
			throw new PrenomIntrouvableException("aucun prénom tendance ou ancien n'a pu être trouvé.");
		}
		catch(Exception e) {
			throw new DaoException("échec à l'obtention d'un prénom tendance ou ancien depuis la bdd : " + e.getClass() + " - " + e.getMessage());
		}
		return prenomTendance;
	}
	
	
	/**
	 * chercher des prénoms et savoir s'il ont déjà été estimé : LIKE si rechercheExacte = true.
	 * 
	 * @param recherche (le prenom à rechercher)
	 * @param sexe ("1" garçon, "2" fille)
	 * @param refClient (uuid client pour chercher si estimation existante)
	 * @param rechercheExacte (requete like avec % ou non")
	 * @return Map<String prenom, Boolean estEstime> collection clef prenom, valeur booleen (true si le prénom est estimé).
	 */
	public Map<String,Boolean> chercherPrenomEtEstimation(String recherche, String sexe, UUID refClient, Boolean rechercheExacte) {
		
		String rechercheParam;
		
		// si la recherche exacte est demandée (true), juste passer le prénom en majuscules.
		if(rechercheExacte) {
			rechercheParam = recherche.toUpperCase();
		}
		
		//si la recherche exacte n'est pas demandée (false), mettre en forme la requête LIKE %PRENOM%
		else {
			final StringBuilder concatRechercheLike = new StringBuilder();
			concatRechercheLike.append("%");
			concatRechercheLike.append(recherche.toUpperCase());
			concatRechercheLike.append("%");
			
			rechercheParam = concatRechercheLike.toString();
		}

		final String requeteSQL = "Prenom.chercherPrenomEtEstimationExistante";
		
		Query requete = em.createNamedQuery(requeteSQL);
		requete.setParameter("sex", sexe);
		requete.setParameter("recherche", rechercheParam);
		requete.setParameter("refClient", refClient);
		
		// TreeMap permet de trier par ordre alphabétique le prénom (clef).
		Map<String,Boolean> listePrenomsRecherche = new TreeMap<String,Boolean>();
		
		// la requête retourne une liste d'un tableau d'objet [prenom, akachan ("true"/"false", ou null si non estimé)]
		List<Object[]> liste= requete.getResultList();
		
		for(Object[] objet : liste) {
			
			Boolean estEstime = false;
			
			// si akachan est non null, le prénom est estimé.
			if(objet[1] != null) {
				estEstime = true;
			}
			listePrenomsRecherche.put((String)objet[0], estEstime);
		}
		
		return listePrenomsRecherche;
	}
	
	
	
}
