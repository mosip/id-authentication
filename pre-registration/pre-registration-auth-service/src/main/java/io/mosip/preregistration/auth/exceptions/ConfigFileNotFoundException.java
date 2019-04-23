package io.mosip.preregistration.auth.exceptions;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class ConfigFileNotFoundException  extends BaseUncheckedException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1298682891599963309L;

	public ConfigFileNotFoundException(String msg) {
		super("", msg);
	}

	public ConfigFileNotFoundException(String msg, Throwable cause) {
		super("", msg, cause);
	}

	public ConfigFileNotFoundException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}

	public ConfigFileNotFoundException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	public ConfigFileNotFoundException() {
		super();
	}
}