package org.mosip.kernel.otpmanagerservice.exceptionhandler;

import java.util.List;

import org.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Class to handle exception when a particular resource is not found.
 * 
 * @author Ritesh Sinha
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
public class MosipResourceNotFoundExceptionHandler extends BaseUncheckedException {

	/**
	 * Serializable ID.
	 */
	private static final long serialVersionUID = 786489177104135460L;
	/**
	 * This variable holds the MosipErrors list.
	 */
	private final List<MosipErrors> list;

	/**
	 * Constructor for MosipResourceNotFoundExceptionHandler class.
	 * 
	 * @param list
	 *            The error list.
	 */
	public MosipResourceNotFoundExceptionHandler(List<MosipErrors> list) {
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
