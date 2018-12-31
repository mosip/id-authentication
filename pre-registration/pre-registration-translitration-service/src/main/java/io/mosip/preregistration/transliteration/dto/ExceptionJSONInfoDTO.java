/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.transliteration.dto;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * This DTO class defines the errorcodes and errormessages during exception handling.
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 *
 */
@Getter
@Setter
public class ExceptionJSONInfoDTO implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3999014525078508265L;
	
	
	/**
	 * Error Code
	 */
	@ApiModelProperty(value = "Error Code", position = 1)
	private String errorCode;
	
	/**
	 * Error Message
	 */
	@ApiModelProperty(value = "Error Message", position = 2)
	private String message;

	/**
	 * @param errorcode
	 * @param message
	 */
	public ExceptionJSONInfoDTO(String errorcode, String message) {
		super();
		errorCode = errorcode;
		this.message = message;
	}
	
	public String getErrorcode() {
		return errorCode;
	}


	public String getMessage() {
		return message;
	}
}
