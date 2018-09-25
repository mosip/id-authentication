package org.mosip.registration.processor.filesystem.ceph.adapter.impl.exception.systemexception;



import org.mosip.kernel.core.exception.BaseUncheckedException;
import org.mosip.registration.processor.filesystem.ceph.adapter.impl.exception.utils.IISPlatformErrorCodes;

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