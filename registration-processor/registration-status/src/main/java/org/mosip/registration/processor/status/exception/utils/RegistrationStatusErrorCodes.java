package org.mosip.registration.processor.status.exception.utils;

/**
 * Internal Packet handler error codes.
 *
 */
public final class RegistrationStatusErrorCodes {

	private static final String IIS_EPU_ATU_PREFIX = "IIS_";

	private RegistrationStatusErrorCodes() {
		throw new IllegalStateException("Utility class");
	}

	// Generic
	private static final String IIS_EPU_ATU_GEN_MODULE = IIS_EPU_ATU_PREFIX + "GEN_";
	public static final String IIS_EPU_ATU_ENROLMENT_STATUS_TABLE_NOTACCESSIBLE = IIS_EPU_ATU_GEN_MODULE
			+ "ENROLMENT_STATUS_TABLE_NOTACCESSIBLE";
	public static final String IIS_EPU_ATU_TRANSACTION_TABLE_NOTACCESSIBLE = IIS_EPU_ATU_GEN_MODULE
			+ "TRANSACTION_TABLE_NOTACCESSIBLE";
}
