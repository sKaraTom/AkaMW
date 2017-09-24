package fr.santa.akachan.middleware.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.UUID;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.santa.akachan.middleware.dao.DaoException;
import fr.santa.akachan.middleware.dao.EstimationDao;
import fr.santa.akachan.middleware.dao.PrenomInseeDao;
import fr.santa.akachan.middleware.objetmetier.estimation.Estimation;
import fr.santa.akachan.middleware.objetmetier.prenom.TendanceInvalideException;
import fr.santa.akachan.middleware.objetmetier.prenomInsee.PrenomInsee;
import fr.santa.akachan.middleware.objetmetier.prenomInsee.PrenomInseeInexistantException;

@Stateless
public class PrenomInseeService {
	
	private static final Logger LOGGER =
			LoggerFactory.getLogger(PrenomInseeService.class);
	
	@EJB
	private PrenomInseeDao prenomInseeDao;
	
	
	/** 
	 * obtenir un tableau de 1900 à 2015 avec nombre de naissances associées pour ce un prénom.
	 * 1.peupler le tableau de l'index 0 à 115 (correspond aux années 1900 à 2015)
	 * 2. ajouter pour chaque année renseignée (année -1900 pour tomber sur l'index) le nombre de naissances.
	 * 
	 * @param label le prénom
	 * @param sexe le sexe du prénom
	 * @return ArrayList<Integer> une liste de nombre de naissances de 1900 à 2015 pour courbe de statistiques.
	 * @throws DaoException
	 */
	public ArrayList<Integer> obtenirNaissancesPrenom(String label, String sexe) throws DaoException {
		
		List<PrenomInsee> statsPrenom = prenomInseeDao.obtenirStatsPrenom(label,sexe);
		ArrayList<Integer> listeNaissances = new ArrayList<>();
			
			// je commence par peupler la liste de 0 naissance pour les années 1900 à 2015 (index 0 à 115)
			for (Integer i=0; i<=115;i++) {
				listeNaissances.add(0);
			}
			
			// puis je peuple à chaque année de naissance récupérée (année de naissance -1900 pour tomber sur l'index).
			// à cet index j'ajoute le nombre de naissances.
			for( PrenomInsee prenom : statsPrenom) {
				listeNaissances.set((prenom.getAnnee()-1900), prenom.getNombreNaissances());
			}
		return listeNaissances;
	}
	

	
	
	
}
