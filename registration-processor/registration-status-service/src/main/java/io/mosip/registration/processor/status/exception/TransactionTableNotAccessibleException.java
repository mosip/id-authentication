package io.mosip.registration.processor.status.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

public class TransactionTableNotAccessibleException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public TransactionTableNotAccessibleException() {
		super();
	}

	public TransactionTableNotAccessibleException(String message) {
		super(PlatformErrorMessages.RPR_RGS_TRANSACTION_TABLE_NOT_ACCESSIBLE.getCode(), message);
	}

	public TransactionTableNotAccessibleException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_RGS_TRANSACTION_TABLE_NOT_ACCESSIBLE.getCode() + EMPTY_SPACE, message, cause);
	}
}