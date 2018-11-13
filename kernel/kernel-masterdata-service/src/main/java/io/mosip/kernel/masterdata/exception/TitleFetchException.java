package io.mosip.kernel.masterdata.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Exception when list of title not fetched properly
 * 
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
public class TitleFetchException extends BaseUncheckedException {

	private static final long serialVersionUID = -1154779999212799100L;

	public TitleFetchException() {
		super();

	}

	public TitleFetchException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);

	}

	public TitleFetchException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);

	}

	public TitleFetchException(String errorMessage) {
		super(errorMessage);

	}

}
