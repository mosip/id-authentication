package io.mosip.registration.constants;

/**
 * Class for Registration Processor exception Codes
 *
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public class RegProcessorExceptionCode {

	/**
	 * The constructor
	 */
	private RegProcessorExceptionCode() {

	}

	private static final String REG_SERVICE_CODE = "REG-SER-";

	public static final String REG_FRAMEWORK_PACKET_HANDLING_EXCEPTION = REG_SERVICE_CODE + "PHA-201";
	public static final String PACKET_CREATION_EXCEPTION = REG_SERVICE_CODE + "PHA-202";
	public static final String PACKET_ZIP_CREATION = REG_SERVICE_CODE + "ZCM-203";
	public static final String ENROLLMENT_ZIP_CREATION = REG_SERVICE_CODE + "ZCM-204";
	public static final String PACKET_ENCRYPTION_MANAGER = REG_SERVICE_CODE + "PEM-205";
	public static final String AES_ENCRYPTION_MANAGER = REG_SERVICE_CODE + "AEM-206";
	public static final String AES_SEED_GENERATION = REG_SERVICE_CODE + "ASG-207";
	public static final String AES_KEY_MANAGER = REG_SERVICE_CODE + "EKM-208";
	public static final String AES_ENCRYPTION = REG_SERVICE_CODE + "AEI-209";
	public static final String CONCAT_ENCRYPTED_DATA = REG_SERVICE_CODE + "AEI-210";
	public static final String ENCRYPTED_PACKET_STORAGE = REG_SERVICE_CODE + "STM-211";
	public static final String PACKET_INSERTION = REG_SERVICE_CODE + "IPD-212";
	public static final String CREATE_PACKET_ENTITY = REG_SERVICE_CODE + "IPD-213";
	public static final String LOGIN_SERVICE = REG_SERVICE_CODE + "IPD-214";
	public static final String SERVICE_DELEGATE_UTIL = REG_SERVICE_CODE + "IPD-215";
	public static final String SERVICE_DATA_PROVIDER_UTIL = REG_SERVICE_CODE + "DPU-216";
	public static final String RSA_ENCRYPTION_MANAGER = REG_SERVICE_CODE + "REM-219";
	public static final String UPDATE_SYNC_AUDIT = REG_SERVICE_CODE + "ADI-220";
	public static final String FETCH_UNSYNC_AUDIT = REG_SERVICE_CODE + "ADI-221";
	public static final String READ_PROPERTY_FILE_ERROR = REG_SERVICE_CODE + "PFR-222";
	public static final String PACKET_UPDATE_STATUS=REG_SERVICE_CODE+"UPS-217";
	public static final String PACKET_RETRIVE_STATUS=REG_SERVICE_CODE+"RPS-218";
	public static final String MACHINE_MAPPING_RUN_TIME_EXCEPTION=REG_SERVICE_CODE+"RDI-219";
	public static final String MACHINE_MAPPING_STATIONID_RUN_TIME_EXCEPTION=REG_SERVICE_CODE+"UMM-220";
	public static final String MACHINE_MAPPING_CENTERID_RUN_TIME_EXCEPTION=REG_SERVICE_CODE+"UMM-221";
	public static final String MACHINE_MAPPING_USERLIST_RUN_TIME_EXCEPTION=REG_SERVICE_CODE+"UMM-222";	
	public static final String SYNC_STATUS_VALIDATE=REG_SERVICE_CODE+"SSV-223";
	public static final String MACHINE_MASTER_RECORD_NOT_FOUND=REG_SERVICE_CODE+"MMD-224";
}
