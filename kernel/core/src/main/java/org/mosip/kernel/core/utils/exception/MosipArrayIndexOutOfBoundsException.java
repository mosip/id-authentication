package org.mosip.kernel.core.utils.exception;

import org.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Thrown to indicate that an array has been accessed with an illegal index. The
 * index is either negative or greater than or equal to the size of the array
 * 
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
public class MosipArrayIndexOutOfBoundsException extends BaseUncheckedException {
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
	public MosipArrayIndexOutOfBoundsException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

}
