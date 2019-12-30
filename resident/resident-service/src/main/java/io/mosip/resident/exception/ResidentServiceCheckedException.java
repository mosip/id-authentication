package io.mosip.resident.exception;

import io.mosip.kernel.core.exception.BaseCheckedException;
/**
 * 
 * @author Girish Yarru
 *
 */
public class ResidentServiceCheckedException extends BaseCheckedException {
	private static final long serialVersionUID = -1561461793874550645L;

	public ResidentServiceCheckedException() {
		super();
	}

	public ResidentServiceCheckedException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	public ResidentServiceCheckedException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

}
