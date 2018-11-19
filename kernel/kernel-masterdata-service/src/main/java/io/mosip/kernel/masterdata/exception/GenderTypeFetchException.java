package io.mosip.kernel.masterdata.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Custom exception class for fetching gender type
 * 
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
public class GenderTypeFetchException extends BaseUncheckedException {

	private static final long serialVersionUID = -2274049900950444446L;

	public GenderTypeFetchException() {
		super();

	}

	public GenderTypeFetchException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);

	}

	public GenderTypeFetchException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);

	}

	public GenderTypeFetchException(String errorMessage) {
		super(errorMessage);

	}

}
