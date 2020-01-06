package io.mosip.preregistration.demographic.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import lombok.Getter;

@Getter
public class DuplicatePridKeyException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	private MainResponseDTO<?> mainresponseDTO;


	/**
	 * @param msg pass the error message
	 */
	public DuplicatePridKeyException(String msg) {
		super("", msg);
	}

	/**
	 * @param errCode pass the error code
	 * @param msg pass the error message
	 */
	public DuplicatePridKeyException(String errCode, String msg) {
		super(errCode, msg);
	}
	/**
	 * @param errCode pass the error code
	 * @param msg pass the error message
	 */
	public DuplicatePridKeyException(String errCode, String msg,MainResponseDTO<?> response) {
		super(errCode, msg);
		this.mainresponseDTO=response;
	}

	/**
	 * @param errCode pass the error code
	 * @param msg pass the error message
	 * @param cause pass the cause
	 */
	public DuplicatePridKeyException(String errCode, String msg, Throwable cause) {
		super(errCode, msg, cause);
	}

}
