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
	public static final String PUBLIC_PART_FILE_NAME = "publicPart.dat";

	// Logger
	private static final String LOG_MODULE = "REG - UTILITY - ";
	public static final String APPLICATION_ID = "REG";
	public static final String APPLICATION_NAME = "REGISTRATION";
	public static final String LOG_APP = LOG_MODULE + "APP";
	public static final String LOG_TPM_INITIALIZATION = LOG_MODULE + "PLATFORM_TPM_INITIALIZATION";
	public static final String LOG_PUBLIC_KEY = LOG_MODULE + "TPM_PUBLIC_KEY";
	public static final String LOG_TPM_FILE_UTILS = LOG_MODULE + "TPM_FILE_UTILS";

}
