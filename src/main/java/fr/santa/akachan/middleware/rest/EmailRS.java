package fr.santa.akachan.middleware.rest;

import static javax.ws.rs.core.Response.status;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.PRECONDITION_FAILED;
import static javax.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE;

import java.util.List;

import javax.ejb.EJB;
import javax.jws.WebService;
import javax.mail.MessagingException;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fr.santa.akachan.middleware.authentification.Authentifie;
import fr.santa.akachan.middleware.objetmetier.compte.EmailInvalideException;
import fr.santa.akachan.middleware.objetmetier.email.AuthentificationEchoueeException;
import fr.santa.akachan.middleware.objetmetier.email.ConnexionEchoueeException;
import fr.santa.akachan.middleware.objetmetier.email.ContenuInvalideException;
import fr.santa.akachan.middleware.objetmetier.email.DestinataireInvalideException;
import fr.santa.akachan.middleware.objetmetier.email.SujetInvalideException;
import fr.santa.akachan.middleware.objetmetier.estimation.Estimation;
import fr.santa.akachan.middleware.service.EmailService;

@WebService
@Transactional
@Path("/email")
public class EmailRS {
	
	@EJB
	EmailService emailService;
	
	
	@POST
	@Produces("text/plain")
    @Consumes(MediaType.APPLICATION_JSON)
	@Path("/contact")
	public Response envoyerMailContact(List<String> listeChampsMailClient) {
		
		Response.ResponseBuilder builder = null;
		
			try {
				emailService.envoyerMailContact(listeChampsMailClient);
				String validOk = "message envoyé avec succès.";
				builder = Response.ok(validOk);
				
			} catch (AuthentificationEchoueeException e) {
				// échec de l'authentification de l'émetteur (mail Akachan)
				builder = status(PRECONDITION_FAILED).entity(e.getMessage());
				
			} catch (DestinataireInvalideException | SujetInvalideException | ContenuInvalideException | EmailInvalideException e) {
				builder = status(BAD_REQUEST).entity(e.getMessage());
				
			} catch (ConnexionEchoueeException e) {
				// problème au moment de l'envoi.
				builder = status(SERVICE_UNAVAILABLE).entity(e.getMessage());
				
			} catch (MessagingException e) {
				// problème au niveau du transport.
				builder = Response.status(Response.Status.SERVICE_UNAVAILABLE); 
			}
			
		return builder.build();
		
	}
	
	@POST
	@Authentifie
	@Produces("text/plain")
    @Consumes(MediaType.APPLICATION_JSON)
	@Path("/selection/{prenomClient}/{mailClient}/{mailAutre}")
	public Response envoyerMailSelectionDePrenoms(List<Estimation> listePrenomsSelectionnes,
			@PathParam("prenomClient")String prenomClient,@PathParam("mailClient")String mailClient,@PathParam("mailAutre")String mailAutre ) {
		
		Response.ResponseBuilder builder = null;
		
		try {
			emailService.envoyerMailSelectionDePrenoms(listePrenomsSelectionnes, prenomClient, mailClient, mailAutre);
			String validOk = "message envoyé avec succès.";
			builder = Response.ok(validOk);
			
		} catch (AuthentificationEchoueeException e) {
			// échec de l'authentification de l'émetteur (mail Akachan)
			builder = status(PRECONDITION_FAILED).entity(e.getMessage());
			
		} catch (DestinataireInvalideException | SujetInvalideException | ContenuInvalideException | EmailInvalideException e) {
			builder = status(BAD_REQUEST).entity(e.getMessage());
			
		} catch (ConnexionEchoueeException e) {
			// problème au moment de l'envoi.
			builder = status(SERVICE_UNAVAILABLE).entity(e.getMessage());
			
		} catch (MessagingException e) {
			// problème au niveau du transport.
			builder = Response.status(Response.Status.SERVICE_UNAVAILABLE); 
		}	
	
		
		return builder.build();
		
	}
	
	
}
