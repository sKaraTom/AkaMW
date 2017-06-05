package fr.santa.akachan.middleware.dao;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.santa.akachan.middleware.cache.CachePrenomService;
import fr.santa.akachan.middleware.objetmetier.client.Client;
import fr.santa.akachan.middleware.objetmetier.client.ClientExistantException;
import fr.santa.akachan.middleware.objetmetier.client.ClientIntrouvableException;
import fr.santa.akachan.middleware.objetmetier.compte.Compte;
import fr.santa.akachan.middleware.objetmetier.estimation.Estimation;

@Stateless
@Transactional
public class ClientDao {
	
	private static final Logger LOGGER =
			LoggerFactory.getLogger(ClientDao.class);
	
	
	@PersistenceContext
	private EntityManager em;

	
	public Long obtenirNombreClients() {
		
		final String requeteJPQL = "Client.obtenirNbreClients";
		final Query requete = em.createNamedQuery(requeteJPQL);
		
		Long total = (Long) requete.getSingleResult();
		LOGGER.info("***************** "+total);
		
		return total;
	}
	
	
	public List<Client> obtenirClients() {

		final String requeteJPQL = "SELECT c FROM Client c";

		final TypedQuery<Client> requete = em.createQuery(requeteJPQL, Client.class);

		return requete.getResultList();
	}

	public Client obtenirClient(final UUID refClient) throws ClientIntrouvableException {

		Client client = null;

		client = em.find(Client.class, refClient);
		
		if(Objects.isNull(client)) {
			throw new ClientIntrouvableException();
		}
		

		return client;
	}
	
	// TODO : inutile avec onetoone Compte. à supprimer juqu'aux WS
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
