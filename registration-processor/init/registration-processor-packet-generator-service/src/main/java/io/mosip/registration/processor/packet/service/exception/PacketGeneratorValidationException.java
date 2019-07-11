package io.mosip.registration.processor.packet.service.exception;

import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

/**
 * The Class RegStatusValidationException.
 * @author Rishabh Keshari
 */
public class PacketGeneratorValidationException extends RegBaseCheckedException {


	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2685586188884469940L;

	/**
	 * Instantiates a new id repo data validation exception.
	 */
	public PacketGeneratorValidationException() {
		super();
	}

	/**
	 * Instantiates a new id repo data validation exception.
	 *
	 * @param errorCode the error code
	 * @param errorMessage the error message
	 */
	public PacketGeneratorValidationException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	/**
	 * Instantiates a new id repo data validation exception.
	 *
	 * @param errorCode the error code
	 * @param errorMessage the error message
	 * @param rootCause the root cause
	 */
	public PacketGeneratorValidationException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	/**
	 * Instantiates a new id repo data validation exception.
	 *
	 * @param exceptionConstant the exception constant
	 */
	public PacketGeneratorValidationException(PlatformErrorMessages exceptionConstant) {
	super(exceptionConstant);
	}

	/**
	 * Instantiates a new id repo data validation exception.
	 *
	 * @param exceptionConstant the exception constant
	 * @param rootCause the root cause
	 */
	public PacketGeneratorValidationException(PlatformErrorMessages  exceptionConstant, Throwable rootCause) {
		super(exceptionConstant, rootCause);
	}

}
