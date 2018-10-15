package io.mosip.kernel.smsnotifier.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class MosipInvalidNumberException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6174268206879672695L;

	public MosipInvalidNumberException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
