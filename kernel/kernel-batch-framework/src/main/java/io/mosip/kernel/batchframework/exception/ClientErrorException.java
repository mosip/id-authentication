package io.mosip.kernel.batchframework.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Exception class for server error.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
public class ClientErrorException extends BaseUncheckedException {

	/**
	 * Generated ID.
	 */
	private static final long serialVersionUID = 1199377189777056753L;

	/**
	 * Constructor for ClientErrorException class.
	 * 
	 * @param arg0
	 *            the errorCode.
	 * @param arg1
	 *            the errorMessage.
	 * @param arg2
	 *            the errorCause.
	 */
	public ClientErrorException(String arg0, String arg1, Throwable arg2) {
		super(arg0, arg1, arg2);

	}

}
