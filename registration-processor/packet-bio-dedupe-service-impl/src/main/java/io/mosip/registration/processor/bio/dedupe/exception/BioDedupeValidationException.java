package io.mosip.registration.processor.bio.dedupe.exception;

import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

/**
 * The Class BioDedupeValidationException.
 * @author Rishabh Keshari
 */
public class BioDedupeValidationException extends BioDedupeAppException {


	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2685586188884469940L;

	/**
	 * Instantiates a new id repo data validation exception.
	 */
	public BioDedupeValidationException() {
		super();
	}

	/**
	 * Instantiates a new id repo data validation exception.
	 *
	 * @param errorCode the error code
	 * @param errorMessage the error message
	 */
	public BioDedupeValidationException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	/**
	 * Instantiates a new id repo data validation exception.
	 *
	 * @param errorCode the error code
	 * @param errorMessage the error message
	 * @param rootCause the root cause
	 */
	public BioDedupeValidationException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	/**
	 * Instantiates a new id repo data validation exception.
	 *
	 * @param exceptionConstant the exception constant
	 */
	public BioDedupeValidationException(PlatformErrorMessages exceptionConstant) {
	super(exceptionConstant);
	}

	/**
	 * Instantiates a new id repo data validation exception.
	 *
	 * @param exceptionConstant the exception constant
	 * @param rootCause the root cause
	 */
	public BioDedupeValidationException(PlatformErrorMessages  exceptionConstant, Throwable rootCause) {
		super(exceptionConstant, rootCause);
	}

}
