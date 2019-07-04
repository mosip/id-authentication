package io.mosip.registration.processor.core.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

public class RegistrationProcessorUnCheckedException extends BaseUncheckedException {


	private static final long serialVersionUID = 3436061208217864526L;

	/**
	 * Instantiates a new reg proc checked exception.
	 */
	public RegistrationProcessorUnCheckedException() {
		super();
	}

	/**
	 * Instantiates a new reg proc checked exception.
	 *
	 * @param errorCode    the error code
	 * @param errorMessage the error message
	 */
	public RegistrationProcessorUnCheckedException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	/**
	 * Instantiates a new reg proc checked exception.
	 *
	 * @param errorCode    the error code
	 * @param errorMessage the error message
	 * @param rootCause    the root cause
	 */
	public RegistrationProcessorUnCheckedException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	/**
	 * Instantiates a new reg proc checked exception.
	 *
	 * @param exceptionConstant the exception constant
	 */
	public RegistrationProcessorUnCheckedException(PlatformErrorMessages exceptionConstant) {
		this(exceptionConstant.getCode(), exceptionConstant.getMessage());
	}

	/**
	 * Instantiates a new reg proc checked exception.
	 *
	 * @param exceptionConstant the exception constant
	 * @param rootCause         the root cause
	 */
	public RegistrationProcessorUnCheckedException(PlatformErrorMessages exceptionConstant, Throwable rootCause) {
		this(exceptionConstant.getCode(), exceptionConstant.getMessage(), rootCause);
	}



}
