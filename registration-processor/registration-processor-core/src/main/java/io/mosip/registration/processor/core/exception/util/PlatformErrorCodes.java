package io.mosip.registration.processor.core.exception.util;

/**
 * The Enum RPRPlatformErrorMessages.
 *
 * @author M1047487
 */
public final class PlatformErrorCodes {

	/**
	 * RPR platform error codes.
	 */
	private static final String RPR_REGISTRATION_PROCESSOR_PREFIX = "RPR-";
	
	/** The Constant RPR_PACKET_RECEIVER_MODULE. */
	private static final String RPR_PACKET_RECEIVER_MODULE = "PKR-";
	
	/** The Constant RPR_REGISTRATION_STATUS_MODULE. */
	private static final String RPR_REGISTRATION_STATUS_MODULE = "RGS-";
	
	/** The Constant RPR_PACKET_INFO_STORAGE_MODULE. */
	private static final String RPR_PACKET_INFO_STORAGE_MODULE = "PIS-";
	
	/** The Constant RPR_FILESYSTEM_ADAPTOR_CEPH_MODULE. */
	private static final String RPR_FILESYSTEM_ADAPTOR_CEPH_MODULE = "FAC-";
	
	/** The Constant RPR_PACKET_MANAGER_MODULE. */
	private static final String RPR_PACKET_MANAGER_MODULE = "PKM-";
	
	/** The Constant RPR_CAMEL_BRIDGE_MODULE. */
	private static final String RPR_CAMEL_BRIDGE_MODULE = "CMB-";
	
	/** The Constant RPR_QUALITY_CHECKER_MODULE. */
	private static final String RPR_QUALITY_CHECKER_MODULE = "QCR-";
	
	/** The Constant RPR_PACKET_SCANNER_JOB_MODULE. */
	private static final String RPR_PACKET_SCANNER_JOB_MODULE = "PSJ-";
	
	/** The Constant RPR_PACKET_DECRYPTION_JOB_MODULE. */
	private static final String RPR_PACKET_DECRYPTION_JOB_MODULE = "PDJ-";

	/** The Constant error codes */
	private static final String ERROR_CODE1 = "001";
	private static final String ERROR_CODE2 = "002";
	private static final String ERROR_CODE3 = "003";
	private static final String ERROR_CODE4 = "004";
	private static final String ERROR_CODE5 = "005";
	private static final String ERROR_CODE6 = "006";
	private static final String ERROR_CODE7 = "007";
	private static final String ERROR_CODE8 = "008";
	private static final String ERROR_CODE9 = "009";

	// Packet Receiver exception error code
	private static final String RPR_PACKET_RECEIVER_ERROR_CODE = RPR_REGISTRATION_PROCESSOR_PREFIX
			+ RPR_PACKET_RECEIVER_MODULE;
	public static final String RPR_PKR_FILE_SIZE_EXCEEDED = RPR_PACKET_RECEIVER_ERROR_CODE + ERROR_CODE1;
	public static final String RPR_PKR_ENROLMENT_STATUS_TABLE_NOT_ACCESSIBLE = RPR_PACKET_RECEIVER_ERROR_CODE
			+ ERROR_CODE2;
	public static final String RPR_PKR_DUPLICATE_UPLOAD = RPR_PACKET_RECEIVER_ERROR_CODE + ERROR_CODE3;
	public static final String RPR_PKR_INVALID_PACKET = RPR_PACKET_RECEIVER_ERROR_CODE + ERROR_CODE4;
	public static final String RPR_PKR_PACKET_NOT_AVAILABLE = RPR_PACKET_RECEIVER_ERROR_CODE + ERROR_CODE5;
	public static final String RPR_PKR_VALIDATION_ERROR = RPR_PACKET_RECEIVER_ERROR_CODE + ERROR_CODE6;
	public static final String RPR_PKR_PACKET_NOT_SYNC = RPR_PACKET_RECEIVER_ERROR_CODE + ERROR_CODE7;
	public static final String RPR_PKR_TIMEOUT = RPR_PACKET_RECEIVER_ERROR_CODE + ERROR_CODE8;
	public static final String RPR_PKR_UNEXCEPTED_ERROR = RPR_PACKET_RECEIVER_ERROR_CODE + ERROR_CODE9;

