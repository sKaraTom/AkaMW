package fr.santa.akachan.middleware.rest;

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

import fr.santa.akachan.middleware.securite.Securise;
import fr.santa.akachan.middleware.service.EmailService;

@WebService
@Transactional
@Path("/email")
public class EmailRS {
	
	@EJB
	EmailService emailService;
	
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
			
		} catch (AddressException e) {
			builder = Response.status(Response.Status.BAD_REQUEST);	
			
		} catch (MessagingException e) {
			builder = Response.status(Response.Status.CONFLICT);	
		}
		
		return builder.build();
		
		
	}
	
	
}
