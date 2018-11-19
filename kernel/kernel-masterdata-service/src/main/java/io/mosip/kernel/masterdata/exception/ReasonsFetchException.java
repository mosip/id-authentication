package io.mosip.kernel.masterdata.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class ReasonsFetchException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1342203912137448867L;
	
	/**
	 * 
	 * @param errorCode
	 * @param errorMessage
	 */
	public ReasonsFetchException(final String errorCode,final String errorMessage) {
		super(errorCode,errorMessage);
	}

}
