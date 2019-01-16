package io.mosip.kernel.idgenerator.machineid.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Class to handle MachineId generator service exception.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
public class MachineIdServiceException extends BaseUncheckedException {
	/**
	 * Serializable version ID.
	 */
	private static final long serialVersionUID = 1705160923118312929L;

	/**
	 * Constructor for the exception class with the arguments errorCode,
	 * errorMessage, rootCause.
	 * 
	 * @param errorCode
	 *            the error code.
	 * @param errorMessage
	 *            the error message.
	 * @param rootCause
	 *            the root cause.
	 */
	public MachineIdServiceException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}
}
