package io.mosip.registration.processor.packet.receiver.exception.utils;

/**
 * Internal Packet handler error codes.
 *
 */
public final class IISPlatformErrorCodes {

	/** The Constant IIS_EPU_ATU_PREFIX. */
	private static final String IIS_EPU_ATU_PREFIX = "IIS_";

	/**
	 * Instantiates a new IIS platform error codes.
	 */
	private IISPlatformErrorCodes() {
		throw new IllegalStateException("Utility class");
	}

	/** The Constant IIS_EPU_ATU_GEN_MODULE. */
	// Generic
	private static final String IIS_EPU_ATU_GEN_MODULE = IIS_EPU_ATU_PREFIX + "GEN_";
	
	/** The Constant IIS_EPU_ATU_INVALID_PACKET. */
	public static final String IIS_EPU_ATU_INVALID_PACKET = IIS_EPU_ATU_GEN_MODULE + "INVALID_PACKET";
	
	/** The Constant IIS_EPU_ATU_ENROLMENT_STATUS_TABLE_NOTACCESSIBLE. */
	public static final String IIS_EPU_ATU_ENROLMENT_STATUS_TABLE_NOTACCESSIBLE = IIS_EPU_ATU_GEN_MODULE
			+ "ENROLMENT_STATUS_TABLE_NOTACCESSIBLE";
	
	/** The Constant IIS_EPU_ATU_FILE_SIZE_EXCEED. */
	public static final String IIS_EPU_ATU_FILE_SIZE_EXCEED = IIS_EPU_ATU_GEN_MODULE + "FILE_SIZE_EXCEEDED";
	
	/** The Constant IIS_EPU_ATU_DUPLICATE_UPLOAD. */
	public static final String IIS_EPU_ATU_DUPLICATE_UPLOAD = IIS_EPU_ATU_GEN_MODULE + "DUPLICATE_PACKET_UPLOAD";
	
	/** The Constant IIS_EPU_ATU_PACKET_NOT_AVAILABLE. */
	public static final String IIS_EPU_ATU_PACKET_NOT_AVAILABLE = IIS_EPU_ATU_GEN_MODULE + "PACKET_NOT_AVAILABLE";
	
	/** The Constant IIS_EPU_ATU_VALIDATION_ERROR. */
	public static final String IIS_EPU_ATU_VALIDATION_ERROR = IIS_EPU_ATU_GEN_MODULE + "VALIDATION_ERROR";
	
	/** The Constant IIS_EPU_ATU_PACKET_NOT_SYNC. */
	public static final String IIS_EPU_ATU_PACKET_NOT_SYNC = IIS_EPU_ATU_GEN_MODULE
			+ "PACKET NOT YET SYNC IN REGISTRATION";

	/** The Constant IIS_EPU_ATU_SYSTEM_MODULE. */
	// System Exceptions
	private static final String IIS_EPU_ATU_SYSTEM_MODULE = IIS_EPU_ATU_PREFIX + "SYSTEM_";
	
	/** The Constant IIS_EPU_ATU_UNEXCEPTED_ERROR. */
	public static final String IIS_EPU_ATU_UNEXCEPTED_ERROR = IIS_EPU_ATU_SYSTEM_MODULE + "UNEXCEPTED_ERROR";
	
	/** The Constant IIS_EPU_ATU_BAD_GATEWAY. */
	public static final String IIS_EPU_ATU_BAD_GATEWAY = IIS_EPU_ATU_SYSTEM_MODULE + "BAD_GATEWAY";
	
	/** The Constant IIS_EPU_ATU_SERVICE_UNAVAILABLE. */
	public static final String IIS_EPU_ATU_SERVICE_UNAVAILABLE = IIS_EPU_ATU_SYSTEM_MODULE + "SERVICE_UNAVAILABLE";
	
	/** The Constant IIS_EPU_ATU_SERVER_ERROR. */
	public static final String IIS_EPU_ATU_SERVER_ERROR = IIS_EPU_ATU_SYSTEM_MODULE + "SERVER_ERROR";
	
	/** The Constant IIS_EPU_ATU_TIMEOUT. */
	public static final String IIS_EPU_ATU_TIMEOUT = IIS_EPU_ATU_SYSTEM_MODULE + "TIMEOUT";

}
