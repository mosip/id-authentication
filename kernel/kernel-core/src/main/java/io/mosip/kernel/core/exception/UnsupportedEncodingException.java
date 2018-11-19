package io.mosip.kernel.core.exception;

/**
 * @author Priya Soni
 *
 */
public class UnsupportedEncodingException extends IOException {

	private static final long serialVersionUID = -8185171240584538662L;


	public UnsupportedEncodingException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}


	public UnsupportedEncodingException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
