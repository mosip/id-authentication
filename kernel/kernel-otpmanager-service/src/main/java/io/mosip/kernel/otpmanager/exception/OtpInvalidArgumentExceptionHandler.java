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
public class OtpInvalidArgumentExceptionHandler extends BaseUncheckedException {
	/**
	 * Serializable version ID.
	 */
	private static final long serialVersionUID = 8152409863253682472L;

	/**
	 * This variable holds the MosipErrors list.
	 */
	private final List<Errors> list;

	/**
	 * @param list
	 *            The error list.
	 */
	public OtpInvalidArgumentExceptionHandler(List<Errors> list) {
		this.list = list;
	}

	/**
	 * Getter for error list.
	 * 
	 * @return The error list.
	 */
	public List<Errors> getList() {
		return list;
	}
}
