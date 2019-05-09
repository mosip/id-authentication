package io.mosip.registration.tpm.constants;

/**
 * The constants used in TPM module
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public class Constants {

	private Constants() {
	}

	public static final byte[] NULL_VECTOR = new byte[0];

	/**
	 * Constants utilized by Logger -- Starts
	 */
	// Module Name
	private static final String APP_NAME = "REG - ";
	private static final String LOG_MODULE = APP_NAME + "TPM - ";

	// Application ID and Name
	public static final String APPLICATION_ID = "REG";
	public static final String APPLICATION_NAME = "REGISTRATION";
	
	// Classes
	private static final String TPM_SIGNATURE = APP_NAME + "SIGNATURE_SERVICE - ";
	private static final String TPM_SIGN_VALIDATION = APP_NAME + "SIGNATURE_VALIDATION_SERVICE - ";
	private static final String TPM_ASYMMETRIC_KEY_CREATION = APP_NAME + "ASYMMETRIC_KEY_CREATION - ";
	private static final String TPM_ASYMMETRIC_ENCRYPTION = APP_NAME + "ASYMMETRIC_ENCRYPTION_SERVICE - ";
	private static final String TPM_ASYMMETRIC_DECRYPTION = APP_NAME + "ASYMMETRIC_DECRYPTION_SERVICE - ";
	private static final String TPM_SERVICE = APP_NAME + "TPM_SERVICE - ";

	public static final String LOG_TPM_INITIALIZATION = LOG_MODULE + "PLATFORM_TPM_INITIALIZATION";
	public static final String LOG_PUBLIC_KEY = LOG_MODULE + "SIGN_KEY_CREATION_SERVICE";
	public static final String TPM_SIGN_DATA = TPM_SIGNATURE + "SIGN_DATA";
	public static final String TPM_SIGN_VALIDATE_BY_KEY = TPM_SIGN_VALIDATION + "VALIDATE_SIGNATURE_USING_PUBLIC_KEY";
	public static final String TPM_ASYM_KEY_CREATION = TPM_ASYMMETRIC_KEY_CREATION + "CREATE_PERSISTENT_KEY";
	public static final String TPM_ASYM_ENCRYPTION = TPM_ASYMMETRIC_ENCRYPTION + "ENCRYPT_USING_TPM";
	public static final String TPM_ASYM_DECRYPTION = TPM_ASYMMETRIC_DECRYPTION + "DECRYPT_USING_TPM";
	public static final String TPM_SERVICE_SIGN = TPM_SERVICE + "SIGN_DATA";
	public static final String TPM_SERVICE_VALIDATE_SIGN_BY_PUBLIC_PART = TPM_SERVICE
			+ "VALIDATING_SIGNATURE_USING_PUBLIC_PART";
	public static final String TPM_SERVICE_ASYMMETRIC_ENCRYPTION = TPM_SERVICE + "ASYMMETRIC_ENCRYPT";
	public static final String TPM_SERVICE_ASYMMETRIC_DECRYPTION = TPM_SERVICE + "ASYMMETRIC_DECRYPT";
	public static final String TPM_SERVICE_GET_SIGN_PUBLIC = TPM_SERVICE + "GET_SIGNING_PUBLIC_PART";
	/**
	 * Constants utilized by Logger -- Ends
	 */

}
