package io.mosip.preregistration.booking.serviceimpl.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import lombok.Getter;

/**
 * @author M1046129
 *
 */

@Getter
public class DocumentNotFoundException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;
	private MainResponseDTO<?> mainResponseDTO;

	public DocumentNotFoundException(String msg) {
		super("", msg);
	}

	public DocumentNotFoundException(String msg, Throwable cause) {
		super("", msg, cause);
	}

	public DocumentNotFoundException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}
	
	public DocumentNotFoundException(String errorCode, String errorMessage,MainResponseDTO<?> response) {
		super(errorCode, errorMessage, null);
		this.mainResponseDTO=response;
	}

	public DocumentNotFoundException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	public DocumentNotFoundException() {
		super();
	}

}
