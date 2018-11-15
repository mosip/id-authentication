package io.mosip.kernel.masterdata.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Custom Exception Class in case of Biometric Type list not found
 * 
 * @author Neha
 * @since 1.0.0
 *
 */
public class BiometricTypeNotFoundException extends BaseUncheckedException {

	/**
	 * Generated serial version id
	 */
	private static final long serialVersionUID = -2274049900950489546L;

	/**
	 * Constructor to initialize handler exception
	 * 
	 * @param errorCode
	 *            The error code for this exception
	 * @param errorMessage
	 *            The error message for this exception
	 */
	public BiometricTypeNotFoundException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
