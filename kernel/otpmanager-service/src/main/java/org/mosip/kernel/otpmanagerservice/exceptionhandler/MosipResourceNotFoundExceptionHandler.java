package org.mosip.kernel.otpmanagerservice.exceptionhandler;

import java.util.List;

import org.mosip.kernel.core.exception.BaseUncheckedException;

public class MosipResourceNotFoundExceptionHandler extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 786489177104135460L;
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
