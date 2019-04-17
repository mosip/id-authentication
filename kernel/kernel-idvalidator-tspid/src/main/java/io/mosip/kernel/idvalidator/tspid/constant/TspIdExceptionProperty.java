package io.mosip.kernel.idvalidator.tspid.constant;

/**
 * This enum contains all exception properties that are required to validate
 * TSPID.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
public enum TspIdExceptionProperty {
	INVALID_TSPID_LENGTH("KER-IDV-401", "Tspid Length Must Be "), 
	INVALID_TSPID("KER-IDV-402", "Tspid cannot be null or empty");

	/**
	 * the errorCode.
	 */
	private String errorCode;
	/**
	 * the errorMessage.
	 */
	private String errorMessage;

	/**
	 * Constructor of RidExceptionProperty.
	 * 
	 * @param errorCode
	 *            the errorCode.
	 * @param errorMessage
	 *            the errorMessage.
	 */
	TspIdExceptionProperty(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	/**
	 * Getter for errorCode
	 * 
	 * @return the errorCode.
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * Getter for errorMessage
	 * 
	 * @return the errorMessage.
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
}
