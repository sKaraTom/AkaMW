package fr.santa.akachan.middleware.service;

import java.util.*;
import java.util.regex.Pattern;

import javax.mail.*;
import javax.mail.internet.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.santa.akachan.middleware.email.AuthentificationEchoueeException;
import fr.santa.akachan.middleware.email.ConnexionEchoueeException;
import fr.santa.akachan.middleware.email.ContenuInvalideException;
import fr.santa.akachan.middleware.email.CourrierGmail;
import fr.santa.akachan.middleware.email.DestinataireInvalideException;
import fr.santa.akachan.middleware.email.SujetInvalideException;
import fr.santa.akachan.middleware.objetmetier.compte.EmailInvalideException;
import fr.santa.akachan.middleware.objetmetier.estimation.Estimation;
import fr.santa.akachan.middleware.rest.EstimationRS;

import javax.ejb.Stateless;

@Stateless
public class EmailService {
	
	private static final Logger LOGGER =
			LoggerFactory.getLogger(EmailService.class);
	
	
	/** Mail d'un client ou prospect --> Akachan (formulaire de contact) 
	 * A partir d'un formulaire saisi par le client/prospect, mettre en forme un mail à envoyer à Akachanapp.
	 * 
	 * @param listeChampsMailClient tous les champs saisis par le client.
	 * @throws AuthentificationEchoueeException	si l'authentification de l'émetteur (akachanaap) échoue.
	 * @throws DestinataireInvalideException 
	 * @throws SujetInvalideException 
	 * @throws ContenuInvalideException 
	 * @throws MessagingException si la création du Transport a rencontré un problème.
	 * @throws ConnexionEchoueeException 
	 */
	public void envoyerMailContact(List<String> listeChampsMailClient) throws AuthentificationEchoueeException, DestinataireInvalideException, SujetInvalideException, ContenuInvalideException, ConnexionEchoueeException, MessagingException {
			
			// listeChamps de la saisie formulaire de contact :
			// 0. prenom client
			// 1. email client.
			// 2. sujet (titre)
			// 3. message (contenu)
		
			String body = creerBodyMailContact(listeChampsMailClient);
		
			CourrierGmail emailAEnvoyer = new CourrierGmail();
			emailAEnvoyer.creerSessionEtNouveauMessage();
			emailAEnvoyer.ajouterDestinataire("akachanapp@gmail.com");
			emailAEnvoyer.setSujet(listeChampsMailClient.get(2));
			emailAEnvoyer.setContenuHtml(body);
			emailAEnvoyer.envoyerMail();
		}
	
