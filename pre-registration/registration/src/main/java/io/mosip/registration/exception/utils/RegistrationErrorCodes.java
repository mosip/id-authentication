package io.mosip.registration.exception.utils;

/**
 * Internal Packet handler error codes.
 *
 */
public final class RegistrationErrorCodes {

	private static final String IIS_EPU_ATU_PREFIX = "IIS_";

	private RegistrationErrorCodes() {
		throw new IllegalStateException("Utility class");
	}

	// Generic
	private static final String IIS_EPU_ATU_GEN_MODULE = IIS_EPU_ATU_PREFIX + "GEN_";
	public static final String IIS_EPU_ATU_REGISTRATION_TABLE_NOTACCESSIBLE = IIS_EPU_ATU_GEN_MODULE
			+ "REGISTRATION_TABLE_NOTACCESSIBLE";
}
