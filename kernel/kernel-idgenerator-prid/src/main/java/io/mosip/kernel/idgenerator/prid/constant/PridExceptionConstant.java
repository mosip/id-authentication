package io.mosip.kernel.idgenerator.prid.constant;

/**
 * This enum contains all exception properties that are required to validate
 * PRID.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
public enum PridExceptionConstant {
	PRID_FETCH_EXCEPTION("KER-PIG-001", "Error occur while fetching counter and value details"),
	PRID_INSERTION_EXCEPTION("KER-PIG-002", "Error occur while updating counter details");

	/**
	 * The error code.
	 */
	private String errorCode;

	/**
	 * The error message.
	 */
	private String errorMessage;

	/**
	 * Constructor for PridExceptionConstant.
	 * 
	 * @param errorCode    the error code.
	 * @param errorMessage the error message.
	 */
	PridExceptionConstant(String errorCode, String errorMessage) {
		this.errorCode = errorCode;

		this.errorMessage = errorMessage;
	}

	/**
	 * Getter for error code.
	 * 
	 * @return the error code.
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * Getter for error message.
	 * 
	 * @return the error message.
	 */
	public String getErrorMessage() {

		return errorMessage;
	}

}
