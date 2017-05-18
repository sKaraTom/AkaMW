package fr.santa.akachan.middleware.securite;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ejb.EJB;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;


@Securise
@Provider
@Priority(Priorities.AUTHENTICATION)
public class FiltreAuthentification implements ContainerRequestFilter {
	
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		//récupérer header authorization de la requête HTTP
		String headerAuthorization = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
		
		 // vérifier que le header 'Authorization' est bien présent et formaté.
        if (headerAuthorization == null || !headerAuthorization.startsWith("Bearer ")) {
            //throw new AccesNonAutoriseException("header Authorization requis");
        	// TODO : à revoir, je ne peux pas ajouter de throws AccesNonAutoriseException à la méthode.
        }

        // Extraire le token du header http
        String token = headerAuthorization.substring("Bearer".length()).trim();
        
        // TODO A revoir par rapport aux exceptions de la méthode validerToken déjà catchées..
        try {
            // valider le token
            this.validerToken(token);

        } catch (Exception e) {
            requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }
		
	
	public void validerToken(String token) {
		
		ClefSecrete clefSecrete = new ClefSecrete();
		
		try {
			 
		    Jwts.parser().setSigningKey(clefSecrete.getSecret()).parseClaimsJws(token).getBody().getSubject().equals("users/TzMUocMF4p");
		 
		    //OK, we can trust this JWT
		 
		} catch (SignatureException e) {
		 
		    //don't trust the JWT!
		}
		catch (ExpiredJwtException e) {
			// date expiration passée.
		}
	}
	
}
