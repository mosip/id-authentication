package org.mosip.kernel.eidgenerator.exception;

import org.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
public class MosipNullValueException extends BaseUncheckedException {
	private static final long serialVersionUID = 2842522178894167519L;

	public MosipNullValueException() {
		super();

	}

	public MosipNullValueException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);

	}

	public MosipNullValueException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);

	}

}
