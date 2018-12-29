/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.booking.dto;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This DTO class is used to accept the response values.
 * 
 * @author Rajath KR
 * @since 1.0.0
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class MainListResponseDTO<T> implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6705845720255847210L;

	/** The error details. */
	@ApiModelProperty(value = "response error details", position = 1)
	private ExceptionJSONInfoDTO err;

	/**
	 * Response Status
	 */
	@ApiModelProperty(value = "response status", position = 2)
	private Boolean status;

	/**
	 * Response Date Time
	 */
	@ApiModelProperty(value = "response time", position = 3)
	private String resTime;

	/**
	 * List of Response
	 */
	@ApiModelProperty(value = "list of response", position = 4)
	private List<T> response;

}
