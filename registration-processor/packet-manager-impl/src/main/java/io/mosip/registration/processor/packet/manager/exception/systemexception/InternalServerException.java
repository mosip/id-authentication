package io.mosip.registration.processor.packet.manager.exception.systemexception;



import io.mosip.kernel.core.exception.BaseUncheckedException;

import io.mosip.registration.processor.packet.manager.exception.utils.IISPlatformErrorCodes;

/**
 * InternalServerException occurs for any internal server issue.
 *
 */
public class InternalServerException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public InternalServerException() {
		super();
	}

	public InternalServerException(String message) {
		super(IISPlatformErrorCodes.IIS_EPU_FSS_SERVER_ERROR, message);
	}

	public InternalServerException(String msg, Throwable cause) {
		super(IISPlatformErrorCodes.IIS_EPU_FSS_SERVER_ERROR + EMPTY_SPACE, msg, cause);
	}
}
