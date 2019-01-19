package io.mosip.kernel.batchframework.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Exception class for invalid file uri.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
public class InvalidFileUriException extends BaseUncheckedException {

	/**
	 * Generated ID.
	 */
	private static final long serialVersionUID = 6546438600251924999L;

	/**
	 * Constructor for InvalidFileUriException class.
	 * 
	 * @param arg0
	 *            the errorCode.
	 * @param arg1
	 *            the errorMessage.
	 * @param arg2
	 *            the errorCause.
	 */
	public InvalidFileUriException(String arg0, String arg1, Throwable arg2) {
		super(arg0, arg1, arg2);
	}

}
