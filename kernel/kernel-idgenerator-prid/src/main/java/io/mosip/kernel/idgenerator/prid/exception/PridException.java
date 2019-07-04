package io.mosip.kernel.idgenerator.prid.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Exception class for prid.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
public class PridException extends BaseUncheckedException {

	/**
	 * Generated ID.
	 */
	private static final long serialVersionUID = 1585042846828488115L;

	/**
	 * Constructor for PridException.
	 * 
	 * @param errorCode    the error code.
	 * @param errorMessage the error message.
	 * @param rootCause    the cause.
	 */
	public PridException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);

	}

}
