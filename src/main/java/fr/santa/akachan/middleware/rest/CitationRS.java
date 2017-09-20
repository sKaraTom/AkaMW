package fr.santa.akachan.middleware.rest;

import java.util.List;

import javax.ejb.EJB;
import javax.jws.WebService;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.*;
import static javax.ws.rs.core.Response.status;

import fr.santa.akachan.middleware.authentification.Authentifie;
import fr.santa.akachan.middleware.dao.DaoException;
import fr.santa.akachan.middleware.objetmetier.citation.Citation;
import fr.santa.akachan.middleware.objetmetier.citation.CitationExistanteException;
import fr.santa.akachan.middleware.objetmetier.citation.CitationInexistanteException;
import fr.santa.akachan.middleware.objetmetier.citation.CitationInvalideException;
import fr.santa.akachan.middleware.service.CitationService;


@WebService
@Transactional
@Path("/citation")
public class CitationRS {
	
	@EJB
	CitationService citationService;
	
	
	@POST
	@Authentifie
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response ajouterCitation(Citation citation) {

	   Response.ResponseBuilder builder = null;
	 
	   try {
			citationService.ajouterCitation(citation);
			builder = Response.ok(citation);
			
		} catch (CitationExistanteException e) {
			builder = Response.status(Response.Status.CONFLICT);
			
		} catch (CitationInvalideException e) {
			builder = Response.status(Response.Status.BAD_REQUEST);
		}
			
		   return builder.build();
		}
	
	@GET
	@Authentifie
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenirCitations(){
		
		Response.ResponseBuilder builder = null;
		
		List<Citation> liste;
		try {
			liste = citationService.obtenirCitations();
			builder = Response.ok(liste);
			
		} catch (DaoException e) {
			builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR);
		}
		
		return builder.build();
	}
	

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/aleatoire")
	public Response obtenirCitationAleatoire(){
		
		Response.ResponseBuilder builder = null;
		
		Citation citationAleatoire;
		try {
			citationAleatoire = citationService.obtenirCitationAleatoire();
			builder = Response.ok(citationAleatoire);
			
		} catch (DaoException e) {
			status(INTERNAL_SERVER_ERROR).entity(e.getMessage());
		} catch (CitationInexistanteException e) {
			status(BAD_REQUEST).entity(e.getMessage());
		}
		
		return builder.build();
	}
	
	
}
