package io.mosip.registration.processor.packet.scanner.job.exception.utils;

/**
 * The Class Packet Scanner Error Codes.
 */
public final class PacketScannerErrorCodes {

	/** The Constant IIS_EPP_EPV_PREFIX. */
	private static final String IIS_EPP_EPV_PREFIX = "IIS_";

	/**
	 * Instantiates a new packet scanner error codes.
	 */
	private PacketScannerErrorCodes() {
		throw new IllegalStateException("Utility class");
	}

	/** The Constant IIS_EPP_EPV_PREFIX_GEN_MODULE. */
	// Generic
	private static final String IIS_EPP_EPV_PREFIX_GEN_MODULE = IIS_EPP_EPV_PREFIX + "GEN_";
	
	/** The Constant IIS_EPP_EPV_CORE_KERNEL_NOT_RESPONDING. */
	public static final String IIS_EPP_EPV_CORE_KERNEL_NOT_RESPONDING = IIS_EPP_EPV_PREFIX_GEN_MODULE
			+ "CORE_KERNEL_NOT_RESPONDING";
	
	/** The Constant IIS_EPP_EPV_DFS_NOT_ACCESSIBLE. */
	public static final String IIS_EPP_EPV_DFS_NOT_ACCESSIBLE = IIS_EPP_EPV_PREFIX_GEN_MODULE + "DFS_NOT_ACCESSIBLE";
	
	/** The Constant IIS_EPP_EPV_RETRY_FOLDER_NOT_ACCESSIBLE. */
	public static final String IIS_EPP_EPV_RETRY_FOLDER_NOT_ACCESSIBLE = IIS_EPP_EPV_PREFIX_GEN_MODULE
			+ "RETRY_FOLDER_NOT_ACCESSIBLE";
	
	/** The Constant IIS_EPP_EPV_VIRUS_SCAN_FOLDER_NOT_ACCESSIBLE. */
	public static final String IIS_EPP_EPV_VIRUS_SCAN_FOLDER_NOT_ACCESSIBLE = IIS_EPP_EPV_PREFIX_GEN_MODULE
			+ "VIRUS_SCAN_FOLDER_NOT_ACCESSIBLE";
	
	/** The Constant IIS_EPP_EPV_SPACE_UNAVAILABLE_FOR_RETRY_FOLDER. */
	public static final String IIS_EPP_EPV_SPACE_UNAVAILABLE_FOR_RETRY_FOLDER = IIS_EPP_EPV_PREFIX_GEN_MODULE
			+ "SPACE_UNAVAILABLE_FOR_RETRY_FOLDER";
	
	/** The Constant IIS_EPP_EPV_VIRUS_SCAN_FAILED. */
	public static final String IIS_EPP_EPV_VIRUS_SCAN_FAILED = IIS_EPP_EPV_PREFIX_GEN_MODULE + "VIRUS_SCAN_FAILED";
	
	/** The Constant IIS_EPP_EPV_REGISTRATION_STATUS_TABLE_NOT_ACCESSIBLE. */
	public static final String IIS_EPP_EPV_REGISTRATION_STATUS_TABLE_NOT_ACCESSIBLE = IIS_EPP_EPV_PREFIX_GEN_MODULE
			+ "REGISTRATION_STATUS_TABLE_NOT_ACCESSIBLE";
	
	/** The Constant IIS_EPP_EPV_FTP_FOLDER_NOT_ACCESSIBLE. */
	public static final String IIS_EPP_EPV_FTP_FOLDER_NOT_ACCESSIBLE = IIS_EPP_EPV_PREFIX_GEN_MODULE
			+ "FTP_FOLDER_NOT_ACCESSIBLE";

}
