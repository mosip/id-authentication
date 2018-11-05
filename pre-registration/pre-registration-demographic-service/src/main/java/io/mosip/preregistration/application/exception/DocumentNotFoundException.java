package io.mosip.preregistration.application.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * DocumentNotFoundException
 * 
 * @author M1043008
 *
 */

public class DocumentNotFoundException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7303748392658525834L;

	public DocumentNotFoundException() {
		super();
	}

	public DocumentNotFoundException(String arg0, String arg1, Throwable arg2) {
		super(arg0, arg1, arg2);
	}

	public DocumentNotFoundException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
