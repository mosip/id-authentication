package io.mosip.kernel.core.util.exception;

import io.mosip.kernel.core.exception.BaseCheckedException;

/**
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
public class JsonProcessingException extends BaseCheckedException {
	private static final long serialVersionUID = 7784354823823721387L;


	public JsonProcessingException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);

	}


	public JsonProcessingException(String string) {

	}

}
