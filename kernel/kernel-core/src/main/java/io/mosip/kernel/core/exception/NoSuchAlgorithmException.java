package io.mosip.kernel.core.exception;

/**
 * Base class for all preconditions violation exceptions.
 * 
 * @author Omsaieswar Mulakaluri
 * @since 1.0.0
 */
public class NoSuchAlgorithmException extends BaseUncheckedException {

	/** Serializable version Id. */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor without cause
	 * 
	 * @param errorCode
	 * @param errorMessage
	 */
	public NoSuchAlgorithmException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);

	}

	/**
	 * Constructor with cause
	 * 
	 * @param errorCode
	 * @param errorMessage
	 * @param cause
	 */
	public NoSuchAlgorithmException(String errorCode, String errorMessage, Throwable cause) {
		super(errorCode, errorMessage, cause);

	}

}
