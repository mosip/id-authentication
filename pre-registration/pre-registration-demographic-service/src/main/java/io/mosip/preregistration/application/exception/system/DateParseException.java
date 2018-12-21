package io.mosip.preregistration.application.exception.system;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * @author M1046129
 *
 */
public class DateParseException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public DateParseException(String msg) {
		super("", msg);
	}

	public DateParseException(String errCode, String msg) {
		super(errCode, msg);
	}

	public DateParseException(String errCode, String msg, Throwable cause) {
		super(errCode, msg, cause);
	}

}
