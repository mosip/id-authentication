package io.mosip.kernel.core.exception;

/**
 * @author Priya Soni
 * @author Sidhant Agarwal
 *
 */
public class IOException extends BaseCheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7464354823823721387L;

	/**
	 * @param errorCode
	 * @param errorMessage
	 * @param rootCause
	 */
	public IOException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);

	}

	/**
	 * @param errorCode
	 * @param errorMessage
	 */
	public IOException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);

	}

}
