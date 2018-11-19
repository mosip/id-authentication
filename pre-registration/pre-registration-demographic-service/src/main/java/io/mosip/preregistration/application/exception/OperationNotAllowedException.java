package io.mosip.preregistration.application.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * OperationNotAllowedException
 * 
 * @author M1043226
 *
 */
public class OperationNotAllowedException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public OperationNotAllowedException(String msg, Throwable cause) {
		super("", msg, cause);
	}

	public OperationNotAllowedException(String msg) {
		super("", msg);
	}
}
