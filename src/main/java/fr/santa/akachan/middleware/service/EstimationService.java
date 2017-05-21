package fr.santa.akachan.middleware.service;

import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.transaction.Transactional;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import fr.santa.akachan.middleware.cache.CachePrenomService;
import fr.santa.akachan.middleware.dao.ClientDao;
import fr.santa.akachan.middleware.dao.DaoException;
import fr.santa.akachan.middleware.dao.EstimationDao;
import fr.santa.akachan.middleware.dao.PrenomDao;
import fr.santa.akachan.middleware.objetmetier.client.Client;
import fr.santa.akachan.middleware.objetmetier.client.ClientIntrouvableException;
import fr.santa.akachan.middleware.objetmetier.estimation.Estimation;
import fr.santa.akachan.middleware.objetmetier.estimation.EstimationExistanteException;
import fr.santa.akachan.middleware.objetmetier.prenom.PrenomInexistantException;
import fr.santa.akachan.middleware.objetmetier.prenom.PrenomInsee;

@Stateless
@Transactional
public class EstimationService {

	@EJB
	private ClientDao clientDao;
	
	@EJB
	private EstimationDao estimationDao;
	
	@EJB
	private CachePrenomService cachePrenomService;
	
	// nombre total tous clients confondus.
	public Long obtenirNbTotalEstimations() {
		
		Long total = estimationDao.obtenirNbTotalEstimations();
		return total;
	}
	
	public List<String> obtenirTop3Estimations(String sexe) {
		
		List<String> listeTopPrenoms = estimationDao.obtenirTop3PrenomsEstimes(sexe);
		
		return listeTopPrenoms;
	}
	
	// nombre total pour un client.
	public Long obtenirNbEstimClient(UUID refClient) throws DaoException {
		Long totalEstimClient = estimationDao.obtenirNbEstimClient(refClient);
		return totalEstimClient;
	}
	
	// nombre pour un client, par sexe.
	public Long obtenirNbEstimClientParSexe(UUID refClient, String sexe) throws DaoException {
		
		Long total = estimationDao.obtenirNbEstimClientParSexe(refClient, sexe);
		return total;
	}
	
	/* METHODE POUR TEST STATEFUL
	public void estimerPrenom(Estimation estimation, UUID refClient) throws ClientIntrouvableException, PrenomInexistantException, EstimationExistanteException {
		
		cachePrenomService.estimerPrenom(estimation, refClient);
	}
	*/
	
	public void estimerPrenom (Estimation estimation, UUID refClient)
			throws ClientIntrouvableException, PrenomInexistantException, EstimationExistanteException {
		
		Client client = clientDao.obtenirClient(refClient);
		
		if (!clientDao.contenirClient(refClient)) {
            throw new ClientIntrouvableException();
		}
		
		if (StringUtils.isBlank(estimation.getPrenom())) {
			throw new PrenomInexistantException();
		}
		
		else {
		estimation.setRefClient(refClient);
		estimation.setPrenom(StringUtils.upperCase(estimation.getPrenom()));
		estimation.setFavori(0);
		estimationDao.creerEstimation(estimation);
		clientDao.modifierClient(client);
		}
	}
	
	public void changerDeListeEstimations(final List<Estimation>estimations, final String akachan) {
	
		for(Estimation estimation:estimations) {
			// si on passe des estimations en liste noire, retirer le marqueur favori.
			if(akachan.equals("false")) {
				estimation.setFavori(0);
			}
			// inscrire date changement de liste.
			estimation.setDateEstimation(Calendar.getInstance());
		}

		estimationDao.changerDeListeEstimations(estimations, akachan);
	}
	
	
	public void modifierEstimation (Estimation estimation) {
		
		// forcer le fuseau à être sur Paris à la modification d'estimation, sinon soustraction de 2h...
		// TODO à revoir ce problème de GMT
		Calendar dateFormatee = estimation.getDateEstimation();
		dateFormatee.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
		
		estimation.setDateEstimation(dateFormatee);
		
		estimationDao.modifierEstimation(estimation);
	}

	
	}
	
	
