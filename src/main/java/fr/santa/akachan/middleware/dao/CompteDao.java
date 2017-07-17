package fr.santa.akachan.middleware.dao;


import java.util.Objects;

import javax.ejb.Stateless;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.santa.akachan.middleware.objetmetier.compte.Compte;
import fr.santa.akachan.middleware.objetmetier.compte.CompteDejaExistantException;
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
	 * @throws CompteDejaExistantException si un compte identique est dans la bdd.
	 */
	public void ajouter(final Compte compte) throws CompteInvalideException, CompteDejaExistantException {
		
		try {
			em.persist(compte);
		}
		catch(final EntityExistsException e) {
			throw new CompteDejaExistantException();
		}
		catch(final IllegalArgumentException e ) {
			throw new CompteInvalideException();
		}
	}
	
	/** 
	 * obtenir un compte.
	 * utile pour modifier un compte par exemple.
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
		
		// si problème (code 500, catch de l'EntityNotFound ne marche pas) utiliser em.find()
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
	public Boolean contenir(final Compte compte) {
		
		Boolean estTrouve = null;
		
		Compte compteExistant = em.find(Compte.class, compte.getEmail());
		
		//if(compteExistant.equals(null)) {
		if (!Objects.isNull(compteExistant)){
		//if(em.contains(compte)) {
			estTrouve = true;
		}
		else {
			estTrouve = false;
		}

		return estTrouve;
	}
	
}
