package fr.santa.akachan.middleware.rest;

import java.io.UnsupportedEncodingException;

import javax.ejb.EJB;
import javax.jws.WebService;
import javax.mail.internet.InternetAddress;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.santa.akachan.middleware.cache.CachePrenomService;
import fr.santa.akachan.middleware.objetmetier.client.Client;
import fr.santa.akachan.middleware.objetmetier.client.ClientExistantException;
import fr.santa.akachan.middleware.objetmetier.client.ClientInvalideException;
import fr.santa.akachan.middleware.objetmetier.compte.Compte;
import fr.santa.akachan.middleware.objetmetier.compte.CompteDejaExistantException;
import fr.santa.akachan.middleware.objetmetier.compte.CompteInexistantException;
import fr.santa.akachan.middleware.objetmetier.compte.CompteInvalideException;
import fr.santa.akachan.middleware.objetmetier.compte.EmailInvalideException;
import fr.santa.akachan.middleware.securite.Jeton;
import fr.santa.akachan.middleware.securite.JwtCreation;
import fr.santa.akachan.middleware.securite.Securise;
import fr.santa.akachan.middleware.service.CompteService;

@WebService
@Transactional
@Path("/compte")
public class CompteRS {
	
	
	@EJB
	private JwtCreation jwtCreation;
	
	@EJB
	private CompteService compteService;

	

	@POST
	@Path("/creer")
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response creerCompte (Compte compte) {
		
		Response.ResponseBuilder builder = null;
		
            try {
				compteService.creerCompte(compte);
				builder = Response.ok(compte);
            }
			catch (CompteDejaExistantException e) {
					 builder = Response.status(Response.Status.CONFLICT);
			} 
            catch (CompteInvalideException e) {
				builder = Response.status(Response.Status.BAD_REQUEST);
				
			} catch (EmailInvalideException e) {
				builder = Response.status(Response.Status.NOT_ACCEPTABLE);
			}

        return builder.build();
	}
	
	@POST
	@Securise
	@Path("/obtenir")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenirCompte(@FormParam("email") String email) {
		
		
		Response.ResponseBuilder builder = null;
		
		Compte compte;
		try {
			compte = compteService.obtenirCompte(email);
			builder = Response.ok(compte);
		
		} catch (CompteInexistantException e) {
			builder = Response.status(Response.Status.BAD_REQUEST);
		}
		return builder.build();
	}
	
	
	@POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response connecterCompte(@FormParam("email") String email, 
            						@FormParam("password") String password) {
		
		Response.ResponseBuilder builder = null;
		Jeton jeton;

			try {
				jeton = compteService.connecter(email, password);
				builder = Response.ok(jeton);
				
			} catch (UnsupportedEncodingException e) {
				builder = Response.status(Response.Status.NOT_ACCEPTABLE);
				
			} catch (CompteInvalideException e) {
				builder = Response.status(Response.Status.UNAUTHORIZED);	
			
			} catch (CompteInexistantException e) {
				builder = Response.status(Response.Status.BAD_REQUEST);
				
			} 
		
		return builder.build();
	}
	
	@GET
	@Securise
	@Path("/token")
	@Produces("text/plain")
	public Response validerToken() {
		
		Response.ResponseBuilder builder = null;
		
		String validOk = "ok";
		
		builder = Response.ok(validOk);
		return builder.build();
		
	}
	
	
}
