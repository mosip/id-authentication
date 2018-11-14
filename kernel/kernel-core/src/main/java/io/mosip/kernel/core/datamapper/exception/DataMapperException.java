package io.mosip.kernel.core.datamapper.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * 
 * Custom class for DataMapper Exception
 * 
 * @author Neha
 * @since 1.0.0
 *
 */
public class DataMapperException extends BaseUncheckedException {

	/**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = 2L;

	/**
	 * Constructor for DataMapperException
	 * 
	 * @param errorCode
	 *            The error code
	 * @param errorMessage
	 *            The error message
	 * @param rootCause
	 *            the specified cause
	 */
	public DataMapperException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	/**
	 * Constructor for DataMapperException
	 * 
	 * @param errorCode
	 *            The error code
	 * @param errorMessage
	 *            The error message
	 */
	public DataMapperException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	/**
	 * Constructor for DataMapperException
	 * 
	 * @param errorMessage
	 *            The error message
	 */
	public DataMapperException(String errorMessage) {
		super(errorMessage);
	}

}
