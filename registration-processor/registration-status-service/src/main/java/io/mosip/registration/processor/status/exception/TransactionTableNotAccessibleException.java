package io.mosip.registration.processor.status.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

import io.mosip.registration.processor.status.exception.utils.RegistrationStatusErrorCodes;

public class TransactionTableNotAccessibleException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public TransactionTableNotAccessibleException() {
		super();
	}

	public TransactionTableNotAccessibleException(String message) {
		super(RegistrationStatusErrorCodes.IIS_EPU_ATU_TRANSACTION_TABLE_NOTACCESSIBLE, message);
	}

	public TransactionTableNotAccessibleException(String message, Throwable cause) {
		super(RegistrationStatusErrorCodes.IIS_EPU_ATU_TRANSACTION_TABLE_NOTACCESSIBLE + EMPTY_SPACE, message, cause);
	}
}