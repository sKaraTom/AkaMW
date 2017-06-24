package fr.santa.akachan.middleware.service;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

import fr.santa.akachan.middleware.email.CourrierGmail;

import javax.ejb.Stateless;

@Stateless
public class EmailService {


	public void envoyerMail(String email) throws AddressException, MessagingException {
		
//		GmailSender sender = new GmailSender();
//		sender.setSender("akachanapp", "akacompteenvoi06");
//		sender.addRecipient(email);
//		sender.setSubject("The subject");
//		sender.setBody("The body");
////		sender.addAttachment("TestFile.txt");
//		sender.send();
		
		CourrierGmail emailAEnvoyer = new CourrierGmail();
		emailAEnvoyer.setEmetteur("akachanapp", "akacompteenvoi06");
		emailAEnvoyer.ajouterDestinataire(email);
		emailAEnvoyer.setSujet("Ma sélection de prénoms");
		emailAEnvoyer.setContenu("un prenom ici");
//		sender.ajouterPieceJointe("TestFile.txt");
		emailAEnvoyer.envoyerMail();
	}
	
	
	
}
