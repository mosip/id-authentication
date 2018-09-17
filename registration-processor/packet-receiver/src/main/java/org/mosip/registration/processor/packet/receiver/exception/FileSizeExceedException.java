package org.mosip.registration.processor.packet.receiver.exception;

import org.mosip.kernel.core.exception.BaseUncheckedException;
import org.mosip.registration.processor.packet.receiver.exception.utils.IISPlatformErrorCodes;

/**
 * FileSizeExceedException occurs when uploaded file size is more than specified
 * size.
 *
 */
public class FileSizeExceedException extends BaseUncheckedException {
	private static final long serialVersionUID = 1L;

	public FileSizeExceedException() {
		super();
	}

	public FileSizeExceedException(String message) {
		super(IISPlatformErrorCodes.IIS_EPU_ATU_FILE_SIZE_EXCEED, message);
	}

	public FileSizeExceedException(String message, Throwable cause) {
		super(IISPlatformErrorCodes.IIS_EPU_ATU_FILE_SIZE_EXCEED + EMPTY_SPACE, message, cause);
	}
}
