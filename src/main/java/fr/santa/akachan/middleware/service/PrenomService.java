package fr.santa.akachan.middleware.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import fr.santa.akachan.middleware.dao.DaoException;
import fr.santa.akachan.middleware.dao.PrenomInseeDao;
import fr.santa.akachan.middleware.objetmetier.prenomInsee.PrenomInsee;

@Stateless
public class PrenomService {

	@EJB
	private PrenomInseeDao prenomInseeDao;
	
	
	
	
	
}
