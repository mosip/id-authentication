/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.application.exception.system;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * This class defines the SystemFileIOException
 * 
 * @author Rajath KR
 * @since 1.0.0
 * 
 */
public class SystemFileIOException extends BaseUncheckedException {
	private static final long serialVersionUID = 1L;

	public SystemFileIOException(String msg) {
		super("", msg);
	}

	public SystemFileIOException(String errCode, String msg) {
		super(errCode, msg);
	}

	public SystemFileIOException(String errCode, String msg, Throwable cause) {
		super(errCode, msg, cause);
	}

}
