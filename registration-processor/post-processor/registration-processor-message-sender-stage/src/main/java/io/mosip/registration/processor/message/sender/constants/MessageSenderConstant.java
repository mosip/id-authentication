package io.mosip.registration.processor.message.sender.constants;

public class MessageSenderConstant {
	/**
	 * Private Constructor for this class
	 */
	private MessageSenderConstant() {

	}

	
	public static final String MESSAGE_SENDER_FAILED ="Message sender failed for registrationId ";
	public static final String MESSAGE_SENDER_NOTIF_SUCC ="Notification sent successfully for registrationId ";
	public static final String REFERENCE_TYPE_ID ="updated registration record";
	public static final String LATEST_TRANSACTION_TYPE_CODE ="updated registration status record";
	public static final String MESAGE_SENDER_EPTN_MISSING ="Email/phone/template/notification type is missing for registrationId " ;
	public static final String MESAGE_SENDER_TEMP_MISSING ="Template not found for notification with registrationId " ;
	public static final String MESAGE_SENDER_PACKET_STORE ="The Packet store set by the System is not accessible";
	public static final String MESAGE_SENDER_INTERNAL_ERROR ="Internal error occurred while processing registrationId ";

	
	
}
