package io.mosip.kernel.masterdata.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Custom exception class for wrong mapping
 * 
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
public class GenderTypeMappingException extends BaseUncheckedException {

	private static final long serialVersionUID = -2274049907770489546L;

	public GenderTypeMappingException() {
		super();

	}

	public GenderTypeMappingException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);

	}

	public GenderTypeMappingException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);

	}

	public GenderTypeMappingException(String errorMessage) {
		super(errorMessage);

	}

}
