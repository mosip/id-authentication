package io.mosip.kernel.core.exception;

/**
 * Base class for all preconditions violation exceptions.
 * 
 * @author Ritesh Sinha
 * @author Sidhant Agarwal
 * @author Sagar Mahapatra
 * @author Ravi Balaji
 * @author Priya Soni
 * @since 1.0.0
 */
public class IllegalArgumentException extends BaseUncheckedException {
	/** Serializable version Id. */
	private static final long serialVersionUID = 924722202110630628L;

	public IllegalArgumentException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);

	}

	public IllegalArgumentException(String errorCode, String errorMessage, Throwable cause) {
		super(errorCode, errorMessage, cause);

	}

}
