package fr.santa.akachan.middleware.rest;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.ejb.EJB;
import javax.jws.WebService;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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

import fr.santa.akachan.middleware.authentification.Authentifie;
import fr.santa.akachan.middleware.dao.DaoException;
import fr.santa.akachan.middleware.objetmetier.client.ClientIntrouvableException;
import fr.santa.akachan.middleware.objetmetier.estimation.Estimation;
import fr.santa.akachan.middleware.objetmetier.estimation.EstimationExistanteException;
import fr.santa.akachan.middleware.objetmetier.estimation.EstimationIntrouvableException;
import fr.santa.akachan.middleware.objetmetier.estimation.EstimationInvalideException;
import fr.santa.akachan.middleware.objetmetier.prenomInsee.PrenomInsee;
import fr.santa.akachan.middleware.objetmetier.prenomInsee.PrenomInseeInexistantException;
import fr.santa.akachan.middleware.service.EstimationService;

@WebService
@Transactional
@Path("/estimation")
public class EstimationRS {

	private static final Logger LOGGER =
			LoggerFactory.getLogger(EstimationRS.class);
	
	
	@EJB
	private EstimationService estimationService;
	
	
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
	@Authentifie
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/total/{sexe}")
	public Response obtenirNbTotalEstimParSexe(@PathParam("sexe")String sexe){
		
		Response.ResponseBuilder builder = null;
		
		final Long totalEstimationsParSexe = estimationService.obtenirNbTotalEstimParSexe(sexe);
		builder = Response.ok(totalEstimationsParSexe);
		
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
	
	@GET
	@Authentifie
	@Path("/listeA/{ref}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenirListeAkachanTrue(@PathParam("ref") final UUID refClient) {
		
		 Response.ResponseBuilder builder = null;
		 
		 final List<Estimation> liste = estimationService.obtenirListeAkachanTrue(refClient);

         builder = Response.ok(liste);
         return builder.build();
	}
	
	@GET
	@Authentifie
	@Path("/listeN/{ref}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenirListeNoire(@PathParam("ref") final UUID refClient) {
		
		 Response.ResponseBuilder builder = null;
		 
		 final List<Estimation> liste = estimationService.obtenirListeNoire(refClient);

         builder = Response.ok(liste);
         return builder.build();
	}
	
	@GET
	@Authentifie
	@Path("/listeF/{ref}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenirListeFavoris(@PathParam("ref") final UUID refClient) {
		
		 Response.ResponseBuilder builder = null;
		 
		 final List<Estimation> liste = estimationService.obtenirListeFavoris(refClient);

         builder = Response.ok(liste);
         return builder.build();
	}
	
	
	@POST
	@Authentifie
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
	
	} catch (EstimationExistanteException e) {
		builder = Response.status(Response.Status.CONFLICT);
		
	} catch (EstimationInvalideException e) {
		builder = Response.status(Response.Status.BAD_REQUEST);
	}
	   return builder.build();
	}
	
	@PUT
	@Authentifie
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
	@Authentifie
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response modifierEstimation(Estimation estimation) {

	   Response.ResponseBuilder builder = null;
	   try {
		estimationService.modifierEstimation(estimation);
		builder = Response.ok(estimation);
		
	   } catch (DaoException e) {
		   builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage());
		   
	   } catch (EstimationIntrouvableException e) {
		   builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage());
	}
	   

	   return builder.build();
	}
	
	@DELETE
	@Authentifie
	@Produces("text/plain")
	@Path("/{refClient}")
	public Response effacerToutesEstimationsClient(@PathParam("refClient")final UUID refClient) {
		
		Response.ResponseBuilder builder = null;
		try {
			estimationService.effacerToutesEstimationsClient(refClient);
			String succes = "suppression effectuée avec succès.";
			builder = Response.ok(succes);
			
		} catch (DaoException e) {
			builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage());
		}
		
		return builder.build();
	}
	
	
	
	
	
	}
	
