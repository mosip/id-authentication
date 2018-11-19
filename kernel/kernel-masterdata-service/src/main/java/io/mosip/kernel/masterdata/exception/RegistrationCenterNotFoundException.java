package io.mosip.kernel.masterdata.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
/**
 * 
 * @author Abhishek Kumar
 * @version 1.0.0
 * @since  24-10-2018
 */
public class RegistrationCenterNotFoundException extends BaseUncheckedException{

	/**
	 * generated serialVersionUID
	 */
	private static final long serialVersionUID =  -1154778480212799100L;

	public RegistrationCenterNotFoundException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
