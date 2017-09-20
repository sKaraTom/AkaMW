package fr.santa.akachan.middleware.service;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.santa.akachan.middleware.authentification.Jeton;
import fr.santa.akachan.middleware.authentification.JetonService;
import fr.santa.akachan.middleware.dao.CompteDao;
import fr.santa.akachan.middleware.dao.DaoException;
import fr.santa.akachan.middleware.objetmetier.client.Client;
import fr.santa.akachan.middleware.objetmetier.compte.Compte;
import fr.santa.akachan.middleware.objetmetier.compte.CompteExistantException;
import fr.santa.akachan.middleware.objetmetier.compte.CompteInexistantException;
import fr.santa.akachan.middleware.objetmetier.compte.CompteInvalideException;
import fr.santa.akachan.middleware.objetmetier.compte.CompteNonAdminException;
import fr.santa.akachan.middleware.objetmetier.compte.EmailInvalideException;
import fr.santa.akachan.middleware.objetmetier.compte.PasswordInvalideException;
import fr.santa.akachan.middleware.rest.EstimationRS;


@Stateless
//@Transactional
public class CompteService {

	private static final Logger LOGGER =
			LoggerFactory.getLogger(CompteService.class);
	
	@EJB
	private CompteDao compteDao;
	
	@EJB
	private JetonService jetonService;
	
