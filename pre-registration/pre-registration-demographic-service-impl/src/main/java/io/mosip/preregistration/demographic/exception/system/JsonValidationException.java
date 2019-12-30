/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.demographic.exception.system;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import lombok.Getter;

/**
 * This class defines the JsonValidationException
 * 
 * @author Rajath KR
 * @since 1.0.0
 * 
 */
@Getter
public class JsonValidationException extends BaseUncheckedException {
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	private MainResponseDTO<?> mainResposneDTO;
	/**
	 * @param msg  pass the error message
	 */
	public JsonValidationException(String msg) {
		super("", msg);
	}

	/**
	 * @param errCode  pass the error code
	 * @param msg  pass the error message
	 */
	public JsonValidationException(String errCode, String msg) {
		super(errCode, msg);
	}

	/**
	 * @param errCode  pass the error code
	 * @param msg  pass the error message
	 */
	public JsonValidationException(String errCode, String msg,MainResponseDTO<?> response) {
		super(errCode, msg);
		this.mainResposneDTO=response;
	}
	/**
	 * @param errCode  pass the error code
	 * @param msg  pass the error message
	 * @param cause  pass the error cause
	 */
	public JsonValidationException(String errCode, String msg, Throwable cause) {
		super(errCode, msg, cause);
	}

}
