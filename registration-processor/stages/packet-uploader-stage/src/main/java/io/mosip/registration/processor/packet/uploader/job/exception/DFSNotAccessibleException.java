package io.mosip.registration.processor.packet.uploader.job.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.packet.uploader.job.exceptions.util.PacketUploaderJobErrorCodes;

public class DFSNotAccessibleException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public DFSNotAccessibleException() {
		super();
	}

	public DFSNotAccessibleException(String errorMessage) {
		super(PacketUploaderJobErrorCodes.IIS_EPP_EPV_DFS_NOT_ACCESSIBLE, errorMessage);
	}

	public DFSNotAccessibleException(String message, Throwable cause) {
		super(PacketUploaderJobErrorCodes.IIS_EPP_EPV_DFS_NOT_ACCESSIBLE, message, cause);
	}

}
