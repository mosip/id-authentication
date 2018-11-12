package io.mosip.kernel.masterdata.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Custom exception class for gender not found
 * 
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
public class GenderTypeNotFoundException extends BaseUncheckedException {

	private static final long serialVersionUID = -2234534500950489546L;

	public GenderTypeNotFoundException() {
		super();

	}

	public GenderTypeNotFoundException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);

	}

	public GenderTypeNotFoundException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);

	}

	public GenderTypeNotFoundException(String errorMessage) {
		super(errorMessage);

	}

}
