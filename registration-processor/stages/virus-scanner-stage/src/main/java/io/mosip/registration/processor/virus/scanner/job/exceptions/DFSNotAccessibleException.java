package io.mosip.registration.processor.virus.scanner.job.exceptions;

import io.mosip.kernel.core.exception.BaseUncheckedException;

import io.mosip.registration.processor.virus.scanner.job.exceptions.util.VirusScannerJobErrorCodes;

public class DFSNotAccessibleException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public DFSNotAccessibleException() {
		super();
	}

	public DFSNotAccessibleException(String errorMessage) {
		super(VirusScannerJobErrorCodes.IIS_EPP_EPV_DFS_NOT_ACCESSIBLE, errorMessage);
	}

	public DFSNotAccessibleException(String message, Throwable cause) {
		super(VirusScannerJobErrorCodes.IIS_EPP_EPV_DFS_NOT_ACCESSIBLE, message, cause);
	}

}
