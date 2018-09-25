package org.mosip.kernel.core.utils.exception;

import org.mosip.kernel.core.exception.BaseCheckedException;

/**
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
public class MosipJsonProcessingException extends BaseCheckedException {
	private static final long serialVersionUID = 7784354823823721387L;

	/**
	 * @param errorCode
	 * @param errorMessage
	 * @param rootCause
	 */
	public MosipJsonProcessingException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);

	}

	/**
	 * @param string
	 */
	public MosipJsonProcessingException(String string) {

	}

}
