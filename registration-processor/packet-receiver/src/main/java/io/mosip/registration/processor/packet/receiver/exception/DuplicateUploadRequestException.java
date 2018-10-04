package io.mosip.registration.processor.packet.receiver.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

import io.mosip.registration.processor.packet.receiver.exception.utils.IISPlatformErrorCodes;

/**
 * DuplicateUploadRequestException occurs When a packet is already present in
 * landing zone and again client tries to upload same packet
 *
 */
public class DuplicateUploadRequestException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public DuplicateUploadRequestException() {
		super();
	}

	public DuplicateUploadRequestException(String msg) {
		super(IISPlatformErrorCodes.IIS_EPU_ATU_DUPLICATE_UPLOAD, msg);
	}

	public DuplicateUploadRequestException(String msg, Throwable cause) {
		super(IISPlatformErrorCodes.IIS_EPU_ATU_DUPLICATE_UPLOAD + EMPTY_SPACE, msg, cause);
	}
}
