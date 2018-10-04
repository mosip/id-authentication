package io.mosip.registration.processor.packet.receiver.exception.systemexception;


import io.mosip.kernel.core.exception.BaseUncheckedException;

import io.mosip.registration.processor.packet.receiver.exception.utils.IISPlatformErrorCodes;

/**
 * TimeoutException occurs when time exceeds specified time limit.
 *
 */
public class TimeoutException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public TimeoutException() {
		super();

	}

	public TimeoutException(String message) {
		super(IISPlatformErrorCodes.IIS_EPU_ATU_TIMEOUT, message);
	}
	
	public TimeoutException(String message,Throwable cause) {
		super(IISPlatformErrorCodes.IIS_EPU_ATU_TIMEOUT + EMPTY_SPACE, message,cause);
	}
	
	

}