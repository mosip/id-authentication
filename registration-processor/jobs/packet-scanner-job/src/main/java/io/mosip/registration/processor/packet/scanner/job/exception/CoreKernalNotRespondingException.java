package io.mosip.registration.processor.packet.scanner.job.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorCodes;
/**
 * The Class CoreKernalNotRespondingException.
 */
public class CoreKernalNotRespondingException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new core kernal not responding exception.
	 *
	 * @param errorMessage the error message
	 */
	public CoreKernalNotRespondingException(String errorMessage) {
		super(PlatformErrorCodes.RPR_PSJ_CORE_KERNEL_NOT_RESPONDING, errorMessage);
	}

	/**
	 * Instantiates a new core kernal not responding exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public CoreKernalNotRespondingException(String message, Throwable cause) {
		super(PlatformErrorCodes.RPR_PSJ_CORE_KERNEL_NOT_RESPONDING + EMPTY_SPACE, message, cause);
	}

}
