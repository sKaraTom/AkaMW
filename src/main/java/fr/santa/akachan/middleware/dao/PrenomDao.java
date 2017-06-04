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

import fr.santa.akachan.middleware.objetmetier.prenom.PrenomInsee;

@Stateless
@Transactional
public class PrenomDao {

	
	@PersistenceContext
	private EntityManager em;
	
	
	public PrenomInsee obtenirPrenom(final Integer reference) {

		PrenomInsee prenomInsee;

		prenomInsee = em.find(PrenomInsee.class, reference);

		 if(Objects.isNull(prenomInsee)){
		 // exception prenomInexistant à mettre
		 }
		return prenomInsee;
	}
	
	/**
	 * @param label
	 * @return liste des stats d'un prénom
	 * @throws DaoException
	 */
	public List<PrenomInsee> obtenirStatsPrenom(String label, String sexe) throws DaoException {

		List<PrenomInsee> liste = null;

		// je demande de selectionner les prenoms selon un label defini
		final String requeteJPQL = "SELECT p FROM PrenomInsee p WHERE p.label=:lab "
				+ "AND  p.sexe=:sex";

		final TypedQuery<PrenomInsee> requete = em.createQuery(requeteJPQL, PrenomInsee.class);
		requete.setParameter("lab", label);
		requete.setParameter("sex", sexe);

		try {
			liste = requete.getResultList();
		} catch (Exception e) {
			throw new DaoException(e);
		}

		return liste;
	}
	
	/** ************ 1 PRENOM ALEATOIRE SQL*********/
	
	public String obtenirPrenomAleatoireSql (String sexe, UUID refClient) {
		
		final StringBuilder requeteSQL = new StringBuilder();
				requeteSQL.append("SELECT pre_label FROM t_prenom p");
				requeteSQL.append(" WHERE NOT EXISTS (SELECT est_prenom FROM t_estimation e WHERE e.est_refClient=:refclient");
				requeteSQL.append(" AND e.est_prenom=p.pre_label)");
				requeteSQL.append(" AND pre_sexe= :sex");
				requeteSQL.append(" ORDER BY RANDOM() LIMIT 1");
		
		Query q = em.createNativeQuery(requeteSQL.toString());
			q.setParameter("sex", sexe);
			q.setParameter("refclient", refClient);
		
		// TODO : traiter exceptions et propager.
		String prenomAleatoire = (String)q.getSingleResult();
		return prenomAleatoire;
	}
	
	public String obtenirPrenomAeatoireTendance(String sexe, UUID refClient) {
		
		final StringBuilder requeteSQL = new StringBuilder();
				requeteSQL.append("SELECT pre_label FROM t_prenom p");
				requeteSQL.append(" WHERE NOT EXISTS (SELECT est_prenom FROM t_estimation e WHERE e.est_refClient=:refclient");
				requeteSQL.append(" AND e.est_prenom=p.pre_label)");
				requeteSQL.append(" AND pre_sexe= :sex");
				requeteSQL.append(" AND pre_annee >= 2000");
				requeteSQL.append(" AND pre_nombre >= 300");
				requeteSQL.append(" ORDER BY RANDOM() LIMIT 1");
				
		Query q = em.createNativeQuery(requeteSQL.toString());
			q.setParameter("sex", sexe);
			q.setParameter("refclient", refClient);
						
		String prenomTendance = (String)q.getSingleResult();
		return prenomTendance;
	}
	
	public String obtenirPrenomAeatoireAncien(String sexe, UUID refClient) {
		
		final StringBuilder requeteSQL = new StringBuilder();
				requeteSQL.append("SELECT pre_label FROM t_prenom p");
				requeteSQL.append(" WHERE NOT EXISTS (SELECT est_prenom FROM t_estimation e WHERE e.est_refClient=:refclient");
				requeteSQL.append(" AND e.est_prenom=p.pre_label)");
				requeteSQL.append(" AND pre_sexe= :sex");
				requeteSQL.append(" AND pre_annee <= 1940");
				requeteSQL.append(" AND pre_nombre >= 100");
				requeteSQL.append(" ORDER BY RANDOM() LIMIT 1");
				
		Query q = em.createNativeQuery(requeteSQL.toString());
			q.setParameter("sex", sexe);
			q.setParameter("refclient", refClient);
						
		String prenomTendance = (String)q.getSingleResult();
		return prenomTendance;
	} 
	
	
	
	/** ************ LISTE PRENOMS ELIGIBLES SQL*********/
	
