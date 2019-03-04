package io.mosip.preregistration.documents.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;


/**
 * @author M1046129
 *
 */
public class FSServerException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public FSServerException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}

	public FSServerException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

}
