package io.mosip.registration.processor.core.exception;

import io.mosip.kernel.core.exception.BaseCheckedException;

public class IntroducerValidationException extends BaseCheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new IntroducerValidationException .
	 */
	public IntroducerValidationException() {
		super();
	}

	/**
	 * 
	 * @param message
	 */
	public IntroducerValidationException(String code,String message) {
		super(code, message);
	}

}
