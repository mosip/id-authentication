package io.mosip.kernel.masterdata.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class LocationRecordsNotFoundException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6233448971062476004L;
	
	/**
	 * 
	 * @param errorCode
	 * @param errorMessage
	 */
	public LocationRecordsNotFoundException(String errorCode,String errorMessage) {
		super(errorCode,errorMessage);
	}

}
