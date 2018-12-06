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
	public static final String RPR_PACKET_SCANNER_JOB_MODULE = RPR_REGISTRATION_PROCESSOR_PREFIX + "PSS-";

	/** The Constant RPR_PACKET_DECRYPTION_JOB_MODULE. */
	public static final String RPR_PACKET_DECRYPTION_JOB_MODULE = RPR_REGISTRATION_PROCESSOR_PREFIX + "PDS-";
	
	/** The Constant RPR_MANUAL_VERIFICATION_MODULE. */
	public static final String RPR_MANUAL_VERIFICATION_MODULE = RPR_REGISTRATION_PROCESSOR_PREFIX + "MVS-";

	public static final String RPR_REST_CLIENT_MODULE = RPR_REGISTRATION_PROCESSOR_PREFIX + "RCT-";

	public static final String RPR_SYSTEM_EXCEPTION = RPR_REGISTRATION_PROCESSOR_PREFIX + "SYS";

	/**
	 * Instantiates a new RPR platform error codes.
	 */
	private PlatformErrorConstants() {
		throw new IllegalStateException("Utility class");
	}

}
