package io.mosip.kernel.core.qrcodegenerator.exception;

import io.mosip.kernel.core.exception.BaseCheckedException;

/**
 * A base class which covers the range of exceptions which may occur when
 * encoding a QRcode using the Writer framework.
 * 
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
public class QrcodeGenerationException extends BaseCheckedException {

	/**
	 * Unique id for serialization
	 */
	private static final long serialVersionUID = 473719335574042491L;

	/**
	 * Constructor with errorCode, errorMessage, and rootCause
	 * 
	 * @param errorCode
	 *            The error code for this exception
	 * @param errorMessage
	 *            The error message for this exception
	 * @param rootCause
	 *            Cause of this exception
	 */
	public QrcodeGenerationException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

}
