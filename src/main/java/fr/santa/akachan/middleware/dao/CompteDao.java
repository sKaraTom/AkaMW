package fr.santa.akachan.middleware.dao;


import java.util.Objects;

import javax.ejb.Stateless;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import fr.santa.akachan.middleware.objetmetier.compte.Compte;
import fr.santa.akachan.middleware.objetmetier.compte.CompteDejaExistantException;
import fr.santa.akachan.middleware.objetmetier.compte.CompteInexistantException;
import fr.santa.akachan.middleware.objetmetier.compte.CompteInvalideException;


@Stateless
//@Transactional
public class CompteDao {

	@PersistenceContext
	private EntityManager em;
	
	
	public Compte obtenir(final String email)
		throws CompteInexistantException {
		
		Compte compte = em.find(Compte.class, email);
		return compte;

	}
	
	public void ajouter(final Compte compte) throws CompteInvalideException, CompteDejaExistantException {
		
		try {
			em.persist(compte);
		}
		catch(final EntityExistsException e) {
			throw new CompteDejaExistantException();
		}
		
		/*
		if(!em.contains(compte)) {
			em.persist(compte);
		}
		
		 Compte compteAValider = em.find(Compte.class, compte.getEmail());
		 
		if(Objects.isNull(compteAValider)) {
			em.persist(compte);
		}
		
		if(compteAValider.equals(null)){
			em.persist(compte);
		}

		else {
			throw new CompteDejaExistantException();
		}
	*/
	}
	
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
	
	
	public Boolean contenir(final Compte compte) {
		
		Boolean estTrouve = null;
		
		/*
		try
		{
			em.getReference(Compte.class, compte.getEmail());
			estTrouve = true;
		}
		catch (final EntityNotFoundException e)
		{
			estTrouve = false;
		}
			*/
		
		
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
