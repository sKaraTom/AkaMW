package fr.santa.akachan.middleware.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.jws.WebService;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.santa.akachan.middleware.authentification.Authentifie;
import fr.santa.akachan.middleware.dao.DaoException;
import fr.santa.akachan.middleware.objetmetier.prenominsee.PrenomInsee;
import fr.santa.akachan.middleware.objetmetier.prenominsee.PrenomInseeInexistantException;
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
    @Produces(MediaType.TEXT_PLAIN)
	@Path("/pop/total/{sexe}/{label}")
	public Response obtenirNombreTotalNaissancesPourUnPrenom(@PathParam("label") String label, @PathParam("sexe") String sexe) {
		
		Response.ResponseBuilder builder = null;

       	Long total = prenomInseeService.obtenirNombreTotalNaissancesPourUnPrenom(label, sexe);

		builder = Response.ok(total);

		return builder.build();
	}
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
	@Path("/pop/max/{sexe}/{label}")
	public Response obtenirAnneesMaxNaissancesPourUnPrenom(@PathParam("label") String label, @PathParam("sexe") String sexe) {
		
		 Response.ResponseBuilder builder = null;

        List<PrenomInsee> liste = null;
        
		try {
			liste = prenomInseeService.obtenirAnneesMaxNaissancesPourUnPrenom(label, sexe);
			builder = Response.ok(liste);
			
		} catch (DaoException e) {
			builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage());
		}
		
		return builder.build();
	}
	
	@GET
	@Authentifie
    @Produces(MediaType.APPLICATION_JSON)
	@Path("/admin/{sexe}/{label}")
	public Response obtenirStatsPrenom(@PathParam("label") String label, @PathParam("sexe") String sexe) {
		
		 Response.ResponseBuilder builder = null;

        List<PrenomInsee> liste = null;
        
		try {
			liste = prenomInseeService.obtenirStatsPrenom(label, sexe);
			builder = Response.ok(liste);
			
		} catch (DaoException e) {
			builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage());
			
		} catch (PrenomInseeInexistantException e) {
			builder = Response.status(Response.Status.NO_CONTENT).entity(e.getMessage());
		}
		
		return builder.build();
	}
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
	@Path("/pop/{sexe}/{label}")
	public Response obtenirNaissances(@PathParam("label") String label, @PathParam("sexe") String sexe) {
		
		 Response.ResponseBuilder builder = null;

        List<Integer> liste = null;
        
		try {
			liste = prenomInseeService.obtenirNaissancesPrenom(label,sexe);
			builder = Response.ok(liste);
			
		} catch (DaoException e) {
			builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR);
		}
		
		return builder.build();
	}
	
	
	

	
	
}
