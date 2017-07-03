package fr.santa.akachan.middleware.email;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.AuthenticationFailedException;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
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

    public CourrierGmail()
    {
        this.multipart = new MimeMultipart();
        this.username = "akachanapp";
        this.password = "akacompteenvoi06";
    }
    
    
    public String getUsername() {
		return username;
	}
    
    public String getPassword() {
    	return password;
    }
    
    /** étape préalable : ouvrir une session et instancier un nouveau message.
     */
    public void creerSessionEtNouveauMessage () {
        // une session est ouverte.
        this.session = getSession();
        this.message = new MimeMessage(session);
    }
    
    /** définir l'émetteur (destinateur) de l'envoi.
     * Ne sert que si on souhaite émettre un message depuis un autre
     * compte qu'akachanapp
     * @param username (sans @aaaa.com)
     * @param password
     */
    public void setEmetteur(String username, String password)
    {
        this.username = username;
        this.password = password;
    }
    
    /** ajouter un destinataire
     * 
     * @param destinataire (adresse mail complète)
     * @throws AddressException
     * @throws MessagingException
     */
    public void ajouterDestinataire(String destinataire) throws AddressException, MessagingException
    {
    	message.addRecipient(Message.RecipientType.TO, new InternetAddress(destinataire));
    }
    
    /** définir le titre du mail.
     * 
     * @param String titreMail 
     * @throws MessagingException
     */
    public void setSujet(String titreMail) throws MessagingException
    {
        message.setSubject(titreMail);
    }
    
    /** définir le contenu du message TEXTE.
     * 
     * @param body
     * @throws MessagingException
     */
    public void setContenuTexte(String body) throws MessagingException
    {
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText(body);
        
        multipart.addBodyPart(messageBodyPart);

        message.setContent(multipart);
    }
    
    
    /** définir le contenu du message HTML.
     * 
     * @param body	message à intégrer déjà mis en forme html
     * @throws MessagingException
     */
    public void setContenuHtml(String body) throws MessagingException
    {
        BodyPart messageBodyPart = new MimeBodyPart();

        messageBodyPart.setContent(body,"text/html");
        
        multipart.addBodyPart(messageBodyPart);

        message.setContent(multipart);
    }
    
    /** méthode d'envoi de l'email avec l'objet Transport.
     * 
     * @throws MessagingException
     * @throws AuthentificationEchoueeException 
     */
    public void envoyerMail() throws MessagingException, AuthentificationEchoueeException
    {
        Transport transport = session.getTransport(protocol);
        
        try {
        transport.connect(username, password);
        } catch (AuthenticationFailedException a){
        	throw new AuthentificationEchoueeException();
        }
        
        transport.sendMessage(message, message.getAllRecipients());

        transport.close();
    }
    
    /** ajouter une pièce jointe au mail à envoyer.
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
    
    /** obtenir le contenu fichier en pièce jointe.
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
    
    /** methode pour ouvrir une session.
     * 
     * @return la session.
     */
    private Session getSession()
    {
        Properties proprietes = obtenirProprietesServeurMail();
        Session session = Session.getDefaultInstance(proprietes);

        return session;
    }
    
    /** paramétrer les propriétés pour la session.
     * 
     * @return Properties proprietes
     */
    private Properties obtenirProprietesServeurMail()
    {
        Properties proprietes = System.getProperties();
        proprietes.put("mail.smtp.starttls.enable", "true");
        proprietes.put("mail.smtp.host", protocol + ".gmail.com");
        proprietes.put("mail.smtp.user", username);
        proprietes.put("mail.smtp.password", password);
        proprietes.put("mail.smtp.port", "587");
        proprietes.put("mail.smtp.auth", "true");

        return proprietes;
    }
    
    
    
    
}