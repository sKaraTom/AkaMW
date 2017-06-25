package fr.santa.akachan.middleware.service;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.santa.akachan.middleware.email.AuthentificationEchoueeException;
import fr.santa.akachan.middleware.email.CourrierGmail;
import fr.santa.akachan.middleware.rest.EstimationRS;

import javax.ejb.Stateless;

@Stateless
public class EmailService {
	
	private static final Logger LOGGER =
			LoggerFactory.getLogger(EmailService.class);

	public void envoyerMail(String email) throws AddressException, MessagingException, AuthentificationEchoueeException {
		
		CourrierGmail emailAEnvoyer = new CourrierGmail();
		emailAEnvoyer.creerSessionEtNouveauMessage();
		emailAEnvoyer.ajouterDestinataire(email);
		emailAEnvoyer.setSujet("Ma sélection de prénoms");
		emailAEnvoyer.setContenuHtml("un prenom ici");
//		sender.ajouterPieceJointe("TestFile.txt");
		emailAEnvoyer.envoyerMail();
	}
	
	/** Mail d'un client ou prospect --> Akachan (formulaire de contact) 
	 * A partir d'un formulaire saisi par le client/prospect, mettre en forme un mail à envoyer à Akachanapp.
	 * 
	 * @param listeChampsMailClient tous les champs saisis par le client.
	 * @throws AddressException
	 * @throws MessagingException 
	 * @throws AuthentificationEchoueeException	si l'authentification de l'émetteur (akachanaap) échoue.
	 */
	public void envoyerMailContact(List<String> listeChampsMailClient) throws AddressException, MessagingException, AuthentificationEchoueeException {
			
			// listeChamps de la saisie formulaire de contact :
			// 0. prenom client
			// 1. email client.
			// 2. sujet (titre)
			// 3. message (contenu)
		
			StringBuilder builderContenu = new StringBuilder();
			builderContenu.append(listeChampsMailClient.get(0));
			builderContenu.append( " dont le mail est : ");
			builderContenu.append(listeChampsMailClient.get(1));
			builderContenu.append(" vous envoie ce message : ");
			builderContenu.append(listeChampsMailClient.get(3));
			
			String miseEnFormeContenu = builderContenu.toString();
		
			CourrierGmail emailAEnvoyer = new CourrierGmail();
			emailAEnvoyer.creerSessionEtNouveauMessage();
			emailAEnvoyer.ajouterDestinataire("akachanapp@gmail.com");
			emailAEnvoyer.setSujet(listeChampsMailClient.get(2));
			emailAEnvoyer.setContenuTexte(miseEnFormeContenu);
			emailAEnvoyer.envoyerMail();
		}
	
	
	
	
}
