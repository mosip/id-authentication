package io.mosip.registration.processor.core.exception.util;

/**
 * The Enum RPRPlatformErrorMessages.
 *
 * @author M1047487
 */
public final class RPRPlatformErrorCodes {
	
	/**
	 * Instantiates a new RPR platform error codes.
	 */
	//**************************PacketReciever***********************************// 
	//Generic Excpetion 
	private static final String RPR_REGISTRATION_PROCESSOR_PREFIX = "RPR-";
	private static final String RPR_PACKET_RECEIVER_MODULE = "PKR-";
	private static final String RPR_REGISTRATION_STATUS_MODULE = "RGS-";
	private static final String RPR_PACKET_INFO_STORAGE_MODULE = "PIS-";
	private static final String RPR_FILESYSTEM_ADAPTOR_CEPH_MODULE = "FAC-";
	private static final String RPR_PACKET_MANAGER_MODULE = "PKM-";
	private static final String RPR_CAMEL_BRIDGE_MODULE = "CMB-";

	private static final String ERROR_CODE1 = "001";
	private static final String ERROR_CODE2 = "002";
	private static final String ERROR_CODE3 = "003";
	private static final String ERROR_CODE4 = "004";
	private static final String ERROR_CODE5 = "005";
	private static final String ERROR_CODE6 = "006";
	private static final String ERROR_CODE7 = "007";
	
	//System Exception
	private static final String ERROR_CODE8 = "008";
	private static final String ERROR_CODE9 = "009";
	
	//**************************Registration Status***********************************// 
	private static final String ERROR_CODE10 = "001";
	private static final String ERROR_CODE11 = "002";
	
	
	//**************************Packet Info Storage Service***********************************// 
	private static final String ERROR_CODE12 = "001";
	
	//**************************File System Adapter Ceph***********************************// 
	private static final String ERROR_CODE13 = "001";
	private static final String ERROR_CODE14 = "002";
	private static final String ERROR_CODE15 = "003";
	private static final String ERROR_CODE16 = "004";
	private static final String ERROR_CODE17 = "005";
	
	//**************************Packet Manager***********************************// 
	private static final String ERROR_CODE18 = "001";
	private static final String ERROR_CODE19 = "002";
	private static final String ERROR_CODE20 = "003";
	private static final String ERROR_CODE21 = "004";
	//system exception codes
	private static final String ERROR_CODE22 = "005";
	private static final String ERROR_CODE23 = "006";
	private static final String ERROR_CODE24 = "007";
	private static final String ERROR_CODE25 = "008";
	private static final String ERROR_CODE26 = "010";
	
	//**************************Registration status camel bridge***********************************// 
	private static final String ERROR_CODE27 = "001";
	private static final String ERROR_CODE28 = "002";
	private static final String ERROR_CODE29 = "003";
	private static final String ERROR_CODE30 = "004";
	
	

	//**************************PacketReciever***********************************// 
	//Generic Exception
	private static final String RPR_PACKET_RECEIVER_ERROR_CODE = RPR_REGISTRATION_PROCESSOR_PREFIX
			+ RPR_PACKET_RECEIVER_MODULE;
	public static final String RPR_PKR_FILE_SIZE_EXCEEDED = RPR_PACKET_RECEIVER_ERROR_CODE + ERROR_CODE1;
	public static final String RPR_PKR_ENROLMENT_STATUS_TABLE_NOT_ACCESSIBLE = RPR_PACKET_RECEIVER_ERROR_CODE + ERROR_CODE2;
	public static final String RPR_PKR_DUPLICATE_UPLOAD = RPR_PACKET_RECEIVER_ERROR_CODE + ERROR_CODE3;
	public static final String RPR_PKR_INVALID_PACKET = RPR_PACKET_RECEIVER_ERROR_CODE + ERROR_CODE4;
	public static final String RPR_PKR_PACKET_NOT_AVAILABLE = RPR_PACKET_RECEIVER_ERROR_CODE + ERROR_CODE5;
	public static final String RPR_PKR_VALIDATION_ERROR = RPR_PACKET_RECEIVER_ERROR_CODE + ERROR_CODE6;
	public static final String RPR_PKR_PACKET_NOT_SYNC = RPR_PACKET_RECEIVER_ERROR_CODE + ERROR_CODE7;
	
	//System Exception
	public static final String RPR_PKR_TIMEOUT = RPR_PACKET_RECEIVER_ERROR_CODE + ERROR_CODE8;
	public static final String RPR_PKR_UNEXCEPTED_ERROR = RPR_PACKET_RECEIVER_ERROR_CODE + ERROR_CODE9;
	
	//**************************Registration Status***********************************//
	private static final String RPR_REGISTRATION_STATUS_ERROR_CODE = RPR_REGISTRATION_PROCESSOR_PREFIX
			+ RPR_REGISTRATION_STATUS_MODULE;
	
