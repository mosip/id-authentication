package io.mosip.registration.processor.packet.scanner.job.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorCodes;
/**
 * The Class DFSNotAccessibleException.
 */
public class DFSNotAccessibleException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new DFS not accessible exception.
	 */
	public DFSNotAccessibleException() {
		super();
	}

	/**
	 * Instantiates a new DFS not accessible exception.
	 *
	 * @param errorMessage the error message
	 */
	public DFSNotAccessibleException(String errorMessage) {
		super(PlatformErrorCodes.RPR_PSJ_DFS_NOT_ACCESSIBLE, errorMessage);
	}

	/**
	 * Instantiates a new DFS not accessible exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public DFSNotAccessibleException(String message, Throwable cause) {
		super(PlatformErrorCodes.RPR_PSJ_DFS_NOT_ACCESSIBLE, message, cause);
	}

}