	/** envoyer une sélection de prénoms choisis par le client à son adresse mail et/ou une autre adresse.
	 * 
	 * @param listePrenomsSelectionnes liste d'estimations sélectionnée depuis la liste Akachan.
	 * @param prenomClient pour l'inclure dans le sujet du mail.
	 * @param mailClient ou "nonVoulu" si non sélectionné.
	 * @param mailAutre ou "nonVoulu" si non sélectionné.
	 * @throws AuthentificationEchoueeException
	 * @throws DestinataireInvalideException
	 * @throws SujetInvalideException
	 * @throws ContenuInvalideException
	 * @throws ConnexionEchoueeException
	 * @throws MessagingException
	 * @throws EmailInvalideException 
	 */
	public void envoyerMailSelectionDePrenoms(List<Estimation> listePrenomsSelectionnes,String prenomClient, String mailClient, String mailAutre) 
			throws AuthentificationEchoueeException, DestinataireInvalideException, SujetInvalideException, 
			ContenuInvalideException, ConnexionEchoueeException, MessagingException, EmailInvalideException {
		
		CourrierGmail emailAEnvoyer = new CourrierGmail();
		emailAEnvoyer.creerSessionEtNouveauMessage();
		
		if(!mailClient.equals("nonVoulu")) {
			validerEmail(mailClient);
			emailAEnvoyer.ajouterDestinataire(mailClient);
		};
		
		if(!mailAutre.equals("nonVoulu")) {
			validerEmail(mailAutre);
			emailAEnvoyer.ajouterDestinataire(mailAutre);
		}
		
		emailAEnvoyer.setSujet(prenomClient + " vous a envoyé une liste de prénoms");
		
		StringBuilder bodyListe = new StringBuilder();
		
		for(Estimation estimation:listePrenomsSelectionnes) {
			bodyListe.append(estimation.getPrenom());
			bodyListe.append(" ");
			
			if(estimation.getSexe().equals("1")) {
				bodyListe.append("<span style='font-size:small;'>garçon</span>,");
			}
			else {
				bodyListe.append("<span style='font-size:small;'>fille</span>,");
			}
			bodyListe.append("		");
		}
		String bodyMisEnForme = creerBodyMailSelectionDePrenoms(bodyListe.toString());
		
		emailAEnvoyer.setContenuHtml(bodyMisEnForme);
		emailAEnvoyer.envoyerMail();
	}
	
	
	/** créer un contenu html de mail, depuis une liste de champs saisis par le client/prospect (formulaire contact).
	 * à envoyer sur le mail Akachan.
	 * @param listeChampsMailClient
	 * @return
	 */
	public String creerBodyMailContact(List<String> listeChampsMailClient) {
		
		// listeChamps de la saisie formulaire de contact :
		// 0. prenom client
		// 1. email client.
		// 2. sujet (titre)
		// 3. message (contenu)
		
		StringBuilder body = new StringBuilder();
		body.append("<!DOCTYPE html><html><head></head><body>");
		body.append("<div style='height:30px;background-color:#eb505f;border-radius:5px;'></div>");
		body.append("<div align='center' style='padding-top:30px;padding-bottom:20px;font-size:110%;'>");
		body.append("<p><b>");
		body.append(listeChampsMailClient.get(0));
		body.append("</b> dont l'adresse email est : ");
		body.append(listeChampsMailClient.get(1));
		body.append(" vous envoie ce message : ");
		body.append("</p>");
		body.append("<h3 style='color: #1e9ecc;'>");
		body.append(listeChampsMailClient.get(3));
		body.append("</h3></div>");
		//adresse du site final à mettre ici :
		body.append("<p style='font-size:small;'><a target='_blank' href='http://localhost:4200/'>Akachan.io</a></p>");
		body.append("<div style='height:30px;background-color:#eb505f;border-radius:5px;'></div>");
		body.append("</body></html>");
	    	
    	return body.toString();
		
	}
	
	/** créer le contenu html d'un mail, pour envoyer une liste de prénoms à un destinataire;
	 * 
	 * @param body la liste de prénoms formatée en une String.
	 * @return
	 */
	 private String creerBodyMailSelectionDePrenoms(String body) {
	    	
	    	StringBuilder builder = new StringBuilder();
	    	
	    	builder.append("<div style='height:30px;background-color:#eb505f;border-radius:5px;'></div>");
	    	builder.append("<div align='center' style='padding-top:30px;padding-bottom:20px;font-size:110%;'>");
	    	builder.append("<p>Voici les prénoms que j'ai sélectionnés sur le site Akachan : </p>");
	    	builder.append("<h2 style='color: #1e9ecc;'>");
	    	builder.append(body);
	    	builder.append("</h2></div>");
	    	builder.append("<div align='left' style='font-size:small;'>retrouvez-les et bien d'autres sur le site ");
	    	builder.append("<a target='_blank' href='http://localhost:4200/'>Akachan.io</a></div>"); // adresse du site final à mettre ici.
	    	builder.append("<div style='height:30px;background-color:#eb505f;border-radius:5px;'></div>");
	    	
	    	return builder.toString();
    }
	
		private void validerEmail(final String email) throws EmailInvalideException {
			
			Boolean emailValide = Pattern.matches("^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)+$", email);
			
			if(!emailValide) {
				throw new EmailInvalideException("email invalide.");
			}
		}
		
	
}
