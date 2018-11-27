package io.mosip.preregistration.documents.exception.utils;

/**
 * Pre-Registration error messages 
 *
 *@author M1037717 
 */
public final class RegistrationErrorMessages {

	private static final String PRG_PAM_PREFIX = "PRG_";

	private RegistrationErrorMessages() {
		throw new IllegalStateException("Utility class");
	}

	// Generic
	private static final String PRG_PAM_MODULE = PRG_PAM_PREFIX + "PAM_";

	public static final String REGISTRATION_TABLE_NOTACCESSIBLE = PRG_PAM_MODULE + "REGISTRATION_TABLE_NOTACCESSIBLE";

	public static final String AGE_CRITERIA_DOESNOT_MET = PRG_PAM_MODULE + "AGE_CRITERIA_DOESNOT_MET";

	public static final String DELETE_OPERATION_NOT_ALLOWED_PRIMARY = PRG_PAM_MODULE
			+ "DELETE_OPERATION_NOT_ALLOWED_FOR_PRIMARY";

	public static final String DELETE_OPERATION_NOT_ALLOWED_FOR_OTHERTHEN_DRAFT = PRG_PAM_MODULE
			+ "DELETE_OPERATION_NOT_ALLOWED_FOR_OTHERTHEN_DRAFT";

	public static final String RECORD_NOT_FOUND = PRG_PAM_MODULE + "RECORD_NOT_FOUND";
}
