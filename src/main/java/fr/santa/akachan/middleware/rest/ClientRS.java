package fr.santa.akachan.middleware.rest;

import java.util.UUID;

import javax.ejb.EJB;
import javax.jws.WebService;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fr.santa.akachan.middleware.authentification.Authentifie;
import fr.santa.akachan.middleware.dao.DaoException;
import fr.santa.akachan.middleware.objetmetier.client.Client;
import fr.santa.akachan.middleware.objetmetier.client.ClientIntrouvableException;
import fr.santa.akachan.middleware.service.ClientService;

@WebService
@Transactional
@Path("/client")
public class ClientRS {

	@EJB
	ClientService clientService;
	
	
	@GET
	@Path("/total")
	@Produces("text/plain")
	public Response obtenirNombreClients() {
		
		 Response.ResponseBuilder builder = null;
		 
		 Long totalClients;

		 try {
			totalClients = clientService.obtenirNombreClients();
			builder = Response.ok(totalClients);
			
		} catch (DaoException e) {
			builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR);
		}	
         return builder.build();
	}
	
	@GET
	@Authentifie
	@Path("/total/{sexe}")
	@Produces("text/plain")
	public Response obtenirNombreClientsParSexe(@PathParam("sexe") final String sexe) {
		
		 Response.ResponseBuilder builder = null;
		 
		 Long total;

		 try {
			total = clientService.obtenirNombreClientsParSexe(sexe);
			builder = Response.ok(total);
			
		} catch (DaoException e) {
			builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage());
		}	
         return builder.build();
	}
	
	
	@GET
	@Authentifie
	@Path("/{ref}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenirClient(@PathParam("ref") final UUID refClient) {
		
		 Response.ResponseBuilder builder = null;
		 
		 Client client;
		try {
			client = clientService.obtenirClientSansDonneesSensibles(refClient);
	        builder = Response.ok(client);
	        
		} catch (ClientIntrouvableException e) {
			builder = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage());
			
		} catch (DaoException e) {
			builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage());
		}


         return builder.build();
	}
	
	
}
