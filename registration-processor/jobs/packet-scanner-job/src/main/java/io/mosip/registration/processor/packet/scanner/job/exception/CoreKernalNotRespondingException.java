package io.mosip.registration.processor.packet.scanner.job.exception;

import org.mosip.kernel.core.exception.BaseUncheckedException;

import io.mosip.registration.processor.packet.scanner.job.exception.utils.PacketScannerErrorCodes;

public class CoreKernalNotRespondingException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public CoreKernalNotRespondingException(String errorMessage) {
		super(PacketScannerErrorCodes.IIS_EPP_EPV_CORE_KERNEL_NOT_RESPONDING, errorMessage);
	}

	public CoreKernalNotRespondingException(String message, Throwable cause) {
		super(PacketScannerErrorCodes.IIS_EPP_EPV_CORE_KERNEL_NOT_RESPONDING + EMPTY_SPACE, message, cause);
	}

}
