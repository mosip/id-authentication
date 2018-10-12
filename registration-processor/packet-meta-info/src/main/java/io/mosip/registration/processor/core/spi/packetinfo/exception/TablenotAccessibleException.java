package io.mosip.registration.processor.core.spi.packetinfo.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.spi.packetinfo.exception.code.PacketMetaInfoErrorCode;

public class TablenotAccessibleException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public TablenotAccessibleException() {
		super();
	}

	public TablenotAccessibleException(String errorMessage) {
		super(PacketMetaInfoErrorCode.TABLE_NOT_ACCESSIBLE, errorMessage);
	}

	public TablenotAccessibleException(String message, Throwable cause) {
		super(PacketMetaInfoErrorCode.TABLE_NOT_ACCESSIBLE, message, cause);
	}

}