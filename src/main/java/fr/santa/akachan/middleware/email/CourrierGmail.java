package fr.santa.akachan.middleware.email;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
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
    }
    
    /** définir l'émetteur (destinateur) de l'envoi.
     * 
     * @param username (sans @aaaa.com)
     * @param password
     */
    public void setEmetteur(String username, String password)
    {
        this.username = username;
        this.password = password;
        
        // une session est ouverte.
        this.session = getSession();
        this.message = new MimeMessage(session);
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
    
    /** définir le contenu du message.
     * 
     * @param body
     * @throws MessagingException
     */
    public void setContenu(String body) throws MessagingException
    {
        BodyPart messageBodyPart = new MimeBodyPart();
//        messageBodyPart.setText(body);
        String contenuHtml = creerContenuEmail(body);
        
        messageBodyPart.setContent(contenuHtml, "text/html");
        
        multipart.addBodyPart(messageBodyPart);

        message.setContent(multipart);
    }
    
    /** méthode d'envoi de l'email avec l'objet Transport.
     * 
     * @throws MessagingException
     */
    public void envoyerMail() throws MessagingException
    {
        Transport transport = session.getTransport(protocol);
        transport.connect(username, password);
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
    
    private String creerContenuEmail(String body) {
    	
    	StringBuilder builder = new StringBuilder();
    	
    	builder.append("<div style='height:30px;background-color:#eb505f;border-radius:5px;'></div>");
    	builder.append("<div align='center' style='padding-top:30px;padding-bottom:20px;font-size:110%;'>");
    	builder.append("<p>Voici les prénoms que j'ai sélectionné sur le site Akachan : </p>");
    	builder.append("<h2 style='color: #1e9ecc;'>");
    	builder.append(body);
    	builder.append("</h2></div>");
    	builder.append("<div align='left' style='font-size:small;'>retrouvez-les et bien d'autres sur le site ");
    	builder.append("<a target='_blank' href='http://localhost:4200/'>Akachan.io</a></div>"); // adresse du site final à mettre ici.
    	builder.append("<div style='height:30px;background-color:#eb505f;border-radius:5px;'></div>");
//    	builder.append("");
    	
    	return builder.toString();
    }
    
    
    
}