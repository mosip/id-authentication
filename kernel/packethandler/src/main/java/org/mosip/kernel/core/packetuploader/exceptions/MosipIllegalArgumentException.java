package io.mosip.kernel.core.packetuploader.exceptions;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class MosipIllegalArgumentException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 925507057490535674L;

	public MosipIllegalArgumentException() {
		
	}

	public MosipIllegalArgumentException(String arg0, String arg1, Throwable arg2) {
		super(arg0, arg1, arg2);
	}

	public MosipIllegalArgumentException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
