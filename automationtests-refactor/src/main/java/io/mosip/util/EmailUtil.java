
package io.mosip.util;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;

/**
 * The class is to read content from email
 * 
 * @author Arjun
 *
 */
public class EmailUtil {
	
	private static Logger logger = Logger.getLogger(EmailUtil.class);

	/**
	 * The method returns the messagebody and subject of latest email received
	 * 
	 * @param configFileName - property filename
	 * @param emailID - email address
	 * @param password - password for the email address
	 * @return Map of MessageBody,Subject
	 */
	@SuppressWarnings("null")
	public Map<String, String> readEmail(String configFileName, String emailID, String password) {
		Properties props = new Properties();
		Map<String, String> emailParameter = new HashMap<String, String>();
		try {
			props.load(new FileInputStream(new File("src/test/resources/" + configFileName + ".properties")));
			Session session = Session.getDefaultInstance(props, null);
			Store store = session.getStore("imaps");
			store.connect("email.mindtree.com", emailID, password);
			Folder inbox = store.getFolder("INBOX");
			inbox.open(Folder.READ_ONLY);
			Message[] messages = inbox.getMessages();
			// To get latest email or message or top most email in inbox
			Message message = messages[inbox.getMessageCount() - 1];
			emailParameter.put("MessageBody", getTextFromMessage(message));
			emailParameter.put("Subject", message.getSubject());
			inbox.close(true);
			store.close();
			logger.info("Read the message from the email: " + emailID + " successfully");
		} catch (Exception e) {
			logger.error("Error in retrieving the message from the email: " + emailID + " - " + e.getMessage());
		}
		return emailParameter;
	}
	
	/**
	 * Method is to get message content from email
	 * 
	 * @author Vignesh
	 * @param message - Message
	 * @return String - Message Content
	 * @throws MessagingException - Exception for message
	 * @throws java.io.IOException - IO Exception
	 */
	private String getTextFromMessage(Message message) throws MessagingException, java.io.IOException {
	    String result = "";
	    if (message.isMimeType("text/plain")) {
	        result = message.getContent().toString();
	    } else if (message.isMimeType("multipart/*")) {
	        MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
	        result = getTextFromMimeMultipart(mimeMultipart);
	    }
	    return result;
	}

	/**
	 * The method is to get the message body content if message type is MimeMultipart
	 * 
	 * @author Vignesh
	 * @param mimeMultipart - object of mimeMultipart message type
	 * @return String - message content
	 * @throws MessagingException - Message exception
	 * @throws java.io.IOException - IO Exception
	 */
	private String getTextFromMimeMultipart(
	        MimeMultipart mimeMultipart)  throws MessagingException, java.io.IOException{
	    String result = "";
	    int count = mimeMultipart.getCount();
	    for (int i = 0; i < count; i++) {
	        BodyPart bodyPart = mimeMultipart.getBodyPart(i);
	        if (bodyPart.isMimeType("text/plain")) {
	            result = result + "\n" + bodyPart.getContent();
	            break; // without break same text appears twice in my tests
	        } else if (bodyPart.getContent() instanceof MimeMultipart){
	            result = result + getTextFromMimeMultipart((MimeMultipart)bodyPart.getContent());
	        }
	    }
	    return result;
	}	
	
	public static void main(String arg[])
	{
		EmailUtil objEmailUtil = new EmailUtil();
		objEmailUtil.readEmail("EmailConfig", "vignesh.vijayakumar@mindtree.com", "marMT@2019");
		//objEmailUtil.readEmail("EmailConfig", "arjun.mosip@gmail.com", "Arjun@123");
	}

}