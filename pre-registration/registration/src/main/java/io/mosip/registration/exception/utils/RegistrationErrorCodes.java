package io.mosip.registration.exception.utils;

/**
 * Internal Packet handler error codes.
 *
 */
public final class RegistrationErrorCodes {

	private static final String PRG_PAM_PREFIX = "PRG_";

	private RegistrationErrorCodes() {
		throw new IllegalStateException("Utility class");
	}

	// Generic
	private static final String PRG_PAM_MODULE = PRG_PAM_PREFIX + "PAM_";
	
	
	public static final String REGISTRATION_TABLE_NOTACCESSIBLE = PRG_PAM_MODULE+"REGISTRATION_TABLE_NOTACCESSIBLE";
	
	public static final String AGE_CRITERIA_DOESNOT_MET = PRG_PAM_MODULE+"RAGE_CRITERIA_DOESNOT_MET";
	
	public static final String DELETE_OPERATION_NOT_ALLOWED_PRIMARY = PRG_PAM_MODULE+"DELETE_OPERATION_NOT_ALLOWED_FOR_PRIMARY";
	
	public static final String DELETE_OPERATION_NOT_ALLOWED_FOR_OTHERTHEN_DRAFT = PRG_PAM_MODULE+"DELETE_OPERATION_NOT_ALLOWED_FOR_OTHERTHEN_DRAFT";
	
	public static final String RECORD_NOT_FOUND = PRG_PAM_MODULE+"RECORD_NOT_FOUND";
}
