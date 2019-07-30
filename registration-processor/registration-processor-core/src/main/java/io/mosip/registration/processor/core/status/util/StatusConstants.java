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
    
    //Quality checker stage
    public static final String QUALITY_CHECKER_MODULE = RPR_REGISTRATION_PROCESSOR_PREFIX + "QCK-";
    public static final String QUALITY_CHECKER_MODULE_SUCCESS = QUALITY_CHECKER_MODULE + SUCCESS;
    public static final String QUALITY_CHECKER_MODULE_FAILED = QUALITY_CHECKER_MODULE + FAILED;
    
    //packet validator stage
    public static final String PACKET_VALIDATOR_MODULE = RPR_REGISTRATION_PROCESSOR_PREFIX + "PKV-";
    public static final String PACKET_VALIDATOR_MODULE_SUCCESS = RPR_REGISTRATION_PROCESSOR_PREFIX + SUCCESS;
    public static final String PACKET_VALIDATOR_MODULE_FAILED = RPR_REGISTRATION_PROCESSOR_PREFIX + FAILED;

    //External stage
    public static final String EXTERNAL_SATGE_MODULE = RPR_REGISTRATION_PROCESSOR_PREFIX + "EXS-";
    public static final String EXTERNAL_SATGE_MODULE_SUCCESS = EXTERNAL_SATGE_MODULE + SUCCESS;
    public static final String EXTERNAL_SATGE_MODULE_FAILED = EXTERNAL_SATGE_MODULE + FAILED;
    
    //OSI Validator stage
    public static final String OSI_VALIDAOR_MODULE =  RPR_REGISTRATION_PROCESSOR_PREFIX + "OSI-";
    public static final String OSI_VALIDAOR_MODULE_SUCCESS = OSI_VALIDAOR_MODULE + SUCCESS;
    public static final String OSI_VALIDAOR_MODULE_FAILED = OSI_VALIDAOR_MODULE + FAILED;
    
    //Message sender stage
    public static final String MESSAGE_SENDER_MODULE = RPR_REGISTRATION_PROCESSOR_PREFIX + "MSS-";
    public static final String OMESSAGE_SENDER_MODULE_SUCCESS = OSI_VALIDAOR_MODULE + SUCCESS;
    public static final String OMESSAGE_SENDER_MODULE_FAILED = OSI_VALIDAOR_MODULE + FAILED;
    
    //Printing stage
    public static final String PRINT_STAGE_MODULE = RPR_REGISTRATION_PROCESSOR_PREFIX + "PPS-";
    public static final String PRINT_STAGE_MODULE_SUCCESS = PRINT_STAGE_MODULE + SUCCESS;
    public static final String PRINT_STAGE_MODULE_FAILED = PRINT_STAGE_MODULE + FAILED;

    

    




    

    
    //System Exceptions
    public static final String SYSTEM_EXCEPTION_CODE = RPR_REGISTRATION_PROCESSOR_PREFIX + SYSTEM_EXCEPTION;

    


}
