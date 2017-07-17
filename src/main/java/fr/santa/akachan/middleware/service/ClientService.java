package fr.santa.akachan.middleware.service;

import java.util.List;
import java.util.UUID;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.transaction.Transactional;

import fr.santa.akachan.middleware.dao.ClientDao;
import fr.santa.akachan.middleware.dao.DaoException;
import fr.santa.akachan.middleware.objetmetier.client.Client;
import fr.santa.akachan.middleware.objetmetier.client.ClientExistantException;
import fr.santa.akachan.middleware.objetmetier.client.ClientIntrouvableException;
import fr.santa.akachan.middleware.objetmetier.client.ClientInvalideException;
import fr.santa.akachan.middleware.objetmetier.compte.Compte;
import fr.santa.akachan.middleware.objetmetier.estimation.Estimation;

@Stateless
@Transactional
public class ClientService {

	@EJB
	private ClientDao clientDao;
	
	/** 
	 * Obtenir le nombre total de clients inscrits.
	 * 
	 * @return nombre (Long)
	 * @throws DaoException 
	 */
	public Long obtenirNombreClients() throws DaoException {
		
		Long total = clientDao.obtenirNombreClients();
		return total;
	}
	

	/** 
	 * obtenir un client par son uuid sans les champs sensibles (password du compte, uuid client)
	 * (sert côté ihm pour infos compte)
	 * 
	 * @param refClient
	 * @return le client obtenu
	 * @throws ClientIntrouvableException
	 */
	public Client obtenirClientSansDonneesSensibles(UUID refClient) throws ClientIntrouvableException {
		
		Client clientDeReference = clientDao.obtenirClient(refClient);
		
		// Instancier un client qui prendra les valeurs souhaitées du client qu'on doit retourner :
		// choisir les champs souhaités et ne pas modifier le client de référence.
		Client clientARetourner = new Client();
		clientARetourner.setPrenom(clientDeReference.getPrenom());
		clientARetourner.setSexe(clientDeReference.getSexe());
		
		// instancier un compte et remplacer le mot de passe par "confidentiel".
		// Le lier au client retourné.
		Compte compte = new Compte(clientDeReference.getCompte().getEmail(),"confidentiel",clientARetourner);
		clientARetourner.setCompte(compte);
		
		return clientARetourner;
	}
	
	/** 
	 * obtenir toutes les estimations positives (akachan = "true")
	 * 
	 * @param refClient
	 * @return List<Estimation> liste Akachan
	 */
	public List<Estimation> obtenirListeAkachanTrue(final UUID refClient) {
		
		List<Estimation> listeAkachan = clientDao.obtenirListAkachanTrue(refClient);
		
		return listeAkachan;
	}	
	
	/** 
	 * obtenir toutes les estimations négatives (akachan = "false")
	 * 
	 * @param refClient
	 * @return List<Estimation> liste Noire
	 */
	public List<Estimation> obtenirListeNoire(final UUID refClient) {
		
		List<Estimation> listeNoire = clientDao.obtenirListeNoire(refClient);
		
		return listeNoire;
	}	
	
	/** 
	 * Obtenir les estimations favorites d'un client (favori = 1)
	 * 
	 * @param refClient
	 * @return List<Estimation> liste des estimations favorites.
	 */
	public List<Estimation> obtenirListeFavoris(final UUID refClient) {
		
		List<Estimation> listeFavoris = clientDao.obtenirListeFavoris(refClient);
		
		return listeFavoris;
	}
	
	
	
	
	
}
