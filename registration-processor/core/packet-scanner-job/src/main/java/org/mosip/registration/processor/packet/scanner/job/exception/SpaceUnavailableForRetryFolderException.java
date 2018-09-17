package org.mosip.registration.processor.packet.scanner.job.exception;

import org.mosip.kernel.core.exception.BaseUncheckedException;
import org.mosip.registration.processor.packet.scanner.job.exception.utils.PacketScannerErrorCodes;

public class SpaceUnavailableForRetryFolderException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public SpaceUnavailableForRetryFolderException(String errorMessage) {
		super(PacketScannerErrorCodes.IIS_EPP_EPV_SPACE_UNAVAILABLE_FOR_RETRY_FOLDER, errorMessage);
	}

	public SpaceUnavailableForRetryFolderException(String message, Throwable cause) {
		super(PacketScannerErrorCodes.IIS_EPP_EPV_SPACE_UNAVAILABLE_FOR_RETRY_FOLDER + EMPTY_SPACE, message, cause);
	}

}
