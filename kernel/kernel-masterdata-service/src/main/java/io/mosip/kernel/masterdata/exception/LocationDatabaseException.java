package io.mosip.kernel.masterdata.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class LocationDatabaseException extends BaseUncheckedException {

	/**
	 * system generated serialId
	 */
	private static final long serialVersionUID = 1779111207738456666L;
	
	/**
	 * constructor for database Exception
	 * @param errorCode
	 * @param errorMessage
	 */
	public LocationDatabaseException(String errorCode,String errorMessage) {
		super(errorCode,errorMessage);
	}

}
