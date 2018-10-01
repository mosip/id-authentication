package org.mosip.kernel.core.util.exception;

import org.mosip.kernel.core.exception.BaseCheckedException;

/**
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
public class MosipJsonGenerationException extends BaseCheckedException {
	private static final long serialVersionUID = 7464354823823756787L;

	/**
	 * @param errorCode
	 * @param errorMessage
	 * @param rootCause
	 */
	public MosipJsonGenerationException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);

	}

	/**
	 * @param string
	 */
	public MosipJsonGenerationException(String string) {

	}

}
