package io.mosip.kernel.lkeymanager.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Exception class for License Key Manager service.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
public class LicenseKeyServiceException extends BaseUncheckedException {

	/**
	 * Serializable version ID.
	 */
	private static final long serialVersionUID = 2506481216920647423L;

	/**
	 * Constructor with erroCode, errorMessage and rootCause as the arguments.
	 * 
	 * @param errorCode
	 *            the error code.
	 * @param errorMessage
	 *            the error message.
	 * @param rootCause
	 *            root cause of the exceptionn.
	 */
	public LicenseKeyServiceException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}
}
