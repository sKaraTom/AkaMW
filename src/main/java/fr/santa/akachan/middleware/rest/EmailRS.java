package fr.santa.akachan.middleware.rest;

import java.util.List;

import javax.ejb.EJB;
import javax.jws.WebService;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fr.santa.akachan.middleware.email.AuthentificationEchoueeException;
import fr.santa.akachan.middleware.objetmetier.estimation.Estimation;
import fr.santa.akachan.middleware.securite.Securise;
import fr.santa.akachan.middleware.service.EmailService;

@WebService
@Transactional
@Path("/email")
public class EmailRS {
	
	@EJB
	EmailService emailService;
	
	
	
	// TODO : methode de test, à supprimer après avoir créé les bonnes reliées à l'ihm.
	@GET
	@Produces("text/plain")
//    @Consumes(MediaType.APPLICATION_JSON)
	@Path("/{emailDestinataire}")
	public Response envoyerMail(@PathParam("emailDestinataire")String email) {
		
		Response.ResponseBuilder builder = null;
		
		try {
			emailService.envoyerMail(email);

			String validOk = "ok";
			builder = Response.ok(validOk);
			
		} catch (AuthentificationEchoueeException e) {
			// si l'authentification de l'émetteur (mail akachanapp) a échoué.
			builder = Response.status(Response.Status.SERVICE_UNAVAILABLE);	
		} catch (AddressException e) {
			builder = Response.status(Response.Status.BAD_REQUEST);	
			
		} catch (MessagingException e) {
			builder = Response.status(Response.Status.CONFLICT);	
		}
		
		return builder.build();
		
	}
	
	
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
			
		} catch (AddressException e) {
			builder = Response.status(Response.Status.BAD_REQUEST);	
			
		} catch (MessagingException e) {
			builder = Response.status(Response.Status.CONFLICT);	
		
		} catch (AuthentificationEchoueeException e) {
			// si l'authentification de l'émetteur (mail akachanapp) a échoué.
			builder = Response.status(Response.Status.SERVICE_UNAVAILABLE);	
		}
		
		return builder.build();
		
	}
	
	@POST
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
			
		} catch (AddressException e) {
			builder = Response.status(Response.Status.BAD_REQUEST);	
			
		} catch (MessagingException e) {
			builder = Response.status(Response.Status.CONFLICT);	
		
		} catch (AuthentificationEchoueeException e) {
			// si l'authentification de l'émetteur (mail akachanapp) a échoué.
			builder = Response.status(Response.Status.SERVICE_UNAVAILABLE);	
		}
		
		return builder.build();
		
	}
	
	
}
