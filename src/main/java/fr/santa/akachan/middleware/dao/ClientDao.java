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

	
	/** 
	 * Obtenir le nombre total de clients inscrits.
	 * 
	 * @return nombre (Long)
	 * @throws DaoException 
	 */
	public Long obtenirNombreClients() throws DaoException {
		
		final String requeteJPQL = "Client.obtenirNbreClients";
		final Query requete = em.createNamedQuery(requeteJPQL);
		Long total;
		try {
		total = (Long) requete.getSingleResult();
		}
		catch(Exception e) {
			throw new DaoException();
		}
		
		return total;
	}
	
	/** 
	 * Obtenir la liste de tous les clients inscrits.
	 * TODO : A dérouler jusqu'aux WS pour admin.
	 * 
	 * @return Liste de clients (List<Client>)
	 */
	public List<Client> obtenirClients() {

		final String requeteJPQL = "SELECT c FROM Client c";

		final TypedQuery<Client> requete = em.createQuery(requeteJPQL, Client.class);

		return requete.getResultList();
	}

	/** 
	 * obtenir un client par son uuid (sert côté ihm pour infos compte)
	 * 
	 * @param refClient
	 * @return
	 * @throws ClientIntrouvableException
	 */
	public Client obtenirClient(final UUID refClient) throws ClientIntrouvableException {

		Client client = null;

		client = em.find(Client.class, refClient);
		
		if(Objects.isNull(client)) {
			throw new ClientIntrouvableException();
		}
		
		return client;
	}
	
	
	/** 
	 * Vérifier si le client existe dans la bdd.
	 * TODO : inutilisé, voir si supprimer après page admin.
	 * 
	 * @param uuid
	 * @return Boolean (true si client existe dans bdd, false sinon).
	 */
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
