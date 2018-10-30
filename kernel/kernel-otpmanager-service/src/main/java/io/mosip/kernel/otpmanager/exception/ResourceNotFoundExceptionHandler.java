package io.mosip.kernel.otpmanager.exception;

import java.util.List;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Class to handle exception when a particular resource is not found.
 * 
 * @author Ritesh Sinha
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
public class ResourceNotFoundExceptionHandler extends BaseUncheckedException {

	/**
	 * Serializable ID.
	 */
	private static final long serialVersionUID = 786489177104135460L;
	/**
	 * This variable holds the MosipErrors list.
	 */
	private final List<Errors> list;

	/**
	 * Constructor for MosipResourceNotFoundExceptionHandler class.
	 * 
	 * @param list
	 *            The error list.
	 */
	public ResourceNotFoundExceptionHandler(List<Errors> list) {
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
