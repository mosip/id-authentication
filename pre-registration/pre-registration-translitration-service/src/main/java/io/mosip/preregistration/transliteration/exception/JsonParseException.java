/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.transliteration.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import lombok.Getter;

/**
 * This class defines the JsonParseException
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 *
 */

@Getter
public class JsonParseException extends BaseUncheckedException {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	private MainResponseDTO<?> mainResponseDto;

	/**
	 * @param msg
	 */
	public JsonParseException(String msg,MainResponseDTO<?> resposne) {
		super("", msg);
		this.mainResponseDto=resposne;
	}

	/**
	 * @param errCode
	 * @param msg
	 */
	public JsonParseException(String errCode, String msg,MainResponseDTO<?> resposne) {
		super(errCode, msg);
		this.mainResponseDto=resposne;
	}

	/**
	 * @param errCode
	 * @param msg
	 * @param cause
	 */
	public JsonParseException(String errCode, String msg, Throwable cause,MainResponseDTO<?> resposne) {
		super(errCode, msg, cause);
		this.mainResponseDto=resposne;
	}

}
