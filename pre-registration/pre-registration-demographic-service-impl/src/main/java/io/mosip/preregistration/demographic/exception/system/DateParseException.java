/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.demographic.exception.system;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;

/**
 * This class defines the DateParseException
 * 
 * @author Jagadishwari S
 * @since 1.0.0
 * 
 */
public class DateParseException extends BaseUncheckedException {
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	 private MainResponseDTO<?> mainresponseDTO;

	/**
	 * @param msg pass the error message
	 */
	public DateParseException(String msg) {
		super("", msg);
	}

	/**
	 * @param errCode pass the error code
	 * @param msg pass the error message
	 */
	public DateParseException(String errCode, String msg) {
		super(errCode, msg);
	}
	
	/**
	 * @param errCode pass the error code
	 * @param msg pass the error message
	 */
	public DateParseException(String errCode, String msg,MainResponseDTO<?>response) {
		super(errCode, msg);
		this.mainresponseDTO=response;
	}

	/**
	 * @param errCode pass the error code
	 * @param msg pass the error message
	 * @param cause pass the error cause
	 */
	public DateParseException(String errCode, String msg, Throwable cause) {
		super(errCode, msg, cause);
	}

}
