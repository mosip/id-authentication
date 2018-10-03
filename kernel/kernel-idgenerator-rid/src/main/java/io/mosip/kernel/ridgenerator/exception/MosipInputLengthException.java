package io.mosip.kernel.ridgenerator.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
public class MosipInputLengthException extends BaseUncheckedException {
	private static final long serialVersionUID = 2842522173497867519L;

	public MosipInputLengthException() {
		super();

	}

	public MosipInputLengthException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);

	}

	public MosipInputLengthException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);

	}

}
