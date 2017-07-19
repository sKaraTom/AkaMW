package fr.santa.akachan.middleware.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Stateless
@Transactional
public class PrenomDao {
	
	@PersistenceContext
	private EntityManager em;
	
	
	
}
