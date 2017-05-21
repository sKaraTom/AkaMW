package fr.santa.akachan.middleware.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.ejb.StatefulTimeout;
import javax.ejb.Stateless;
import javax.transaction.Transactional;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.santa.akachan.middleware.dao.ClientDao;
import fr.santa.akachan.middleware.dao.EstimationDao;
import fr.santa.akachan.middleware.dao.PrenomDao;
import fr.santa.akachan.middleware.objetmetier.client.Client;
import fr.santa.akachan.middleware.objetmetier.client.ClientIntrouvableException;
import fr.santa.akachan.middleware.objetmetier.estimation.Estimation;
import fr.santa.akachan.middleware.objetmetier.estimation.EstimationExistanteException;
import fr.santa.akachan.middleware.objetmetier.prenom.PrenomInexistantException;

@Stateless
@Transactional
public class CachePrenomService {
	
	@EJB
	private PrenomDao prenomDao;
	
	@EJB
	private ClientDao clientDao;
	
	@EJB
	private EstimationDao estimationDao;
	
	@EJB
	private CachePrenom cachePrenom;
	
	private static final Logger LOGGER =
			LoggerFactory.getLogger(CachePrenomService.class);
	
	public CachePrenomService() {
		super();
		
	}

	public void peuplerListes() {
		
	}
	
	/** A REFACTORER EN PRENANT EN COMPTE LE SEXE (liste différente...) */
	public String genererPrenomAleatoire(String sexe, UUID refClient, Integer choixTendance) {
		/*
		// peupler la liste à son premier appel.
		if ((choixTendance.equals(1)) && (listePrenoms.isEmpty())) {
			this.listePrenoms = prenomDao.obtenirListePrenomsPourClient(sexe, refClient);
			LOGGER.info("***************************************nbre prenoms initialisation liste principale : " + listePrenoms.size());
		}
		if((choixTendance.equals(2)) && (listePrenomsTendances.isEmpty())) {
			listePrenomsTendances = prenomDao.obtenirPrenomsTendancesClient(sexe, refClient);
		}
		if((choixTendance.equals(3)) && (listePrenomsAnciens.isEmpty())) {
			listePrenomsAnciens = prenomDao.obtenirPrenomsAnciensClient(sexe, refClient);
		}

		String prenom = null;
		Random hasard = new Random();
		
		// suivant le choix de la tendance, le prénom est pioché dans une liste différente.
		if(choixTendance.equals(1)) {
			// prend en compte le 0.
			Integer chiffreAleat = hasard.nextInt(listePrenoms.size());
			prenom = listePrenoms.get(chiffreAleat);
			LOGGER.info("***************************************nbre prenoms liste / get prénom aléatoire : " + listePrenoms.size());
		}
		if(choixTendance.equals(2))
		{
			Integer chiffreAleat = hasard.nextInt(listePrenomsTendances.size());
			prenom = listePrenomsTendances.get(chiffreAleat);	
		}
		if(choixTendance.equals(3)) {
			Integer chiffreAleat = hasard.nextInt(listePrenomsAnciens.size());
			prenom = listePrenomsAnciens.get(chiffreAleat);	
		}
		*/
		
		if(cachePrenom.estVide()) {
			cachePrenom.setListePrenoms(prenomDao.obtenirListePrenomsPourClient(sexe, refClient));
			LOGGER.info("***************************************nbre prenoms initialisation liste principale : " + cachePrenom.getTailleListe());
		}
		String prenom = null;
		Random hasard = new Random();
		
		Integer chiffreAleat = hasard.nextInt(cachePrenom.getTailleListe()); // prend en compte le 0.
		prenom = cachePrenom.getPrenom(chiffreAleat);
		LOGGER.info("***************************************nbre prenoms liste / get prénom aléatoire : " + cachePrenom.getTailleListe());
		
		// renvoie un prénom formaté : majuscule, même sur prénom composé.
		return WordUtils.capitalizeFully(prenom, new char[] { '-',' ' });
	}
	
	
	public void estimerPrenom (Estimation estimation, UUID refClient)
			throws ClientIntrouvableException, PrenomInexistantException, EstimationExistanteException {
	
		Client client = clientDao.obtenirClient(refClient);
		
		String prenomMajuscules = StringUtils.upperCase(estimation.getPrenom());
		LOGGER.info("***************************************nbre prenoms liste principale / avant remove : " + cachePrenom.getTailleListe());
		cachePrenom.supprimerPrenom(prenomMajuscules);
		LOGGER.info("***************************************nbre prenoms liste principale / apres remove : " + cachePrenom.getTailleListe());	
		
		// partie pour la persistance.	
		estimation.setRefClient(refClient); // à revoir, peut être inclus directement à la création de l'estimation.
		estimation.setPrenom(prenomMajuscules);
		estimation.setFavori(0); // idem, à la création de l'estimation.
		estimationDao.creerEstimation(estimation);
		clientDao.modifierClient(client);
	}
	
	
	
}
