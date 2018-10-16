package io.mosip.registration.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class OperationNotAllowedException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public OperationNotAllowedException(String msg,Throwable cause) {
		super("",msg, cause);
	}
	public OperationNotAllowedException(String msg) {
		super("",msg);
	}
}
