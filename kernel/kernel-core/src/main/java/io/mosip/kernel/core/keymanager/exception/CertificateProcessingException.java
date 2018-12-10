package io.mosip.kernel.core.keymanager.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Custom exception for CertificateProcessingException
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
public class CertificateProcessingException extends BaseUncheckedException {
	/**
	 * The generated serial version id
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor initialize CertificateProcessingException
	 * 
	 * @param errorCode
	 *            The errorcode for this exception
	 * @param errorMessage
	 *            The error message for this exception
	 */
	public CertificateProcessingException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
