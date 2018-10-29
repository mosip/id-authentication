package io.mosip.kernel.core.exception;

/**
 * @author Priya Soni
 *
 */
public class MosipUnsupportedCharsetException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6711647152648795666L;

	/**
	 * Unchecked exception thrown when no support is available for a requested
	 * charset
	 * 
	 * @param errorCode
	 * @param errorMessage
	 * @param rootCause
	 */
	public MosipUnsupportedCharsetException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

}
