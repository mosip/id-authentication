package io.mosip.kernel.core.util.exception;

/**
 * @author Priya Soni
 *
 */
public class MosipFileNotFoundException extends MosipIOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1762806620894866489L;

	/**
	 * @param errorCode
	 * @param errorMessage
	 * @param rootCause
	 */
	public MosipFileNotFoundException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);

	}

	/**
	 * @param errorCode
	 * @param errorMessage
	 */
	public MosipFileNotFoundException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);

	}

}
