package org.mosip.registration.constants;

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

	private static String REG_PROCESSOR_CODE = "REG-PRO-";
	
	private static String IDIS_FRAMEWORK_CODE = "IDC-FRA-";
	
	public static String IDIS_FRAMEWORK_PACKET_HANDLING_EXCEPTION = REG_PROCESSOR_CODE + "PHA-201";
	public static String PACKET_CREATION_EXCEPTION = REG_PROCESSOR_CODE + "PHA-202";
	public static String PACKET_ZIP_CREATION = REG_PROCESSOR_CODE + "ZCM-203";
	public static String ENROLLMENT_ZIP_CREATION = REG_PROCESSOR_CODE + "ZCM-204";
	public static String PACKET_ENCRYPTION_MANAGER = REG_PROCESSOR_CODE + "PEM-205";
	public static String AES_ENCRYPTION_MANAGER = REG_PROCESSOR_CODE + "AEM-206";
	public static String AES_SEED_GENERATION = REG_PROCESSOR_CODE + "ASG-207";
	public static String AES_KEY_MANAGER = REG_PROCESSOR_CODE + "EKM-208";
	public static String AES_ENCRYPTION = REG_PROCESSOR_CODE + "AEI-209";
	public static String CONCAT_ENCRYPTED_DATA = REG_PROCESSOR_CODE + "AEI-210";
	public static String ENCRYPTED_PACKET_STORAGE = REG_PROCESSOR_CODE + "STM-211";
	public static String PACKET_INSERTION = REG_PROCESSOR_CODE + "IPD-212";
	public static String CREATE_PACKET_ENTITY = REG_PROCESSOR_CODE + "IPD-213";
	public static String LOGIN_SERVICE=IDIS_FRAMEWORK_CODE+"IPD-214";
	public static String SERVICE_DELEGATE_UTIL=IDIS_FRAMEWORK_CODE+"IPD-215";
	
}
