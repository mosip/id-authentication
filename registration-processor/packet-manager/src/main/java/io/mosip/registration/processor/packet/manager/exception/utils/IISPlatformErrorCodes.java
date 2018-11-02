package io.mosip.registration.processor.packet.manager.exception.utils;

/**
 * Internal Packet clean up error codes.
 *
 */
public final class IISPlatformErrorCodes {

	/** The Constant IIS_EPU_FSS_PREFIX. */
	private static final String IIS_EPU_FSS_PREFIX = "IIS_";

	/**
	 * Instantiates a new IIS platform error codes.
	 */
	private IISPlatformErrorCodes() {
		throw new IllegalStateException("Utility class");
	}

	/** The Constant IIS_EPU_FSS_GEN_MODULE. */
	// Generic
	private static final String IIS_EPU_FSS_GEN_MODULE = IIS_EPU_FSS_PREFIX + "GEN_";
	
	/** The Constant IIS_EPU_FSS_CORE_KERNEL_NOT_RESPONDING. */
	public static final String IIS_EPU_FSS_CORE_KERNEL_NOT_RESPONDING = IIS_EPU_FSS_GEN_MODULE + "CORE_KERNEL_NOT_RESPONDING";
	
	/** The Constant IIS_EPU_FSS_FILE_PATH_NOT_ACCESSIBLE. */
	public static final String IIS_EPU_FSS_FILE_PATH_NOT_ACCESSIBLE = IIS_EPU_FSS_GEN_MODULE + "FILE_PATH_NOT_ACCESSIBLE";
	
	/** The Constant IIS_EPU_FSS_FILE_NOT_FOUND_IN_DESTINATION. */
	public static final String IIS_EPU_FSS_FILE_NOT_FOUND_IN_DESTINATION = IIS_EPU_FSS_GEN_MODULE + "FILE_NOT_FOUND_IN_DESTINATION ";
	
	/** The Constant IIS_EPU_FSS_FILE_NOT_FOUND_IN_SOURCE. */
	public static final String IIS_EPU_FSS_FILE_NOT_FOUND_IN_SOURCE = IIS_EPU_FSS_GEN_MODULE + "FILE_NOT_FOUND_IN_SOURCE";
	
	/** The Constant IIS_EPU_FSS_SYSTEM_MODULE. */
	// System Exceptions
	private static final String IIS_EPU_FSS_SYSTEM_MODULE = IIS_EPU_FSS_PREFIX + "SYSTEM_";
	
	/** The Constant IIS_EPU_FSS_UNEXCEPTED_ERROR. */
	public static final String IIS_EPU_FSS_UNEXCEPTED_ERROR = IIS_EPU_FSS_SYSTEM_MODULE + "UNEXCEPTED_ERROR";
	
	/** The Constant IIS_EPU_FSS_BAD_GATEWAY. */
	public static final String IIS_EPU_FSS_BAD_GATEWAY = IIS_EPU_FSS_SYSTEM_MODULE + "BAD_GATEWAY";
	
	/** The Constant IIS_EPU_FSS_SERVICE_UNAVAILABLE. */
	public static final String IIS_EPU_FSS_SERVICE_UNAVAILABLE = IIS_EPU_FSS_SYSTEM_MODULE + "SERVICE_UNAVAILABLE";
	
	/** The Constant IIS_EPU_FSS_SERVER_ERROR. */
	public static final String IIS_EPU_FSS_SERVER_ERROR = IIS_EPU_FSS_SYSTEM_MODULE + "SERVER_ERROR";
	
	/** The Constant IIS_EPU_FSS_TIMEOUT. */
	public static final String IIS_EPU_FSS_TIMEOUT = IIS_EPU_FSS_SYSTEM_MODULE + "TIMEOUT";	
}
