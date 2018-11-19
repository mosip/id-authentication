package io.mosip.kernel.masterdata.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class ReasonsNotFoundException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6073465802637277023L;

	/**
	 * 
	 * @param errorCode
	 * @param errorMessage
	 */
	public ReasonsNotFoundException(final String errorCode,final String errorMessage) {
		super(errorCode,errorMessage);
	}
}
