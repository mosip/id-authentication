package org.mosip.kernel.ridgenerator.exception;

import org.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
public class MosipEmptyInputException extends BaseUncheckedException {
	private static final long serialVersionUID = 2842524563494167519L;

	public MosipEmptyInputException() {
		super();

	}

	public MosipEmptyInputException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);

	}

	public MosipEmptyInputException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);

	}

}
