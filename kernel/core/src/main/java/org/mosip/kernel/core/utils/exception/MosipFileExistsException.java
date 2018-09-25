package org.mosip.kernel.core.utils.exception;

/**
 * @author Priya Soni
 *
 */
public class MosipFileExistsException extends MosipIOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2842522173494167519L;

	/**
	 * @param errorCode
	 * @param errorMessage
	 * @param rootCause
	 */
	public MosipFileExistsException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);

	}

	/**
	 * @param errorCode
	 * @param errorMessage
	 */
	public MosipFileExistsException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);

	}

}
