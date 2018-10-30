package io.mosip.kernel.core.exception;

/**
 * @author Priya Soni
 *
 */
public class UnsupportedEncodingException extends IOException {

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
	public UnsupportedEncodingException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	/**
	 * @param errorCode
	 * @param errorMessage
	 */
	public UnsupportedEncodingException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
