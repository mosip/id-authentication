package io.mosip.registration.processor.biodedupe.constants;

public class BioDedupeConstants {

	/** The Constant FILE_SEPARATOR. */
	public static final String FILE_SEPARATOR = "\\";

	/** The Constant INDIVIDUAL_BIOMETRICS. */
	public static final String INDIVIDUAL_BIOMETRICS = "individualBiometrics";
	
	public static final String VALUE = "value";
	
	public static final String CBEFF_PRESENT_IN_PACKET = "Cbeff is present in the packet, destination stage is abis_handler";
	
	public static final String CBEFF_ABSENT_IN_PACKET = "Cbeff is absent in the packet for child, destination stage is UIN";
	
	public static final String UPDATE_PACKET_BIOMETRIC_NOT_NULL = "Update packet individual biometric not null, destination stage is abis_handler";
	
	public static final String UPDATE_PACKET_BIOMETRIC_NULL = "Update packet individual biometric null, destination stage is UIN";
	
	public static final String ABIS_RESPONSE_NOT_NULL = "ABIS response Details not null, destination stage is Manual_verification";
	
	public static final String ABIS_RESPONSE_NULL = "ABIS response Details null, destination stage is UIN";
	
	public static final String CBEFF_NOT_FOUND = "Cbeff not found for the lost packet";
	
	public static final String APPLICANT_TYPE_CHILD = "Applicant type is child and Cbeff not present returning false";
	
	public static final String APPLICANT_TYPE_ADULT = "Applicant type is adult and Cbeff not present throwing exception";
	
	public static final String LOST_PRE_ABIS_IDENTITIFICATION = "Lost Packet Pre abis identification";
	
	public static final String NO_MATCH_FOUND_FOR_LOST = "No match found, rejecting the lost packet for ";
	
	public static final String FOUND_UIN_IN_BIO_CHECK = "Found a matching UIN in bio check for the lost packet ";
	
	public static final String FOUND_UIN_IN_DEMO_CHECK = "Found a matching UIN in demo check for the lost packet ";
	
	public static final String MULTIPLE_RID_FOUND = "Multiple matched regId found, saving data in manual verification";
	
	/** The Constant INTERNAL_ERROR. */
	private static final String INTERNAL_ERROR = "Internal error occurred in bio-dedupe stage while processing for registrationId ";
}
