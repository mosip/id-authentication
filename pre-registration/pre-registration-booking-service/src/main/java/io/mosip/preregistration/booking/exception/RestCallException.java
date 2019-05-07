package io.mosip.preregistration.booking.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import lombok.Getter;

/**
 * @author M1046129
 *
 */

@Getter
public class RestCallException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;
	private MainResponseDTO<?> mainResponseDTO;

	public RestCallException(String msg) {
		super("", msg);
	}

	public RestCallException(String msg, Throwable cause) {
		super("", msg, cause);
	}

	public RestCallException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}
	
	public RestCallException(String errorCode, String errorMessage,MainResponseDTO<?> response) {
		super(errorCode, errorMessage, null);
		this.mainResponseDTO=response;
	}

	public RestCallException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	public RestCallException() {
		super();
	}
}
