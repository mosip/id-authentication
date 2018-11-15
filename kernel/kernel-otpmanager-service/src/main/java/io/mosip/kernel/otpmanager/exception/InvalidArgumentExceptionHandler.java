package io.mosip.kernel.otpmanager.exception;

import java.util.List;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Class to handle exceptions for invalid OTP validation inputs.
 * 
 * @author Ritesh Sinha
 * @author Sagar Mahapatra
 * @since 1.0.0
 * 
 */
public class InvalidArgumentExceptionHandler extends BaseUncheckedException {
	/**
	 * Serializable version ID.
	 */
	private static final long serialVersionUID = 8152409863253682472L;

	/**
	 * This variable holds the MosipErrors list.
	 */
	private final List<Error> list;

	/**
	 * @param list
	 *            The error list.
	 */
	public InvalidArgumentExceptionHandler(List<Error> list) {
		this.list = list;
	}

	/**
	 * Getter for error list.
	 * 
	 * @return The error list.
	 */
	public List<Error> getList() {
		return list;
	}
}
