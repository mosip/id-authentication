/**
 * 
 */
package io.mosip.kernel.auth.constant;

/**
 * @author Ramadurai Pandian
 *
 */
public class AuthConstant {

	public final static String APPTYPE_UIN = "UIN";

	public final static String APPTYPE_USERID = "USERID";

	public final static String APPTYPE_USER = "USERIDTYPE";

	public final static String USERPWD_SUCCESS_MESSAGE = "Username and password combination had been validated successfully";

	public final static String CLIENT_SECRET_SUCCESS_MESSAGE = "Clientid and Token combination had been validated successfully";

	public final static String TOKEN_SUCCESS_MESSAGE = "Token had been validated successfully";

	public final static String DATASOURCE = "_datasource";

	public static final String LDAP = "ldap";

	public static final String EMAIL = "email";

	public static final String PHONE = "mobile";

	public static final String OTP_SENT_MESSAGE = "OTP Sent Successfully";

	public static final String OTP_VALIDATION_MESSAGE = "OTP validated Successfully";
	
	public static final String AUTH_COOOKIE_HEADER="Authorization";
	
	public static final String AUTH_HEADER="Authorization=";

	public static final String TOKEN_INVALID_MESSAGE = "Token has been invalidated successfully";

	public static final String AUTH_TOKEN_EXPIRED_MESSAGE = "Auth token expired ";

	public static final String UIN_NOTIFICATION_MESSAGE = "UIN sent successfully for the channels";

	public static final String INDIVIDUAL = "INDIVIDUAL";

	public static final int RETURN_EXP_TIME = -10;

	public static final String ALL_CHANNELS_MESSAGE = "OTP message sent across all the channels";
	
	public static final String SUCCESS_STATUS = "success";
	
	public static final String IDA = "ida";
	
	public static final String COOKIE="Cookie";

	public static final String FAILURE_STATUS = "failure";
	
    public static final String LDAP_INITAL_CONTEXT_FACTORY="com.sun.jndi.ldap.LdapCtxFactory";
	
	public static final String PWD_ACCOUNT_LOCKED_TIME_ATTRIBUTE="pwdAccountLockedTime";
	
	public static final String PWD_FAILURE_TIME_ATTRIBUTE="pwdFailureTime";
	
	public static final String INVALID_REQUEST = "should not be null or empty";

	public static final String WHITESPACE = " ";
	
	public static final String SMS_NOTIFYTYPE = "SMS";
	
	public static final String EMAIL_NOTIFYTYPE = "EMAIL";

}
