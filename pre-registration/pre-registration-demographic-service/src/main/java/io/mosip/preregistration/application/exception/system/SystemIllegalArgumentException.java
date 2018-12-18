package io.mosip.preregistration.application.exception.system;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class SystemIllegalArgumentException extends BaseUncheckedException {
	private static final long serialVersionUID = 1L;

	public SystemIllegalArgumentException(String msg) {
		super("", msg);
	}

	public SystemIllegalArgumentException(String errCode, String msg) {
		super(errCode, msg);
	}

	public SystemIllegalArgumentException(String errCode, String msg, Throwable cause) {
		super(errCode, msg, cause);
	}

}
