package io.mosip.registration.processor.packet.receiver.exception.utils;

/**
 * Internal Packet handler error codes.
 *
 */
public final class IISPlatformErrorCodes {

	private static final String IIS_EPU_ATU_PREFIX = "IIS_";

	private IISPlatformErrorCodes() {
		throw new IllegalStateException("Utility class");
	}

	// Generic
	private static final String IIS_EPU_ATU_GEN_MODULE = IIS_EPU_ATU_PREFIX + "GEN_";
	public static final String IIS_EPU_ATU_INVALID_PACKET = IIS_EPU_ATU_GEN_MODULE + "INVALID_PACKET";
	public static final String IIS_EPU_ATU_ENROLMENT_STATUS_TABLE_NOTACCESSIBLE = IIS_EPU_ATU_GEN_MODULE
			+ "ENROLMENT_STATUS_TABLE_NOTACCESSIBLE";
	public static final String IIS_EPU_ATU_FILE_SIZE_EXCEED = IIS_EPU_ATU_GEN_MODULE + "FILE_SIZE_EXCEEDED";
	public static final String IIS_EPU_ATU_DUPLICATE_UPLOAD = IIS_EPU_ATU_GEN_MODULE + "DUPLICATE_PACKET_UPLOAD";
	public static final String IIS_EPU_ATU_PACKET_NOT_AVAILABLE = IIS_EPU_ATU_GEN_MODULE + "PACKET_NOT_AVAILABLE";
	public static final String IIS_EPU_ATU_VALIDATION_ERROR = IIS_EPU_ATU_GEN_MODULE + "VALIDATION_ERROR";

	// System Exceptions
	private static final String IIS_EPU_ATU_SYSTEM_MODULE = IIS_EPU_ATU_PREFIX + "SYSTEM_";
	public static final String IIS_EPU_ATU_UNEXCEPTED_ERROR = IIS_EPU_ATU_SYSTEM_MODULE + "UNEXCEPTED_ERROR";
	public static final String IIS_EPU_ATU_BAD_GATEWAY = IIS_EPU_ATU_SYSTEM_MODULE + "BAD_GATEWAY";
	public static final String IIS_EPU_ATU_SERVICE_UNAVAILABLE = IIS_EPU_ATU_SYSTEM_MODULE + "SERVICE_UNAVAILABLE";
	public static final String IIS_EPU_ATU_SERVER_ERROR = IIS_EPU_ATU_SYSTEM_MODULE + "SERVER_ERROR";
	public static final String IIS_EPU_ATU_TIMEOUT = IIS_EPU_ATU_SYSTEM_MODULE + "TIMEOUT";

}
