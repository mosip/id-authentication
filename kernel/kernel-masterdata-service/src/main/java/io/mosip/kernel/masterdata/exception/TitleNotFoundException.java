package io.mosip.kernel.masterdata.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Exception when titles not found in masterdata
 * 
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
public class TitleNotFoundException extends BaseUncheckedException {

	private static final long serialVersionUID = -1154778666612799100L;

	public TitleNotFoundException() {
		super();

	}

	public TitleNotFoundException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);

	}

	public TitleNotFoundException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);

	}

	public TitleNotFoundException(String errorMessage) {
		super(errorMessage);

	}

}
