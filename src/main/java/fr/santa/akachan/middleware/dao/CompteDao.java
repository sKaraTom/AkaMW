package fr.santa.akachan.middleware.dao;


import java.util.List;
import java.util.Objects;

import javax.ejb.Stateless;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.santa.akachan.middleware.dto.CompteDTO;
import fr.santa.akachan.middleware.objetmetier.compte.Compte;
import fr.santa.akachan.middleware.objetmetier.compte.CompteExistantException;
import fr.santa.akachan.middleware.objetmetier.compte.CompteInexistantException;
import fr.santa.akachan.middleware.objetmetier.compte.CompteInvalideException;


@Stateless
@Transactional
public class CompteDao {
	
	private static final Logger LOGGER =
			LoggerFactory.getLogger(CompteDao.class);
	
	@PersistenceContext
	private EntityManager em;
	
	
	/**
	 * ajouter un compte à la bdd
	 * 
	 * @param compte
	 * @throws CompteInvalideException si le compte à créer n'est pas valide.
	 * @throws CompteExistantException si un compte identique est dans la bdd.
	 */
	public void ajouter(final Compte compte) throws CompteInvalideException, CompteExistantException {
		
		try {
			em.persist(compte);
		}
		catch(final EntityExistsException e) {
			throw new CompteExistantException();
		}
		catch(final IllegalArgumentException e ) {
			throw new CompteInvalideException();
		}
	}
	
	/** 
	 * obtenir un compte.
	 * 
	 * @param email de référence (pk)
	 * @return le compte obtenu.
	 * @throws CompteInexistantException si la recherche par email ne renvoie rien.
	 */
	public Compte obtenir(final String email) throws CompteInexistantException {
		
		Compte compte = em.find(Compte.class, email);
		
		if (Objects.isNull(compte)){
			throw new CompteInexistantException("le compte n'existe pas");
		}
		return compte;
	}
	
	
	/**
	 * obtenir tous les comptesDTO (interface ADMIN)
	 * Utilisation de compteDTO pour ne transférer que les champs voulu et le nombre d'estimations du client.
	 * 
	 * @return List<CompteDTO> tous les comptesDTO (email, date de création, role, prenom, sexe, nombre d'estimations)
	 * @throws DaoException 
	 */
	public List<CompteDTO> obtenirTousComptesDTO() throws DaoException {
		
		final StringBuilder requeteJPQL = new StringBuilder();
		requeteJPQL.append("SELECT new fr.santa.akachan.middleware.dto.CompteDTO(c.email,c.dateDeCreation,c.role,cl.uuid,cl.prenom,cl.sexe,");
		requeteJPQL.append(" (SELECT COUNT(e.refClient) FROM Estimation e WHERE e.refClient=cl.uuid))");
		requeteJPQL.append(" FROM Client cl LEFT JOIN cl.compte c WHERE c.role != 'admin'");
		
		final TypedQuery<CompteDTO> requete = em.createQuery(requeteJPQL.toString(), CompteDTO.class);
		
		List<CompteDTO> listeComptes;
		
		try {
			listeComptes = requete.getResultList();
		}
		catch(Exception e) {
			throw new DaoException("échec à l'obtention des comptes depuis la bdd :" + e.getClass() + " - " + e.getMessage());
		}
		
		return listeComptes;
	}
	
	
	/**
	 * modifier un compte
	 * 
	 * @param compte
	 */
	public void modifier(final Compte compte) {
		
		em.merge(compte);
	}
	
	/**
	 * supprimer un compte
	 * 
	 * @param email
	 * @throws CompteInexistantException si le compte à supprimer est introuvable dans la bdd.
	 */
	public void supprimerCompte(String email) throws CompteInexistantException {
		
		try {
			final Compte compteASupprimer = em.getReference(Compte.class, email);
			em.remove(compteASupprimer);
		}
		catch (final EntityNotFoundException e)
		{
			throw new CompteInexistantException("Compte inconnu");
		}
	}
	
	/**
	 * vérifier qu'un compte existe bien dans la bdd.
	 * 
	 * @param compte le compte à vérifier
	 * @return true si le compte existe dans la bdd
	 */
	public Boolean contenir(final String email) {
		
		try {
			Compte compte = em.getReference(Compte.class,email);
			compte.getRole(); // pour contourner le chargement lazy de em.getReference()
			return true;
		}
		catch(EntityNotFoundException e) {
			return false;
		}
		
	}
	
}
