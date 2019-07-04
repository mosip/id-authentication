package io.mosip.admin.masterdata.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;


public class MasterDataCardException extends BaseUncheckedException{

	private static final long serialVersionUID = -925982106430540465L;

	public MasterDataCardException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	public MasterDataCardException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
