package fr.santa.akachan.middleware.dao;

import java.util.List;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import fr.santa.akachan.middleware.objetmetier.client.Client;
import fr.santa.akachan.middleware.objetmetier.client.ClientExistantException;
import fr.santa.akachan.middleware.objetmetier.compte.Compte;
import fr.santa.akachan.middleware.objetmetier.estimation.Estimation;

@Stateless
@Transactional
public class ClientDao {

	@PersistenceContext
	private EntityManager em;

	
	public List<Client> obtenirClients() {

		final String requeteJPQL = "SELECT c FROM Client c";

		final TypedQuery<Client> requete = em.createQuery(requeteJPQL, Client.class);

		return requete.getResultList();
	}

	public Client obtenirClient(final UUID refClient) {

		Client client = null;

		client = em.find(Client.class, refClient);

		return client;
	}

	public void creerClient(Client client) throws ClientExistantException {
		
		try {
			em.persist(client);
		}
		catch(final EntityExistsException e) {
			throw new ClientExistantException();
		}
	}

	public void modifierClient(final Client client) {
		
		em.merge(client);
	}
	
	// toutes les estimations, true et false. utilité ?
	public List<Estimation> obtenirListeEstimations(final UUID refClient) {

		 Client client = em.find(Client.class, refClient);
		 
		 List<Estimation> liste = client.getListeAkachan();

		return liste;
	}
	
	public List<Estimation> obtenirListAkachanTrue(final UUID refClient) {

		 final String requeteJPQL = "Estimation.obtenirListeAkachan";
		 
		 final Query requete = em.createNamedQuery(requeteJPQL);
			requete.setParameter("refclient", refClient);
			
		List<Estimation> listeAkachan = requete.getResultList();
		 
		return listeAkachan;
	}
	
	public List<Estimation> obtenirListeNoire(final UUID refClient) {

		 final String requeteJPQL = "Estimation.obtenirListeNoire";
		 
		 final Query requete = em.createNamedQuery(requeteJPQL);
			requete.setParameter("refclient", refClient);
			
		List<Estimation> listeNoire = requete.getResultList();
		 
		return listeNoire;
	}
	
	public List<Estimation> obtenirListeFavoris(final UUID refClient) {
		
		final String requeteJPQL ="Estimation.obtenirListeFavoris";
		
		final Query requete = em.createNamedQuery(requeteJPQL);
		requete.setParameter("refclient", refClient);
		
		List<Estimation> listeFavoris = requete.getResultList();
	 
		return listeFavoris;
	}
	
	
	public Boolean contenirClient(final UUID uuid){
		
		Boolean estTrouve = null;
		
		try
		{
			em.getReference(Client.class, uuid);
			estTrouve = true;
		}
		catch (final EntityNotFoundException e)
		{
			estTrouve = false;
		}
		return estTrouve;
	}
	
}
