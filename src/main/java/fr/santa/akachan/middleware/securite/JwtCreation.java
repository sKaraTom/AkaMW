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

	
	
	public String creerToken2(Compte compte) throws UnsupportedEncodingException {
		
		ClefSecrete clefSecrete = new ClefSecrete();
		Client client = compte.getClient();
		
		Date date = new Date();
		long t = date.getTime();
		Date dateExpiration = new Date(t + (240*60*60*1000)); // date de maintenant + 10 jours. 10*24h*60mn*60sec*1000ms
		// 5000l pour test 5 secondes
		
		String token = Jwts.builder()
				  .setSubject("users/TzMUocMF4p")
				  .setIssuedAt(date)
				  .setExpiration(dateExpiration)
				  .claim("prenom", client.getPrenom())
				  .claim("sexe", client.getSexe())
				  .signWith(
				    SignatureAlgorithm.HS256,
				    clefSecrete.getSecret().getBytes("UTF-8")
				  )
				  .compact();
		
	return token;	
	}
	
	
}
