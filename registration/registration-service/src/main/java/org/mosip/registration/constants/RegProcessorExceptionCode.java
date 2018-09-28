package org.mosip.registration.constants;

public class RegProcessorExceptionCode {

	private static String IDIS_FRAMEWORK_CODE = "IDC-FRA-";
	
	public static String IDIS_FRAMEWORK_PACKET_HANDLING_EXCEPTION = IDIS_FRAMEWORK_CODE + "PHA-201";
	public static String PACKET_CREATION_EXCEPTION = IDIS_FRAMEWORK_CODE + "PHA-202";
	public static String PACKET_ZIP_CREATION = IDIS_FRAMEWORK_CODE + "ZCM-203";
	public static String ENROLLMENT_ZIP_CREATION = IDIS_FRAMEWORK_CODE + "ZCM-204";
	public static String PACKET_ENCRYPTION_MANAGER = IDIS_FRAMEWORK_CODE + "PEM-205";
	public static String AES_ENCRYPTION_MANAGER = IDIS_FRAMEWORK_CODE + "AEM-206";
	public static String AES_SEED_GENERATION = IDIS_FRAMEWORK_CODE + "ASG-207";
	public static String AES_KEY_MANAGER = IDIS_FRAMEWORK_CODE + "EKM-208";
	public static String AES_ENCRYPTION = IDIS_FRAMEWORK_CODE + "AEI-209";
	public static String CONCAT_ENCRYPTED_DATA = IDIS_FRAMEWORK_CODE + "AEI-210";
	public static String ENCRYPTED_PACKET_STORAGE = IDIS_FRAMEWORK_CODE + "STM-211";
	public static String PACKET_INSERTION = IDIS_FRAMEWORK_CODE + "IPD-212";
	public static String CREATE_PACKET_ENTITY = IDIS_FRAMEWORK_CODE + "IPD-213";
	
}
