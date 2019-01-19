package io.mosip.kernel.keygenerator.bouncycastle.constant;

/**
 * Exception constants for this Application
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public enum KeyGeneratorExceptionConstant {

	/**
	 * {@link #MOSIP_NO_SUCH_ALGORITHM_EXCEPTION} exception constant
	 */
	MOSIP_NO_SUCH_ALGORITHM_EXCEPTION("KER-KEG-010", "no such algorithm is present");

	/**
	 * Constructor for this {@link Enum}
	 */
	private KeyGeneratorExceptionConstant() {
	}

	/**
	 * Constructor for this {@link Enum}
	 * 
	 * @param errorCode    errorCode for exception
	 * @param errorMessage errorMessage for exception
	 */
	KeyGeneratorExceptionConstant(String errorCode, String errorMessage) {
		this.setErrorCode(errorCode);
		this.setErrorMessage(errorMessage);
	}

	/**
	 * Getter for {@link #errorCode}
	 * 
	 * @return {@link #errorCode}
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * Setter for {@link #errorCode}
	 * 
	 * @param errorCode {@link #errorCode}
	 */
	private void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * Getter for {@link #errorMessage}
	 * 
	 * @return {@link #errorMessage}
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * Setter for {@link #errorMessage}
	 * 
	 * @param errorMessage
	 */
	private void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * Error Code for Exception
	 */
	private String errorCode;
	/**
	 * Error Message for Exception
	 */
	private String errorMessage;
}
