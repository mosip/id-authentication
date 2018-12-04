package io.mosip.registration.processor.stages.osivalidator.utils;

/**
 * The Class StatusMessage.
 */
public class StatusMessage {

	/**
	 * Instantiates a new status message.
	 */
	private StatusMessage() {

	}

	/** The Constant OPERATOR. */
	public static final String OPERATOR = "OPERATOR";

	/** The Constant SUPERVISOR. */
	public static final String SUPERVISOR = "SUPERVISOR";

	/** The Constant INTRODUCER. */
	public static final String INTRODUCER = "INTRODUCER";

	/** The Constant FINGER_PRINT. */
	public static final String FINGER_PRINT = "FINGER_PRINT_VALIDATION_FAILED";

	/** The Constant IRIS. */
	public static final String IRIS = "IRIS_VALIDATION_FAILED";

	/** The Constant FACE. */
	public static final String FACE = "FACE_VALIDATION_FAILED";

	/** The Constant PIN. */
	public static final String PIN = "PIN_VALIDATION_FAILED";

	/** The Constant VALIDATION_DETAILS. */
	public static final String VALIDATION_DETAILS = "ALL_THE_VALIDATION_DETAILS_ARE_NULL";

	/** The Constant OSI_VALIDATION_SUCCESS. */
	public static final String OSI_VALIDATION_SUCCESS = "OSI_VALIDATION_SUCCESS";

	public static final String PARENT_UIN_AND_RID_NOT_IN_PACKET = "The UIN and RID of Parent is not present in Packet";

	public static final String PARENT_RID_NOT_IN_REGISTRATION_TABLE = "The RID of Parent is not present in Packet";

	public static final String PACKET_IS_ON_HOLD = "Packet is on hold as Parent UIN is not yet generated";
	
	public static final String MACHINE_ID_NOT_FOUND="The Machine ID was not found in Master DB for Registration ID.";
	
	public static final String MACHINE_NOT_ACTIVE= "The Machine ID was not active when Registration ID  was created";
	
	public static final String CENTER_ID_NOT_FOUND= "The Center ID was not found in Master DB for Registration ID";
	
	public static final String CENTER_NOT_ACTIVE= "The Center was not active when Registration ID was created";
	
	public static final String OFFICER_ID_NOT_FOUND="The Officer was not found in Master DB for Registration ID ";
	
	public static final String OFFICER_NOT_ACTIVE="The Officer was not active when Registration ID was created";
	
	public static final String SUPERVISOR_ID_NOT_FOUND="The Supervisor was not found in Master DB for Registration ID ";
	
	public static final String SUPERVISOR_NOT_ACTIVE="The Supervisor was not active when Registration ID was created";
	
	public static final String CENTER_MACHINE_USER_MAPPING_NOT_FOUND="The Center-Machine-User Mapping for Center, Machine & supervisor/officer was not found";
	
	public static final String GPS_DATA_NOT_PRESENT= "The GPS Details for the Packet is Not Present";
	
	public static final String PARENT_UIN_NOT_FOUND_IN_TABLE= "The UIN of Parent is not present in individual_demographic_dedup for Packet";

}
