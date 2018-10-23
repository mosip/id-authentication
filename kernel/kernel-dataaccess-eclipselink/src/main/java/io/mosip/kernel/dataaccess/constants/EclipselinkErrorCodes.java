package io.mosip.kernel.dataaccess.constants;

/**
 * Error code constants for Eclipselink implementation of Dao Manager
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
public enum EclipselinkErrorCodes {
	ERR_DATABASE("KER-DAE-001"), ECLIPSELINK_EXCEPTION("KER-DAE-002"), NO_RESULT_EXCEPTION("KER-DAE-003");

	/**
	 * Field for error code
	 */
	private final String errorCode;

	/**
	 * Function to set errorcode
	 * 
	 * @param errorCode
	 *            The errorcode
	 */
	private EclipselinkErrorCodes(final String errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * Function to get errorcode
	 * 
	 * @return The errorcode
	 */
	public String getErrorCode() {
		return errorCode;
	}

}
