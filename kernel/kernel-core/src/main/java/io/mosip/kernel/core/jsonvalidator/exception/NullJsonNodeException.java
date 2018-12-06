package io.mosip.kernel.core.jsonvalidator.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Exception class when the Json String provided for validation is null.
 * 
 * @author Swati Raj
 * @since 1.0.0
 *
 */
public class NullJsonNodeException extends BaseUncheckedException {

	private static final long serialVersionUID = -1097192955195432642L;

	public NullJsonNodeException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
