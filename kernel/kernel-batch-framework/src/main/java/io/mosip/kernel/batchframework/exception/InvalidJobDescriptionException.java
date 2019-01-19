package io.mosip.kernel.batchframework.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Exception class for invalid job description.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
public class InvalidJobDescriptionException extends BaseUncheckedException {

	/**
	 * Generated ID.
	 */
	private static final long serialVersionUID = 2831913605383311980L;

	/**
	 * Constructor for InvalidJobDescriptionException class.
	 * 
	 * @param errorCode
	 *            the errorCode
	 * @param errorMessage
	 *            the errorMessage.
	 */
	public InvalidJobDescriptionException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);

	}

}
