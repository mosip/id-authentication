package io.mosip.kernel.masterdata.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Custom Exception Class in case of Biometric type mapping fails
 * 
 * @author Neha
 * @since 1.0.0
 *
 */
public class BiometricTypeMappingException extends BaseUncheckedException {

	/**
	 * Generated serial version id
	 */
	private static final long serialVersionUID = 6453612871681975183L;

	/**
	 * Constructor to initialize handler exception
	 * 
	 * @param errorCode
	 *            The error code for this exception
	 * @param errorMessage
	 *            The error message for this exception
	 */
	public BiometricTypeMappingException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);

	}

}
