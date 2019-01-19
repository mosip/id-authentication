package io.mosip.kernel.batchframework.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Exception class for empty job description.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
public class EmptyJobDescriptionException extends BaseUncheckedException {

	/**
	 * Generated ID.
	 */
	private static final long serialVersionUID = 4894332265950696489L;

	/**
	 * Constructor for EmptyJobDescription class.
	 * 
	 * @param errorCode
	 *            the errorCode.
	 * @param errorMessage
	 *            the errorMessage.
	 */
	public EmptyJobDescriptionException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);

	}

}
