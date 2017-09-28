package fr.santa.akachan.middleware.rest;

import java.util.List;

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
	
	
	@GET
	@Authentifie
	@Path("/total")
	@Produces(MediaType.TEXT_PLAIN)
	public Response obtenirTotalCitations() {
		
		Response.ResponseBuilder builder = null;
		
		try {
			Integer total = citationService.obtenirNombreTotalCitations();
			builder = Response.ok(total);
			
		} catch (DaoException e) {
			builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage());
		}
		
		return builder.build();
		
	}
	
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
			builder = Response.status(Response.Status.CONFLICT).entity(e.getMessage());
			
		} catch (CitationInvalideException e) {
			builder = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage());
			
		} catch (DaoException e) {
			builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage());
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
			builder = status(INTERNAL_SERVER_ERROR).entity(e.getMessage());
		}
		
		return builder.build();
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response modifierCitation(final Citation citation) {
		
		Response.ResponseBuilder builder = null;
		
		try {
			citationService.modifierCitation(citation);
			builder = Response.ok("la modification de la citation n°" + citation.getId() + " s'est bien effectuée.");
			
		} catch (CitationInvalideException e) {
			builder = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage());
		}
		
		return builder.build();
	}
	
	@DELETE
	@Authentifie
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{id}")
	public Response supprimerCitation(@PathParam("id")final Integer id) {
		
		Response.ResponseBuilder builder = null;
		
		try {
			citationService.supprimerCitation(id);
			builder = Response.ok("la citation n°"+id+" est supprimée.");
			
		} catch (CitationInexistanteException e) {
			builder = Response.status(Response.Status.NOT_FOUND).entity(e.getMessage());
			
		} catch (DaoException e) {
			builder = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage());
		}
		
		return builder.build();
	}
	
	
}
