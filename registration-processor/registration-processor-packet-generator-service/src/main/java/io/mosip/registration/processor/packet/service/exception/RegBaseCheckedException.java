package io.mosip.registration.processor.packet.service.exception;

import io.mosip.kernel.core.exception.BaseCheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

/**
 * The class to handle all the checked exception in REG
 * 
 * @author Balaji Sridharan
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

	public RegBaseCheckedException(PlatformErrorMessages exceptionConstant, String message, Throwable rootCause) {
		super(exceptionConstant.getCode(), message, rootCause);
	}

}
