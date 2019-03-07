package io.mosip.registration.processor.packet.service.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class EncryptorBaseCheckedException extends BaseUncheckedException{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new encryptor base checked exception.
	 */
	public EncryptorBaseCheckedException() {
		super();
	}

	/**
	 * Instantiates a new encryptor base checked exception.
	 *
	 * @param errorCode the error code
	 * @param errorMessage the error message
	 * @param cause the cause
	 */
	public EncryptorBaseCheckedException(String errorCode, String errorMessage, Throwable cause) {
		super(errorCode, errorMessage, cause);
	}

	/**
	 * Instantiates a new encryptor base checked exception.
	 *
	 * @param errorCode the error code
	 * @param errorMessage the error message
	 */
	public EncryptorBaseCheckedException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	/**
	 * Instantiates a new encryptor base checked exception.
	 *
	 * @param errorMessage the error message
	 */
	public EncryptorBaseCheckedException(String errorMessage) {
		super(errorMessage);
	}

}
