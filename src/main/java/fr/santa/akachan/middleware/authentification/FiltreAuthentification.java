package fr.santa.akachan.middleware.authentification;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ejb.EJB;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

/**
 * méthode qui alloue à l'annotation @Securise la validation
 * d'un tokenpassé dans le header d'une requête.
 *
 */
@Authentifie
@Provider
@Priority(Priorities.AUTHENTICATION)
public class FiltreAuthentification implements ContainerRequestFilter {
	
	
	private static final Logger LOGGER =
			LoggerFactory.getLogger(FiltreAuthentification.class);
	
	@EJB
	JetonService jetonService;
	
	
	/**
	 * méthode implémentée de l'interface ContainerRequestFilter
	 * extraire le token, et vérifier qu'il est valide sinon lever une exception et envoyer un code http d'erreur.
	 * 
	 * @throw NotAuthorizedException si le header est invalide.
	 */
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		//récupérer header authorization de la requête HTTP
		String headerAuthorization = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
		
		 // vérifier que le header 'authorization' est bien présent et formaté.
        if (headerAuthorization == null || !headerAuthorization.startsWith("Bearer ")) {
        	 throw new NotAuthorizedException("header Authorization requis");
        }	

        // Extraire le token du header http
        String token = headerAuthorization.substring("Bearer".length()).trim();
        
        // valider le token :
        // si date d'expiration passée : code 400
        // pour toute autre exception : code 401
        try {	
        	jetonService.validerToken(token);
        } catch (ExpiredJwtException e) {
        	requestContext.abortWith(
	                Response.status(Response.Status.BAD_REQUEST).entity("la session a expiré, veuillez vous reconnecter.").build());
        } catch (Exception e) {
           // n'importe quelle exception annule la connexion côté ihm.
        	requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED).build());
        }
        
    }
	
		
}