	// Registration Status exception error code
	private static final String RPR_REGISTRATION_STATUS_ERROR_CODE = RPR_REGISTRATION_PROCESSOR_PREFIX
			+ RPR_REGISTRATION_STATUS_MODULE;
	public static final String RPR_RGS_REGISTRATION_STATUS_TABLE_NOT_ACCESSIBLE = RPR_REGISTRATION_STATUS_ERROR_CODE
			+ ERROR_CODE1;
	public static final String RPR_RGS_TRANSACTION_TABLE_NOT_ACCESSIBLE = RPR_REGISTRATION_STATUS_ERROR_CODE
			+ ERROR_CODE2;

	// Packet Info Storage exception error code
	private static final String RPR_PACKET_INFO_STORAGE_ERROR_CODE = RPR_REGISTRATION_PROCESSOR_PREFIX
			+ RPR_PACKET_INFO_STORAGE_MODULE;
	public static final String RPR_PIS_TABLE_NOT_ACCESSIBLE = RPR_PACKET_INFO_STORAGE_ERROR_CODE + ERROR_CODE1;

	// File System Adapter Ceph exception error code
	private static final String RPR_FILESYSTEM_ADAPTOR_CEPH_ERROR_CODE = RPR_REGISTRATION_PROCESSOR_PREFIX
			+ RPR_FILESYSTEM_ADAPTOR_CEPH_MODULE;
	public static final String RPR_FAC_CONNECTION_NOT_AVAILABLE = RPR_FILESYSTEM_ADAPTOR_CEPH_ERROR_CODE + ERROR_CODE1;
	public static final String RPR_FAC_INVALID_CONNECTION_PARAMETERS = RPR_FILESYSTEM_ADAPTOR_CEPH_ERROR_CODE
			+ ERROR_CODE2;
	public static final String RPR_FAC_PACKET_NOT_AVAILABLE = RPR_FILESYSTEM_ADAPTOR_CEPH_ERROR_CODE + ERROR_CODE3;
	public static final String RPR_FAC_TIMEOUT = RPR_FILESYSTEM_ADAPTOR_CEPH_ERROR_CODE + ERROR_CODE4;
	public static final String RPR_FAC_UNEXCEPTED_ERROR = RPR_FILESYSTEM_ADAPTOR_CEPH_ERROR_CODE + ERROR_CODE5;

	// Packet Manager exception error code
	private static final String RPR_PACKET_MANAGER_ERROR_CODE = RPR_REGISTRATION_PROCESSOR_PREFIX
			+ RPR_PACKET_MANAGER_MODULE;
	public static final String RPR_PKM_CORE_KERNEL_NOT_RESPONDING = RPR_PACKET_MANAGER_ERROR_CODE + ERROR_CODE1;
	public static final String RPR_PKM_FILE_PATH_NOT_ACCESSIBLE = RPR_PACKET_MANAGER_ERROR_CODE + ERROR_CODE2;
	public static final String RPR_PKM_FILE_NOT_FOUND_IN_DESTINATION = RPR_PACKET_MANAGER_ERROR_CODE + ERROR_CODE3;
	public static final String RPR_PKM_FILE_NOT_FOUND_IN_SOURCE = RPR_PACKET_MANAGER_ERROR_CODE + ERROR_CODE4;
	public static final String RPR_PKM_UNEXCEPTED_ERROR = RPR_PACKET_MANAGER_ERROR_CODE + ERROR_CODE5;
	public static final String RPR_PKM_BAD_GATEWAY = RPR_PACKET_MANAGER_ERROR_CODE + ERROR_CODE6;
	public static final String RPR_PKM_SERVICE_UNAVAILABLE = RPR_PACKET_MANAGER_ERROR_CODE + ERROR_CODE7;
	public static final String RPR_PKM_SERVER_ERROR = RPR_PACKET_MANAGER_ERROR_CODE + ERROR_CODE8;
	public static final String RPR_PKM_TIMEOUT = RPR_PACKET_MANAGER_ERROR_CODE + ERROR_CODE9;

