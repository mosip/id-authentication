package io.mosip.registration.processor.core.exception;

import io.mosip.kernel.core.exception.BaseCheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

public class RegistrationProcessorCheckedException extends BaseCheckedException{

	private static final long serialVersionUID = 3436061208217864526L;

	/**
	 * Instantiates a new reg proc checked exception.
	 */
	public RegistrationProcessorCheckedException() {
		super();
	}

	/**
	 * Instantiates a new reg proc checked exception.
	 *
	 * @param errorCode    the error code
	 * @param errorMessage the error message
	 */
	public RegistrationProcessorCheckedException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	/**
	 * Instantiates a new reg proc checked exception.
	 *
	 * @param errorCode    the error code
	 * @param errorMessage the error message
	 * @param rootCause    the root cause
	 */
	public RegistrationProcessorCheckedException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	/**
	 * Instantiates a new reg proc checked exception.
	 *
	 * @param exceptionConstant the exception constant
	 */
	public RegistrationProcessorCheckedException(PlatformErrorMessages exceptionConstant) {
		this(exceptionConstant.getCode(), exceptionConstant.getMessage());
	}

	/**
	 * Instantiates a new reg proc checked exception.
	 *
	 * @param exceptionConstant the exception constant
	 * @param rootCause         the root cause
	 */
	public RegistrationProcessorCheckedException(PlatformErrorMessages exceptionConstant, Throwable rootCause) {
		this(exceptionConstant.getCode(), exceptionConstant.getMessage(), rootCause);
	}

}
