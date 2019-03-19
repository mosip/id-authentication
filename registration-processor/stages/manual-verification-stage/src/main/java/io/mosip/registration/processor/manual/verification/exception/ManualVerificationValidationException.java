package io.mosip.registration.processor.manual.verification.exception;

import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

/**
 * The Class ManualVerificationValidationException.
 * @author Rishabh Keshari
 */
public class ManualVerificationValidationException extends ManualVerificationAppException {


	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2685586188884469940L;

	/**
	 * Instantiates a new id repo data validation exception.
	 */
	public ManualVerificationValidationException() {
		super();
	}

	/**
	 * Instantiates a new id repo data validation exception.
	 *
	 * @param errorCode the error code
	 * @param errorMessage the error message
	 */
	public ManualVerificationValidationException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	/**
	 * Instantiates a new id repo data validation exception.
	 *
	 * @param errorCode the error code
	 * @param errorMessage the error message
	 * @param rootCause the root cause
	 */
	public ManualVerificationValidationException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	/**
	 * Instantiates a new id repo data validation exception.
	 *
	 * @param exceptionConstant the exception constant
	 */
	public ManualVerificationValidationException(PlatformErrorMessages exceptionConstant) {
	super(exceptionConstant);
	}

	/**
	 * Instantiates a new id repo data validation exception.
	 *
	 * @param exceptionConstant the exception constant
	 * @param rootCause the root cause
	 */
	public ManualVerificationValidationException(PlatformErrorMessages  exceptionConstant, Throwable rootCause) {
		super(exceptionConstant, rootCause);
	}

}
