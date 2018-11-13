package io.mosip.registration.processor.status.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.RPRPlatformErrorCodes;

public class TransactionTableNotAccessibleException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public TransactionTableNotAccessibleException() {
		super();
	}

	public TransactionTableNotAccessibleException(String message) {
		super(RPRPlatformErrorCodes.RPR_RGS_TRANSACTION_TABLE_NOT_ACCESSIBLE, message);
	}

	public TransactionTableNotAccessibleException(String message, Throwable cause) {
		super(RPRPlatformErrorCodes.RPR_RGS_TRANSACTION_TABLE_NOT_ACCESSIBLE + EMPTY_SPACE, message, cause);
	}
}