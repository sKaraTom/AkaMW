package fr.santa.akachan.middleware.rest;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.ejb.EJB;
import javax.jws.WebService;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.santa.akachan.middleware.dao.DaoException;
import fr.santa.akachan.middleware.objetmetier.client.ClientIntrouvableException;
import fr.santa.akachan.middleware.objetmetier.estimation.Estimation;
import fr.santa.akachan.middleware.objetmetier.estimation.EstimationExistanteException;
import fr.santa.akachan.middleware.objetmetier.prenom.PrenomInexistantException;
import fr.santa.akachan.middleware.objetmetier.prenom.PrenomInsee;
import fr.santa.akachan.middleware.service.EstimationService;

@WebService
@Transactional
@Path("/estimation")
public class EstimationRS {

	private static final Logger LOGGER =
			LoggerFactory.getLogger(EstimationRS.class);
	
	
	@EJB
	private EstimationService estimationService;
	
	
	// Webservice de test des random. TODO : à supprimer avant mise en prod.
	@GET
	@Produces("text/xhtml")
	@Path("/test")
	public String testerRefAleatoire () {
		
		Random hasard = new Random();
		// ça marche : r.nextInt((max+1) - min) + min;
		Integer reference = hasard.nextInt((10+1)-5)+5;
		
		return reference.toString();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/total")
	public Response obtenirNbreTotalEstimations(){
		
		Response.ResponseBuilder builder = null;
		
		final Long totalEstimations = estimationService.obtenirNbTotalEstimations();
		builder = Response.ok(totalEstimations);
		
		return builder.build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/top/{sexe}")
	public Response obtenirTop3Estimations(@PathParam("sexe")String sexe){
		
		Response.ResponseBuilder builder = null;
		
		final List<String> listeTopPrenoms = estimationService.obtenirTop3Estimations(sexe);
		builder = Response.ok(listeTopPrenoms);
		
		return builder.build();
	}
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
	@Path("/stats/{refclient}")
	public Response obtenirNbEstimClient(@PathParam("refclient") UUID refClient) {
		
		 Response.ResponseBuilder builder = null;

	        Long totalEstimClients;
	        
			try {
					totalEstimClients = estimationService.obtenirNbEstimClient(refClient);
					 builder = Response.ok(totalEstimClients);
			} 
			catch (DaoException e) {
					builder = Response.status(Response.Status.BAD_GATEWAY);
			}
			return builder.build();
	}
	
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
	@Path("/stats/{refclient}/{sexe}")
	public Response obtenirNbEstimClientParSexe(@PathParam("refclient") UUID refClient, @PathParam("sexe") String sexe) {
		
		 Response.ResponseBuilder builder = null;

	        Long totalEstimClientsParsexe;
	        
			try {
				totalEstimClientsParsexe = estimationService.obtenirNbEstimClientParSexe(refClient, sexe);
				 builder = Response.ok(totalEstimClientsParsexe);
			} catch (DaoException e) {
				 builder = Response.status(Response.Status.BAD_GATEWAY);
			}

			return builder.build();
	}
	
	
	@POST
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{refClient}")
	public Response estimerPrenom(Estimation estimation, @PathParam("refClient")UUID refClient) {

	   Response.ResponseBuilder builder = null;
	   try {
		estimationService.estimerPrenom(estimation, refClient);
	    builder = Response.ok(estimation);
		
	} catch (ClientIntrouvableException e) {
		builder = Response.status(Response.Status.BAD_REQUEST);
	
	} catch (PrenomInexistantException e) {
		builder = Response.status(Response.Status.BAD_REQUEST);
		
	} catch (EstimationExistanteException e) {
		builder = Response.status(Response.Status.CONFLICT);
		
	}
	   return builder.build();
	}
	
	@PUT
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{choixAkachan}")
	public Response changerDeListeEstimations(List<Estimation>estimations, @PathParam("choixAkachan")String akachan) {
		
		 Response.ResponseBuilder builder = null;
		 
		 estimationService.changerDeListeEstimations(estimations, akachan);
		  builder = Response.ok(estimations);

		   return builder.build();
	}
	
	@PUT
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response modifierEstimation(Estimation estimation) {

	   Response.ResponseBuilder builder = null;
	   estimationService.modifierEstimation(estimation);
	   builder = Response.ok(estimation);

	   return builder.build();
	}
	
	
	
	}
	
