package fr.santa.akachan.middleware.service;

import java.io.UnsupportedEncodingException;
import java.util.Objects;
import java.util.regex.Pattern;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.santa.akachan.middleware.dao.CompteDao;
import fr.santa.akachan.middleware.objetmetier.client.Client;
import fr.santa.akachan.middleware.objetmetier.compte.Compte;
import fr.santa.akachan.middleware.objetmetier.compte.CompteDejaExistantException;
import fr.santa.akachan.middleware.objetmetier.compte.CompteInexistantException;
import fr.santa.akachan.middleware.objetmetier.compte.CompteInvalideException;
import fr.santa.akachan.middleware.objetmetier.compte.EmailInvalideException;
import fr.santa.akachan.middleware.rest.EstimationRS;
import fr.santa.akachan.middleware.securite.Jeton;
import fr.santa.akachan.middleware.securite.JwtCreation;


@Stateless
//@Transactional
public class CompteService {

	private static final Logger LOGGER =
			LoggerFactory.getLogger(CompteService.class);
	
	@EJB
	private CompteDao compteDao;
	
	@EJB
	private JwtCreation jwtCreation;
	
	
	public void creerCompte(final Compte compte)
			throws CompteInvalideException, CompteDejaExistantException, EmailInvalideException {
		
		// d'abord vérifier que le compte est valide avant d'interroger la BDD.
		this.validerCompte(compte);
		
		boolean estContenu = compteDao.contenir(compte);
		
		if(!estContenu) {
			// forcer le formatage du prénom client pour la persistance.
			String prenomClientAFormater = compte.getClient().getPrenom();
			String prenomFormate = WordUtils.capitalizeFully(prenomClientAFormater, new char[] { '-',' ' });
			compte.getClient().setPrenom(prenomFormate);
			
			// instancier un client puis lui associer le compte avec setCompte(compte).
			Client clientAPersister = new Client();	
			clientAPersister.setPrenom(prenomFormate);
			clientAPersister.setSexe(compte.getClient().getSexe());
			clientAPersister.setCompte(compte);
			
			//associer le client créé au compte.
			compte.setClient(clientAPersister);
			
			//persister le compte va persister en cascade le client.
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
	
	public void modifierCompte(final Compte compte) throws CompteInexistantException, CompteInvalideException, EmailInvalideException {
		
		// d'abord vérifier que le compte est valide avant d'interroger la BDD.
		this.validerCompte(compte);
		
		Compte compteValide = compteDao.obtenir(compte.getEmail());
		
		if (compteValide.getPassword().equals(compte.getPassword())) {
		
		// créer un client qui a les valeurs du compte.client pour lui associer le compte.
		// nécessaire pour persistance du compte avec relation bidirectionnelle.
		Client client = new Client();
		client.setUuid(compte.getClient().getUuid());
		client.setPrenom(WordUtils.capitalizeFully(compte.getClient().getPrenom(), new char[] { '-',' ' }));
		client.setSexe(compte.getClient().getSexe());
		client.setCompte(compte);
		
		compte.setClient(client);
		
		compteDao.modifier(compte);
		}
		else {
			throw new CompteInvalideException("password non valide");
		}
		}
		
	
	public Jeton connecter(final String email, final String password)
		throws CompteInvalideException, CompteInexistantException, UnsupportedEncodingException {
		
			Compte compteValide = compteDao.obtenir(email);
			Jeton jeton = null;
			
			// redondant avec méthode de la dao.
			if(Objects.isNull(compteValide)) {
				throw new CompteInexistantException("le compte n'existe pas");
			}
			
			if (!compteValide.getPassword().equals(password) ||
					!compteValide.getEmail().equals(email)) {
					throw new CompteInvalideException("connexion impossible");
			}

			//assert compteValide.getEmail().equals(compte.getEmail());
			
			else {
				String token = jwtCreation.creerToken2(compteValide);
				// je créé un jeton contenant l'uuid client, son prénom, et le token à retourner.
				jeton = new Jeton(compteValide.getClient().getUuid().toString(),compteValide.getClient().getPrenom(), token) ;
			}
			
			return jeton;
	
	}

	private void validerCompte(final Compte compte)
			throws CompteInvalideException, EmailInvalideException {
	
		if (Objects.isNull(compte))
			throw new CompteInvalideException("Le compte ne peut être null.");
		
		if ((StringUtils.isBlank(compte.getEmail())) || (StringUtils.isBlank(compte.getPassword())) )
			throw new CompteInvalideException("Le mail ou mot de passe du compte ne peuvent valoir null/blanc.");
		
		if((StringUtils.isBlank(compte.getClient().getPrenom())) || (StringUtils.isBlank(compte.getClient().getSexe())))
				throw new CompteInvalideException("Le mail ou mot de passe du compte ne peuvent valoir null/blanc.");
		
		// vérification email valide.
		this.validerEmail(compte.getEmail());
	}
	
	
	private void validerEmail(final String email) throws EmailInvalideException {
		
		Boolean emailValide = Pattern.matches("^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)+$", email);
		
		if(!emailValide) {
			throw new EmailInvalideException("email non formaté correctement.");
		}
	}
	
}
