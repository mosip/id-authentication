package io.mosip.kernel.emailnotification.exception;

import java.util.List;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class InvalidArgumentsException extends BaseUncheckedException {
	/**
	 * Generated serial version.
	 */
	private static final long serialVersionUID = -1416474253520030879L;
	/**
	 * This variable holds the MosipErrors list.
	 */
	private final List<Error> list;

	/**
	 * @param list
	 *            The error list.
	 */
	public InvalidArgumentsException(List<Error> list) {
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
