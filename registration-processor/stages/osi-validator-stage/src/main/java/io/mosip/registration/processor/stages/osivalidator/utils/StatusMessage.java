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
	public static final String FINGER_PRINT = "Fingerprint validation failed";

	/** The Constant IRIS. */
	public static final String IRIS = "Iris validation failed";

	/** The Constant FACE. */
	public static final String FACE = "Face validation failed";

	/** The Constant PIN. */
	public static final String PIN = "Pin validation failed";

	/** The Constant VALIDATION_DETAILS. */
	public static final String VALIDATION_DETAILS = "All the validation details are null";

	/** The Constant OSI_VALIDATION_SUCCESS. */
	public static final String OSI_VALIDATION_SUCCESS = "OSI Validation is successful";

	/** The Constant PARENT_UIN_AND_RID_NOT_IN_PACKET. */
	public static final String PARENT_UIN_AND_RID_NOT_IN_PACKET = "The UIN and RID of Parent is not present in Packet";

	/** The Constant PARENT_RID_NOT_IN_REGISTRATION_TABLE. */
	public static final String PARENT_RID_NOT_IN_REGISTRATION_TABLE = "The RID of Parent is not present in Packet";

	/** The Constant PACKET_IS_ON_HOLD. */
	public static final String PACKET_IS_ON_HOLD = "Packet is on hold as Parent UIN is not yet generated";

	/** The Constant MACHINE_ID_NOT_FOUND. */
	public static final String MACHINE_ID_NOT_FOUND = "The Machine ID was not found in Master DB for Registration ID";

	/** The Constant MACHINE_NOT_ACTIVE. */
	public static final String MACHINE_NOT_ACTIVE = "The Machine ID was not active when Registration ID  was created";

	/** The Constant CENTER_ID_NOT_FOUND. */
	public static final String CENTER_ID_NOT_FOUND = "The Center ID was not found in Master DB for Registration ID";

	/** The Constant CENTER_NOT_ACTIVE. */
	public static final String CENTER_NOT_ACTIVE = "The Center was not active when Registration ID was created";

	/** The Constant OFFICER_ID_NOT_FOUND. */
	public static final String OFFICER_ID_NOT_FOUND = "The Officer was not found in Master DB for Registration ID ";

	/** The Constant OFFICER_NOT_ACTIVE. */
	public static final String OFFICER_NOT_ACTIVE = "The Officer was not active when Registration ID was created";

	/** The Constant SUPERVISOR_ID_NOT_FOUND. */
	public static final String SUPERVISOR_ID_NOT_FOUND = "The Supervisor was not found in Master DB for Registration ID ";

	/** The Constant SUPERVISOR_NOT_ACTIVE. */
	public static final String SUPERVISOR_NOT_ACTIVE = "The Supervisor was not active when Registration ID was created";

	/** The Constant CENTER_MACHINE_USER_MAPPING_NOT_FOUND. */
	public static final String CENTER_MACHINE_USER_MAPPING_NOT_FOUND = "The Center-Machine-User Mapping for Center, Machine & supervisor/officer was not found";

	/** The Constant GPS_DATA_NOT_PRESENT. */
	public static final String GPS_DATA_NOT_PRESENT = "The GPS Details for the Packet is Not Present";

	/** The Constant PARENT_UIN_NOT_FOUND_IN_TABLE. */
	public static final String PARENT_UIN_NOT_FOUND_IN_TABLE = "The UIN of Parent is not present in individual_demographic_dedup for Packet";

	/** The Constant DEVICE_NOT_FOUND. */
	public static final String DEVICE_NOT_FOUND = "was not available for Registration ID";

	/** The Constant DEVICE_WAS_IN_ACTIVE. */
	public static final String DEVICE_WAS_IN_ACTIVE = "was inactive for a Packet with Registration ID";

	/** The Constant DEVICE_ID. */
	public static final String DEVICE_ID = "The Device ID";

	/** The Constant CENTER_ID. */
	public static final String CENTER_ID = "and Center ID ";



}
