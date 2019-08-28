/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.batchjob.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import lombok.Getter;

/**
 * @author Kishan Rathore
 * @since 1.0.0
 *
 */
@Getter
public class NoPreIdAvailableException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8146722453630331685L;
	public NoPreIdAvailableException(String message) {
		super("",message);
	}
	public NoPreIdAvailableException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	public NoPreIdAvailableException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

}
