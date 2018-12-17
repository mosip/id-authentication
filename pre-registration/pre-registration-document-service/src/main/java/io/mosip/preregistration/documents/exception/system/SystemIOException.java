package io.mosip.preregistration.documents.exception.system;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class SystemIOException  extends BaseUncheckedException {
	private static final long serialVersionUID = 1L;

	public SystemIOException(String msg) {
		super("", msg);
	}

	public SystemIOException(String errCode, String msg) {
		super(errCode, msg);
	}

	public SystemIOException(String errCode, String msg, Throwable cause) {
		super(errCode, msg, cause);
	}
}
