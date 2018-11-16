package io.mosip.registration.processor.scanner.virusscanner.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorCodes;


public class DFSNotAccessibleException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public DFSNotAccessibleException() {
		super();
	}

	public DFSNotAccessibleException(String errorMessage) {
		super(PlatformErrorCodes.RPR_PSJ_DFS_NOT_ACCESSIBLE, errorMessage);
	}

	public DFSNotAccessibleException(String message, Throwable cause) {
		super(PlatformErrorCodes.RPR_PSJ_DFS_NOT_ACCESSIBLE, message, cause);
	}

}
