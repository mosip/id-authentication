package io.mosip.preregistration.translitration.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class IllegalPraramException extends BaseUncheckedException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6810058264320216283L;
	
	/**
	 * @param msg
	 */
	public IllegalPraramException(String msg) {
		super("", msg);
	}

	/**
	 * @param errCode
	 * @param msg
	 */
	public IllegalPraramException(String errCode, String msg) {
		super(errCode, msg);
	}

	/**
	 * @param errCode
	 * @param msg
	 * @param cause
	 */
	public IllegalPraramException(String errCode, String msg, Throwable cause) {
		super(errCode, msg, cause);
	}

}
