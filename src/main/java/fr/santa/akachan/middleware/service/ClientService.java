package fr.santa.akachan.middleware.service;

import java.util.List;
import java.util.UUID;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.transaction.Transactional;

import fr.santa.akachan.middleware.dao.ClientDao;
import fr.santa.akachan.middleware.objetmetier.client.Client;
import fr.santa.akachan.middleware.objetmetier.client.ClientExistantException;
import fr.santa.akachan.middleware.objetmetier.client.ClientIntrouvableException;
import fr.santa.akachan.middleware.objetmetier.client.ClientInvalideException;
import fr.santa.akachan.middleware.objetmetier.estimation.Estimation;

@Stateless
@Transactional
public class ClientService {

	@EJB
	private ClientDao clientDao;
	
	public void creerClient (Client client) throws ClientInvalideException, ClientExistantException {
		
		//validation plus complète à implémenter.
		if(client == null) {
			throw new ClientInvalideException();
		}
		if(clientDao.contenirClient(client.getUuid())) {
			throw new ClientExistantException();
		}
		else {
		clientDao.creerClient(client);
		}
	}
	
	public Client obtenirClient(UUID refClient) throws ClientIntrouvableException {
		
		Client client = clientDao.obtenirClient(refClient);
		return client;
	}
	
	
	public List<Estimation> obtenirListeAkachanTrue(final UUID refClient) {
		
		List<Estimation> listeAkachan = clientDao.obtenirListAkachanTrue(refClient);
		
		return listeAkachan;
	}	
	
	public List<Estimation> obtenirListeNoire(final UUID refClient) {
		
		List<Estimation> listeNoire = clientDao.obtenirListeNoire(refClient);
		
		return listeNoire;
	}	
	
	public List<Estimation> obtenirListeFavoris(final UUID refClient) {
		
		List<Estimation> listeFavoris = clientDao.obtenirListeFavoris(refClient);
		
		return listeFavoris;
	}
	
	
	
	
	
}
