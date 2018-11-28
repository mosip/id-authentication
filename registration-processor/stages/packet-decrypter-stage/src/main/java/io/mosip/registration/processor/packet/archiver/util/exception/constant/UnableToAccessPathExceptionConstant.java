package io.mosip.registration.processor.packet.archiver.util.exception.constant;

/**
 * The Enum UnableToAccessPathExceptionConstant.
 * 
 * @author M1039285
 */
public enum UnableToAccessPathExceptionConstant {

	/** The unable to access path error code. */
	UNABLE_TO_ACCESS_PATH_ERROR_CODE("RER-ARC-002", "The file path is not accessible");

	/** The error code. */
	public final String errorCode;

	/** The error message. */
	public final String errorMessage;

	/**
	 * Instantiates a new unable to access path exception constant.
	 *
	 * @param string1
	 *            the string 1
	 * @param string2
	 *            the string 2
	 */
	UnableToAccessPathExceptionConstant(String string1, String string2) {
		this.errorCode = string1;
		this.errorMessage = string2;
	}

	/**
	 * Gets the error code.
	 *
	 * @return the error code
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * Gets the error message.
	 *
	 * @return the error message
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
}