	/** 
	 * créer un compte
	 * 
	 * @param compte à persister
	 * @throws CompteInvalideException
	 * @throws CompteExistantException si un compte identique est trouvé dans la base
	 * @throws EmailInvalideException
	 * @throws PasswordInvalideException 
	 */
	public void creerCompte(final Compte compte)
			throws CompteInvalideException, CompteExistantException, EmailInvalideException, PasswordInvalideException {
		
		// d'abord vérifier que le compte est valide (les variables sont formatées correctement)
		// avant d'interroger la BDD.
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
			
			//persister le compte va persister en cascade le client (relation bidirectionnelle).
			compteDao.ajouter(compte);
		}
		else {
			throw new CompteExistantException();
		}
	}
	
	// TODO : inutilisé côté ihm, voir si utile pour admin
	public Compte obtenirCompte(final String email) throws CompteInexistantException {
			
		Compte compte = compteDao.obtenir(email);
		
		return compte;
	}
	
	/** 
	 * modifier les informations client du compte.
	 * 
	 * @param compte qui contient le client à modifier.
	 * @throws CompteInexistantException si le compte à modifier n'existe pas en bdd
	 * @throws CompteInvalideException si le compte, ou paramètres du client null.
	 * @throws EmailInvalideException si mail n'est pas bien formaté.
	 */
	public void modifierCompte(final Compte compte) throws CompteInexistantException, CompteInvalideException, EmailInvalideException {
		
		if (Objects.isNull(compte)) {
			throw new CompteInvalideException("Aucun compte réceptionné à modifier.");
		}
		
		Compte compteValide = compteDao.obtenir(compte.getEmail());
		
		if((StringUtils.isBlank(compte.getClient().getPrenom())) || (StringUtils.isBlank(compte.getClient().getSexe()))) {
			throw new CompteInvalideException("Le prenom ou le sexe n'est pas renseigné.");
		}	
			
		if (compteValide.getPassword().equals(compte.getPassword())) {
			
			String prenomAFormater = compte.getClient().getPrenom();
			compteValide.getClient().setPrenom(WordUtils.capitalizeFully(prenomAFormater, new char[] { '-',' ' }));
			
			compteValide.getClient().setSexe(compte.getClient().getSexe());
			compteDao.modifier(compteValide);
		}
		else {
			throw new CompteInvalideException("Le mot de passe n'est pas valide.");
		}
	}
	
	/** 
	 * modifier le mot de passe.
	 * email client pour obtenir compte à modifier, password actuel à valider, nouveau password à persister.
	 * 
	 * @param listeChamps (0:email client,1:password actuel, 2:nouveau password)
	 * @throws CompteInexistantException si le mail réceptionné n'a pas permis d'obtenir de compte.
	 * @throws CompteInvalideException si le password actuel saisi n'est pas bon.
	 * @throws PasswordInvalideException si le nouveau password n'est pas formaté correctement.
	 */
	public void modifierMotDePasse(final List<String> listeChamps) 
			throws CompteInexistantException, CompteInvalideException, PasswordInvalideException {
		
		String emailClient = listeChamps.get(0);
		String passwordActuel = listeChamps.get(1);
		String nouveauPassword = listeChamps.get(2);
		
		Compte compteValide = compteDao.obtenir(emailClient);
		
		if (!compteValide.getPassword().equals(passwordActuel) ) {
			throw new CompteInvalideException("votre mot de passe actuel saisi n'est pas correct.");
		}
		
		this.validerPassword(nouveauPassword);
		
		compteValide.setPassword(nouveauPassword);
		compteDao.modifier(compteValide);
	}
		
	/** 
	 * connecter un compte : retourne un token d'authentification si succès.
	 * 
	 * @param email
	 * @param password
	 * @return
	 * @throws CompteInvalideException si email ou mot de passe non valide.
	 * @throws CompteInexistantException si aucun compte n'est trouvé avec ces crédentiels.
	 * @throws UnsupportedEncodingException problème à la création du token.
	 */
	public Jeton connecter(final String email, final String password)
		throws CompteInvalideException, CompteInexistantException, UnsupportedEncodingException {
		
			Compte compteValide = compteDao.obtenir(email);
			Jeton jeton = null;
			
			if (!compteValide.getPassword().equals(password) ||
				!compteValide.getEmail().equals(email)) {
				throw new CompteInvalideException("email ou mot de passe invalide.");
			}
			else {
				Integer dureeExpirationToken = 100*24*60*60*1000; // nombre de jours*24h*60mn*60sec*1000ms
				String token = jetonService.creerToken(compteValide,dureeExpirationToken);
				
				// je créé un jeton contenant l'uuid client, son prénom, et le token à retourner.
				jeton = new Jeton(compteValide.getClient().getUuid().toString(),compteValide.getClient().getPrenom(), token) ;
			}
			
			return jeton;
	
	}
	
	/**
	 * se connecter à la console d'administration.
	 * si succès envoie d'un token à durée courte côté ihm
	 * 
	 * @param compte
	 * @return String sessionId
	 * @throws CompteInexistantException
	 * @throws CompteInvalideException
	 * @throws CompteNonAdminException
	 * @throws UnsupportedEncodingException 
	 */
	public String connecterAdmin(final Compte compte) throws CompteInexistantException, CompteInvalideException, CompteNonAdminException, UnsupportedEncodingException {
		
		Compte compteValide = compteDao.obtenir(compte.getEmail());
		
		if (!compteValide.getPassword().equals(compte.getPassword()) ||
			!compteValide.getEmail().equals(compte.getEmail())) {
			throw new CompteInvalideException("login ou mot de passe invalide.");
		}
		
		if(!compteValide.getRole().equals("admin")) {
			throw new CompteNonAdminException("accès non autorisé.");
		}
		
		Integer dureeExpirationToken = 3600000; // 1h = 60mn*60sec*1000ms
		String token = jetonService.creerToken(compteValide,dureeExpirationToken);
		
		return token;
	}
	
	
	/** 
	 * valider un compte :
	 * vérifier que le compte n'est pas null, et qu'aucun champ n'est vide ou null.
	 *  
	 * @param compte à valider.
	 * @throws CompteInvalideException si le compte ou un champ null/blanc
	 * @throws EmailInvalideException si l'email n'est pas formaté correctement.
	 * @throws PasswordInvalideException 
	 */
	private void validerCompte(final Compte compte)
			throws CompteInvalideException, EmailInvalideException, PasswordInvalideException {
	
		if (Objects.isNull(compte))
			throw new CompteInvalideException("Le compte ne peut être null.");
		
		if((StringUtils.isBlank(compte.getClient().getPrenom())) || (StringUtils.isBlank(compte.getClient().getSexe())))
				throw new CompteInvalideException("Le prenom ou le sexe n'est pas renseigné.");
		
		this.validerEmail(compte.getEmail());
		this.validerPassword(compte.getPassword());
	}
	
	/** 
	 * regex de validation du mail.
	 * 
	 * @param email
	 * @throws EmailInvalideException
	 */
	private void validerEmail(final String email) throws EmailInvalideException {
		
		if (StringUtils.isBlank(email)) {
			throw new EmailInvalideException("l'email ne peut être vide.");
		}
		
		Boolean emailValide = Pattern.matches("^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$", email);
		
		if(!emailValide) {
			throw new EmailInvalideException("email invalide.");
		}
	}
	
	/**
	 * valider un password : qu'il n'est pas blanc, et a au minimum 4 caractères.
	 * 
	 * @param password
	 * @throws PasswordInvalideException
	 */
	private void validerPassword(final String password) throws PasswordInvalideException {
		
		if (StringUtils.isBlank(password)) {
			throw new PasswordInvalideException("le mot de passe doit contenir des caractères.");
		}
		
		if(password.length() < 4) {
			throw new PasswordInvalideException("le mot de passe doit contenir au moins 4 caractères");
		}
		
		Boolean passwordSansEspace = Pattern.matches(".*\\S.*",password);
		
		if(!passwordSansEspace) {
			throw new PasswordInvalideException("le mot de passe ne peut contenir d'espaces");
		}
	}
	
}
