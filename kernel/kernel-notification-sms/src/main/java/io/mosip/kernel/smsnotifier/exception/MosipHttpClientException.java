package io.mosip.kernel.smsnotifier.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class MosipHttpClientException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2070234611389539605L;

	public MosipHttpClientException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);

	}

}
