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
		
		Date d = new Date();
		
		String token = Jwts.builder()
				  .setSubject("users/TzMUocMF4p")
				  .setIssuedAt(d)
				  .setExpiration( new Date(d.getTime()+(24*60*60*1000))) // date de maintenant + 1 jour.
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
