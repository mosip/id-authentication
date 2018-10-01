package org.mosip.kernel.core.util.exception;

import org.mosip.kernel.core.exception.BaseCheckedException;

/**
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */

public class MosipJsonParseException extends BaseCheckedException {
	private static final long serialVersionUID = 7469054823823721387L;

	/**
	 * @param errorCode
	 * @param errorMessage
	 * @param rootCause
	 */
	public MosipJsonParseException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);

	}

}
