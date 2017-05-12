package fr.santa.akachan.middleware.service;

import java.io.UnsupportedEncodingException;
import java.util.Objects;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import fr.santa.akachan.middleware.dao.CompteDao;
import fr.santa.akachan.middleware.objetmetier.client.Client;
import fr.santa.akachan.middleware.objetmetier.compte.Compte;
import fr.santa.akachan.middleware.objetmetier.compte.CompteDejaExistantException;
import fr.santa.akachan.middleware.objetmetier.compte.CompteInexistantException;
import fr.santa.akachan.middleware.objetmetier.compte.CompteInvalideException;
import fr.santa.akachan.middleware.securite.Jeton;
import fr.santa.akachan.middleware.securite.JwtCreation;


@Stateless
//@Transactional
public class CompteService {

	
	@EJB
	private CompteDao compteDao;
	
	@EJB
	private JwtCreation jwtCreation;
	
	
	public void creerCompte(final Compte compte)
			throws CompteInvalideException, CompteDejaExistantException {
		
		this.validerCompte(compte);
		
		//si client null --> pour générer un client avec uuid.
		if(compte.getClient() == null) {
		Client client = new Client();
		compte.setClient(client);
		}
		
		boolean estContenu = compteDao.contenir(compte);
		
		if(!estContenu) {
			// forcer le formatage du prénom client pour la persistance.
			String prenomClientAFormater = compte.getClient().getPrenom();
			String prenomFormate = WordUtils.capitalizeFully(prenomClientAFormater, new char[] { '-',' ' });
			compte.getClient().setPrenom(prenomFormate);
			
			compteDao.ajouter(compte);
		}
		else {
			throw new CompteDejaExistantException();
		}
		
	}
	
	
	public Compte obtenirCompte(final String email) throws CompteInexistantException {
			
		Compte compte = compteDao.obtenir(email);
		
		return compte;
	}
	
	
	
	public Jeton connecter(final String email, final String password)
		throws CompteInvalideException, CompteInexistantException, UnsupportedEncodingException {
		
			Compte compteValide = compteDao.obtenir(email);
			Jeton jeton = null;
			
			if(Objects.isNull(compteValide)) {
			//if (compteValide.equals(null)) {
				throw new CompteInexistantException("le compte n'existe pas");
			}
			
			if (!compteValide.getPassword().equals(password) ||
					!compteValide.getEmail().equals(email)) {
					throw new CompteInvalideException("connexion impossible");
			}

			//assert compteValide.getEmail().equals(compte.getEmail());
			
			else {
				String token = jwtCreation.creerToken2(compteValide);
				jeton = new Jeton(compteValide.getClient().getUuid().toString(),compteValide.getClient().getPrenom(), token) ;
			}
			
			return jeton;
	
	}

	private void validerCompte(final Compte compte)
			throws CompteInvalideException {
	
		if (Objects.isNull(compte))
			throw new CompteInvalideException("Le compte ne peut être null.");
		
		if ((StringUtils.isBlank(compte.getEmail())) || (StringUtils.isBlank(compte.getPassword())) )
			throw new CompteInvalideException("Le mail ou mot de passe du compte ne peuvent valoir null/blanc.");
		
		// ajouter vérification email valide.
		
	}
	
	
}