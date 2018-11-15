package io.mosip.kernel.masterdata.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class ReasonArgumentNotFoundException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1743455046312639771L;
	
	public ReasonArgumentNotFoundException(final String errorCode,final String errorMessage) {
	super(errorCode,errorMessage);	
	}

}
