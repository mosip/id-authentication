package org.mosip.kernel.otpmanagerservice.exceptionhandler;

import java.util.List;

import org.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Class to handle exception when entity response is not found a particular key.
 * 
 * @author Ritesh Sinha
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
public class MosipRequiredKeyNotFoundExceptionHandler extends BaseUncheckedException {
	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = 8659572631028669116L;

	/**
	 * This variable holds the MosipErrors list.
	 */
	private final List<MosipErrors> list;

	/**
	 * Constructor for MosipRequiredKeyNotFoundExceptionHandler class.
	 * 
	 * @param list
	 *            The error list.
	 */
	public MosipRequiredKeyNotFoundExceptionHandler(List<MosipErrors> list) {
		this.list = list;
	}

	/**
	 * Getter for error list.
	 * 
	 * @return The error list.
	 */
	public List<MosipErrors> getList() {
		return list;
	}
}