	public static final String RPR_RGS_REGISTRATION_STATUS_TABLE_NOT_ACCESSIBLE = RPR_REGISTRATION_STATUS_ERROR_CODE
			+ ERROR_CODE10;
	public static final String RPR_RGS_TRANSACTION_TABLE_NOT_ACCESSIBLE = RPR_REGISTRATION_STATUS_ERROR_CODE
			+ ERROR_CODE11;
	
	//**************************Packet Info Storage Service***********************************// 
	private static final String RPR_PACKET_INFO_STORAGE_ERROR_CODE = RPR_REGISTRATION_PROCESSOR_PREFIX
			+ RPR_PACKET_INFO_STORAGE_MODULE;
	
	public static final String RPR_PIS_TABLE_NOT_ACCESSIBLE = RPR_PACKET_INFO_STORAGE_ERROR_CODE
			+ ERROR_CODE12;
	
	//**************************File System Adapter Ceph***********************************//
	private static final String RPR_FILESYSTEM_ADAPTOR_CEPH_ERROR_CODE = RPR_REGISTRATION_PROCESSOR_PREFIX
			+ RPR_FILESYSTEM_ADAPTOR_CEPH_MODULE;
	
	public static final String RPR_FAC_CONNECTION_NOT_AVAILABLE = RPR_FILESYSTEM_ADAPTOR_CEPH_ERROR_CODE
			+ ERROR_CODE13;
	public static final String RPR_FAC_INVALID_CONNECTION_PARAMETERS = RPR_FILESYSTEM_ADAPTOR_CEPH_ERROR_CODE
	 + ERROR_CODE14;
	
	public static final String RPR_FAC_PACKET_NOT_AVAILABLE = RPR_FILESYSTEM_ADAPTOR_CEPH_ERROR_CODE
			 + ERROR_CODE15;
	
	//System Exception
	public static final String RPR_FAC_TIMEOUT = RPR_FILESYSTEM_ADAPTOR_CEPH_ERROR_CODE + ERROR_CODE16;
	public static final String RPR_FAC_UNEXCEPTED_ERROR = RPR_FILESYSTEM_ADAPTOR_CEPH_ERROR_CODE + ERROR_CODE17;
	
	//**************************Packet Manager***********************************//
	private static final String RPR_PACKET_MANAGER_ERROR_CODE = RPR_REGISTRATION_PROCESSOR_PREFIX
			+ RPR_PACKET_MANAGER_MODULE;
	
	public static final String RPR_PKM_CORE_KERNEL_NOT_RESPONDING = RPR_PACKET_MANAGER_ERROR_CODE + ERROR_CODE18;
	public static final String RPR_PKM_FILE_PATH_NOT_ACCESSIBLE = RPR_PACKET_MANAGER_ERROR_CODE + ERROR_CODE19;
	public static final String RPR_PKM_FILE_NOT_FOUND_IN_DESTINATION = RPR_PACKET_MANAGER_ERROR_CODE + ERROR_CODE20;
	public static final String RPR_PKM_FILE_NOT_FOUND_IN_SOURCE = RPR_PACKET_MANAGER_ERROR_CODE + ERROR_CODE21;
	
	//System Exceptions
	public static final String RPR_PKM_UNEXCEPTED_ERROR = RPR_PACKET_MANAGER_ERROR_CODE + ERROR_CODE22;
	public static final String RPR_PKM_BAD_GATEWAY = RPR_PACKET_MANAGER_ERROR_CODE + ERROR_CODE23;
	public static final String RPR_PKM_SERVICE_UNAVAILABLE  = RPR_PACKET_MANAGER_ERROR_CODE + ERROR_CODE24;
	public static final String RPR_PKM_SERVER_ERROR = RPR_PACKET_MANAGER_ERROR_CODE + ERROR_CODE25;
	public static final String RPR_PKM_TIMEOUT  = RPR_PACKET_MANAGER_ERROR_CODE + ERROR_CODE26;

	//**************************Registration processor camel bridge***********************************//
	private static final String RPR_CAMEL_BRIDGE_ERROR_CODE = RPR_REGISTRATION_PROCESSOR_PREFIX
			+ RPR_CAMEL_BRIDGE_MODULE;
	
	public static final String RPR_CMB_DEPLOYMENT_FAILURE = RPR_CAMEL_BRIDGE_ERROR_CODE + ERROR_CODE27;
	public static final String RPR_CMB_UNSUPPORTED_ENCODING = RPR_CAMEL_BRIDGE_ERROR_CODE + ERROR_CODE28;
	public static final String RPR_CMB_CONFIGURATION_SERVER_FAILURE_EXCEPTION = RPR_CAMEL_BRIDGE_ERROR_CODE
			+ ERROR_CODE29;
	public static final String RPR_CMB_UNKNOWN_EXCEPTION = RPR_CAMEL_BRIDGE_ERROR_CODE + ERROR_CODE30;
		
	
	private RPRPlatformErrorCodes() {
		throw new IllegalStateException("Utility class");
	}
	
	

}
