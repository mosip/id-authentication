/**
 * 
 */
package io.mosip.registration.processor.packet.manager.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

/**
 * This CoreKernelNotRespondingException occurs when  Core Kernel Configuration Service is not responding.
 *
 * @author Sowmya Goudar
 */
public class CoreKernelNotRespondingException extends BaseUncheckedException{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;


	/**
	 * Instantiates a new core kernel not responding exception.
	 */
	public CoreKernelNotRespondingException() {
		super();

	}

	/**
	 * Instantiates a new core kernel not responding exception.
	 *
	 * @param errorMessage the error message
	 */
	public CoreKernelNotRespondingException(String errorMessage) {
		super(PlatformErrorMessages.RPR_PKM_CORE_KERNEL_NOT_RESPONDING.getCode(), errorMessage);
	}

	/**
	 * Instantiates a new core kernel not responding exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public CoreKernelNotRespondingException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_PKM_CORE_KERNEL_NOT_RESPONDING.getCode() + EMPTY_SPACE, message, cause);

	}



}
