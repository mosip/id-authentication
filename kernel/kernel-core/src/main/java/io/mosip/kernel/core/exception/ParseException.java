package io.mosip.kernel.core.exception;

/**
 * Signals that an error has been reached unexpectedly while parsing.
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 */
public class ParseException extends BaseUncheckedException {
	private static final long serialVersionUID = 924722202110630628L;

	public ParseException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);

	}

	public ParseException(String errorCode, String errorMessage, Throwable cause) {
		super(errorCode, errorMessage, cause);

	}

}
