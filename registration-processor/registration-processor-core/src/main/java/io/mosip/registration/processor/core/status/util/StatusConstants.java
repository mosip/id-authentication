package io.mosip.registration.processor.core.status.util;
/**
 * 
 * @author Girish Yarru
 *
 */
public final class StatusConstants {
	private static final String RPR_REGISTRATION_PROCESSOR_PREFIX = "RPR-";
	private static final String SUCCESS = "SUCCESS-";
	private static final String FAILED = "FAILED-";
	private static final String SYSTEM_EXCEPTION = "SYS-EXCEPTION-001";
	public static final String SYSTEM_EXCEPTION_MESSAGE = "System Exception Occurred - Unable to Process Packet";
	
	//packet Receiver
	public static final String PACKET_RECEIVER_MODULE = RPR_REGISTRATION_PROCESSOR_PREFIX + "PKR-";
	public static final String PACKET_RECEIVER_MODULE_SUCCESS = PACKET_RECEIVER_MODULE + SUCCESS;
	public static final String PACKET_RECEIVER_MODULE_FAILURE = PACKET_RECEIVER_MODULE + FAILED;
	
	//packet Uploader Stage
	public static final String PACKET_UPLOADER_MODULE = RPR_REGISTRATION_PROCESSOR_PREFIX + "PKU-";
    public static final String PACKET_UPLOADER_MODULE_SUCCESS = PACKET_UPLOADER_MODULE + SUCCESS;
    public static final String PACKET_UPLOADER_MODULE_FAILED = PACKET_UPLOADER_MODULE + FAILED;
    
    //System Exceptions
    public static final String SYSTEM_EXCEPTION_CODE = RPR_REGISTRATION_PROCESSOR_PREFIX + SYSTEM_EXCEPTION;

    


}
