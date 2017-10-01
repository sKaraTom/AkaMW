package fr.santa.akachan.middleware.rest;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.ejb.EJB;
import javax.jws.WebService;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.santa.akachan.middleware.authentification.Jeton;
import fr.santa.akachan.middleware.authentification.JetonService;
import fr.santa.akachan.middleware.dao.DaoException;
import fr.santa.akachan.middleware.dto.CompteDTO;
import fr.santa.akachan.middleware.authentification.Authentifie;
import fr.santa.akachan.middleware.objetmetier.compte.Compte;
import fr.santa.akachan.middleware.objetmetier.compte.CompteExistantException;
import fr.santa.akachan.middleware.objetmetier.compte.CompteInexistantException;
import fr.santa.akachan.middleware.objetmetier.compte.CompteInvalideException;
import fr.santa.akachan.middleware.objetmetier.compte.CompteNonAdminException;
import fr.santa.akachan.middleware.objetmetier.compte.EmailInvalideException;
import fr.santa.akachan.middleware.objetmetier.compte.PasswordInvalideException;
import fr.santa.akachan.middleware.service.CompteService;

@WebService
@Transactional
@Path("/compte")
public class CompteRS {
	
	private static final Logger LOGGER =
			LoggerFactory.getLogger(CompteRS.class);
	
	@EJB
	private JetonService jwtCreation;
	
	@EJB
	private CompteService compteService;

	

	@POST
	@Path("/creer")
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces("text/plain")
	public Response creerCompte (Compte compte) {
		
		Response.ResponseBuilder builder = null;
		
            try {
				compteService.creerCompte(compte);
				builder = Response.ok("compte créé avec succès.");
            }
			catch (CompteExistantException e) {
					 builder = Response.status(Response.Status.CONFLICT);
			} 
            catch (CompteInvalideException e) {
				builder = Response.status(Response.Status.BAD_REQUEST);
				
			} catch (EmailInvalideException e) {
				builder = Response.status(Response.Status.NOT_ACCEPTABLE);
				
			} catch (PasswordInvalideException e) {
				builder = Response.status(Response.Status.NOT_ACCEPTABLE);
			}

        return builder.build();
	}
	
	@POST
	@Authentifie
	@Path("/obtenir")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenirCompte(@FormParam("email") final String email) {
		
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
	
	@GET
	@Authentifie
	@Path("/admin/comptesDTO")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenirTousComptesDTO() {
		
		Response.ResponseBuilder builder = null;
		
		List<CompteDTO> listeComptes;
		
		try {
			listeComptes = compteService.obtenirTousComptesDTO();
			builder = Response.ok(listeComptes);
			
		} catch (DaoException e) {
			builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage());
		}

		return builder.build();
	}
	
	
	@PUT
	@Authentifie
	@Path("/modifier")
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response modifierCompte(final Compte compte) {
		
		Response.ResponseBuilder builder = null;

		try {
			compteService.modifierCompte(compte);
			builder = Response.ok(compte);
			
		} catch (CompteInexistantException e) {
			builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage());	
		} catch (CompteInvalideException e) {
			builder = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage());
		} catch (EmailInvalideException e) {
			builder = Response.status(Response.Status.NOT_ACCEPTABLE).entity(e.getMessage());
		}
		return builder.build();
	}
	
	@PUT
	@Authentifie
	@Path("/modifier/password")
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces("text/plain")
	public Response modifierMotDePasse(final List<String> listeChamps) {
		
		Response.ResponseBuilder builder = null;
		
		try {
			compteService.modifierMotDePasse(listeChamps);
			String succès = "le nouveau mot de passe est enregistré.";
			builder = Response.ok(succès);
			
		} catch (CompteInexistantException e) {
			builder = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage());
			
		} catch (CompteInvalideException e) {
			builder = Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage());
			
		} catch (PasswordInvalideException e) {
			builder = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()); 
		}
		return builder.build();
	}
	
	
	@POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response connecterCompte(@FormParam("email") final String email, 
            						@FormParam("password") final String password) {
		
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
	
	@POST
	@Path("/admin/connexion")
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response connecterAdmin(final Compte compte) {
		
		Response.ResponseBuilder builder = null;
				
		try {
			String tokenDeSession;
			tokenDeSession = compteService.connecterAdmin(compte);
			builder = Response.ok(tokenDeSession);
			
		} catch (CompteInexistantException e) {
			builder = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage());
			
		} catch (CompteInvalideException e) {
			builder = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage());
			
			
		} catch (CompteNonAdminException e) {
			builder = Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage());
			
		} catch (UnsupportedEncodingException e) {
			builder = Response.status(Response.Status.NOT_ACCEPTABLE);
		}
		
		return builder.build();
	}
	
	
	@DELETE
	@Path("/admin/suppression")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("text/plain")
	public Response supprimerCompte(final CompteDTO compteDTO) {
		
		Response.ResponseBuilder builder = null;
		
		String suppressionReussie = "compte dont l'email est " + compteDTO.getEmail() + " supprimé avec succès.";
		
		try {
			compteService.supprimerCompteEtEstimations(compteDTO);
			builder = Response.ok(suppressionReussie);
			
		} catch (CompteInexistantException e) {
			builder = Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage());
			
		} catch (DaoException e) {
			builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage());
		}
		
		return builder.build();
	}
	
	
	@GET
	@Authentifie
	@Path("/token")
	@Produces("text/plain")
	public Response validerToken() {
		
		Response.ResponseBuilder builder = null;
		
		String validOk = "ok";
		
		builder = Response.ok(validOk);
		return builder.build();
		
	}
	
	
}
