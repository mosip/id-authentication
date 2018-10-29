package io.mosip.kernel.core.exception;

/**
 * @author Priya Soni
 *
 */
public class MosipUnsupportedEncodingException extends MosipIOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8185171240584538662L;

	/**
	 * Exception thrown to signal the Character Encoding is not supported
	 * 
	 * @param errorCode
	 * @param errorMessage
	 * @param rootCause
	 */
	public MosipUnsupportedEncodingException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	/**
	 * @param errorCode
	 * @param errorMessage
	 */
	public MosipUnsupportedEncodingException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
