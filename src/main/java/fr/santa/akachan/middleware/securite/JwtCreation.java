package fr.santa.akachan.middleware.securite;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import fr.santa.akachan.middleware.objetmetier.client.Client;
import fr.santa.akachan.middleware.objetmetier.compte.Compte;
import io.jsonwebtoken.*;

import javax.ejb.Stateless;
import javax.transaction.Transactional;

@Stateless
@Transactional
public class JwtCreation {

	
	Key key = MacProvider.generateKey();
	
	
	public String creerToken() {
		
	
		String JwtToken = Jwts.builder()
				.setSubject("Joe")
				.signWith(SignatureAlgorithm.HS512, key)
				.compact();
		
		// bloc à s'inspirer pour validation
		assert Jwts.parser()
					.setSigningKey(key)
					.parseClaimsJws(JwtToken)
					.getBody()
					.getSubject()
					.equals("Joe");
		
		try {
			 
		    Jwts.parser().setSigningKey(key).parseClaimsJws(JwtToken);
		 
		    //OK, we can trust this JWT
		 
		} catch (SignatureException e) {
		 
		    //don't trust the JWT!
		}
		
		return JwtToken;
	
	}
	
	public String creerToken2(Compte compte) throws UnsupportedEncodingException {
		
		Client client = compte.getClient();
		
		Date d = new Date();
		
		String jwt = Jwts.builder()
				  .setSubject("users/TzMUocMF4p")
				  .setIssuedAt(d)
				  .setExpiration( new Date(d.getTime()+(24*60*60*1000)))
				  .claim("prenom", client.getPrenom())
				  .claim("sexe", client.getSexe())
				  .signWith(
				    SignatureAlgorithm.HS256,
				    "secret".getBytes("UTF-8")
				  )
				  .compact();
		
	return jwt;
	}
	
	
	/*
	private String createJWT(String id, String issuer, String subject, long ttlMillis) {
		 
	    //The JWT signature algorithm we will be using to sign the token
	    SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
	 
	    long nowMillis = System.currentTimeMillis();
	    Date now = new Date(nowMillis);
	 
	    //We will sign our JWT with our ApiKey secret
	    byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(apiKey.getSecret());
	    Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
	 
	    //Let's set the JWT Claims
	    JwtBuilder builder = Jwts.builder().setId(id)
	                                .setIssuedAt(now)
	                                .setSubject(subject)
	                                .setIssuer(issuer)
	                                .signWith(signatureAlgorithm, signingKey);
	 
	    //if it has been specified, let's add the expiration
	    if (ttlMillis >= 0) {
	    long expMillis = nowMillis + ttlMillis;
	        Date exp = new Date(expMillis);
	        builder.setExpiration(exp);
	    }
	 
	    //Builds the JWT and serializes it to a compact, URL-safe string
	    return builder.compact();
	}
	*/
	
}
