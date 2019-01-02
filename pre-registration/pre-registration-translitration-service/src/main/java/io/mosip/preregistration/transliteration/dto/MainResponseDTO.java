/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.transliteration.dto;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Main response DTO
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 *
 * @param <T>
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class MainResponseDTO<T> implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3384945682672832638L;

	/** The error details. */
	@ApiModelProperty(value = "Error Details", position = 1)
	private ExceptionJSONInfoDTO err;

	/**
	 * Response Status
	 */
	@ApiModelProperty(value = "Response Status", position = 2)
	private boolean status;

	/**
	 * Response Date Time
	 */
	@ApiModelProperty(value = "Response Time", position = 3)
	private String resTime;

	/**
	 * Object of Response
	 */
	@ApiModelProperty(value = "Response", position = 4)
	private T response;

}
