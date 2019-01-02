/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.documents.dto;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * This DTO class defines the errorcodes and errormessages during exception
 * handling.
 * 
 * @author Rajath KR
 * @since 1.0.0
 */
public class ExceptionJSONInfoDTO implements Serializable {

	/** The Constant serialVersionUID. */
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
