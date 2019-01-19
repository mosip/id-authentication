package io.mosip.kernel.core.exception;

/**
 * Exception to be thrown when a null argument found.
 * 
 * @author Urvil Joshi
 * @author Ritesh Sinha
 * @author Sagar Mahapatra
 * @author Priya Soni
 * @since 1.0.0
 */
public class NullPointerException extends BaseUncheckedException {

	/** Serializable version Id. */
	private static final long serialVersionUID = 784321102100630614L;

	/**
	 * Constructor with errorCode, and rootCause
	 * 
	 * @param errorCode
	 *            The error code for this exception
	 * @param errorMessage
	 *            The error message for this exception
	 */
	public NullPointerException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
	
	/**
	 * @param arg0
	 *            Error Code Corresponds to Particular Exception
	 * @param arg1
	 *            Message providing the specific context of the error.
	 * @param arg2
	 *            Cause of exception
	 */
	public NullPointerException(String arg0, String arg1, Throwable arg2) {
		super(arg0, arg1, arg2);

	}

}
