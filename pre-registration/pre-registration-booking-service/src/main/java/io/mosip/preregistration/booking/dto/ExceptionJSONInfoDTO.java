package io.mosip.preregistration.booking.dto;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;

/**
 * Exception json Info
 * 
 * @author M1037717
 *
 */
public class ExceptionJSONInfoDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3999014525078508265L;

	@ApiModelProperty(value = "Error Code", position = 1)
	private String errorCode;
	@ApiModelProperty(value = "Error Message", position = 2)
	private String message;

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
