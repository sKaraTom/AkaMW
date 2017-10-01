package fr.santa.akachan.middleware.dao;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.santa.akachan.middleware.objetmetier.client.Client;
import fr.santa.akachan.middleware.objetmetier.client.ClientIntrouvableException;

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
	 * obtenir le nombre total de clients par sexe
	 * 
	 * @param sexe
	 * @return
	 * @throws DaoException
	 */
	public Long obtenirNombreClientsParSexe(final String sexe) throws DaoException {
		
		final String requeteJPQL = "Client.obtenirNbreClientsParSexe";
		
		final Query requete = em.createNamedQuery(requeteJPQL);
		requete.setParameter("sexe", sexe);
		
		Long total;
		
		try {
		total = (Long) requete.getSingleResult();
		}
		
		catch(Exception e) {
			throw new DaoException("echec à l'obtention du nombre de clients par sexe : " +  e.getClass() + " - " + e.getMessage());
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
	 * obtenir un client par son uuid
	 * 
	 * @param refClient
	 * @return
	 * @throws ClientIntrouvableException
	 */
	public Client obtenirClient(final UUID refClient) throws ClientIntrouvableException {

		Client client = null;

		client = em.find(Client.class, refClient);
		
		if(Objects.isNull(client)) {
			throw new ClientIntrouvableException("aucun client trouvé à cet uuid : ");
		}
		
		return client;
	}
	
	public Client obtenirClientSansDonneesSensibles(final UUID refClient) throws ClientIntrouvableException, DaoException {
		
		final String requeteJPQL = "Client.obtenirClientSansDonneesSensibles";
		
		final TypedQuery<Client> requete = em.createNamedQuery(requeteJPQL,Client.class);
		requete.setParameter("uuid", refClient);
		
		Client client;
		
		try {
			client = (Client)requete.getSingleResult();
		}
		
		catch(NoResultException e) {
			throw new ClientIntrouvableException("Aucun client existant pour cet uuid.");
		}

		catch(Exception e) {
			throw new DaoException("un problème est survenu à l'obtention du client depuis la bdd : " + e.getClass() + " - " + e.getMessage());
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
		
		try
		{
			em.getReference(Client.class, uuid);
			return true;
		}
		catch (final EntityNotFoundException e)
		{
			return false;
		}
	}
	
}
