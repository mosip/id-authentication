package org.mosip.kernel.core.utils.exception;


/**
 * @author  Megha Tanga
 *
 */
public class MosipDataFormatException extends MosipIOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1762806620894866489L;

	/**
	 * @param errorCode
	 * @param errorMessage
	 * @param rootCause
	 */
	public MosipDataFormatException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);

	}

	/**
	 * @param errorCode
	 * @param errorMessage
	 */
	public MosipDataFormatException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);

	}

}

