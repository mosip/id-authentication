package io.mosip.registration.processor.stages.uingenerator.util;


/**
 * The Class UinStatusMessage.
 * @author M1049387
 */
public class UinStatusMessage {

	/**
	 * Instantiates a new uin status message.
	 */
	private UinStatusMessage() {

	}
	
	/** The Constant PACKET_UIN_UPDATION_SUCCESS_MSG. */
	public static final String PACKET_UIN_UPDATION_SUCCESS_MSG = "The Uin Updated Successfully";

	/** The Constant PACKET_UIN_UPDATION_FAILURE_MSG. */
	public static final String PACKET_UIN_UPDATION_FAILURE_MSG = "The Uin Updation Failured";

	public static final String UIN_GENRATION_SMS_NOTIFICATION_SUCCESS = "Sms sent Successfully";

	public static final String UIN_GENRATION_EMAIL_NOTIFICATION_SUCCESS = "Email sent Successfully";

	public static final String UIN_UPDATION_ALREADY_ACTIVATED = "Uin is already activated";

	public static final String UIN_UPDATION_ACTIVATED = "Uin is activated";

	public static final String UIN_UPDATION_RE_ACTIVATION_FAILURE = "Uin activation failure";

	public static final String UIN_DEACTIVATE_FAILURE = "Uin is already deactivated for regId ";

	public static final String UIN_DEACTIVATE_SUCCESS = "Uin is deactivated for regId ";
	
	public static final String UIN_UPDATION_SUCCESS = "Data updated successfully for regId ";



}
