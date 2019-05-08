package io.mosip.preregistration.core.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import lombok.Getter;

/**
 * @author Sanober Noor
 * @since 1.0.0
 *
 */
@Getter
public class HashingException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 43021822814026167L;
	 private MainResponseDTO<?> mainresponseDTO;

	public HashingException() {
		super();
	}

	public HashingException(String arg0, String arg1, Throwable arg2) {
		super(arg0, arg1, arg2);
	}

	public HashingException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
	public HashingException(String errorCode, String errorMessage,MainResponseDTO<?> response) {
		super(errorCode, errorMessage);
		this.mainresponseDTO=response;
	}

	public HashingException(String errorMessage) {
		super(errorMessage);
	}
	

}
