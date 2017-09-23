package fr.santa.akachan.middleware.authentification;

import io.jsonwebtoken.impl.crypto.MacProvider;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.santa.akachan.middleware.objetmetier.client.Client;
import fr.santa.akachan.middleware.objetmetier.compte.Compte;
import io.jsonwebtoken.*;

import javax.ejb.Stateless;
import javax.transaction.Transactional;

@Stateless
@Transactional
public class JetonService {
	
	
	private static final Logger LOGGER =
			LoggerFactory.getLogger(JetonService.class);
	
	private final String clefClient = "%^$lsf#&asfgva120" ;
	private final String clefAdmin = "i1O8lea-yW*%qYZ";
	
	
	/**
	 * méthode de création d'un token.
	 * 
	 * @param compte pour extraire les informations à intégrer au token, dureeMsAvantExpiration pour définir une durée avant expiration du token.
	 * @return String un token
	 * @throws UnsupportedEncodingException
	 */
	public String creerToken(Compte compte, Long dureeMsAvantExpiration, String choixClef) throws UnsupportedEncodingException {
		
		Client client = compte.getClient();
		
		Date date = new Date();
		long t = date.getTime();
		Date dateExpiration = new Date(t + dureeMsAvantExpiration); // date de maintenant + durée (ms)
		
		// construction du token
		String token = Jwts.builder()
						  .setSubject("users/TzMUocMF4p")
						  .setIssuedAt(date)
						  .setExpiration(dateExpiration)
						  .claim("prenom", client.getPrenom())
						  .claim("sexe", client.getSexe())
						  .signWith(
						    SignatureAlgorithm.HS256,
						    choixClef.getBytes("UTF-8")
						  )
						  .compact();
		
		return token;	
	}
	
	/**
	 * méthode de validation d'un token.
	 * 
	 * @param token
	 * @throws ExpiredJwtException si la date d'expiration est passée.
	 * @throws UnsupportedJwtException
	 * @throws MalformedJwtException
	 * @throws SignatureException
	 * @throws IllegalArgumentException
	 * @throws UnsupportedEncodingException
	 */
	public void validerToken(String token, String choixClef) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException,
	SignatureException, IllegalArgumentException, UnsupportedEncodingException {
		
			Jws<Claims> jws = Jwts.parser().setSigningKey(choixClef.getBytes("UTF-8")).parseClaimsJws(token);
		

	}
	
	
	
	
}
