package io.mosip.kernel.masterdata.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Custom Exception Class in case of mapping failure of registration center
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
public class RegistrationCenterUserMachineMappingHistoryException extends BaseUncheckedException {

	/**
	 * Generated serial version id
	 */
	private static final long serialVersionUID = 2440119817192794191L;

	/**
	 * Constructor the initialize Handler exception
	 * 
	 * @param errorCode    The errorcode for this exception
	 * @param errorMessage The error message for this exception
	 */
	public RegistrationCenterUserMachineMappingHistoryException(String errorCode,String errorMessage) {
		super(errorCode, errorMessage);
	}

}
