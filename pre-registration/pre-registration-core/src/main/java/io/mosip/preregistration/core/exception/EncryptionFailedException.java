package io.mosip.preregistration.core.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * @author Jagadishwari S
 * @since 1.0.0
 *
 */
public class EncryptionFailedException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5135952690225019228L;

	public EncryptionFailedException(String msg) {
		super("", msg);
	}

	public EncryptionFailedException(String msg, Throwable cause) {
		super("", msg, cause);
	}

	public EncryptionFailedException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}

	public EncryptionFailedException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	public EncryptionFailedException() {
		super();
	}
}
