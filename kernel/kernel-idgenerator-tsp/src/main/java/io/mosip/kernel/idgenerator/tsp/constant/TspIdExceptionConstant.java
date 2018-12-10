package io.mosip.kernel.idgenerator.tsp.constant;

/**
 * Exception constants for TSPID generator.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
public enum TspIdExceptionConstant {

	TSPID_FETCH_EXCEPTION("KER-TSG-001", "Error Occur While Fetching Id"),

	TSPID_INSERTION_EXCEPTION("KER-TSG-002", "Error Occur While Inserting Id");

	/**
	 * The error code.
	 */
	private String errorCode;

	/**
	 * The error message.
	 */
	private String errorMessage;

	/**
	 * Constructor for TspIdExceptionConstant.
	 * 
	 * @param errorCode
	 *            the errorCode.
	 * @param errorMessage
	 *            the errorMessage.
	 */
	TspIdExceptionConstant(String errorCode, String errorMessage) {
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
