package io.mosip.kernel.masterdata.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Custom Exception Class in case of mapping failure of registration center
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
public class RegistrationCenterMappingException extends BaseUncheckedException {

	/**
	 * Generated serial version id
	 */
	private static final long serialVersionUID = 635562471858855910L;

	/**
	 * Constructor the initialize Handler exception
	 * 
	 * @param errorCode
	 *            The errorcode for this exception
	 * @param errorMessage
	 *            The error message for this exception
	 */
	public RegistrationCenterMappingException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
