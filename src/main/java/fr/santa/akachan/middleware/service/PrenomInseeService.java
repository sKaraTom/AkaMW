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
import fr.santa.akachan.middleware.objetmetier.prenomInsee.PrenomInsee;
import fr.santa.akachan.middleware.objetmetier.prenomInsee.PrenomInseeInexistantException;
import fr.santa.akachan.middleware.objetmetier.prenomInsee.TendanceInvalideException;

@Stateless
public class PrenomInseeService {
	
	private static final Logger LOGGER =
			LoggerFactory.getLogger(PrenomInseeService.class);
	
	@EJB
	private PrenomInseeDao prenomInseeDao;
	
	@EJB
	private EstimationDao estimationDao;
	
	
	/** 
	 * Faire une recherche de prénoms (sql LIKE ou recherche exacte selon paramètre booleen)
	 * et renvoyer en booléen associé si une estimation existe déjà pour ce client.
	 * (Permet côté IHM de savoir si prénom déjà estimé).
	 * 
	 * @param estimation
	 * @param rechercheExacte
	 * @return un hashmap <prenom trouvé, booleen si estimation existante>
	 * @throws PrenomInexistantException si la map retournée depuis la dao est vide.
	 */
	public Map<String, Boolean> chercherPrenomEtEstimation(Estimation estimation, Boolean rechercheExacte) throws PrenomInseeInexistantException {
		
		String prenomAChercher = estimation.getPrenom();
		String sexe = estimation.getSexe();
		UUID refClient = estimation.getRefClient();
		
		Map<String, Boolean> resultatRecherche = prenomInseeDao.chercherPrenomEtEstimation(prenomAChercher, sexe, refClient, rechercheExacte);
		
		if(resultatRecherche.isEmpty()) {
			throw new PrenomInseeInexistantException();
		}
		
		return resultatRecherche;
	}
	
	
	/**
	 * générer un prénom aléatoire suivant l'id client (pour ne pas estimer 2 fois un prénom),
	 * le sexe, et le choix de la tendance.
	 * 
	 * @param sexe
	 * @param refClient
	 * @param choixTendance
	 * @return String un prénom généré aléatoirement.
	 * @throws TendanceInvalideException si le nombre de la tendance ne correspond pas à une des valeurs définies.
	 */
	public String genererPrenomAleatoireSql(String sexe, UUID refClient,Integer choixTendance) throws TendanceInvalideException {
		 
		String prenomAleatoire = null;
		
		if (choixTendance.equals(1)) {
			prenomAleatoire = this.prenomInseeDao.obtenirPrenomAleatoireSql(sexe, refClient);
		}
		
		else if(choixTendance.equals(2)) {
			prenomAleatoire = this.prenomInseeDao.obtenirPrenomAeatoireTendance(sexe, refClient);
		}
		
		else if(choixTendance.equals(3)) {
			prenomAleatoire = this.prenomInseeDao.obtenirPrenomAeatoireAncien(sexe, refClient);
		}
		
		else {
			throw new TendanceInvalideException();
		}
		
		 return WordUtils.capitalizeFully(prenomAleatoire, new char[] { '-',' ' });
	}
	
	
	/* METHODES AVANT CACHE : récupérer une liste éligible et faire un random dessus
	public String genererPrenomAleatoire(String sexe, UUID refClient, Integer choixTendance) throws PrenomInexistantException {
		
		List<String> listePrenoms = null;
		
		if (choixTendance.equals(1)) {
			listePrenoms = prenomDao.obtenirListePrenomsPourClient(sexe, refClient);
		}
		else if(choixTendance.equals(2)) {
			listePrenoms = prenomDao.obtenirPrenomsTendancesClient(sexe, refClient);
		}
		else if(choixTendance.equals(3)) {
			listePrenoms = prenomDao.obtenirPrenomsAnciensClient(sexe, refClient);
		}
			
		if (!listePrenoms.isEmpty()) {
			
			Random hasard = new Random();
			// prend en compte le 0.
			Integer chiffreAleat = hasard.nextInt(listePrenoms.size());
	
			String prenom = listePrenoms.get(chiffreAleat);
			
			return WordUtils.capitalizeFully(prenom, new char[] { '-',' ' });
		}
		else { throw new PrenomInexistantException();
		}
	}
	
	
	*/
	
	
	/* VERSION SQL
	public String genererPrenomAleatoireSQL(String sexe, UUID refClient) {

		List<String> ListePrenomsParGenre = prenomDao.obtenirListePrenomsSQL(sexe, refClient);

		// pas besoin de prendre en compte le 0 sur la liste.
		Random hasard = new Random();
		Integer reference = hasard.nextInt(ListePrenomsParGenre.size());

		String prenom = ListePrenomsParGenre.get(reference);
		//return StringUtils.capitalize(prenom); // majuscule mais pas sur 2eme prenom composé
		return WordUtils.capitalizeFully(prenom, new char[] { '-',' ' });								
	}
	*/
	
	
	/** 
	 * obtenir un tableau de 1900 à 2015 avec nombre de naissances associées pour ce un prénom.
	 * 1.peupler le tableau de l'index 0 à 115 (correspond aux années 1900 à 2015)
	 * 2. ajouter pour chaque année renseignée (année -1900 pour tomber sur l'index) le nombre de naissances.
	 * 
	 * @param label
	 * @param sexe
	 * @return une liste d'années (index), nombre de naissances pour courbe de statistiques.
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
				listeNaissances.set((prenom.getAnnee()-1900), prenom.getNombreNaissance());
			}
		return listeNaissances;
	}
	

	
	
	
}