	/**
	 * REQUETE EN POSTGESQL --> EXCEPT REMPLACE MINUS.
	 * @param sexe : "1" pour garçon "2" pour fille
	 * @param refClient : uuid
	 * @return Liste des prénoms par sexe - liste Akachan client
	 */
	public List<String> obtenirListePrenomsSQL(String sexe, UUID refClient) {
		
		 //TODO : refactorer en StringBuilder.append...
		final String requeteSQL = "SELECT DISTINCT LOWER(pre_label) FROM t_prenom p"
				+ " WHERE pre_sexe= :sex"
				+ " EXCEPT"
				+ " SELECT LOWER(est_prenom) FROM t_estimation WHERE est_refClient=:refclient";
		
		// SELECTIONNER PRENOMS TENDANCES
		final String requeteSQL2 = "SELECT DISTINCT LOWER(pre_label) FROM t_prenom p"
				+ " WHERE pre_sexe= :sex"
				+ " AND pre_annee > 2000"
				+ " AND pre_nombre >500"
				+ " EXCEPT"
				+ " SELECT LOWER(est_prenom) FROM t_estimation WHERE est_refClient=:refclient";
		
		Query q = em.createNativeQuery(requeteSQL);
		q.setParameter("sex", sexe);
		q.setParameter("refclient", refClient);
		
		List<String> prenoms = q.getResultList();
		return prenoms;
		
	}

	/** ************ LISTE PRENOMS ELIGIBLES JPQL*********/
	
	public List<String> obtenirListePrenomsPourClient(String sexe, UUID refClient) {
		
		final StringBuilder requeteJPQL = new StringBuilder();
		requeteJPQL.append("SELECT DISTINCT p.label FROM PrenomInsee p");
		requeteJPQL.append(" WHERE p.label NOT IN (SELECT prenom FROM Estimation e WHERE e.refClient=:refclient AND e.sexe=:sex)");
		requeteJPQL.append(" AND p.sexe=:sex");
		
		/* requete 3 fonctionne aussi.
		final String requeteJPQL3 = "SELECT DISTINCT LOWER(p.label) FROM PrenomInsee p"
				+ " WHERE NOT EXISTS (SELECT prenom FROM Estimation e WHERE e.refClient=:refclient AND e.prenom=p.label AND e.sexe=:sex)"
				+ " AND p.sexe=:sex";
		*/
		
		final Query requete = em.createQuery(requeteJPQL.toString());
		requete.setParameter("sex", sexe);
		requete.setParameter("refclient", refClient);
		
		List<String> ListePrenoms = requete.getResultList();
	
		return ListePrenoms;
	}
	
	public List<String> obtenirPrenomsTendancesClient(String sexe, UUID refClient) {
		
		// SELECTIONNER PRENOMS TENDANCES : au moins 500 naissance 1 année depuis 2000.
		final StringBuilder requeteJPQL = new StringBuilder();
		requeteJPQL.append("SELECT DISTINCT p.label FROM PrenomInsee p");
		requeteJPQL.append(" WHERE NOT EXISTS (SELECT prenom FROM Estimation e WHERE e.refClient=:refclient AND e.prenom=p.label AND e.sexe=:sex)");
		requeteJPQL.append(" AND p.sexe=:sex");
		requeteJPQL.append(" AND p.annee >= 2000");
		requeteJPQL.append(" AND p.nombreNaissance >= 1000");
		
		final Query requete = em.createQuery(requeteJPQL.toString());
		requete.setParameter("sex", sexe);
		requete.setParameter("refclient", refClient);
		
		List<String> ListePrenoms = requete.getResultList();
		
		return ListePrenoms;
	}

	public List<String> obtenirPrenomsAnciensClient(String sexe, UUID refClient) {
		
		// SELECTIONNER PRENOMS ANCIENS (AU MOINS UNE SOMME IMPORTANTE AVANT 1950)
		final StringBuilder requeteJPQL = new StringBuilder();
		requeteJPQL.append("SELECT DISTINCT p.label FROM PrenomInsee p");
		requeteJPQL.append(" WHERE NOT EXISTS (SELECT prenom FROM Estimation e WHERE e.refClient=:refclient AND e.prenom=p.label AND e.sexe=:sex)");
		requeteJPQL.append(" AND p.sexe=:sex");
		requeteJPQL.append(" AND p.annee <= 1940");
		requeteJPQL.append(" AND p.nombreNaissance >= 1000");
		
		final Query requete = em.createQuery(requeteJPQL.toString());
		requete.setParameter("sex", sexe);
		requete.setParameter("refclient", refClient);
		
		List<String> ListePrenoms = requete.getResultList();
		
		return ListePrenoms;
	}
	
	

		
	
	
	
}
