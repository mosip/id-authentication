/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.core.common.dto;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response DTO
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

	/**
	 * Id
	 */
	@ApiModelProperty(value = "request id", position = 1)
	private String id;
	/**
	 * version
	 */
	@ApiModelProperty(value = "request version", position = 2)
	private String version;
	
	/** The error details. */
	@ApiModelProperty(value = "error details", position = 1)
	private ExceptionJSONInfoDTO errors;


	/**
	 * Response Date Time
	 */
	@ApiModelProperty(value = "response time", position = 3)
	private String responsetime;

	/**
	 * List of Response
	 */
	@ApiModelProperty(value = "response", position = 4)
	private List<T> response;

}
