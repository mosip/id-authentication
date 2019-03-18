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

	public static final String GENDER_NAME_NOT_AVAILABLE = "Gender name is not available in Master DB";

	public static final String REGION_NOT_AVAILABLE = "Region is not available in Master DB";

	public static final String PROVINCE_NOT_AVAILABLE = "Province is not available in Master DB";

	public static final String CITY_NOT_AVAILABLE = "City is not available in Master DB";

	public static final String POSTALCODE_NOT_AVAILABLE = "Postal Code is not available in Master DB";

	public static final String MASTERDATA_VALIDATION_FAILED = "Master data validation failed";

}
