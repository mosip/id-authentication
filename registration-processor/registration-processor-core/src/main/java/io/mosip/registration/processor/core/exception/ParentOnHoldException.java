package io.mosip.registration.processor.core.exception;

import io.mosip.kernel.core.exception.BaseCheckedException;

public class ParentOnHoldException extends BaseCheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new IntroducerValidationException .
	 */
	public ParentOnHoldException() {
		super();
	}

	/**
	 * 
	 * @param message
	 */
	public ParentOnHoldException(String code,String message) {
		super(code, message);
	}

}
