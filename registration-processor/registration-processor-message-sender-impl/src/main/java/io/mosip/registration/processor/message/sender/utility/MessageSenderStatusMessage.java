package io.mosip.registration.processor.message.sender.utility;

/**
 * The Class MessageSenderStatusMessage.
 * 
 * @author M1048358
 */
public class MessageSenderStatusMessage {
	
	/**
	 * Instantiates a new message sender status message.
	 */
	private MessageSenderStatusMessage() {
	}
	
	/** The Constant SMS_NOTIFICATION_SUCCESS. */
	public static final String SMS_NOTIFICATION_SUCCESS = "Sms sent Successfully";

	/** The Constant EMAIL_NOTIFICATION_SUCCESS. */
	public static final String EMAIL_NOTIFICATION_SUCCESS = "Email sent Successfully";
	
	/** The Constant TEMPLATE_NOT_FOUND. */
	public static final String TEMPLATE_NOT_FOUND = "sms and email template not found";
}
