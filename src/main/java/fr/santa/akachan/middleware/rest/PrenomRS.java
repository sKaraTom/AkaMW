package fr.santa.akachan.middleware.rest;

import java.util.List;

import javax.ejb.EJB;
import javax.jws.WebService;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fr.santa.akachan.middleware.service.PrenomService;

@WebService
@Transactional
@Path("/prenom")
public class PrenomRS {
	
	@EJB
	private PrenomService prenomService;
	
	
	
	
}
