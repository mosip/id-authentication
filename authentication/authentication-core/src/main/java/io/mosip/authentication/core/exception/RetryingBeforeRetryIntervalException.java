package io.mosip.authentication.core.exception;

/**
 * The Class RetryingBeforeRetryIntervalException - The exception to be thrown
 * if try is performed before the retry interval. This is used to skip the job
 * item with this exception.
 * 
 * @author Loganathan Sekar
 */
public class RetryingBeforeRetryIntervalException extends IdAuthenticationBaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3582700337497330872L;

}
