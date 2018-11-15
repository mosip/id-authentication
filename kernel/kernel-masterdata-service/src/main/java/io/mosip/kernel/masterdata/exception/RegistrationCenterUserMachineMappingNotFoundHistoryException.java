package io.mosip.kernel.masterdata.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
/**
 * * Custom Exception Class in case of error while registration center is not
 * found
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
public class RegistrationCenterUserMachineMappingNotFoundHistoryException
		extends
			BaseUncheckedException {

	/**
	 * generated serialVersionUID
	 */
	private static final long serialVersionUID = -1154778480212799100L;

	/**
	 * @param errorCode
	 * @param errorMessage
	 */
	public RegistrationCenterUserMachineMappingNotFoundHistoryException(
			String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
