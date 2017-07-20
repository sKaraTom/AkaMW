package fr.santa.akachan.middleware.service;

import java.util.Map;
import java.util.UUID;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.text.WordUtils;

import fr.santa.akachan.middleware.dao.DaoException;
import fr.santa.akachan.middleware.dao.PrenomDao;
import fr.santa.akachan.middleware.objetmetier.estimation.Estimation;
import fr.santa.akachan.middleware.objetmetier.prenom.PrenomInexistantException;
import fr.santa.akachan.middleware.objetmetier.prenom.PrenomIntrouvableException;
import fr.santa.akachan.middleware.objetmetier.prenom.TendanceInvalideException;

@Stateless
public class PrenomService {

	@EJB
	private PrenomDao prenomDao;
	
	
	/**
	 * obtenir un prénom aléatoire avec l'option de tendance.
	 * 
	 * @param sexe
	 * @param refClient (pour ne pas obtenir de prénom déjà estimé)
	 * @param choixTendance (Integer : 1 sans option, 2 tendance, 3 ancien)
	 * @return String un prénom généré aléatoirement.
	 * @throws TendanceInvalideException si le nombre de la tendance ne correspond pas à une des valeurs définies.
	 * @throws PrenomIntrouvableException si aucun prénom obtenu.
	 * @throws DaoException si problème de communication avec bdd
	 */
	public String genererPrenomAleatoireSql(String sexe, UUID refClient,Integer choixTendance) throws TendanceInvalideException, DaoException, PrenomIntrouvableException {
		 
		String prenomAleatoire = null;
		
		if (choixTendance.equals(1)) {
			prenomAleatoire = this.prenomDao.obtenirPrenomAleatoire(sexe, refClient);
		}
		
		else if(choixTendance.equals(2)) {
			prenomAleatoire = this.prenomDao.obtenirPrenomAeatoireTendance(sexe, refClient);
		}
		
		else if(choixTendance.equals(3)) {
			prenomAleatoire = this.prenomDao.obtenirPrenomAeatoireAncien(sexe, refClient);
		}
		
		else {
			throw new TendanceInvalideException("l'option de tendance est invalide.");
		}
		
		// formater le prénom : Majuscule sur première lettre (y compris prénom composé) uniquement.
		String prenomFormate = WordUtils.capitalizeFully(prenomAleatoire, new char[] { '-',' ' });
		
		
		 return prenomFormate;
	}
	
	
	/** 
	 * Faire une recherche de prénoms (sql LIKE ou recherche exacte selon paramètre booleen)
	 * et renvoyer une map <String prenom, Boolean estEstime (true si estimation existante pour ce client)>.
	 * 
	 * @param estimation (prenom, sexe, et uuid client utilisés)
	 * @param rechercheExacte
	 * @return une Map<prenom trouvé, booleen si estimation existante>
	 * @throws PrenomInexistantException si la map retournée depuis la dao est vide.
	 */
	public Map<String, Boolean> chercherPrenomEtEstimation(Estimation estimation, Boolean rechercheExacte) throws PrenomInexistantException {
		
		String prenomAChercher = estimation.getPrenom();
		String sexe = estimation.getSexe();
		UUID refClient = estimation.getRefClient();
		
		Map<String, Boolean> resultatRecherche = prenomDao.chercherPrenomEtEstimation(prenomAChercher, sexe, refClient, rechercheExacte);
		
		if(resultatRecherche.isEmpty()) {
			throw new PrenomInexistantException();
		}
		
		return resultatRecherche;
	}
	
	
	
	
}
