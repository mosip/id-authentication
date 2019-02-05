package io.mosip.preregistration.core.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * @author Jagadishwari S
 * @since 1.0.0
 *
 */
public class DecryptionFailedException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5135952690225019228L;

	public DecryptionFailedException(String msg) {
		super("", msg);
	}

	public DecryptionFailedException(String msg, Throwable cause) {
		super("", msg, cause);
	}

	public DecryptionFailedException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}

	public DecryptionFailedException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	public DecryptionFailedException() {
		super();
	}
}
