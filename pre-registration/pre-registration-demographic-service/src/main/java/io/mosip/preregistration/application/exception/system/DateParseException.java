/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.application.exception.system;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * This class defines the DateParseException
 * 
 * @author Jagadishwari S
 * @since 1.0.0
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
