package io.mosip.preregistration.application.exception.system;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * @author M1046129
 *
 */
public class SystemUnsupportedEncodingException extends BaseUncheckedException {
	private static final long serialVersionUID = 1L;

	public SystemUnsupportedEncodingException(String msg) {
		super("", msg);
	}

	public SystemUnsupportedEncodingException(String errCode, String msg) {
		super(errCode, msg);
	}

	public SystemUnsupportedEncodingException(String errCode, String msg, Throwable cause) {
		super(errCode, msg, cause);
	}

}
