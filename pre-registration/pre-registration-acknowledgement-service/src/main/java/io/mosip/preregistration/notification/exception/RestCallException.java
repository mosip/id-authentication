package io.mosip.preregistration.notification.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class RestCallException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8039562312343751179L;

	public RestCallException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RestCallException(String arg0, String arg1, Throwable arg2) {
		super(arg0, arg1, arg2);
		// TODO Auto-generated constructor stub
	}

	public RestCallException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
		// TODO Auto-generated constructor stub
	}

	public RestCallException(String errorMessage) {
		super(errorMessage);
		// TODO Auto-generated constructor stub
	}
	

}
