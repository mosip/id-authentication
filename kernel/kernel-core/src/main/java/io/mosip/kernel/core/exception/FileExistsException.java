package io.mosip.kernel.core.exception;

/**
 * @author Priya Soni
 *
 */
public class FileExistsException extends IOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2842522173494167519L;

	/**
	 * @param errorCode
	 * @param errorMessage
	 * @param rootCause
	 */
	public FileExistsException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);

	}

	/**
	 * @param errorCode
	 * @param errorMessage
	 */
	public FileExistsException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);

	}

}
