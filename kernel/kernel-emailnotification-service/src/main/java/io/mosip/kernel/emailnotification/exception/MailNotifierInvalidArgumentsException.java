package io.mosip.kernel.emailnotification.exception;

import java.util.List;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class MailNotifierInvalidArgumentsException extends BaseUncheckedException {
	/**
	 * Generated serial version.
	 */
	private static final long serialVersionUID = -1416474253520030879L;
	/**
	 * This variable holds the MosipErrors list.
	 */
	private final List<Errors> list;

	/**
	 * @param list
	 *            The error list.
	 */
	public MailNotifierInvalidArgumentsException(List<Errors> list) {
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
