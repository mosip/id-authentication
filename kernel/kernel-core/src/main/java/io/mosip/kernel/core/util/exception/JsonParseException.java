package io.mosip.kernel.core.util.exception;

import io.mosip.kernel.core.exception.BaseCheckedException;

/**
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */

public class JsonParseException extends BaseCheckedException {
	private static final long serialVersionUID = 7469054823823721387L;


	public JsonParseException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);

	}

}
