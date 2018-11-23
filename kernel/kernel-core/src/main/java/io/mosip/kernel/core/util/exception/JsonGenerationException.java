package io.mosip.kernel.core.util.exception;

import io.mosip.kernel.core.exception.BaseCheckedException;

/**
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
public class JsonGenerationException extends BaseCheckedException {
	private static final long serialVersionUID = 7464354823823756787L;


	public JsonGenerationException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);

	}

	public JsonGenerationException(String string) {

	}

}
