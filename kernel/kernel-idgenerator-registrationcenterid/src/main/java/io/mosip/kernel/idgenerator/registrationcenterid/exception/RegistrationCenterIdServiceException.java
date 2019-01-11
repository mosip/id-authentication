package io.mosip.kernel.idgenerator.registrationcenterid.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Exception class for Registration Center ID.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
public class RegistrationCenterIdServiceException extends BaseUncheckedException {
	/**
	 * Serializable verison ID.
	 */
	private static final long serialVersionUID = 3032930875351385151L;

	/**
	 * Constructor with errorcode, errormessage and rootcause as the arguments.
	 * 
	 * @param errorCode
	 *            the error code.
	 * @param errorMessage
	 *            the error message.
	 * @param rootCause
	 *            the root cause of the exception.
	 */
	public RegistrationCenterIdServiceException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}
}
