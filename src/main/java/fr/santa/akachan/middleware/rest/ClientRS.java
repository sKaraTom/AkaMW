package fr.santa.akachan.middleware.rest;

import java.util.List;
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

import fr.santa.akachan.middleware.objetmetier.client.Client;
import fr.santa.akachan.middleware.objetmetier.client.ClientExistantException;
import fr.santa.akachan.middleware.objetmetier.client.ClientInvalideException;
import fr.santa.akachan.middleware.objetmetier.estimation.Estimation;
import fr.santa.akachan.middleware.objetmetier.prenom.PrenomInsee;
import fr.santa.akachan.middleware.securite.Securise;
import fr.santa.akachan.middleware.service.ClientService;

@WebService
@Transactional
@Path("/client")
public class ClientRS {

	@EJB
	ClientService clientService;
	
	@POST
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response creerClient(Client client) {
		
		Response.ResponseBuilder builder = null;
		
		try {
            // Cas nominal --> 200
            clientService.creerClient(client);
            builder = Response.ok(client);

        } catch (final ClientExistantException e) {
            // Cas alternatif : L'adresse existe déjà. --> 409
            builder = Response.status(Response.Status.CONFLICT);

        } catch (final ClientInvalideException e) {
            // Cas alternatif : L'adresse est invalide --> 400
            builder = Response.status(Response.Status.BAD_REQUEST);

        } catch (final Exception e) {
            // Cas alternatif autre --> 500
            builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR);
        }
        return builder.build();
	}
	
	@GET
	@Path("/{ref}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenirClient(@PathParam("ref") final UUID refClient) {
		
		 Response.ResponseBuilder builder = null;
		 
		 final Client client = clientService.obtenirClient(refClient);

         builder = Response.ok(client);
         return builder.build();
	}
	
	@GET
	@Securise
	@Path("/listeA/{ref}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenirListeAkachanTrue(@PathParam("ref") final UUID refClient) {
		
		 Response.ResponseBuilder builder = null;
		 
		 final List<Estimation> liste = clientService.obtenirListeAkachanTrue(refClient);

         builder = Response.ok(liste);
         return builder.build();
	}
	
	@GET
	@Path("/listeN/{ref}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenirListeNoire(@PathParam("ref") final UUID refClient) {
		
		 Response.ResponseBuilder builder = null;
		 
		 final List<Estimation> liste = clientService.obtenirListeNoire(refClient);

         builder = Response.ok(liste);
         return builder.build();
	}
	
	@GET
	@Path("/listeF/{ref}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenirListeFavoris(@PathParam("ref") final UUID refClient) {
		
		 Response.ResponseBuilder builder = null;
		 
		 final List<Estimation> liste = clientService.obtenirListeFavoris(refClient);

         builder = Response.ok(liste);
         return builder.build();
	}
	
}
