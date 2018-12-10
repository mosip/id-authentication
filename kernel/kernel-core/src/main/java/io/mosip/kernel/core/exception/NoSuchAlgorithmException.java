package io.mosip.kernel.core.exception;

/**
 * Base class for all preconditions violation exceptions.
 * 
 * @author Urvil Joshi
 * @author Omsaieswar Mulakaluri
 * @since 1.0.0
 */
public class NoSuchAlgorithmException extends BaseUncheckedException {

	/**
	 * Unique id for serialization
	 */
	private static final long serialVersionUID = 8768923778001408221L;


	/**
	 * Constructor with errorCode and errorMessage
	 * 
	 * @param errorCode
	 *            The error code for this exception
	 * @param errorMessage
	 *            The error message for this exception
	 */
	public NoSuchAlgorithmException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);

	}


	/**
	 * Constructor with errorCode, errorMessage, and rootCause
	 * 
	 * @param errorCode
	 *            The error code for this exception
	 * @param errorMessage
	 *            The error message for this exception
	 * @param rootCause
	 *            Cause of this exception
	 */
	public NoSuchAlgorithmException(String errorCode, String errorMessage, Throwable cause) {
		super(errorCode, errorMessage, cause);

	}

}
