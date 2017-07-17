package fr.santa.akachan.middleware.service;

import java.util.List;
import java.util.Objects;
import java.util.Random;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;

import fr.santa.akachan.middleware.dao.CitationDao;
import fr.santa.akachan.middleware.dao.DaoException;
import fr.santa.akachan.middleware.objetmetier.citation.Citation;
import fr.santa.akachan.middleware.objetmetier.citation.CitationExistanteException;
import fr.santa.akachan.middleware.objetmetier.citation.CitationInexistanteException;
import fr.santa.akachan.middleware.objetmetier.citation.CitationInvalideException;

@Stateless
public class CitationService {

	
	@EJB
	CitationDao citationDao;
	
	/** 
	 * ajouter une citation à la table dédiée.
	 * 
	 * @param citation
	 * @throws CitationExistanteException si la citation à ajouter existe déjà.
	 * @throws CitationInvalideException si la citation n'a pas passé la méthode de validation.
	 */
	public void ajouterCitation(final Citation citation) throws CitationExistanteException, CitationInvalideException {
		
		validerCitation(citation);
		
		citationDao.ajouterCitation(citation);
	}
	
	
	/** 
	 * obtenir toutes les citations de la table.
	 * 
	 * @return List<Citations> liste de toutes les citations
	 * @throws DaoException si erreur lors de la communication avec la bdd.
	 */
	public List<Citation> obtenirCitations() throws DaoException {
		
		return citationDao.obtenirCitations();
	}
	
	/** 
	 * obtenir une citation aléatoire : 
	 * - tirer un chiffre au hasard à partir du nombre total de citations dans la table.
	 * - obtenir la citation à partir de l'id égal au chiffre tiré au hasard. 
	 * 
	 * @return Citation citation aléatoire
	 * @throws DaoException si l'obtention du nombre total a échoué.
	 * @throws CitationInexistanteException si la citation à l'id tiré n'existe pas.
	 */
	public Citation obtenirCitationAleatoire() throws DaoException, CitationInexistanteException {
		
		// initialiser le chiffre aléatoire.
		Integer chiffreAleat = 0;
		Random hasard = new Random();
		
		Integer totalCitations = obtenirNombreTotalCitations();
		
		// hasard.nextInt(max)+min pour ne pas commencer à 0.
		chiffreAleat = hasard.nextInt(totalCitations)+1;
		
		Citation citationAleatoire = citationDao.obtenirCitation(chiffreAleat);
		
		return citationAleatoire;
	}
	
	/**
	 * obtenir le nombre total (Integer) de citations dans la bdd
	 * 
	 * @return Integer total
	 * @throws DaoException si l'obtention du nombre depuis la bdd a échoué.
	 */
	public Integer obtenirNombreTotalCitations() throws DaoException {
		
		Long total = citationDao.obtenirNombreTotalCitations();
		
		Integer totalConverti;
		totalConverti = total.intValue();
		
		return totalConverti;
		
	}
	
	
	/** méthode de vérification qu'une citation est valide.
	 * 
	 * @param citation
	 * @throws CitationInvalideException si la citation n'a pas ses champs correctement renseignés ou est null.
	 */
	private void validerCitation(final Citation citation) throws CitationInvalideException {
		
		if(Objects.isNull(citation) || StringUtils.isBlank(citation.getAuteur()) || StringUtils.isBlank(citation.getContenu())) {
			throw new CitationInvalideException("champ null ou blanc");
		}
			
	}
	
	
}
