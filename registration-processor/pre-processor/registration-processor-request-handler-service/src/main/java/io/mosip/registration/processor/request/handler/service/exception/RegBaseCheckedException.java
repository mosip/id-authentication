package io.mosip.registration.processor.request.handler.service.exception;

import io.mosip.kernel.core.exception.BaseCheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

/**
 * The class to handle all the checked exception in REG
 * 
 * @author Sowmya
 * @since 1.0.0
 *
 */
public class RegBaseCheckedException extends BaseCheckedException {

	/**
	 * Serializable Version Id
	 */
	private static final long serialVersionUID = 7381314129809012005L;

	/**
	 * Instance of {@link Logger}
	 */

	/**
	 * Constructs a new checked exception
	 */
	public RegBaseCheckedException() {
		super();
	}

	public RegBaseCheckedException(PlatformErrorMessages exceptionConstant, Throwable rootCause) {
		super(exceptionConstant.getCode(), exceptionConstant.getMessage(), rootCause);
	}

	public RegBaseCheckedException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	public RegBaseCheckedException(PlatformErrorMessages exceptionConstant, String message, Throwable rootCause) {
		super(exceptionConstant.getCode(), message, rootCause);
	}

	public RegBaseCheckedException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	public RegBaseCheckedException(String errorCode, String errorMessage, Throwable rootCause, String id) {
		super(errorCode, errorMessage, rootCause);
	}

	public RegBaseCheckedException(PlatformErrorMessages exceptionConstant) {
		this(exceptionConstant.getCode(), exceptionConstant.getMessage());
	}

}
