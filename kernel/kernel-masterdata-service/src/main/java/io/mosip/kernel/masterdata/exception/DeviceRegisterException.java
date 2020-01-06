package io.mosip.kernel.masterdata.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class DeviceRegisterException extends BaseUncheckedException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1259410481176574774L;

	public DeviceRegisterException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
