package fr.santa.akachan.middleware.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
	private static final Logger LOGGER =
			LoggerFactory.getLogger(ClientService.class);
	
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
	 * obtenir le nombre total de clients par sexe
	 * 
	 * @param sexe
	 * @return
	 * @throws DaoException
	 */
	public Long obtenirNombreClientsParSexe(final String sexe) throws DaoException {
		
		Long totalParSexe = clientDao.obtenirNombreClientsParSexe(sexe);
		
		return totalParSexe;
	}
	

	/** 
	 * obtenir un client par son uuid sans les champs sensibles (password du compte, uuid client)
	 * (sert côté ihm pour infos compte)
	 * 
	 * @param refClient
	 * @return le client obtenu
	 * @throws ClientIntrouvableException
	 * @throws DaoException 
	 */
	public Client obtenirClientSansDonneesSensibles(UUID refClient) throws ClientIntrouvableException, DaoException {
		
		Client client = clientDao.obtenirClientSansDonneesSensibles(refClient);
		
		return client;
	}
	
	
	
	
}
