package io.mosip.preregistration.notification.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class IOException extends BaseUncheckedException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6723728155340185347L;

	public IOException() {
		super();
	}

	public IOException(String arg0, String arg1, Throwable arg2) {
		super(arg0, arg1, arg2);
	}

	public IOException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	public IOException(String errorMessage) {
		super(errorMessage);
	}

}
