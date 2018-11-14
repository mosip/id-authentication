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
	
	
	/** The Constant IIS_EPU_ATU_SYSTEM_MODULE. */
	// System Exceptions
	//private static final String RPR_PKR_SYSTEM_MODULE = RPR_REGISTRATION_PROCESSOR_PREFIX + "SYSTEM_";
	//public static final String RPR_PKR_UNEXCEPTED_ERROR = RPR_PKR_SYSTEM_MODULE + "UNEXCEPTED_ERROR";
	//public static final String RPR_PKR_BAD_GATEWAY = RPR_PKR_SYSTEM_MODULE + "BAD_GATEWAY";
	//public static final String RPR_PKR_SERVICE_UNAVAILABLE = RPR_PKR_SYSTEM_MODULE + "SERVICE_UNAVAILABLE";
	//public static final String RPR_PKR_SERVER_ERROR = RPR_PKR_SYSTEM_MODULE + "SERVER_ERROR";
	//public static final String RPR_PKR_TIMEOUT = RPR_PKR_SYSTEM_MODULE + "TIMEOUT";
	
	
	
	private RPRPlatformErrorCodes() {
		throw new IllegalStateException("Utility class");
	}
	
	

}
