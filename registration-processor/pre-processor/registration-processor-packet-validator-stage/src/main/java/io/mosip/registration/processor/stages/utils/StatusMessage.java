package io.mosip.registration.processor.stages.utils;

/**
 * The Class StatusMessage.
 */
public final class StatusMessage {

	/**
	 * Instantiates a new status message.
	 */
	private StatusMessage() {

	}

	/** The Constant PACKET_CHECKSUM_VALIDATION_FAILURE. */
	public static final String PACKET_CHECKSUM_VALIDATION_FAILURE = "Packet checksum validation failure";

	/** The Constant PACKET_FILES_VALIDATION_FAILURE. */
	public static final String PACKET_FILES_VALIDATION_FAILURE = "Packet file validation failure";

	/** The Constant PACKET_STRUCTURAL_VALIDATION_SUCCESS. */
	public static final String PACKET_STRUCTURAL_VALIDATION_SUCCESS = "Packet structural validation is successful";

	/** The Constant INPUTSTREAM_NOT_READABLE. */
	public static final String INPUTSTREAM_NOT_READABLE = "Unable to read inputstream";

	public static final String MASTERDATA_VALIDATION_FAILED = "Master data validation failed";

	public static final String MASTERDATA_VALIDATION_FAILURE_INVALID_ATTRIBUTES = "Master data validation failed since the attribute is not valid for ";

	public static final String MASTERDATA_VALIDATION_API_ACCESS_FAILURE = "Master data validation failed due to API resource access exception";

	public static final String MASTERDATA_VALIDATION_FAILED_RESOURCE_NOT_FOUND = "Master data validation failed due to API resource access exception";

	public static final String MANDATORY_FIELD_MISSING = "Mandatory Field is missing from ID JSON for NEW registration";

}
