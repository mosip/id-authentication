package io.mosip.preregistration.booking.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

public class TablenotAccessibleException  extends BaseUncheckedException  {

	private static final long serialVersionUID = 1L;
	
	public TablenotAccessibleException(String msg) {
		super("", msg);
	}
	public TablenotAccessibleException(String msg, Throwable cause) {
		super("", msg, cause);
	}

	
}

