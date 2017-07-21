package fr.santa.akachan.middleware.rest;

import java.util.List;
import java.util.Map;
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

import fr.santa.akachan.middleware.authentification.Securise;
import fr.santa.akachan.middleware.dao.DaoException;
import fr.santa.akachan.middleware.objetmetier.estimation.Estimation;
import fr.santa.akachan.middleware.objetmetier.prenom.PrenomInexistantException;
import fr.santa.akachan.middleware.objetmetier.prenom.PrenomIntrouvableException;
import fr.santa.akachan.middleware.objetmetier.prenom.TendanceInvalideException;
import fr.santa.akachan.middleware.service.PrenomService;

@WebService
@Transactional
@Path("/prenom")
public class PrenomRS {
	
	@EJB
	private PrenomService prenomService;
	
	
	@GET
	@Securise
    @Produces("text/plain")
	@Path("/{sexe}/{refclient}/{tendance}")
	public Response genererPrenomAleatoire(@PathParam("sexe") String sexe,@PathParam("refclient") UUID refClient,@PathParam("tendance") Integer choixTendance) {
		
		Response.ResponseBuilder builder = null;
		 
		String prenomAleatoire;
		
		try {
			prenomAleatoire = prenomService.genererPrenomAleatoireSql(sexe, refClient, choixTendance);
			builder = Response.ok(prenomAleatoire);
		} catch (TendanceInvalideException e) {
			builder = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage());
		
		} catch (PrenomIntrouvableException e) {
			builder = Response.status(Response.Status.NO_CONTENT).entity(e.getMessage());
			
		} catch (DaoException e) {
			builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage());
		}

		return builder.build();
	}
	
	
	@POST
	@Securise
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	@Path("/recherche/{rechercheExacte}")
	public Response rechercherPrenomEtEstimExistante(Estimation estimation,@PathParam("rechercheExacte") Boolean rechercheExacte) {
		
		Response.ResponseBuilder builder = null;

		Map<String, Boolean> resultats;

		try {
			resultats = prenomService.chercherPrenomEtEstimation(estimation, rechercheExacte);
			builder = Response.ok(resultats);
			
		} catch (PrenomInexistantException e) {
			builder = Response.status(Response.Status.NO_CONTENT);
		}

	     return builder.build();
	}
	
	
}
