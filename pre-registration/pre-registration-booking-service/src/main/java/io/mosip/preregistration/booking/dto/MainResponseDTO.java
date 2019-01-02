package io.mosip.preregistration.booking.dto;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Response DTO
 * 
 * @author M1037717
 *
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class MainResponseDTO<T> implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6705845720255847210L;
	
	/** The error details. */
	@ApiModelProperty(value = "response error details", position = 1)
	private ExceptionJSONInfoDTO err;
	@ApiModelProperty(value = "response status", position = 2)
	private Boolean status;
	@ApiModelProperty(value = "response time", position = 3)
	private String resTime;
	@ApiModelProperty(value = "response", position = 4)
	private T response;
}
