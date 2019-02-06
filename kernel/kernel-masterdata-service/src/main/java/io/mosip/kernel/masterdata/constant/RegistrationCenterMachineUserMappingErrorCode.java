package io.mosip.kernel.masterdata.constant;

/**
 * Constants for Registration Center
 * 
 * @author Dharmesh Khandelwal
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
public enum RegistrationCenterMachineUserMappingErrorCode {

	REGISTRATION_CENTER_USER_MACHINE_MAPPING_INSERT_EXCEPTION("KER-MSD-078",
			"Error occurred while inserting mapping of Center, User and Machine details"), REGISTRATION_CENTER_USER_MACHINE_NOT_FOUND(
					"KER-MSD-131",
					"Registration Center, Machine and User Mapping not found"), REGISTRATION_CENTER_USER_MACHINE_DELETE_EXCEPTION(
							"KER-MSD-108",
							"Error occurred while deleting mapping of Center, User and Machine details"), REGISTRATION_CENTER_USER_MACHINE_UPDATE_EXCEPTION(
									"KER-MSD-136",
									"Error occurred while updating mapping of Center, User and Machine details");

	/**
	 * The error code
	 */
	private final String errorCode;
	/**
	 * The error message
	 */
	private final String errorMessage;

	/**
	 * Constructor to set error code and message
	 * 
	 * @param errorCode
	 *            the error code
	 * @param errorMessage
	 *            the error message
	 */
	private RegistrationCenterMachineUserMappingErrorCode(final String errorCode, final String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	/**
	 * Function to get error code
	 * 
	 * @return {@link #errorCode}
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * Function to get the error message
	 * 
	 * @return {@link #errorMessage}r
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

}
