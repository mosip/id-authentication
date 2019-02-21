package io.mosip.preregistration.application.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class InvalidDateFormatException extends BaseUncheckedException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7333914604834231072L;

	public InvalidDateFormatException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public InvalidDateFormatException(String arg0, String arg1, Throwable arg2) {
		super(arg0, arg1, arg2);
		// TODO Auto-generated constructor stub
	}

	public InvalidDateFormatException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
		// TODO Auto-generated constructor stub
	}

	public InvalidDateFormatException(String errorMessage) {
		super(errorMessage);
		// TODO Auto-generated constructor stub
	}
	

}
