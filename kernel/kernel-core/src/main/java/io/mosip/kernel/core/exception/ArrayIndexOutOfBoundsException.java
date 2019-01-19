package io.mosip.kernel.core.exception;

/**
 * Thrown to indicate that an array has been accessed with an illegal index. The
 * index is either negative or greater than or equal to the size of the array
 * 
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
public class ArrayIndexOutOfBoundsException extends BaseUncheckedException {
	/** Serializable version Id. */
	private static final long serialVersionUID = 522722202113670628L;

	/**
	 * @param errorCode
	 *            The error code defined for the exception
	 * @param errorMessage
	 *            The error message defined for the exception
	 * @param rootCause
	 *            Traceback to the method throwing the error
	 */
	public ArrayIndexOutOfBoundsException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

}
