package io.mosip.kernel.cryptomanager.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * The Class CryptoManagerSerivceException.
 * @author Srinivasan
 * @since 1.0.0
 */
public class CryptoManagerSerivceException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5916223368862935708L;
	
	/**
	 * Instantiates a new crypto manager serivce exception.
	 *
	 * @param errorCode the error code
	 * @param errorMessage the error message
	 * @param rootCause the root cause
	 */
	public CryptoManagerSerivceException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
		
	}

	/**
	 * Instantiates a new crypto manager serivce exception.
	 *
	 * @param errorCode the error code
	 * @param errorMessage the error message
	 */
	public CryptoManagerSerivceException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
		
	}

}
