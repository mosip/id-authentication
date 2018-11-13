package io.mosip.registration.processor.packet.receiver.exception.utils;

/**
 * Internal Packet handler error codes.
 *
 */
public final class IISPlatformErrorCodes {

	/**
	 * Instantiates a new IIS platform error codes.
	 */
	//PacketReciever 
	private static final String RPR_REGISTRATION_PROCESSOR_PREFIX = "RPR-";
	private static final String RPR_PACKET_RECEIVER_MODULE = "PKR-";
	private static final String ERROR_CODE1 = "001";
	private static final String ERROR_CODE2 = "002";
	private static final String ERROR_CODE3 = "003";
	private static final String ERROR_CODE4 = "004";
	private static final String ERROR_CODE5 = "005";
	private static final String ERROR_CODE6 = "006";
	private static final String ERROR_CODE7 = "007";
	private static final String ERROR_CODE8 = "008";
	private static final String ERROR_CODE9 = "009";
	private static final String ERROR_CODE10 = "010";
	private static final String ERROR_CODE11 = "011";

	private static final String RPR_PACKET_RECEIVER_ERROR_CODE = RPR_REGISTRATION_PROCESSOR_PREFIX
			+ RPR_PACKET_RECEIVER_MODULE;
	public static final String RPR_PKR_FILE_SIZE_EXCEEDED = RPR_PACKET_RECEIVER_ERROR_CODE + ERROR_CODE1;
	public static final String RPR_PKR_ENROLMENT_STATUS_TABLE_NOT_ACCESSIBLE = RPR_PACKET_RECEIVER_ERROR_CODE + ERROR_CODE2;
	public static final String RPR_PKR_DUPLICATE_UPLOAD = RPR_PACKET_RECEIVER_ERROR_CODE + ERROR_CODE3;
	public static final String RPR_PKR_INVALID_PACKET = RPR_PACKET_RECEIVER_ERROR_CODE + ERROR_CODE4;
	public static final String RPR_PKR_PACKET_NOT_AVAILABLE = RPR_PACKET_RECEIVER_ERROR_CODE + ERROR_CODE5;
	public static final String RPR_PKR_VALIDATION_ERROR = RPR_PACKET_RECEIVER_ERROR_CODE + ERROR_CODE6;
	public static final String RPR_PKR_PACKET_NOT_SYNC = RPR_PACKET_RECEIVER_ERROR_CODE + ERROR_CODE7;
	
	private IISPlatformErrorCodes() {
		throw new IllegalStateException("Utility class");
	}

	/** The Constant IIS_EPU_ATU_SYSTEM_MODULE. */
	// System Exceptions
	private static final String RPR_PKR_SYSTEM_MODULE = RPR_REGISTRATION_PROCESSOR_PREFIX + "SYSTEM_";
	public static final String RPR_PKR_UNEXCEPTED_ERROR = RPR_PKR_SYSTEM_MODULE + "UNEXCEPTED_ERROR";
	public static final String RPR_PKR_BAD_GATEWAY = RPR_PKR_SYSTEM_MODULE + "BAD_GATEWAY";
	public static final String RPR_PKR_SERVICE_UNAVAILABLE = RPR_PKR_SYSTEM_MODULE + "SERVICE_UNAVAILABLE";
	public static final String RPR_PKR_SERVER_ERROR = RPR_PKR_SYSTEM_MODULE + "SERVER_ERROR";
	public static final String RPR_PKR_TIMEOUT = RPR_PKR_SYSTEM_MODULE + "TIMEOUT";

}
