package io.mosip.preregistration.core.common.dto;

import java.io.Serializable;
import java.util.List;

import io.mosip.preregistration.core.common.dto.ExceptionJSONInfoDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Akshay Jain
 * @since 1.0.0
 *
 * @param <T>
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class MainResponseDTO<T> implements Serializable{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3384945682672832638L;
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

	@ApiModelProperty(value = "Response Time", position = 3)
	private String responsetime;

	@ApiModelProperty(value = "Response", position = 4)
	private T response;
	
	/** The error details. */
	@ApiModelProperty(value = "Error Details", position = 5)
	private List<ExceptionJSONInfoDTO> errors;
}
