/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.notification.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import lombok.Getter;

/**
 * This class defines the JsonParseException
 * 
 * @author Sanober Noor 
 * @since 1.0.0
 *
 */
@Getter
public class JsonParseException extends BaseUncheckedException {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	private final MainResponseDTO<?> mainResponseDTO;

	/**
	 * @param msg
	 */
	public JsonParseException(String msg,MainResponseDTO<?> response) {
		super("", msg);
		this.mainResponseDTO=response;
	}

	/**
	 * @param errCode
	 * @param msg
	 */
	public JsonParseException(String errCode, String msg,MainResponseDTO<?> response) {
		super(errCode, msg);
		this.mainResponseDTO=response;
	}

	/**
	 * @param errCode
	 * @param msg
	 * @param cause
	 */
	public JsonParseException(String errCode, String msg, Throwable cause,MainResponseDTO<?> response) {
		super(errCode, msg, cause);
		this.mainResponseDTO=response;
	}

}
