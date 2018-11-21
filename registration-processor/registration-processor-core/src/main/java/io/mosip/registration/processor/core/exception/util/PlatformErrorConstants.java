package io.mosip.registration.processor.core.exception.util;

/**
 * The Enum RPRPlatformErrorMessages.
 *
 * @author M1047487
 */
public final class PlatformErrorConstants {

	/**
	 * RPR platform error codes.
	 */
	private static final String RPR_REGISTRATION_PROCESSOR_PREFIX = "RPR-";
	
	/** The Constant RPR_PACKET_RECEIVER_MODULE. */
	public static final String RPR_PACKET_RECEIVER_MODULE = RPR_REGISTRATION_PROCESSOR_PREFIX + "PKR-";
	
	/** The Constant RPR_REGISTRATION_STATUS_MODULE. */
	public static final String RPR_REGISTRATION_STATUS_MODULE = RPR_REGISTRATION_PROCESSOR_PREFIX + "RGS-";
	
	/** The Constant RPR_PACKET_INFO_STORAGE_MODULE. */
	public static final String RPR_PACKET_INFO_STORAGE_MODULE = RPR_REGISTRATION_PROCESSOR_PREFIX + "PIS-";
	
	/** The Constant RPR_FILESYSTEM_ADAPTOR_CEPH_MODULE. */
	public static final String RPR_FILESYSTEM_ADAPTOR_CEPH_MODULE = RPR_REGISTRATION_PROCESSOR_PREFIX + "FAC-";
	
	/** The Constant RPR_PACKET_MANAGER_MODULE. */
	public static final String RPR_PACKET_MANAGER_MODULE = RPR_REGISTRATION_PROCESSOR_PREFIX + "PKM-";
	
	/** The Constant RPR_CAMEL_BRIDGE_MODULE. */
	public static final String RPR_CAMEL_BRIDGE_MODULE = RPR_REGISTRATION_PROCESSOR_PREFIX + "CMB-";
	
	/** The Constant RPR_QUALITY_CHECKER_MODULE. */
	public static final String RPR_QUALITY_CHECKER_MODULE = RPR_REGISTRATION_PROCESSOR_PREFIX + "QCR-";
	
	/** The Constant RPR_PACKET_SCANNER_JOB_MODULE. */
	public static final String RPR_PACKET_SCANNER_JOB_MODULE = RPR_REGISTRATION_PROCESSOR_PREFIX + "PSJ-";
	
	/** The Constant RPR_PACKET_DECRYPTION_JOB_MODULE. */
	public static final String RPR_PACKET_DECRYPTION_JOB_MODULE = RPR_REGISTRATION_PROCESSOR_PREFIX + "PDJ-";

	public static final String IIS_EPU_ATU_UNKNOWN_RESOURCE_EXCEPTION = null;
	public static final String MAPPING_JSON_EXCEPTION = null;
	public static final String IDENTITY_NOT_FOUND = null;
	public static final String UNABLE_TO_INSERT_DATA = null;
	public static final String FILE_NOT_FOUND = null;

	/**
	 * Instantiates a new RPR platform error codes.
	 */
	private PlatformErrorConstants() {
		throw new IllegalStateException("Utility class");
	}

}
