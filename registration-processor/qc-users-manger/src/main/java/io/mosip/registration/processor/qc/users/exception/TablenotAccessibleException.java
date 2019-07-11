package io.mosip.registration.processor.qc.users.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.qc.users.exception.code.QualityCheckerErrorCode;

public class TablenotAccessibleException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public TablenotAccessibleException() {
		super();
	}

	public TablenotAccessibleException(String errorMessage) {
		super(QualityCheckerErrorCode.TABLE_NOT_ACCESSIBLE, errorMessage);
	}

	public TablenotAccessibleException(String message, Throwable cause) {
		super(QualityCheckerErrorCode.TABLE_NOT_ACCESSIBLE, message, cause);
	}

}