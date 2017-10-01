package fr.santa.akachan.middleware.service;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.santa.akachan.middleware.dao.ClientDao;
import fr.santa.akachan.middleware.dao.DaoException;
import fr.santa.akachan.middleware.dao.EstimationDao;
import fr.santa.akachan.middleware.objetmetier.client.Client;
import fr.santa.akachan.middleware.objetmetier.client.ClientIntrouvableException;
import fr.santa.akachan.middleware.objetmetier.estimation.Estimation;
import fr.santa.akachan.middleware.objetmetier.estimation.EstimationExistanteException;
import fr.santa.akachan.middleware.objetmetier.estimation.EstimationIntrouvableException;
import fr.santa.akachan.middleware.objetmetier.estimation.EstimationInvalideException;

@Stateless
@Transactional
public class EstimationService {
	

	private static final Logger LOGGER =
			LoggerFactory.getLogger(EstimationService.class);
	
	@EJB
	private ClientDao clientDao;
	
	@EJB
	private EstimationDao estimationDao;
	
	
	/**
	 *  obtenir le nombre total tous clients confondus.
	 *  
	 * @return Long nombre total d'estimations
	 */
	public Long obtenirNbTotalEstimations() {
		
		Long total = estimationDao.obtenirNbTotalEstimations();
		return total;
	}
	
	/**
	 * obtenir le total d'estimations pour un sexe donné.
	 * 
	 * @param sexe
	 * @return
	 */
	public Long obtenirNbTotalEstimParSexe(String sexe) {
		
		Long totalParSexe = estimationDao.obtenirNbTotalEstimParSexe(sexe);
		
		return totalParSexe;
	}
	
	
	/**
	 * obtenir les 3 prénoms les plus estimés positivement pour un sexe.
	 * 
	 * @param sexe
	 * @return List<String> liste des 3 prénoms les plus populaires (garçons ou filles)
	 */
	public List<String> obtenirTop3Estimations(String sexe) {
		
		List<String> listeTopPrenoms = estimationDao.obtenirTop3PrenomsEstimes(sexe);
		
		return listeTopPrenoms;
	}
	
	
	/**
	 *  nombre total d'estimations pour un client sans autre distinction.
	 *  
	 * @param refClient
	 * @return Long le total
	 * @throws DaoException
	 */
	public Long obtenirNbEstimClient(UUID refClient) throws DaoException {
		Long totalEstimClient = estimationDao.obtenirNbEstimClient(refClient);
		return totalEstimClient;
	}
	
	/**
	 *  nombre total d'estimations d'un client, par sexe.
	 *  
	 * @param refClient
	 * @param sexe
	 * @return Long le total pour un sexe
	 * @throws DaoException
	 */
	public Long obtenirNbEstimClientParSexe(UUID refClient, String sexe) throws DaoException {
		
		Long total = estimationDao.obtenirNbEstimClientParSexe(refClient, sexe);
		return total;
	}
	
	// TODO : refactorer les 2 méthodes en une (paramètre akachan à remonter jusqu'à l'ihm).
	/** 
	 * obtenir toutes les estimations positives (akachan = "true")
	 * 
	 * @param refClient
	 * @return List<Estimation> liste Akachan
	 */
	public List<Estimation> obtenirListeAkachanTrue(final UUID refClient) {
		
		List<Estimation> listeAkachan = estimationDao.obtenirListeEstimations(refClient,"true");
		
		return listeAkachan;
	}	
	
	/** 
	 * obtenir toutes les estimations négatives (akachan = "false")
	 * 
	 * @param refClient
	 * @return List<Estimation> liste Noire
	 */
	public List<Estimation> obtenirListeNoire(final UUID refClient) {
		
		List<Estimation> listeNoire = estimationDao.obtenirListeEstimations(refClient,"false");
		
		return listeNoire;
	}	
	
	/** 
	 * Obtenir les estimations favorites d'un client (favori = 1)
	 * 
	 * @param refClient
	 * @return List<Estimation> liste des estimations favorites.
	 */
	public List<Estimation> obtenirListeFavoris(final UUID refClient) {
		
		List<Estimation> listeFavoris = estimationDao.obtenirListeFavoris(refClient);
		
		return listeFavoris;
	}
	
	/**
	 * créer une estimation
	 * 
	 * @param estimation
	 * @param refClient
	 * @throws ClientIntrouvableException si la vérification de l'uuid client a échoué
	 * @throws PrenomInexistantException si le prénom reçu est blanc.
	 * @throws EstimationExistanteException s'il existe déjà une estimation pour ce prénom.
	 * @throws EstimationInvalideException 
	 */
	public void estimerPrenom (Estimation estimation, UUID refClient)
			throws ClientIntrouvableException, EstimationExistanteException, EstimationInvalideException {
		
		Client client = clientDao.obtenirClient(refClient);
		
		if (StringUtils.isBlank(estimation.getPrenom())) {
			throw new EstimationInvalideException();
		}
		
		else {
			estimation.setRefClient(refClient);
			estimation.setPrenom(StringUtils.upperCase(estimation.getPrenom()));
			estimation.setFavori(false);
			estimationDao.creerEstimation(estimation);
		}
	}
	
	/**
	 * toggle de la valeur "akachan" : "true" si aimé, "false" si à mettre dans liste noire.
	 * changer la date par la date de ce changement de liste.
	 * 
	 * @param estimations
	 * @param akachan
	 */
	public void changerDeListeEstimations(final List<Estimation>estimations, final String akachan) {
	
		for(Estimation estimation:estimations) {
			// si on passe des estimations en liste noire, retirer le marqueur favori.
			if(akachan.equals("false")) {
				estimation.setFavori(false);
			}
			// remplacer la date de création de l'estimation par la date de changement de liste.
			estimation.setDateEstimation(Calendar.getInstance());
		}

		estimationDao.changerDeListeEstimations(estimations, akachan);
	}
	
	/**
	 * modifier une estimation pour ses variables pouvant être modifiées : booleen akachan, favori, et date.
	 * 
	 * @param estimation l'estimation à modifier
	 * @throws DaoException
	 * @throws EstimationIntrouvableException si l'estimation à modifier est inexistante.
	 */
	public void modifierEstimation (Estimation estimation) throws DaoException, EstimationIntrouvableException {
		
		Estimation estimationAModifier = estimationDao.obtenirEstimation(estimation.getUuid());
		
		estimationAModifier.setAkachan(estimation.getAkachan());
		estimationAModifier.setDateEstimation(estimation.getDateEstimation());
		estimationAModifier.setFavori(estimation.getFavori());
		
		estimationDao.modifierEstimation(estimationAModifier);
	}
	
	/**
	 * supprimer toutes les estimations d'un client
	 * 
	 * @param refClient
	 * @throws DaoException
	 */
	public void effacerToutesEstimationsClient(final UUID refClient) throws DaoException {
		
		estimationDao.supprimerToutesEstimationsClient(refClient);
	}
	
	
	
	
	}
	
	
