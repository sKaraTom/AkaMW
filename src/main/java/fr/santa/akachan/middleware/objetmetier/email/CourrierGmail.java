package fr.santa.akachan.middleware.objetmetier.email;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.AuthenticationFailedException;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class CourrierGmail 
{
    private static String protocol = "smtp";

    private String username;
    private String password;

    private Session session;
    private Message message;
    private Multipart multipart;

    public CourrierGmail() {
        this.multipart = new MimeMultipart();
        this.username = "akachanapp";
        this.password = "akacompteenvoi06";
    }
    
    /** étape préalable : ouvrir une session et instancier un nouveau message.
     */
    public void creerSessionEtNouveauMessage () {
        // une session est ouverte.
        this.session = getSession();
        this.message = new MimeMessage(session);
    }
    
    /** 
     * définir l'émetteur (destinateur) de l'envoi.
     * Ne sert que si on souhaite émettre un message depuis un autre compte qu'akachanapp
     * 
     * @param username (sans @aaaa.com)
     * @param password
     */
    public void setEmetteur(String username, String password)
    {
        this.username = username;
        this.password = password;
    }
    
    /** 
     * ajouter un destinataire
     * 
     * @param destinataire (adresse mail complète)
     * @throws DestinataireInvalideException 
     */
    public void ajouterDestinataire(String destinataire) throws DestinataireInvalideException {
    	
    	try {
    		message.addRecipient(Message.RecipientType.TO, new InternetAddress(destinataire));
    	
    	} catch(Exception e) {
    		throw new DestinataireInvalideException("adresse mail non valide.");
    	}
    }
    
    /** 
     * définir le titre du mail.
     * 
     * @param String titreMail 
     * @throws SujetInvalideException 
     */
    public void setSujet(String titreMail) throws SujetInvalideException {
        
    	try {
    		message.setSubject(titreMail);
    	} catch(Exception e) {
    		throw new SujetInvalideException("le titre du mail n'est pas valide.");
    	}
    }
    
    /** 
     * définir le contenu du message TEXTE.
     * 
     * @param body
     * @throws ContenuInvalideException 
     */
    public void setContenuTexte(String body) throws ContenuInvalideException {
    	
        BodyPart messageBodyPart = new MimeBodyPart();
        
        try {
	        messageBodyPart.setText(body);
	        multipart.addBodyPart(messageBodyPart);
	        message.setContent(multipart);
        
        } catch(Exception e) {
        	throw new ContenuInvalideException("le contenu du mail (texte) n'est pas valide.");
        }
    }
    
    
    /** 
     * définir le contenu du message HTML.
     * 
     * @param body	message à intégrer déjà mis en forme html
     * @throws ContenuInvalideException 
     */
    public void setContenuHtml(String body) throws ContenuInvalideException {
        BodyPart messageBodyPart = new MimeBodyPart();
        
        try {
	        messageBodyPart.setContent(body,"text/html");
	        multipart.addBodyPart(messageBodyPart);
	        message.setContent(multipart);
	        
        } catch(Exception e) {
        	throw new ContenuInvalideException("le contenu du mail (html) n'est pas valide.");
        }
    }
    
    /** 
     * méthode d'envoi de l'email avec l'objet Transport.
     * 
     * @throws MessagingException
     * @throws AuthentificationEchoueeException 
     * @throws DestinataireInvalideException 
     * @throws ConnexionEchoueeException 
     */
    public void envoyerMail() throws AuthentificationEchoueeException, DestinataireInvalideException, ConnexionEchoueeException, MessagingException {
        
    	Transport transport = session.getTransport(protocol);
        
        try {
        	transport.connect(username, password);
        } catch (AuthenticationFailedException a){
        	throw new AuthentificationEchoueeException("mauvais username ou password de l'émetteur.");
        } catch(Exception e){
        	throw new AuthentificationEchoueeException("problème à l'authentification.");
        }
        
        try {
        	transport.sendMessage(message, message.getAllRecipients());
        	
        } catch(SendFailedException s){
        	throw new DestinataireInvalideException("échec à l'envoi du message : l'adresse mail n'est pas valide " );
        	
        } catch(MessagingException m) {
        	throw new ConnexionEchoueeException("la connexion est coupée ou inexistante à la tentative d'envoi.");
        }
        
        transport.close();
    }
    
    /** 
     * ajouter une pièce jointe au mail à envoyer.
     * 
     * @param String emplacementFichier (emplacement sur le disque du fichier)
     * @throws MessagingException
     */
    public void ajouterPieceJointe(String emplacementFichier) throws MessagingException
    {
        BodyPart messageBodyPart = getFileBodyPart(emplacementFichier);
        multipart.addBodyPart(messageBodyPart);

        message.setContent(multipart);
    }
    
    /** 
     * obtenir le contenu fichier en pièce jointe.
     * 
     * @param filePath emplacement du fichier source.
     * @return
     * @throws MessagingException
     */
    private BodyPart getFileBodyPart(String emplacementFichier) throws MessagingException
    {
        BodyPart messageBodyPart = new MimeBodyPart();
        DataSource dataSource = new FileDataSource(emplacementFichier);
        messageBodyPart.setDataHandler(new DataHandler(dataSource));
        messageBodyPart.setFileName(emplacementFichier);

        return messageBodyPart;
    }
    
    /** 
     * methode pour ouvrir une session.
     * 
     * @return la session.
     */
    private Session getSession() {
        
    	 Authenticator auth = new Authenticator() {
 			//override the getPasswordAuthentication method
 			protected PasswordAuthentication getPasswordAuthentication() {
 				return new PasswordAuthentication(username+"@gmail.com", password);
 			}
 		};
 		
    	Properties proprietes = obtenirProprietesServeurMail();
        
        Session nouvelleSession = Session.getInstance(proprietes,auth);

        return nouvelleSession;
    }
    
    /** 
     * paramétrer les propriétés pour la session.
     * propriétes SSL (port  465)
     * 
     * @return Properties proprietes
     */
    private Properties obtenirProprietesServeurMail() {
    	
    	
      Properties proprietes = System.getProperties();
      proprietes.put("mail.smtp.host", protocol + ".gmail.com"); //SMTP Host
      proprietes.put("mail.smtp.socketFactory.port", "465"); //SSL Port
      proprietes.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); //SSL Factory Class
      proprietes.put("mail.smtp.auth", "true"); //Enabling SMTP Authentication
      proprietes.put("mail.smtp.port", "465"); //SMTP Port
    
      return proprietes;
    }
    
    
    
    
}