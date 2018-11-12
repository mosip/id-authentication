package io.mosip.kernel.otpmanager.exception;

import java.util.List;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Class to handle exception when entity response is not found a particular key.
 * 
 * @author Ritesh Sinha
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
public class RequiredKeyNotFoundExceptionHandler extends BaseUncheckedException {
	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = 8659572631028669116L;

	/**
	 * This variable holds the MosipErrors list.
	 */
	private final List<Error> list;

	/**
	 * Constructor for MosipRequiredKeyNotFoundExceptionHandler class.
	 * 
	 * @param list
	 *            The error list.
	 */
	public RequiredKeyNotFoundExceptionHandler(List<Error> list) {
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
