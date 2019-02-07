package io.mosip.kernel.syncdata.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Thrown while performing operation return of the operation assume some data
 * but got no data.
 * 
 * @see io.mosip.kernel.core.exception.BaseUncheckedException
 * 
 * @author Bal Vikash Sharma
 * 
 * @since 1.0.0
 *
 */
public class DataNotFoundException extends BaseUncheckedException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8797529819064607765L;


	/**
	 * Constructor to initialize handler exception
	 * 
	 * @param errorCode
	 *            The error code for this exception
	 * @param errorMessage
	 *            The error message for this exception
	 */
	public DataNotFoundException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
	
	
	/**
	 * Constructor the initialize Handler exception
	 * 
	 * @param errorCode
	 *            The error code for this exception
	 * @param errorMessage
	 *            The error message for this exception
	 * @param rootCause
	 *            the specified cause
	 */
	public DataNotFoundException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}
}