	// Registration processor camel bridge exception error code
	private static final String RPR_CAMEL_BRIDGE_ERROR_CODE = RPR_REGISTRATION_PROCESSOR_PREFIX
			+ RPR_CAMEL_BRIDGE_MODULE;
	public static final String RPR_CMB_DEPLOYMENT_FAILURE = RPR_CAMEL_BRIDGE_ERROR_CODE + ERROR_CODE1;
	public static final String RPR_CMB_UNSUPPORTED_ENCODING = RPR_CAMEL_BRIDGE_ERROR_CODE + ERROR_CODE2;
	public static final String RPR_CMB_CONFIGURATION_SERVER_FAILURE_EXCEPTION = RPR_CAMEL_BRIDGE_ERROR_CODE
			+ ERROR_CODE3;
	public static final String RPR_CMB_UNKNOWN_EXCEPTION = RPR_CAMEL_BRIDGE_ERROR_CODE + ERROR_CODE4;

	// Quality Check exception error code
	private static final String RPR_QUALITY_CHECKER_ERROR_CODE = RPR_REGISTRATION_PROCESSOR_PREFIX
			+ RPR_QUALITY_CHECKER_MODULE;
	public static final String RPR_QCR_RESULT_NOT_FOUND = RPR_QUALITY_CHECKER_ERROR_CODE + ERROR_CODE1;
	public static final String RPR_QCR_INVALID_QC_USER_ID = RPR_QUALITY_CHECKER_ERROR_CODE + ERROR_CODE2;
	public static final String RPR_QCR_INVALID_REGISTRATION_ID = RPR_QUALITY_CHECKER_ERROR_CODE + ERROR_CODE3;

	// Packet scanner job exception error code
	private static final String RPR_PACKET_SCANNER_JOB_ERROR_CODE = RPR_REGISTRATION_PROCESSOR_PREFIX
			+ RPR_PACKET_SCANNER_JOB_MODULE;
	public static final String RPR_PSJ_CORE_KERNEL_NOT_RESPONDING = RPR_PACKET_SCANNER_JOB_ERROR_CODE + ERROR_CODE1;
	public static final String RPR_PSJ_DFS_NOT_ACCESSIBLE = RPR_PACKET_SCANNER_JOB_ERROR_CODE + ERROR_CODE2;
	public static final String RPR_PSJ_RETRY_FOLDER_NOT_ACCESSIBLE = RPR_PACKET_SCANNER_JOB_ERROR_CODE + ERROR_CODE3;
	public static final String RPR_PSJ_VIRUS_SCAN_FOLDER_NOT_ACCESSIBLE = RPR_PACKET_SCANNER_JOB_ERROR_CODE
			+ ERROR_CODE4;
	public static final String RPR_PSJ_SPACE_UNAVAILABLE_FOR_RETRY_FOLDER = RPR_PACKET_SCANNER_JOB_ERROR_CODE
			+ ERROR_CODE5;
	public static final String RPR_PSJ_VIRUS_SCAN_FAILED = RPR_PACKET_SCANNER_JOB_ERROR_CODE + ERROR_CODE6;
	public static final String RPR_PSJ_FTP_FOLDER_NOT_ACCESSIBLE = RPR_PACKET_SCANNER_JOB_ERROR_CODE + ERROR_CODE7;

	// Packet decryption job exception error code
	private static final String RPR_PACKET_DECRYPTION_JOB_ERROR_CODE = RPR_REGISTRATION_PROCESSOR_PREFIX
			+ RPR_PACKET_DECRYPTION_JOB_MODULE;
	public static final String RPR_PDJ_PACKET_NOT_FOUND = RPR_PACKET_DECRYPTION_JOB_ERROR_CODE + ERROR_CODE1;
	public static final String RPR_PDJ_FILE_PATH_NOT_ACCESSIBLE = RPR_PACKET_DECRYPTION_JOB_ERROR_CODE + ERROR_CODE2;
	public static final String RPR_PDJ_PACKET_DECRYPTION_FAILURE_ERROR_CODE = RPR_PACKET_DECRYPTION_JOB_ERROR_CODE + ERROR_CODE3;

	/**
	 * Instantiates a new RPR platform error codes.
	 */
	private PlatformErrorCodes() {
		throw new IllegalStateException("Utility class");
	}

}
