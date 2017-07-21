package fr.santa.akachan.middleware.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ejb.EJB;
import javax.jws.WebService;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.santa.akachan.middleware.authentification.Securise;
import fr.santa.akachan.middleware.dao.DaoException;
import fr.santa.akachan.middleware.dao.PrenomInseeDao;
import fr.santa.akachan.middleware.objetmetier.estimation.Estimation;
import fr.santa.akachan.middleware.objetmetier.prenom.TendanceInvalideException;
import fr.santa.akachan.middleware.objetmetier.prenomInsee.PrenomInsee;
import fr.santa.akachan.middleware.objetmetier.prenomInsee.PrenomInseeInexistantException;
import fr.santa.akachan.middleware.service.PrenomInseeService;

@WebService
@Transactional
@Path("/insee")
public class PrenomInseeRS {
	
	private static final Logger LOGGER =
			LoggerFactory.getLogger(PrenomInseeRS.class);
	
	@EJB
	private PrenomInseeService prenomInseeService;
	
	
	@GET
	@Securise
    @Produces(MediaType.APPLICATION_JSON)
	@Path("/pop/{sexe}/{label}")
	public Response obtenirNaissances(@PathParam("label") String label, @PathParam("sexe") String sexe) {
		
		 Response.ResponseBuilder builder = null;

        ArrayList<Integer> liste = null;
        
		try {
			liste = prenomInseeService.obtenirNaissancesPrenom(label,sexe);
			builder = Response.ok(liste);
		} catch (DaoException e) {
			builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR);
		}
		return builder.build();
	}
	
	
	

	
	
}
