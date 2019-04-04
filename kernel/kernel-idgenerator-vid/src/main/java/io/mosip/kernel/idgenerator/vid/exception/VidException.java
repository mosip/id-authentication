package io.mosip.kernel.idgenerator.vid.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Exception class for vid.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
public class VidException extends BaseUncheckedException {

	/**
	 * Generated ID.
	 */
	private static final long serialVersionUID = -8004414680637137685L;

	/**
	 * Constructor for VidException.
	 * 
	 * @param errorCode    the error code.
	 * @param errorMessage the error message.
	 * @param rootCause    the cause.
	 */
	public VidException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

}
