package fr.santa.akachan.middleware.rest;

import java.util.ArrayList;
import java.util.HashMap;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.santa.akachan.middleware.dao.DaoException;
import fr.santa.akachan.middleware.dao.PrenomDao;
import fr.santa.akachan.middleware.objetmetier.estimation.Estimation;
import fr.santa.akachan.middleware.objetmetier.prenom.PrenomInexistantException;
import fr.santa.akachan.middleware.objetmetier.prenom.PrenomInsee;
import fr.santa.akachan.middleware.objetmetier.prenom.TendanceInvalideException;
import fr.santa.akachan.middleware.service.PrenomService;

@WebService
@Transactional
@Path("/prenom")
public class PrenomRS {
	
	private static final Logger LOGGER =
			LoggerFactory.getLogger(PrenomRS.class);
	
	@EJB
	private PrenomService prenomService;
	
	
	//a voir si on passe le prenomAleatoire en texte plutôt que json.
	@GET
    @Produces(MediaType.APPLICATION_JSON)
	@Path("/{sexe}/{refclient}/{tendance}")
	public Response genererPrenomAleatoire(@PathParam("sexe") String sexe,@PathParam("refclient") UUID refClient,@PathParam("tendance") Integer choixTendance) {
		
		 Response.ResponseBuilder builder = null;
		 
		 //final String prenomAleatoire = prenomService.getPrenomAleatoire(sexe, refClient, choixTendance); // methode cache
		String prenomAleatoire;
		
		try {
			prenomAleatoire = prenomService.genererPrenomAleatoireSql(sexe, refClient, choixTendance); // methode random SQL
			builder = Response.ok(prenomAleatoire);
		} catch (TendanceInvalideException e) {
			builder = Response.status(Response.Status.BAD_REQUEST);
		}

		return builder.build();
	}
	
	@POST
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
	
	
	
	/* METHODES AVANT CACHE
	// a voir si on passe le prenomAleatoire en texte plutôt que json.
	@GET
    @Produces(MediaType.APPLICATION_JSON)
	@Path("/{sexe}/{refclient}/{tendance}")
	public Response genererPrenomAleatoire(@PathParam("sexe") String sexe,@PathParam("refclient") UUID refClient,@PathParam("tendance") Integer choixTendance) {
		
		 Response.ResponseBuilder builder = null;
		 
		 try {
	      	// Cas Nominal
		    final String prenomAleatoire = prenomService.genererPrenomAleatoire(sexe, refClient, choixTendance);
		    builder = Response.ok(prenomAleatoire);
		 }
		 
	    catch (PrenomInexistantException e) {
			builder = Response.status(Response.Status.BAD_REQUEST);
		}
		return builder.build();
		
	}
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
	@Path("/sql/{sexe}/{refclient}")
	public Response genererPrenomAleatoireSQL(@PathParam("sexe") String sexe,@PathParam("refclient") UUID refClient) throws DaoException {
		
		 Response.ResponseBuilder builder = null;

	      	    // Cas Nominal
	    final String prenomAleatoire = prenomService.genererPrenomAleatoireSQL(sexe, refClient);

	    builder = Response.ok(prenomAleatoire);

		return builder.build();
		
	}
	*/
	
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
	@Path("/pop/{sexe}/{label}")
	public Response obtenirNaissances(@PathParam("label") String label, @PathParam("sexe") String sexe) {
		
		 Response.ResponseBuilder builder = null;

  	    // Cas Nominal
        ArrayList<Integer> liste = null;
        
		try {
			liste = prenomService.obtenirNaissancesPrenom(label,sexe);
			builder = Response.ok(liste);
		} catch (DaoException e) {
			builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR);
		}
		return builder.build();
	}
	
	
	

	
	
}
