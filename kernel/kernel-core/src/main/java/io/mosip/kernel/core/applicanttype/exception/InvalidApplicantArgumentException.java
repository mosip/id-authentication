
package io.mosip.kernel.core.applicanttype.exception;

import io.mosip.kernel.core.exception.BaseCheckedException;

/**
 * Thrown when null/empty/invalid query passed for getting applicant type id.
 * 
 * @see io.mosip.kernel.core.exception.BaseUncheckedException
 * 
 * @author Bal Vikash Sharma
 * 
 * @since 1.0.0
 *
 */
public class InvalidApplicantArgumentException extends BaseCheckedException {

	/**
	 * Generated serial version id
	 */
	private static final long serialVersionUID = 2785372588639412708L;

	/**
	 * Constructor to initialize handler exception
	 * 
	 * @param errorCode    The error code for this exception
	 * @param errorMessage The error message for this exception
	 */
	public InvalidApplicantArgumentException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	/**
	 * Constructor the initialize Handler exception
	 * 
	 * @param errorCode    The error code for this exception
	 * @param errorMessage The error message for this exception
	 * @param rootCause    the specified cause
	 */
	public InvalidApplicantArgumentException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}
}
