package io.mosip.kernel.idgenerator.partnerid.excepion;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Exception class for PartnerIdException.
 * 
 * @author Uday Kumar
 * @since 1.0.0
 *
 */
public class PartnerIdException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor for TspIdException.
	 * 
	 * @param errorCode
	 *            the error code.
	 * @param errorMessage
	 *            the error message.
	 * @param rootCause
	 *            the cause.
	 */
	public PartnerIdException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);

	}

}
