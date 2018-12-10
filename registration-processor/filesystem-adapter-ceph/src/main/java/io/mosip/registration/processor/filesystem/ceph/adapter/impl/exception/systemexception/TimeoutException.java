package io.mosip.registration.processor.filesystem.ceph.adapter.impl.exception.systemexception;



import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

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
		super(PlatformErrorMessages.RPR_SYS_TIMEOUT_EXCEPTION.getCode(), message);
	}
	
	public TimeoutException(String message,Throwable cause) {
		super(PlatformErrorMessages.RPR_SYS_TIMEOUT_EXCEPTION.getCode() + EMPTY_SPACE, message,cause);
	}
	
	

}